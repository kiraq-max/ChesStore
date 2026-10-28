package it.unisa.control;

import it.unisa.dao.OrdineDao;
import it.unisa.model.ComposizioneOrdineBean;
import it.unisa.model.OrdineBean;
import it.unisa.model.UtenteBean;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Servlet per l'area amministrativa: visualizzazione e filtraggio di tutti gli ordini del sistema.
 * Accessibile solo agli utenti con ruolo "admin".
 *
 * Endpoint:
 *   GET  /admin/ordini              → lista di tutti gli ordini (con filtri opzionali)
 *   GET  /admin/ordini?id=N         → dettaglio ordine #N con righe composizione
 *   POST /admin/ordini              → applica filtri (clienteEmail, daData, aData)
 */
@WebServlet(name = "AdminOrdiniServlet", urlPatterns = {"/admin/ordini"})
public class AdminOrdiniServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminOrdiniServlet.class.getName());

    private static final String SESSION_UTENTE  = "utente";
    private static final String RUOLO_ADMIN     = "admin";
    private static final String DATE_FORMAT     = "yyyy-MM-dd";
    private static final String VIEW_LIST       = "/WEB-INF/view/admin/ordini.jsp";
    private static final String VIEW_ERROR      = "/WEB-INF/view/error.jsp";

    private OrdineDao ordineDao;

    @Override
    public void init() throws ServletException {
        super.init();
        this.ordineDao = new OrdineDao();
    }

    // Setter per eventuale iniezione nei test
    public void setOrdineDao(OrdineDao ordineDao) {
        this.ordineDao = ordineDao;
    }

    // -------------------------------------------------------------------------
    // GET – lista ordini oppure dettaglio singolo ordine
    // -------------------------------------------------------------------------
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkAdminAuth(request, response)) {
            return;
        }

        String idParam = request.getParameter("id");

        try {
            if (idParam != null && !idParam.trim().isEmpty()) {
                handleDettaglio(request, response, idParam.trim());
            } else {
                handleLista(request, response, null, null, null);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL in AdminOrdiniServlet (doGet)", e);
            request.setAttribute("errorMessage", "Si e' verificato un errore durante il recupero degli ordini.");
            request.getRequestDispatcher(VIEW_ERROR).forward(request, response);
        }
    }

    // -------------------------------------------------------------------------
    // POST – applica filtri per email cliente e/o intervallo date
    // -------------------------------------------------------------------------
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkAdminAuth(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String clienteEmail = request.getParameter("clienteEmail");
        String daDataStr    = request.getParameter("daData");
        String aDataStr     = request.getParameter("aData");

        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);

        Date daData = null;
        Date aData  = null;

        if (daDataStr != null && !daDataStr.trim().isEmpty()) {
            try {
                daData = sdf.parse(daDataStr.trim());
            } catch (ParseException e) {
                LOGGER.log(Level.WARNING, "Formato data 'daData' non valido: " + daDataStr, e);
                request.setAttribute("filterError", "Formato data 'Da' non valido (usa AAAA-MM-GG).");
            }
        }

        if (aDataStr != null && !aDataStr.trim().isEmpty()) {
            try {
                // Includiamo l'intero giorno finale: +23:59:59
                aData = new Date(sdf.parse(aDataStr.trim()).getTime() + 86399000L);
            } catch (ParseException e) {
                LOGGER.log(Level.WARNING, "Formato data 'aData' non valido: " + aDataStr, e);
                request.setAttribute("filterError", "Formato data 'A' non valido (usa AAAA-MM-GG).");
            }
        }

        // Riportiamo i valori dei filtri alla vista per pre-popolare il form
        request.setAttribute("filterEmail",  clienteEmail != null ? clienteEmail.trim() : "");
        request.setAttribute("filterDaData", daDataStr   != null ? daDataStr.trim()   : "");
        request.setAttribute("filterAData",  aDataStr    != null ? aDataStr.trim()    : "");

        try {
            handleLista(request, response,
                    (clienteEmail != null && !clienteEmail.trim().isEmpty()) ? clienteEmail.trim() : null,
                    daData,
                    aData);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL in AdminOrdiniServlet (doPost)", e);
            request.setAttribute("errorMessage", "Si e' verificato un errore durante il filtraggio degli ordini.");
            request.getRequestDispatcher(VIEW_ERROR).forward(request, response);
        }
    }

    // -------------------------------------------------------------------------
    // Handler privati
    // -------------------------------------------------------------------------

    /**
     * Mostra la lista (eventualmente filtrata) di tutti gli ordini del sistema.
     */
    private void handleLista(HttpServletRequest request, HttpServletResponse response,
                              String clienteEmail, Date daData, Date aData)
            throws SQLException, ServletException, IOException {

        List<OrdineBean> ordini = ordineDao.doRetrieveFiltered(clienteEmail, daData, aData);
        request.setAttribute("ordini", ordini);
        request.getRequestDispatcher(VIEW_LIST).forward(request, response);
    }

    /**
     * Mostra il dettaglio di un singolo ordine con le sue righe di composizione (prezzi storici).
     */
    private void handleDettaglio(HttpServletRequest request, HttpServletResponse response, String idParam)
            throws SQLException, ServletException, IOException {

        try {
            int id = Integer.parseInt(idParam);
            OrdineBean ordine = ordineDao.doRetrieveByKey(id);

            if (ordine == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ordine #" + id + " non trovato.");
                return;
            }

            List<ComposizioneOrdineBean> righe = ordineDao.doRetrieveDettaglioOrdine(id);

            request.setAttribute("ordine",      ordine);
            request.setAttribute("righeOrdine", righe);
            request.getRequestDispatcher(VIEW_LIST).forward(request, response);

        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID ordine non valido: " + idParam, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID ordine non valido.");
        }
    }

    // -------------------------------------------------------------------------
    // Controllo autenticazione / autorizzazione
    // -------------------------------------------------------------------------

    /**
     * Verifica che l'utente in sessione esista e abbia il ruolo "admin".
     * Se non autenticato -> redirect a /auth?redirect=admin/ordini.
     * Se autenticato ma non admin -> HTTP 403 Forbidden.
     *
     * @return true se autorizzato, false se la risposta e' gia' stata gestita.
     */
    private boolean checkAdminAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return AuthControlUtils.checkAdminAuth(request, response);
    }
}
