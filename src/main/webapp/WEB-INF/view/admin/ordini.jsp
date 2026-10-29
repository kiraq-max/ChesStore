<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.model.OrdineBean, it.unisa.model.ComposizioneOrdineBean, java.util.List" %>
<%
    List<OrdineBean> ordini         = (List<OrdineBean>) request.getAttribute("ordini");
    OrdineBean ordineDettaglio       = (OrdineBean) request.getAttribute("ordine");
    List<ComposizioneOrdineBean> righe = (List<ComposizioneOrdineBean>) request.getAttribute("righeOrdine");

    boolean isDettaglio = (ordineDettaglio != null);

    String filterEmail  = (String) request.getAttribute("filterEmail");
    String filterDaData = (String) request.getAttribute("filterDaData");
    String filterAData  = (String) request.getAttribute("filterAData");
    String filterError  = (String) request.getAttribute("filterError");
    if (filterEmail  == null) filterEmail  = "";
    if (filterDaData == null) filterDaData = "";
    if (filterAData  == null) filterAData  = "";
%>
<%@ include file="../header.jsp" %>

<section class="admin-section">

    <%-- ===== INTESTAZIONE PAGINA ===== --%>
    <div class="admin-header">
        <div>
            <h1>
                <% if (isDettaglio) { %>
                    &#9776; Dettaglio Ordine #<%= ordineDettaglio.getId() %>
                <% } else { %>
                    &#9776; Gestione Ordini
                <% } %>
            </h1>
            <p class="admin-subtitle">
                <% if (isDettaglio) { %>
                    Composizione dell&apos;ordine con prezzi storici al momento dell&apos;acquisto.
                <% } else { %>
                    Visualizza e filtra tutti gli ordini del sistema.
                <% } %>
            </p>
        </div>
        <div class="admin-nav-actions">
            <% if (isDettaglio) { %>
                <a href="${pageContext.request.contextPath}/admin/ordini" class="btn btn-outline">&larr; Tutti gli Ordini</a>
            <% } %>
            <a href="${pageContext.request.contextPath}/admin/catalogo" class="btn btn-outline">Gestione Catalogo</a>
        </div>
    </div>

    <%-- ===== ERRORE FILTRO DATA ===== --%>
    <% if (filterError != null && !filterError.trim().isEmpty()) { %>
        <div class="admin-alert admin-alert-error">&#9888; <%= filterError %></div>
    <% } %>

    <% if (!isDettaglio) { %>

        <%-- ===== FORM FILTRI ===== --%>
        <div class="admin-card filter-card">
            <h2>&#128270; Filtra Ordini</h2>
            <form id="form-filtro-ordini" action="${pageContext.request.contextPath}/admin/ordini" method="post" class="filter-form" novalidate>
                <div class="filter-row">
                    <div class="form-group">
                        <label for="filter-email">Email Cliente</label>
                        <input type="email"
                               id="filter-email"
                               name="clienteEmail"
                               class="form-control"
                               placeholder="es. mario@email.it"
                               value="<%= filterEmail %>">
                        <span class="field-error" id="filter-email-msg"></span>
                    </div>
                    <div class="form-group">
                        <label for="filter-da-data">Da Data</label>
                        <input type="date"
                               id="filter-da-data"
                               name="daData"
                               class="form-control"
                               value="<%= filterDaData %>">
                        <span class="field-error" id="filter-da-data-msg"></span>
                    </div>
                    <div class="form-group">
                        <label for="filter-a-data">A Data</label>
                        <input type="date"
                               id="filter-a-data"
                               name="aData"
                               class="form-control"
                               value="<%= filterAData %>">
                        <span class="field-error" id="filter-a-data-msg"></span>
                    </div>
                    <div class="form-group filter-actions">
                        <button type="submit" class="btn btn-primary" id="btn-apply-filters">&#128269; Filtra</button>
                        <a href="${pageContext.request.contextPath}/admin/ordini" class="btn btn-outline" id="btn-reset-filters">&#10005; Reset</a>
                    </div>
                </div>
            </form>
        </div>

        <%-- ===== TABELLA ORDINI ===== --%>
        <div class="admin-card">
            <% if (ordini == null || ordini.isEmpty()) { %>
                <div class="orders-empty">
                    <div class="orders-empty-icon">&#128230;</div>
                    <h2>Nessun ordine trovato</h2>
                    <p>Nessun ordine corrisponde ai filtri selezionati.</p>
                    <a href="${pageContext.request.contextPath}/admin/ordini" class="btn btn-outline">Mostra tutti</a>
                </div>
            <% } else { %>
                <div class="admin-table-header">
                    <span class="admin-count">Tot: <strong><%= ordini.size() %></strong> ordini</span>
                </div>
                <table class="orders-table admin-orders-table">
                    <thead>
                        <tr>
                            <th>#ID</th>
                            <th>Data</th>
                            <th>Cliente</th>
                            <th>Indirizzo Spedizione</th>
                            <th>Pagamento</th>
                            <th>Totale</th>
                            <th>Azioni</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% for (OrdineBean o : ordini) { %>
                            <tr class="orders-row">
                                <td><strong>#<%= o.getId() %></strong></td>
                                <td class="date-cell"><%= o.getData() %></td>
                                <td class="email-cell"><%= o.getUtenteEmail() %></td>
                                <td><%= o.getIndirizzoSpedizione() %></td>
                                <td><%= o.getMetodoPagamento() %></td>
                                <td><strong>&euro; <%= String.format("%.2f", o.getTotale()) %></strong></td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/admin/ordini?id=<%= o.getId() %>"
                                       class="btn btn-outline btn-sm"
                                       id="btn-dettaglio-<%= o.getId() %>">
                                        Dettaglio
                                    </a>
                                </td>
                            </tr>
                        <% } %>
                    </tbody>
                </table>
            <% } %>
        </div>

    <% } else { %>

        <%-- ===== DETTAGLIO SINGOLO ORDINE ===== --%>
        <div class="order-detail-card">
            <div class="order-detail-meta">
                <div class="order-detail-row">
                    <span>Numero ordine</span>
                    <strong>#<%= ordineDettaglio.getId() %></strong>
                </div>
                <div class="order-detail-row">
                    <span>Data</span>
                    <strong><%= ordineDettaglio.getData() %></strong>
                </div>
                <div class="order-detail-row">
                    <span>Cliente</span>
                    <strong><%= ordineDettaglio.getUtenteEmail() %></strong>
                </div>
                <div class="order-detail-row">
                    <span>Indirizzo spedizione</span>
                    <strong><%= ordineDettaglio.getIndirizzoSpedizione() %></strong>
                </div>
                <div class="order-detail-row">
                    <span>Metodo di pagamento</span>
                    <strong><%= ordineDettaglio.getMetodoPagamento() %></strong>
                </div>
                <div class="order-detail-row">
                    <span>Totale ordine</span>
                    <strong class="order-total-big">&euro; <%= String.format("%.2f", ordineDettaglio.getTotale()) %></strong>
                </div>
            </div>

            <h2 class="order-items-heading">Prodotti acquistati</h2>
            <table class="orders-table confirm-table">
                <thead>
                    <tr>
                        <th>Prodotto</th>
                        <th>Prezzo unitario (storico)</th>
                        <th>Quantit&agrave;</th>
                        <th>Subtotale</th>
                    </tr>
                </thead>
                <tbody>
                    <% if (righe != null) {
                           for (ComposizioneOrdineBean riga : righe) { %>
                        <tr>
                            <td><%= riga.getNomeProdottoStorico() %></td>
                            <td>&euro; <%= String.format("%.2f", riga.getPrezzoUnitarioStorico()) %></td>
                            <td><%= riga.getQuantita() %></td>
                            <td><strong>&euro; <%= String.format("%.2f", riga.getPrezzoUnitarioStorico() * riga.getQuantita()) %></strong></td>
                        </tr>
                    <% } } %>
                </tbody>
                <tfoot>
                    <tr>
                        <td colspan="3"><strong>Totale Ordine</strong></td>
                        <td><strong>&euro; <%= String.format("%.2f", ordineDettaglio.getTotale()) %></strong></td>
                    </tr>
                </tfoot>
            </table>

            <div class="order-detail-actions">
                <a href="${pageContext.request.contextPath}/admin/ordini" class="btn btn-outline">&larr; Tutti gli Ordini</a>
                <a href="${pageContext.request.contextPath}/admin/catalogo" class="btn btn-primary">Gestione Catalogo</a>
            </div>
        </div>

    <% } %>

</section>

<%@ include file="../footer.jsp" %>
