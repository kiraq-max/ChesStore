package it.unisa.control;

import it.unisa.dao.CategoriaDao;
import it.unisa.dao.ProductDao;
import it.unisa.model.CategoriaBean;
import it.unisa.model.ProductBean;
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

@WebServlet(name = "AdminCatalogoServlet", urlPatterns = {"/admin/catalogo", "/admin/prodotti"})
public class AdminCatalogoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AdminCatalogoServlet.class.getName());

    private static final String SESSION_UTENTE = "utente";
    private static final String RUOLO_ADMIN = "admin";

    private ProductDao productDao;
    private CategoriaDao categoriaDao;

    @Override
    public void init() throws ServletException {
        super.init();
        this.productDao = new ProductDao();
        this.categoriaDao = new CategoriaDao();
    }

    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    public void setCategoriaDao(CategoriaDao categoriaDao) {
        this.categoriaDao = categoriaDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkAdminAuth(request, response)) {
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.trim().isEmpty()) {
            action = "list";
        }

        try {
            switch (action.toLowerCase()) {
                case "delete":
                    handleDelete(request, response);
                    break;
                case "edit":
                    handleShowEdit(request, response);
                    break;
                case "new":
                case "list":
                default:
                    handleShowList(request, response);
                    break;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL in AdminCatalogoServlet (doGet)", e);
            request.setAttribute("errorMessage", "Si è verificato un errore durante la gestione del catalogo.");
            request.getRequestDispatcher("/WEB-INF/view/error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!checkAdminAuth(request, response)) {
            return;
        }

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        try {
            if ("delete".equalsIgnoreCase(action)) {
                handleDelete(request, response);
            } else if ("update".equalsIgnoreCase(action)) {
                handleSaveOrUpdate(request, response, true);
            } else {
                // Default: insert nuovo prodotto
                handleSaveOrUpdate(request, response, false);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL in AdminCatalogoServlet (doPost)", e);
            request.setAttribute("errorMessage", "Errore nel salvataggio del prodotto nel database.");
            request.getRequestDispatcher("/WEB-INF/view/error.jsp").forward(request, response);
        }
    }

    private void handleShowList(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        List<ProductBean> prodotti = productDao.doRetrieveAll();
        List<CategoriaBean> categorie = categoriaDao.doRetrieveAll();

        request.setAttribute("prodotti", prodotti);
        request.setAttribute("categorie", categorie);
        request.getRequestDispatcher("/WEB-INF/view/admin/gestione-catalogo.jsp").forward(request, response);
    }

    private void handleShowEdit(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/admin/catalogo");
            return;
        }

        try {
            int id = Integer.parseInt(idParam.trim());
            ProductBean prodotto = productDao.doRetrieveByKey(id);

            if (prodotto == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Prodotto da modificare non trovato.");
                return;
            }

            List<ProductBean> prodotti = productDao.doRetrieveAll();
            List<CategoriaBean> categorie = categoriaDao.doRetrieveAll();

            request.setAttribute("prodotto", prodotto);
            request.setAttribute("prodotti", prodotti);
            request.setAttribute("categorie", categorie);
            request.setAttribute("isEditMode", true);

            request.getRequestDispatcher("/WEB-INF/view/admin/gestione-catalogo.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            LOGGER.log(Level.WARNING, "ID prodotto non valido per modifica: " + idParam, e);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID prodotto non valido.");
        }
    }

    private void handleDelete(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, IOException {
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idParam.trim());
                productDao.doDelete(id);
                response.sendRedirect(request.getContextPath() + "/admin/catalogo?msg=deleted");
                return;
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "ID non valido per eliminazione prodotto: " + idParam, e);
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/catalogo?error=invalid_id");
    }

    private void handleSaveOrUpdate(HttpServletRequest request, HttpServletResponse response, boolean isUpdate)
            throws SQLException, ServletException, IOException {

        String idParam = request.getParameter("id");
        String nome = request.getParameter("nome");
        String descrizione = request.getParameter("descrizione");
        String prezzoParam = request.getParameter("prezzo");
        String quantitaParam = request.getParameter("quantitaDisponibile");
        String immagine = request.getParameter("immagine");
        String idCategoriaParam = request.getParameter("idCategoria");

        // Validazione
        StringBuilder errors = new StringBuilder();

        if (nome == null || nome.trim().isEmpty()) {
            errors.append("Il nome del prodotto è obbligatorio. ");
        }

        double prezzo = 0.0;
        try {
            if (prezzoParam != null) {
                prezzo = Double.parseDouble(prezzoParam.trim().replace(',', '.'));
                if (prezzo <= 0) {
                    errors.append("Il prezzo deve essere maggiore di zero. ");
                }
            } else {
                errors.append("Il prezzo è obbligatorio. ");
            }
        } catch (NumberFormatException e) {
            errors.append("Formato prezzo non valido. ");
        }

        int quantitaDisponibile = 0;
        try {
            if (quantitaParam != null) {
                quantitaDisponibile = Integer.parseInt(quantitaParam.trim());
                if (quantitaDisponibile < 0) {
                    errors.append("La quantità disponibile non può essere negativa. ");
                }
            } else {
                errors.append("La quantità è obbligatoria. ");
            }
        } catch (NumberFormatException e) {
            errors.append("Formato quantità non valido. ");
        }

        int idCategoria = 0;
        try {
            if (idCategoriaParam != null) {
                idCategoria = Integer.parseInt(idCategoriaParam.trim());
                if (idCategoria <= 0) {
                    errors.append("Seleziona una categoria valida. ");
                }
            } else {
                errors.append("La categoria è obbligatoria. ");
            }
        } catch (NumberFormatException e) {
            errors.append("Formato categoria non valido. ");
        }

        int id = 0;
        if (isUpdate) {
            try {
                id = Integer.parseInt(idParam.trim());
            } catch (Exception e) {
                errors.append("ID prodotto non valido per l'aggiornamento. ");
            }
        }

        if (immagine == null || immagine.trim().isEmpty()) {
            immagine = "images/chess-default.png";
        } else {
            immagine = immagine.trim();
        }

        if (errors.length() > 0) {
            ProductBean formBean = new ProductBean(id, nome, descrizione, prezzo, quantitaDisponibile, immagine, idCategoria);
            request.setAttribute("prodotto", formBean);
            request.setAttribute("formError", errors.toString());
            request.setAttribute("isEditMode", isUpdate);
            request.setAttribute("prodotti", productDao.doRetrieveAll());
            request.setAttribute("categorie", categoriaDao.doRetrieveAll());
            request.getRequestDispatcher("/WEB-INF/view/admin/gestione-catalogo.jsp").forward(request, response);
            return;
        }

        ProductBean product = new ProductBean(id, nome.trim(), descrizione != null ? descrizione.trim() : "",
                prezzo, quantitaDisponibile, immagine, idCategoria);

        if (isUpdate) {
            productDao.doUpdate(product);
            response.sendRedirect(request.getContextPath() + "/admin/catalogo?msg=updated");
        } else {
            productDao.doSave(product);
            response.sendRedirect(request.getContextPath() + "/admin/catalogo?msg=created");
        }
    }

    private boolean checkAdminAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        return AuthControlUtils.checkAdminAuth(request, response);
    }
}
