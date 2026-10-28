package it.unisa.control;

import it.unisa.dao.OrdineDao;
import it.unisa.model.CartBean;
import it.unisa.model.CartBean.CartItem;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CheckoutServlet", urlPatterns = {"/checkout", "/CheckoutServlet"})
public class CheckoutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CheckoutServlet.class.getName());

    private static final String SESSION_CART    = "carrello";
    private static final String SESSION_UTENTE  = "utente";

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

        if (!AuthControlUtils.checkUserAuth(request, response)) {
            return;
        }
        HttpSession session = request.getSession(false);

        CartBean carrello = (CartBean) session.getAttribute(SESSION_CART);

        if (carrello == null || carrello.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        request.setAttribute("cart", carrello);
        request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        if (!AuthControlUtils.checkUserAuth(request, response)) {
            return;
        }
        HttpSession session = request.getSession(false);
        UtenteBean utente   = AuthControlUtils.getLoggedUser(request);
        CartBean carrello   = (session != null) ? (CartBean) session.getAttribute(SESSION_CART) : null;
        if (carrello == null || carrello.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        
        String via             = request.getParameter("via");
        String citta           = request.getParameter("citta");
        String cap             = request.getParameter("cap");
        String metodoPagamento = request.getParameter("metodoPagamento");

        if (isBlank(via) || isBlank(citta) || isBlank(cap) || isBlank(metodoPagamento)) {
            request.setAttribute("cart", carrello);
            request.setAttribute("errorMessage", "Tutti i campi dell'indirizzo e il metodo di pagamento sono obbligatori.");
            request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
            return;
        }

        String indirizzoCompleto = via.trim() + ", " + citta.trim() + " (" + cap.trim() + ")";


        OrdineBean ordine = new OrdineBean();
        ordine.setData(new Date());
        ordine.setTotale(carrello.getTotale());
        ordine.setIndirizzoSpedizione(indirizzoCompleto);
        ordine.setMetodoPagamento(metodoPagamento.trim());
        ordine.setUtenteEmail(utente.getEmail());

        List<ComposizioneOrdineBean> righe = new ArrayList<>();
        for (CartItem item : carrello.getItems()) {
            ComposizioneOrdineBean riga = new ComposizioneOrdineBean();
            riga.setIdProdotto(item.getProduct().getId());
            riga.setNomeProdottoStorico(item.getProduct().getNome());
            riga.setPrezzoUnitarioStorico(item.getProduct().getPrezzo());
            riga.setQuantita(item.getQuantita());
            righe.add(riga);
        }

        try {
            ordineDao.doSave(ordine, righe); 

            carrello.clear();

            request.setAttribute("ordine", ordine);
            request.setAttribute("righeOrdine", righe);
            request.getRequestDispatcher("/WEB-INF/view/conferma-ordine.jsp").forward(request, response);

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore durante il salvataggio dell'ordine per " + utente.getEmail(), e);
            request.setAttribute("cart", carrello);
            request.setAttribute("errorMessage", "Errore durante la creazione dell'ordine: " + e.getMessage()
                    + ". Verifica la disponibilità dei prodotti.");
            request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
        }
    }

    
    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
