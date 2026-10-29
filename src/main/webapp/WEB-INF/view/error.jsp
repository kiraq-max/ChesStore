<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
    <!DOCTYPE html>
    <html lang="it">

    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Errore - ChesStore</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    </head>

    <body>
        <main class="error-container">
            <div class="error-card">
                <div class="error-icon" aria-hidden="true">&#9823;</div>
                <h1>OPSSS! Qualcosa è andato storto</h1>
                <%Integer statusCode=(Integer)request.getAttribute("jakarta.servlet.error.status_code");%>
                    <%String message=(String)request.getAttribute("jakarta.servlet.error.message");
                        if(statusCode!=null){ %>
                        <p class="error-status-code">Codice errore:<strong>
                                <%= statusCode %>
                            </strong></p>
                        <% } %>
                            <p class="error-description">
                                ${not empty errorMessage ? errorMessage : "La risorsa richiesta non è disponibile o si è
                                verificato un errore imprevisto."}
                            </p>

                            <div class="error-actions">
                                <a href="${pageContext.request.contextPath}/index.jsp" class="btn btn-primary">Torna
                                    alla home</a>
                                <button type="button" class="btn btn-secondary" id="btn-error-back">Torna
                                    alla pagina precedente</button>
                            </div>
            </div>

        </main>
        <script src="${pageContext.request.contextPath}/scripts/main.js"></script>
    </body>

    </html>