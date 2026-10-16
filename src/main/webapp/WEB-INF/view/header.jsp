<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

    <!DOCTYPE html>
    <html lang="it">

    <head>
        <meta charset="UTF-8" />
        <meta name="viewport" content="width=device-width, initial-scale = 1.0" />
        <title>ChesStore | Il Negozio di Scacchi</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    </head>

    <body>
        <header class="site-header">
            <div class=" header-container">
                <div class="logo">
                    <a href="${pageContext.request.contextPath}/catalogo">
                        <span class="logo-icon">&#9818;</span>
                        <span class="logo-text">ChesStore</span>
                    </a>
                </div>
                <nav class="main-nav">
                    <ul>
                        <li><a href="${pageContext.request.contextPath}/catalogo">Catalogo</a></li>
                    </ul>
                </nav>
                <div class="header-actions">
                    <% if (session.getAttribute("utente") !=null) { it.unisa.model.UtenteBean
                        utente=(it.unisa.model.UtenteBean) session.getAttribute("utente"); %>
                        <div class="user-menu">
                            <span class="user-greeting"> Ciao, <strong>
                                    <%= utente.getNome()%>
                                </strong></span>
                            <% if("admin".equalsIgnoreCase(utente.getRuolo())){ %>
                                <a href="${pageContext.request.contextPath}/admin/catalogo" class="nav-link admin-link">
                                    Area Admin</a>
                                <%} else { %>
                                    <a href="${pageContext.request.contextPath}/ordini" class="nav-link user-ordini">
                                        I miei Ordini</a>
                                    <% } %>
                                        <a href="${pageContext.request.contextPath}/logout"
                                            class="nav-link logout-link">
                                            Logout
                                        </a>
                        </div>

                        <% } else { %>

                            <div class="auth-links">
                                <a href="${pageContext.request.contextPath}/login" class="nav-link login-btn">Log-in</a>
                            </div>
                            <% } %>
                                <% it.unisa.model.CartBean cart=(it.unisa.model.CartBean)
                                    session.getAttribute("carrello"); int itemCount=(cart !=null) ?
                                    cart.getNumeroElementi() : 0; %>

                                    <div class="cart-action">
                                        <a href="${pageContext.request.contextPath}/carrello" class="cart-button">
                                            <span class="cart-icon" aria-hidden="true">&#128722;</span>
                                            <span class="cart-text">Carrello</span>
                                            <span class="cart-badge" id="cart-badge">
                                                <%= itemCount %>
                                            </span>

                                        </a>
                                    </div>
                </div>
            </div>
        </header>
        <main class="main-content">