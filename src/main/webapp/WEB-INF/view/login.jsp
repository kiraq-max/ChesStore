<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
    <!DOCTYPE html>
    <html lang="it">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Login - ChesStore</title>

        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    </head>

    <body>
        <main class="auth-container">
            <header class="auth-header">
                <h1><a href="${pageContext.request.contextPath}/index.jsp">ChesStore</a></h1>
                <p>Accedi o registrati per continuare</p>
            </header>

            <div class="auth-forms-wrapper">
                <section class="auth-card" id="login-section">
                    <h2>ACCEDI</h2>
                    <div class="server-error" id="login-server-error">${loginError}</div>
                    <form action="${pageContext.request.contextPath}/auth" method="POST" id="form-login" novalidate>
                        <input type="hidden" name="action" value="login">
                        <div class="form-group">
                            <label for="login-email">Email</label>
                            <input type="email" id="login-email" name="email" value="${enteredEmail}"
                                placeholder="nome@esempio.it" required autocomplete="email">
                            <span class="field-error" id="login-email-msg"></span>
                        </div>

                        <div class="form-group">
                            <label for="login-password">Password</label>
                            <input type="password" id="login-password" name="password" placeholder="Password" required
                                autocomplete="current-password">
                            <span class="field-error" id="login-password-msg"></span>
                        </div>
                        <button type="submit" class="btn btn-primary" id="btn-login">Accedi</button>
                    </form>
                </section>
                <section class="auth-card" id="register-section">
                    <h2>Registrati</h2>
                    <div class="server-error" id="register-server-error">${regError}</div>
                    <form action="${pageContext.request.contextPath}/auth" method="POST" id="form-registration"
                        novalidate>
                        <input type="hidden" name="action" value="registrazione">
                        <div class="form-group">
                            <label for="reg-nome">Nome</label>
                            <input type="text" id="reg-nome" name="nome" value="${regNome}" placeholder="nome" required
                                autocomplete="given-name">
                            <span class="field-error" id="reg-nome-msg"></span>
                        </div>

                        <div class="form-group">
                            <label for="reg-cognome">Cognome</label>
                            <input type="text" id="reg-cognome" name="cognome" value="${regCognome}"
                                placeholder="cognome" required autocomplete="family-name">
                            <span class="field-error" id="reg-cognome-msg"></span>
                        </div>

                        <div class="form-group">
                            <label for="reg-email">Email</label>
                            <input type="email" id="reg-email" name="email" value="${regEmail}"
                                placeholder="nome@esempio.it" required autocomplete="email">
                            <span class="field-error" id="reg-email-msg"></span>
                        </div>

                        <div class="form-group">
                            <label for="reg-password">Password</label>
                            <input type="password" id="reg-password" name="password" placeholder="Password" required
                                autocomplete="new-password">
                            <span class="field-error" id="reg-password-msg"></span>
                        </div>

                        <div class="form-group">
                            <label for="reg-conferma-password">Conferma Password</label>
                            <input type="password" id="reg-conferma-password" name="confermaPassword"
                                placeholder="Conferma Password" required autocomplete="new-password">
                            <span class="field-error" id="reg-conferma-password-msg"></span>
                        </div>
                        <button type="submit" class="btn btn-primary" id="btn-registrazione">Registrati</button>
                    </form>
                </section>
            </div>
            <nav class="auth-back-link">
                <a href="${pageContext.request.contextPath}/index.jsp">&larr; Torna al catalogo</a>
            </nav>

        </main>

        <script src="${pageContext.request.contextPath}/scripts/main.js"></script>
    </body>

    </html>