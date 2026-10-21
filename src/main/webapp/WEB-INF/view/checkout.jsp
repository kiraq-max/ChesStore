<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="it.unisa.model.CartBean, it.unisa.model.CartBean.CartItem" %>
        <%@ include file="header.jsp" %>

            <% CartBean cart=(CartBean) request.getAttribute("cart"); String errorMessage=(String)
                request.getAttribute("errorMessage"); %>

                <section class="checkout-section">
                    <h1 class="section-title"> Checkout </h1>
                    <% if(errorMessage !=null) { %>
                        <div class="alert alert-danger">
                            <%= errorMessage%>
                        </div>
                        <% } %>

                            <div class="checkout-layout">

                                <div class="checkout-form-wrapper">
                                    <form id="checkout-form" action="${pageContext.request.contextPath}/checkout"
                                        method="POST" novalidate>
                                        <fieldset class="form-group">
                                            <legend>Indirizzo di Spedizione</legend>
                                            <label for="via">Via/Indirizzo</label>
                                            <input type="text" id="via" name="via" placeholder="Es. Via Roma, 1"
                                                required>
                                            <span class="field-error" id="via-error"></span>

                                            <label for="citta">Città</label>
                                            <input type="text" id="citta" name="citta" placeholder="Inserisci la città"
                                                required>
                                            <span class="field-error" id="citta-error"></span>

                                            <label for="cap">CAP</label>
                                            <input type="text" id="cap" name="cap" placeholder="Inserisci il CAP"
                                                maxlength="5" required>
                                            <span class="field-error" id="cap-error"></span>
                                        </fieldset>

                                        <fieldset class="form-group">
                                            <legend>Metodo di Pagamento</legend>
                                            <label class="radio-label">
                                                <input type="radio" name="metodoPagamento" value="cartaDiCredito">
                                                Carte di Credito/Debito
                                            </label>
                                            <label class="radio-label">
                                                <input type="radio" name="metodoPagamento" value="PayPal">
                                                PayPal
                                            </label>

                                            <label class="radio-label">
                                                <input type="radio" name="metodoPagamento" value="bonificoBancario">
                                                Bonifico Bancario
                                            </label>
                                            <span class="field-error" id="metodoPagamento-error"></span>
                                        </fieldset>

                                        <button type="submit" class="btn btn-primary btn-checkout">Conferma
                                            Ordine</button>
                                    </form>
                                </div>

                                <aside class="checkout-summary">
                                    <h2> Riepilogo Ordine </h2>

                                    <% if (cart==null || cart.isEmpty()) { %>
                                        <p class="empty-cart-msg">Il carrello è vuoto</p>
                                        <% } else { %>
                                            <div class="cart-items-list">
                                                <% for(CartItem item : cart.getItems()) { %>
                                                    <div class="cart-summary-item">
                                                        <div class="summary-item-info">
                                                            <span class="summary-item-name">
                                                                <%= item.getProduct().getNome() %>
                                                            </span>
                                                            <span class="summary-item-price">&euro; <%=
                                                                    String.format("%.2f",item.getProduct().getPrezzo())
                                                                    %></span>
                                                        </div>
                                                        <span class="summary-item-quantity">x <%= item.getQuantita() %>
                                                        </span>
                                                        <span class="summary-item-subtotal">&euro; <%=
                                                                String.format("%.2f",item.getSubtotale()) %></span>
                                                    </div>
                                                    <% } %>
                                            </div>

                                            <div class="summary-totals">
                                                <div class="summary-total-row">
                                                    <span>Subtotale:</span>
                                                    <span>&euro; <%= String.format("%.2f",cart.getTotale()) %></span>
                                                </div>
                                                <div class="summary-total-row">
                                                    <span>Spedizione:</span>
                                                    <span>&euro; 5.00</span>
                                                </div>
                                                <div class="summary-total-row summary-grand-total">
                                                    <span>Totale:</span>
                                                    <span>&euro; <%= String.format("%.2f",cart.getTotale() + 5.0) %>
                                                    </span>
                                                </div>
                                            </div>
                                            <% } %>
                                </aside>

                            </div>

                </section>

                <%@ include file="footer.jsp" %>