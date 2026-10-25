<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="it.unisa.model.OrdineBean, it.unisa.model.ComposizioneOrdineBean, java.util.List" %>
        <%@ include file="header.jsp" %>

            <% List<OrdineBean> ordini = (List<OrdineBean>) request.getAttribute("ordini");
                    OrdineBean ordineDettaglio = (OrdineBean) request.getAttribute("ordine");
                    List<ComposizioneOrdineBean> righeDettaglio = (List<ComposizioneOrdineBean>)
                            request.getAttribute("righeOrdine");

                            boolean isDettaglio = (ordineDettaglio != null);
                            %>

                            <section class="orders-section">

                                <div class="orders-header">
                                    <h1>
                                        <% if (isDettaglio) { %>
                                            Dettaglio Ordine #<%= ordineDettaglio.getId() %>
                                                <% } else { %>
                                                    I miei Ordini
                                                    <% } %>
                                    </h1>
                                    <% if (isDettaglio) { %>
                                        <a href="${pageContext.request.contextPath}/ordini"
                                            class="btn btn-outline">&larr; Torna ai miei ordini</a>
                                        <% } %>
                                </div>

                                <% if (!isDettaglio) { %>

                                    <% if (ordini==null || ordini.isEmpty()) { %>
                                        <div class="orders-empty">
                                            <div class="orders-empty-icon">&#128230;</div>
                                            <h2>Nessun ordine effettuato</h2>
                                            <p>Non hai ancora effettuato nessun ordine su ChesStore.</p>
                                            <a href="${pageContext.request.contextPath}/catalogo"
                                                class="btn btn-primary">Vai al Catalogo</a>
                                        </div>
                                        <% } else { %>
                                            <table class="orders-table">
                                                <thead>
                                                    <tr>
                                                        <th>#</th>
                                                        <th>Data</th>
                                                        <th>Indirizzo Spedizione</th>
                                                        <th>Pagamento</th>
                                                        <th>Totale</th>
                                                        <th></th>
                                                    </tr>
                                                </thead>
                                                <tbody>
                                                    <% for (OrdineBean o : ordini) { %>
                                                        <tr class="orders-row">
                                                            <td><strong>#<%= o.getId() %></strong></td>
                                                            <td>
                                                                <%= o.getData() %>
                                                            </td>
                                                            <td>
                                                                <%= o.getIndirizzoSpedizione() %>
                                                            </td>
                                                            <td>
                                                                <%= o.getMetodoPagamento() %>
                                                            </td>
                                                            <td><strong>&euro; <%= String.format("%.2f", o.getTotale())
                                                                        %></strong></td>
                                                            <td>
                                                                <a href="${pageContext.request.contextPath}/ordini?id=<%= o.getId() %>"
                                                                    class="btn btn-outline btn-sm">
                                                                    Dettaglio
                                                                </a>
                                                            </td>
                                                        </tr>
                                                        <% } %>
                                                </tbody>
                                            </table>
                                            <% } %>

                                                <% } else { %>

                                                    <div class="order-detail-card">
                                                        <div class="order-detail-meta">
                                                            <div class="order-detail-row">
                                                                <span>Numero ordine</span>
                                                                <strong>#<%= ordineDettaglio.getId() %></strong>
                                                            </div>
                                                            <div class="order-detail-row">
                                                                <span>Data</span>
                                                                <strong>
                                                                    <%= ordineDettaglio.getData() %>
                                                                </strong>
                                                            </div>
                                                            <div class="order-detail-row">
                                                                <span>Indirizzo spedizione</span>
                                                                <strong>
                                                                    <%= ordineDettaglio.getIndirizzoSpedizione() %>
                                                                </strong>
                                                            </div>
                                                            <div class="order-detail-row">
                                                                <span>Metodo di pagamento</span>
                                                                <strong>
                                                                    <%= ordineDettaglio.getMetodoPagamento() %>
                                                                </strong>
                                                            </div>
                                                        </div>

                                                        <table class="confirm-table">
                                                            <thead>
                                                                <tr>
                                                                    <th>Prodotto</th>
                                                                    <th>Prezzo unitario</th>
                                                                    <th>Quantit&agrave;</th>
                                                                    <th>Subtotale</th>
                                                                </tr>
                                                            </thead>
                                                            <tbody>
                                                                <% if (righeDettaglio !=null) { for
                                                                    (ComposizioneOrdineBean riga : righeDettaglio) { %>
                                                                    <tr>
                                                                        <td>
                                                                            <%= riga.getNomeProdottoStorico() %>
                                                                        </td>
                                                                        <td>&euro; <%= String.format("%.2f",
                                                                                riga.getPrezzoUnitarioStorico()) %>
                                                                        </td>
                                                                        <td>
                                                                            <%= riga.getQuantita() %>
                                                                        </td>
                                                                        <td><strong>&euro; <%= String.format("%.2f",
                                                                                    riga.getPrezzoUnitarioStorico() *
                                                                                    riga.getQuantita()) %></strong></td>
                                                                    </tr>
                                                                    <% } } %>
                                                            </tbody>
                                                            <tfoot>
                                                                <tr>
                                                                    <td colspan="3"><strong>Totale Ordine</strong></td>
                                                                    <td><strong>&euro; <%= String.format("%.2f",
                                                                                ordineDettaglio.getTotale()) %></strong>
                                                                    </td>
                                                                </tr>
                                                            </tfoot>
                                                        </table>

                                                        <div class="order-detail-actions">
                                                            <a href="${pageContext.request.contextPath}/ordini"
                                                                class="btn btn-outline">&larr; I miei Ordini</a>
                                                            <a href="${pageContext.request.contextPath}/catalogo"
                                                                class="btn btn-primary">Continua lo Shopping</a>
                                                        </div>
                                                    </div>

                                                    <% } %>

                            </section>

                            <%@ include file="footer.jsp" %>