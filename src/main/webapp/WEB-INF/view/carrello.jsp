<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.model.CartBean, it.unisa.model.CartBean.CartItem, it.unisa.model.ProductBean" %>
<%@ include file="header.jsp" %>

<%
    CartBean cart = (CartBean) request.getAttribute("cart");
    if (cart == null) {
        cart = (CartBean) session.getAttribute("carrello");
    }
    boolean isEmpty = (cart == null || cart.isEmpty());
%>

<section class="cart-section">
    <h1 class="cart-title">Il tuo Carrello</h1>

    <% if (isEmpty) { %>
        <div class="cart-empty">
            <div class="cart-empty-icon">&#128722;</div>
            <h2>Il carrello è vuoto</h2>
            <p>Non hai ancora aggiunto prodotti al tuo carrello.</p>
            <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-primary">Vai al Catalogo</a>
        </div>
    <% } else { %>
        <div class="cart-layout">
            <div class="cart-items-wrapper">
                <table class="cart-table">
                    <thead>
                        <tr>
                            <th>Prodotto</th>
                            <th>Prezzo</th>
                            <th>Quantit&agrave;</th>
                            <th>Subtotale</th>
                            <th></th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (CartItem item : cart.getItems()) { 
                            ProductBean p = item.getProduct();
                        %>
                            <tr class="cart-row">
                                <td class="cart-product-cell">
                                    <a href="${pageContext.request.contextPath}/catalogo?id=<%= p.getId() %>" class="cart-product-link">
                                        <div class="cart-thumb-wrapper">
                                            <img src="${pageContext.request.contextPath}/images/<%= p.getImmagine() %>" 
                                                 alt="<%= p.getNome() %>"
                                                 onerror="this.src='${pageContext.request.contextPath}/images/placeholder.png'">
                                        </div>
                                        <div class="cart-product-details">
                                            <span class="cart-product-name"><%= p.getNome() %></span>
                                            <span class="cart-product-stock">Disponibili: <%= p.getQuantitaDisponibile() %></span>
                                        </div>
                                    </a>
                                </td>
                                <td class="cart-price-cell">
                                    &euro; <%= String.format("%.2f", p.getPrezzo()) %>
                                </td>
                                <td class="cart-qty-cell">
                                    <form action="${pageContext.request.contextPath}/carrello" method="POST" class="cart-qty-form">
                                        <input type="hidden" name="action" value="modifica">
                                        <input type="hidden" name="idProdotto" value="<%= p.getId() %>">
                                        <input type="number" name="quantita" value="<%= item.getQuantita() %>" min="1" max="<%= p.getQuantitaDisponibile() %>" class="cart-qty-input">
                                        <button type="submit" class="btn-update-qty" title="Aggiorna">&#x21bb;</button>
                                    </form>
                                </td>
                                <td class="cart-subtotal-cell">
                                    <strong>&euro; <%= String.format("%.2f", item.getSubtotale()) %></strong>
                                </td>
                                <td class="cart-remove-cell">
                                    <form action="${pageContext.request.contextPath}/carrello" method="POST" class="cart-remove-form">
                                        <input type="hidden" name="action" value="rimuovi">
                                        <input type="hidden" name="idProdotto" value="<%= p.getId() %>">
                                        <button type="submit" class="btn-remove-item" title="Rimuovi dal carrello">&times;</button>
                                    </form>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>

                <div class="cart-table-footer">
                    <form action="${pageContext.request.contextPath}/carrello" method="POST" onsubmit="return confirm('Sei sicuro di voler svuotare il carrello?');">
                        <input type="hidden" name="action" value="svuota">
                        <button type="submit" class="btn btn-secondary">Svuota Carrello</button>
                    </form>
                    <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-outline">&larr; Continua lo Shopping</a>
                </div>
            </div>

            <aside class="cart-summary">
                <h2>Riepilogo Ordine</h2>
                <div class="summary-line">
                    <span>Articoli totali</span>
                    <span id="summary-count"><%= cart.getNumeroElementi() %></span>
                </div>
                <div class="summary-line summary-total">
                    <span>Totale</span>
                    <span id="summary-total">&euro; <%= String.format("%.2f", cart.getTotale()) %></span>
                </div>
                <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary btn-checkout">Procedi all'Acquisto</a>
            </aside>
        </div>
    <% } %>
</section>

<%@ include file="footer.jsp" %>
