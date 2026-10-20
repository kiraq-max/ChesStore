package it.unisa.control;

import it.unisa.dao.ProductDao;
import it.unisa.model.CartBean;
import it.unisa.model.ProductBean;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "CartServlet", urlPatterns = {"/carrello", "/CartServlet"})
public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CartServlet.class.getName());
    private static final String SESSION_CART = "carrello";

    private ProductDao productDao;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productDao = new ProductDao();
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(true);
        CartBean cart = getOrCreateCart(session);
        request.setAttribute("cart", cart);
        request.getRequestDispatcher("/WEB-INF/view/carrello.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        boolean isAjax = isAjaxRequest(request);

        if (action == null || action.trim().isEmpty()) {
            if (isAjax) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Parametro 'action' mancante.");
            } else {
                response.sendRedirect(request.getContextPath() + "/carrello");
            }
            return;
        }

        HttpSession session = request.getSession(true);
        CartBean cart = getOrCreateCart(session);

        try {
            switch (action.trim().toLowerCase()) {
                case "aggiungi":
                    handleAggiungi(request, response, cart, isAjax);
                    break;
                case "rimuovi":
                    handleRimuovi(request, response, cart, isAjax);
                    break;
                case "modificaquantita":
                case "modifica":
                    handleModificaQuantita(request, response, cart, isAjax);
                    break;
                case "svuota":
                    handleSvuota(request, response, cart, isAjax);
                    break;
                default:
                    LOGGER.log(Level.WARNING, "Azione carrello sconosciuta: {0}", action);
                    if (isAjax) {
                        sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "Azione non riconosciuta: " + action);
                    } else {
                        response.sendRedirect(request.getContextPath() + "/carrello");
                    }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'operazione sul carrello", e);
            if (isAjax) {
                sendJsonError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore database. Riprova più tardi.");
            } else {
                request.setAttribute("errorMessage", "Si è verificato un errore durante l'aggiornamento del carrello.");
                request.getRequestDispatcher("/WEB-INF/view/error.jsp").forward(request, response);
            }
        }
    }

    private void handleAggiungi(HttpServletRequest request, HttpServletResponse response,
                                CartBean cart, boolean isAjax)
            throws SQLException, IOException, ServletException {

        int idProdotto = parseIntParam(request, "idProdotto", -1);
        int quantita   = parseIntParam(request, "quantita",   1);

        if (idProdotto <= 0) {
            if (isAjax) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST, "ID prodotto non valido.");
            } else {
                response.sendRedirect(request.getContextPath() + "/catalogo");
            }
            return;
        }

        if (quantita <= 0) {
            quantita = 1;
        }

        ProductBean prodotto = productDao.doRetrieveByKey(idProdotto);

        if (prodotto == null) {
            if (isAjax) {
                sendJsonError(response, HttpServletResponse.SC_NOT_FOUND, "Prodotto non trovato.");
            } else {
                response.sendRedirect(request.getContextPath() + "/catalogo");
            }
            return;
        }

        int giàNelCarrello = 0;
        CartBean.CartItem itemEsistente = cart.getItemById(idProdotto);
        if (itemEsistente != null) {
            giàNelCarrello = itemEsistente.getQuantita();
        }

        if (giàNelCarrello >= prodotto.getQuantitaDisponibile()) {
            if (isAjax) {
                sendJsonError(response, HttpServletResponse.SC_BAD_REQUEST,
                        "Quantità massima disponibile già presente nel carrello (" + prodotto.getQuantitaDisponibile() + " pz).");
            } else {
                response.sendRedirect(request.getContextPath() + "/carrello");
            }
            return;
        }

        int totaleDopo = giàNelCarrello + quantita;
        if (totaleDopo > prodotto.getQuantitaDisponibile()) {
            quantita = prodotto.getQuantitaDisponibile() - giàNelCarrello;
        }

        if (quantita > 0) {
            cart.addProduct(prodotto, quantita);
        }

        if (isAjax) {
            CartBean.CartItem updatedItem = cart.getItemById(idProdotto);
            int quantitaEffettiva = (updatedItem != null) ? updatedItem.getQuantita() : 0;
            double subtotaleEffettivo = (updatedItem != null) ? updatedItem.getSubtotale() : 0.0;
            sendJsonSuccess(response, cart, idProdotto, quantitaEffettiva, subtotaleEffettivo);
        } else {
            response.sendRedirect(request.getContextPath() + "/carrello");
        }
    }

    private void handleRimuovi(HttpServletRequest request, HttpServletResponse response,
                               CartBean cart, boolean isAjax)
            throws IOException {

        int idProdotto = parseIntParam(request, "idProdotto", -1);
        if (idProdotto > 0) {
            cart.deleteProduct(idProdotto);
        }

        if (isAjax) {
            sendJsonSuccess(response, cart, idProdotto, 0, 0.0);
        } else {
            response.sendRedirect(request.getContextPath() + "/carrello");
        }
    }

    private void handleModificaQuantita(HttpServletRequest request, HttpServletResponse response,
                                        CartBean cart, boolean isAjax)
            throws IOException, SQLException {

        int idProdotto = parseIntParam(request, "idProdotto", -1);
        int quantita   = parseIntParam(request, "quantita",   0);

        if (idProdotto > 0) {
            if (quantita > 0) {
                ProductBean prodotto = productDao.doRetrieveByKey(idProdotto);
                if (prodotto != null && quantita > prodotto.getQuantitaDisponibile()) {
                    quantita = prodotto.getQuantitaDisponibile();
                }
            }
            cart.updateQuantity(idProdotto, quantita);
        }

        if (isAjax) {
            CartBean.CartItem updatedItem = cart.getItemById(idProdotto);
            int quantitaEffettiva = (updatedItem != null) ? updatedItem.getQuantita() : 0;
            double subtotaleEffettivo = (updatedItem != null) ? updatedItem.getSubtotale() : 0.0;
            sendJsonSuccess(response, cart, idProdotto, quantitaEffettiva, subtotaleEffettivo);
        } else {
            response.sendRedirect(request.getContextPath() + "/carrello");
        }
    }

    private void handleSvuota(HttpServletRequest request, HttpServletResponse response,
                              CartBean cart, boolean isAjax)
            throws IOException {

        cart.clear();

        if (isAjax) {
            sendJsonSuccess(response, cart, 0, 0, 0.0);
        } else {
            response.sendRedirect(request.getContextPath() + "/carrello");
        }
    }

    private CartBean getOrCreateCart(HttpSession session) {
        CartBean cart = (CartBean) session.getAttribute(SESSION_CART);
        if (cart == null) {
            cart = new CartBean();
            session.setAttribute(SESSION_CART, cart);
        }
        return cart;
    }

    private boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }

    private int parseIntParam(HttpServletRequest request, String paramName, int defaultValue) {
        String val = request.getParameter(paramName);
        if (val == null || val.trim().isEmpty()) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void sendJsonSuccess(HttpServletResponse response, CartBean cart)
            throws IOException {
        sendJsonSuccess(response, cart, 0, 0, 0.0);
    }

    private void sendJsonSuccess(HttpServletResponse response, CartBean cart,
                                 int idProdotto, int quantitaProdotto, double subtotaleProdotto)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);
        String json = String.format(
                java.util.Locale.US,
                "{\"success\":true,\"numeroElementi\":%d,\"totale\":%.2f,\"idProdotto\":%d,\"quantita\":%d,\"subtotale\":%.2f}",
                cart.getNumeroElementi(),
                cart.getTotale(),
                idProdotto,
                quantitaProdotto,
                subtotaleProdotto
        );
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }

    private void sendJsonError(HttpServletResponse response, int statusCode, String messaggio)
            throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(statusCode);
        String messaggioSafe = messaggio.replace("\"", "\\\"");
        String json = String.format("{\"success\":false,\"messaggio\":\"%s\"}", messaggioSafe);
        try (PrintWriter out = response.getWriter()) {
            out.print(json);
            out.flush();
        }
    }
}
