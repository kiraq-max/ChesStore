package it.unisa.control;

import it.unisa.dao.UtenteDao;
import it.unisa.model.UtenteBean;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "AuthServlet", urlPatterns = {"/auth", "/AuthServlet", "/login", "/logout"})
public class AuthServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(AuthServlet.class.getName());

    private UtenteDao utenteDao;

    @Override
    public void init() throws ServletException {
        super.init();
        this.utenteDao = new UtenteDao();
    }

    public void setUtenteDao(UtenteDao utenteDao) {
        this.utenteDao = utenteDao;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = request.getParameter("azione");
        }

        String servletPath = request.getServletPath();

        if ("/logout".equals(servletPath) || "logout".equalsIgnoreCase(action)) {
            handleLogout(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("utente") != null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");
        if (action == null) {
            action = request.getParameter("azione");
        }

        if (action == null || action.trim().isEmpty()) {
            action = "login";
        }

        try {
            switch (action.toLowerCase()) {
                case "login":
                    handleLogin(request, response);
                    break;
                case "registrazione":
                case "register":
                    handleRegistrazione(request, response);
                    break;
                case "logout":
                    handleLogout(request, response);
                    break;
                default:
                    response.sendRedirect(request.getContextPath() + "/index.jsp");
                    break;
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Errore SQL durante l'operazione di autenticazione", e);
            request.setAttribute("errorMessage", "Si è verificato un errore sul database. Riprova più tardi.");
            request.getRequestDispatcher("/WEB-INF/view/error.jsp").forward(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("loginError", "Inserisci sia email che password.");
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
            return;
        }

        email = email.trim();
        UtenteBean utente = utenteDao.doRetrieveByEmailPassword(email, password);

        if (utente != null) {
            HttpSession session = request.getSession(true);
            session.setAttribute("utente", utente);
            session.setAttribute("ruolo", utente.getRuolo());

            response.sendRedirect(request.getContextPath() + "/index.jsp");
        } else {
            request.setAttribute("loginError", "Email o password errati. Riprova.");
            request.setAttribute("enteredEmail", email);
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        }
    }

    private void handleRegistrazione(HttpServletRequest request, HttpServletResponse response)
            throws SQLException, ServletException, IOException {
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confermaPassword = request.getParameter("confermaPassword");

        if (nome == null || nome.trim().isEmpty() ||
            cognome == null || cognome.trim().isEmpty() ||
            email == null || email.trim().isEmpty() ||
            password == null || password.trim().isEmpty()) {
            request.setAttribute("regError", "Tutti i campi obbligatori devono essere compilati.");
            preserveRegForm(request, nome, cognome, email);
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
            return;
        }

        if (confermaPassword != null && !password.equals(confermaPassword)) {
            request.setAttribute("regError", "Le password inserite non coincidono.");
            preserveRegForm(request, nome, cognome, email);
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
            return;
        }

        email = email.trim();

        UtenteBean esistente = utenteDao.doRetrieveByEmail(email);
        if (esistente != null) {
            request.setAttribute("regError", "Email già presente. Accedi o utilizza un'altra email.");
            preserveRegForm(request, nome, cognome, email);
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
            return;
        }

        UtenteBean nuovoUtente = new UtenteBean();
        nuovoUtente.setNome(nome.trim());
        nuovoUtente.setCognome(cognome.trim());
        nuovoUtente.setEmail(email);
        nuovoUtente.setPassword(password);
        nuovoUtente.setRuolo("CLIENTE");

        utenteDao.doSave(nuovoUtente);

        HttpSession session = request.getSession(true);
        session.setAttribute("utente", nuovoUtente);
        session.setAttribute("ruolo", nuovoUtente.getRuolo());

        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute("utente");
            session.removeAttribute("ruolo");
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/index.jsp");
    }

    private void preserveRegForm(HttpServletRequest request, String nome, String cognome, String email) {
        request.setAttribute("regNome", nome != null ? nome.trim() : "");
        request.setAttribute("regCognome", cognome != null ? cognome.trim() : "");
        request.setAttribute("regEmail", email != null ? email.trim() : "");
    }
}
