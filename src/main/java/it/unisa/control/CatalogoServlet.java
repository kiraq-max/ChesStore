package it.unisa.control;

import it.unisa.dao.CategoriaDao;
import it.unisa.dao.ProductDao;
import it.unisa.model.CategoriaBean;
import it.unisa.model.ProductBean;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name="CatalogoServlet", urlPatterns= {"/catalogo", "/CatalogoServlet", "/prodotti"})

public class CatalogoServlet extends HttpServlet{
    private static final long serialVersionUID=1L;
    private static final Logger LOGGER = Logger.getLogger(CatalogoServlet.class.getName());

    private ProductDao productDao;
    private CategoriaDao categoriaDao;

    @Override 
    public void init() throws ServletException{
        super.init();
        this.productDao= new ProductDao();
        this.categoriaDao = new CategoriaDao();
    }

    public void setCategoriaDao(CategoriaDao categoriaDao){
        this.categoriaDao = categoriaDao;
    }
    
    public void setProductDao(ProductDao productDao){
        this.productDao = productDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
        String idParam = request.getParameter("id");
        String categoriaParam = request.getParameter("categoria");
        if(categoriaParam == null){
            categoriaParam = request.getParameter("idCategoria");
        }

        try{
            List<CategoriaBean> categorie = categoriaDao.doRetrieveAll();
            request.setAttribute("categorie", categorie);
            if(idParam != null && !idParam.trim().isEmpty()){
                try{
                    int id = Integer.parseInt(idParam.trim());
                    ProductBean prodotto = productDao.doRetrieveByKey(id);
                    if(prodotto!=null){
                    request.setAttribute("prodotto", prodotto);
                    request.getRequestDispatcher("/WEB-INF/view/dettaglio.jsp").forward(request,response);
                    return;
                    } else {
                        response.sendError(HttpServletResponse.SC_NOT_FOUND, "prodotto non trovato");
                        return;
                    } 
                }catch(NumberFormatException e){
                    LOGGER.log(Level.WARNING, "ID prodotto non valido: " + idParam, e);
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id prodotto non valido" );
                    return;
                }
            }

            List<ProductBean> prodotti;
            Integer idCategoriaSelezionata = null;
            if(categoriaParam != null && !categoriaParam.trim().isEmpty()){
                try {
                    idCategoriaSelezionata = Integer.parseInt(categoriaParam.trim());
                    prodotti = productDao.doRetrieveByCategoria(idCategoriaSelezionata);
                }catch(NumberFormatException e){
                    LOGGER.log(Level.WARNING,"categoria non valida " + categoriaParam, e);
                    prodotti = productDao.doRetrieveAll();
                    idCategoriaSelezionata = null;
                }
            } else {
                prodotti = productDao.doRetrieveAll();
            }
            request.setAttribute("prodotti", prodotti);
            request.setAttribute("categoriaSelezionata", idCategoriaSelezionata);
            request.getRequestDispatcher("/WEB-INF/view/catalogo.jsp").forward(request,response);
            
        }catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore nel recupero del catalogo", e);
            request.setAttribute("errorMessage", "impossibile caricare il cataloog dei prodotti al momento");
            request.getRequestDispatcher("/WEB-INF/view/error.jsp").forward(request,response);
        }
    }

    @Override 
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        doGet(request,response);
    }
        
}