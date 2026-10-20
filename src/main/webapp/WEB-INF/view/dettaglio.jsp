<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.model.ProductBean, it.unisa.model.CategoriaBean" %>
<%@ page import="java.util.List" %>

<%
    ProductBean p = (ProductBean) request.getAttribute("prodotto");
    if (p == null) {
        response.sendRedirect(request.getContextPath() + "/catalogo");
        return;
    }

    List<CategoriaBean> categorie = (List<CategoriaBean>) request.getAttribute("categorie");
    String nomeCategoria = "Scacchi";
    if (categorie != null) {
        for (CategoriaBean c : categorie) {
            if (c.getId() == p.getIdCategoria()) {
                nomeCategoria = c.getNome();
                break;
            }
        }
    }
%>

<%@ include file="header.jsp" %>

<div class="product-detail-page">
    <nav class="breadcrumb" aria-label="Percorso di navigazione">
        <a href="${pageContext.request.contextPath}/catalogo">&larr; Torna al Catalogo</a>
        <span class="breadcrumb-separator">/</span>
        <a href="${pageContext.request.contextPath}/catalogo?categoria=<%= p.getIdCategoria() %>"><%= nomeCategoria %></a>
        <span class="breadcrumb-separator">/</span>
        <span class="breadcrumb-current"><%= p.getNome() %></span>
    </nav>

    <section class="product-detail-container">
        <div class="product-detail-image-wrapper">
            <img class="product-detail-image"
                 src="${pageContext.request.contextPath}/images/<%= p.getImmagine() %>"
                 alt="<%= p.getNome() %>"
                 onerror="this.src='${pageContext.request.contextPath}/images/placeholder.svg'">
        </div>

        <div class="product-detail-info">
            <div class="product-detail-header">
                <span class="product-detail-category"><%= nomeCategoria %></span>
                <h1 class="product-detail-title"><%= p.getNome() %></h1>
                <p class="product-detail-price">&euro; <%= String.format("%.2f", p.getPrezzo()) %></p>
            </div>

            <div class="product-detail-stock">
                <% if (p.getQuantitaDisponibile() > 0) { %>
                    <span class="in-stock">&#10003; Disponibile (<%= p.getQuantitaDisponibile() %> pezzi a magazzino)</span>
                <% } else { %>
                    <span class="out-of-stock">&#10007; Momentaneamente Esaurito</span>
                <% } %>
            </div>

            <div class="product-detail-desc">
                <h3>Descrizione del prodotto</h3>
                <p><%= (p.getDescrizione() != null && !p.getDescrizione().trim().isEmpty()) 
                        ? p.getDescrizione() 
                        : "Nessuna descrizione disponibile per questo articolo." %></p>
            </div>

            <div class="product-detail-actions">
                <% if (p.getQuantitaDisponibile() > 0) { %>
                    <form action="${pageContext.request.contextPath}/carrello" method="POST" class="add-to-cart-form">
                        <input type="hidden" name="action" value="aggiungi">
                        <input type="hidden" name="idProdotto" value="<%= p.getId() %>">

                        <div class="quantity-group">
                            <label for="quantita-input">Quantit&agrave;:</label>
                            <input type="number" 
                                   id="quantita-input" 
                                   name="quantita" 
                                   value="1" 
                                   min="1" 
                                   max="<%= p.getQuantitaDisponibile() %>" 
                                   class="product-quantity-selector" 
                                   required>
                        </div>

                        <button type="submit" 
                                class="btn btn-primary btn-add-cart" 
                                id="btn-add-<%= p.getId() %>"
                                data-product-id="<%= p.getId() %>">
                            &#128722; Aggiungi al Carrello
                        </button>

                        <div class="cart-feedback-msg" id="cart-feedback-<%= p.getId() %>"></div>
                    </form>
                <% } else { %>
                    <div class="unavailable-notice">
                        <p>Questo articolo non &egrave; attualmente acquistabile.</p>
                    </div>
                <% } %>
            </div>
        </div>
    </section>
</div>

<%@ include file="footer.jsp" %>
