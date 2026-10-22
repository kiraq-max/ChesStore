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
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "OrdineClienteServlet", urlPatterns = {"/ordini"})
public class OrdineClienteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(OrdineClienteServlet.class.getName());

    private static final String SESSION_UTENTE = "utente";

    private OrdineDao ordineDao;


    @Override
    public void init() throws ServletException {
        super.init();
        this.ordineDao = new OrdineDao();
    }
    public void setOrdineDao(OrdineDao ordineDao) {
        this.ordineDao = ordineDao;
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        UtenteBean utente = (session != null) ? (UtenteBean) session.getAttribute(SESSION_UTENTE) : null;

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        String idParam = request.getParameter("id");

        try {
            if (idParam != null && !idParam.trim().isEmpty()) {
                mostraDettaglioOrdine(request, response, utente, idParam.trim());
            } else {
                mostraListaOrdini(request, response, utente);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL in OrdineClienteServlet per utente: " + utente.getEmail(), e);
            request.setAttribute("errorMessage", "Si è verificato un errore nel recupero degli ordini. Riprova più tardi.");
            request.getRequestDispatcher("/WEB-INF/view/error.jsp").forward(request, response);
        }
    }

    private void mostraListaOrdini(HttpServletRequest request, HttpServletResponse response,
                                   UtenteBean utente)
            throws SQLException, ServletException, IOException {

        List<OrdineBean> ordini = ordineDao.doRetrieveByUtente(utente.getEmail());

        request.setAttribute("ordini", ordini);
        request.setAttribute("utente", utente);
        request.getRequestDispatcher("/WEB-INF/view/miei-ordini.jsp").forward(request, response);
    }
    private void mostraDettaglioOrdine(HttpServletRequest request, HttpServletResponse response,
                                       UtenteBean utente, String idParam)
            throws SQLException, ServletException, IOException {

        int idOrdine;
        try {
            idOrdine = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID ordine non valido.");
            return;
        }

        OrdineBean ordine = ordineDao.doRetrieveByKey(idOrdine);

        if (ordine == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Ordine non trovato.");
            return;
        }

        if (!utente.getEmail().equalsIgnoreCase(ordine.getUtenteEmail())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Non sei autorizzato a visualizzare questo ordine.");
            return;
        }

        List<ComposizioneOrdineBean> righe = ordineDao.doRetrieveDettaglioOrdine(idOrdine);

        request.setAttribute("ordine", ordine);
        request.setAttribute("righeOrdine", righe);
        request.getRequestDispatcher("/WEB-INF/view/miei-ordini.jsp").forward(request, response);
    }
}
