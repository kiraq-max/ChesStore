<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="it.unisa.model.OrdineBean, it.unisa.model.ComposizioneOrdineBean, java.util.List" %>
        <%@ include file="header.jsp" %>

            <% OrdineBean ordine=(OrdineBean) request.getAttribute("ordine"); List<ComposizioneOrdineBean> righe = (List
                <ComposizioneOrdineBean>) request.getAttribute("righeOrdine");%>

                    <section class="confirm-section">
                        <div class="confirm-card">
                            <div class="confirm-icon">&#10003;</div>
                            <h1>Ordine Confermato!</h1>
                            <p class="confirm-sub">Grazie per il tuo acquisto su ChesStore.</p>

                            <% if (ordine !=null) { %>
                                <div class="confirm-details">
                                    <div class="confirm-row">
                                        <span>Numero ordine:</span>
                                        <strong>#<%= ordine.getId() %></strong>
                                    </div>
                                    <div class="confirm-row">
                                        <span>Data:</span>
                                        <strong>
                                            <%= ordine.getData() %>
                                        </strong>
                                    </div>
                                    <div class="confirm-row">
                                        <span>Indirizzo spedizione:</span>
                                        <strong>
                                            <%= ordine.getIndirizzoSpedizione() %>
                                        </strong>
                                    </div>
                                    <div class="confirm-row">
                                        <span>Pagamento:</span>
                                        <strong>
                                            <%= ordine.getMetodoPagamento() %>
                                        </strong>
                                    </div>
                                </div>

                                <table class="confirm-table">
                                    <thead>
                                        <tr>
                                            <th>Prodotto</th>
                                            <th>Quantità</th>
                                            <th>Prezzo</th>
                                            <th>Subtotale</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <% if (righe !=null) { for (ComposizioneOrdineBean riga : righe) { %>
                                            <tr>
                                                <td>
                                                    <%= riga.getNomeProdottoStorico() %>
                                                </td>
                                                <td>
                                                    <%= riga.getQuantita() %>
                                                </td>
                                                <td>&euro; <%= String.format("%.2f", riga.getPrezzoUnitarioStorico()) %>
                                                </td>
                                                <td>&euro; <%= String.format("%.2f", riga.getPrezzoUnitarioStorico() *
                                                        riga.getQuantita()) %>
                                                </td>
                                            </tr>
                                            <% } } %>
                                    </tbody>
                                    <tfoot>
                                        <tr>
                                            <td colspan="3"><strong>Totale Ordine</strong></td>
                                            <td><strong>&euro; <%= String.format("%.2f", ordine.getTotale()) %></strong>
                                            </td>
                                        </tr>
                                    </tfoot>
                                </table>

                                <div class="confirm-actions">
                                    <a href="${pageContext.request.contextPath}/ordini" class="btn btn-outline">I
                                        miei Ordini</a>
                                    <a href="${pageContext.request.contextPath}/catalogo"
                                        class="btn btn-primary">Continua lo Shopping</a>
                                </div>
                                <% } %>

                        </div>
                    </section>

                    <%@ include file="footer.jsp" %>