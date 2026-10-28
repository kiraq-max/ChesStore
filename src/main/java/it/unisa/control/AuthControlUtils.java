package it.unisa.control;

import it.unisa.model.UtenteBean;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.UUID;


public final class AuthControlUtils {

    public static final String SESSION_TOKEN  = "token";
    public static final String SESSION_UTENTE = "utente";
    public static final String SESSION_RUOLO  = "ruolo";

    public static final String RUOLO_ADMIN    = "admin";
    public static final String RUOLO_CLIENTE  = "cliente";

    private AuthControlUtils() {
    }

    
    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    
    public static String registerUserSession(HttpSession session, UtenteBean utente) {
        if (session == null || utente == null) {
            throw new IllegalArgumentException("Sessione o utente non validi per la registrazione.");
        }
        String token = generateToken();
        session.setAttribute(SESSION_TOKEN, token);
        session.setAttribute(SESSION_UTENTE, utente);
        session.setAttribute(SESSION_RUOLO, utente.getRuolo());
        return token;
    }


    public static void invalidateUserSession(HttpSession session) {
        if (session != null) {
            session.removeAttribute(SESSION_TOKEN);
            session.removeAttribute(SESSION_UTENTE);
            session.removeAttribute(SESSION_RUOLO);
            session.invalidate();
        }
    }

   
    public static String getToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        Object tokenObj = session.getAttribute(SESSION_TOKEN);
        return (tokenObj instanceof String) ? (String) tokenObj : null;
    }

    
    public static UtenteBean getLoggedUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        String token = getToken(request);
        if (token == null || token.trim().isEmpty()) {
            return null;
        }

        Object utenteObj = session.getAttribute(SESSION_UTENTE);
        return (utenteObj instanceof UtenteBean) ? (UtenteBean) utenteObj : null;
    }

  
    public static boolean isLoggedIn(HttpServletRequest request) {
        return getLoggedUser(request) != null;
    }

    
    public static boolean isAdmin(HttpServletRequest request) {
        UtenteBean utente = getLoggedUser(request);
        return utente != null && RUOLO_ADMIN.equalsIgnoreCase(utente.getRuolo());
    }

   
    public static boolean checkUserAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return false;
        }
        return true;
    }

    
    public static boolean checkAdminAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!isLoggedIn(request)) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return false;
        }

        if (!isAdmin(request)) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Accesso negato: area riservata agli amministratori.");
            return false;
        }

        return true;
    }
}
