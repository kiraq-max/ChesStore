<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.model.ProductBean, it.unisa.model.CategoriaBean, java.util.List" %>
<%
    List<ProductBean>   prodotti  = (List<ProductBean>)   request.getAttribute("prodotti");
    List<CategoriaBean> categorie = (List<CategoriaBean>) request.getAttribute("categorie");
    ProductBean prodottoModifica  = (ProductBean)         request.getAttribute("prodotto");
    Boolean isEditModeObj         = (Boolean)             request.getAttribute("isEditMode");
    boolean isEdit                = (isEditModeObj != null && isEditModeObj && prodottoModifica != null);
    String  formError             = (String) request.getAttribute("formError");
    String  msg                   = request.getParameter("msg");
%>
<%@ include file="../header.jsp" %>

<section class="admin-section">

    <%-- ===== INTESTAZIONE ===== --%>
    <div class="admin-header">
        <div>
            <h1>Gestione Catalogo Prodotti</h1>
            <p class="admin-subtitle">Pannello di controllo amministratore per la gestione e modifica del catalogo scacchi.</p>
        </div>
        <div class="admin-nav-actions">
            <a href="${pageContext.request.contextPath}/admin/ordini" class="btn btn-outline">Ordini Admin</a>
            <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-outline" target="_blank">Vedi Negozio &rarr;</a>
        </div>
    </div>

    <%-- ===== MESSAGGIO DI CONFERMA ===== --%>
    <% if (msg != null) { %>
        <div class="admin-alert admin-alert-success">
            <% if ("created".equals(msg)) { %>
                &#10004; Prodotto creato con successo e aggiunto al catalogo!
            <% } else if ("updated".equals(msg)) { %>
                &#10004; Prodotto aggiornato con successo!
            <% } else if ("deleted".equals(msg)) { %>
                &#10004; Prodotto eliminato definitivamente dal catalogo!
            <% } %>
        </div>
    <% } %>

    <%-- ===== ERRORE DI VALIDAZIONE SERVER ===== --%>
    <% if (formError != null && !formError.trim().isEmpty()) { %>
        <div class="admin-alert admin-alert-error">
            &#9888; <%= formError %>
        </div>
    <% } %>

    <div class="admin-grid">

        <%-- ===== FORM INSERIMENTO / MODIFICA ===== --%>
        <div class="admin-card form-card">
            <h2><%= isEdit ? "Modifica Prodotto #" + prodottoModifica.getId() : "Aggiungi Nuovo Prodotto" %></h2>

            <form id="form-admin-prodotto"
                  action="${pageContext.request.contextPath}/admin/catalogo"
                  method="post"
                  class="admin-form"
                  novalidate>

                <input type="hidden" name="action" value="<%= isEdit ? "update" : "insert" %>">
                <% if (isEdit) { %>
                    <input type="hidden" name="id" value="<%= prodottoModifica.getId() %>">
                <% } %>

                <div class="form-group">
                    <label for="admin-nome">Nome Prodotto *</label>
                    <input type="text"
                           id="admin-nome"
                           name="nome"
                           class="form-control"
                           required
                           placeholder="es. Scacchiera in Noce e Acero 50cm"
                           value="<%= (isEdit && prodottoModifica.getNome() != null) ? prodottoModifica.getNome() : "" %>">
                    <span class="field-error" id="admin-nome-msg"></span>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="admin-idCategoria">Categoria *</label>
                        <select id="admin-idCategoria" name="idCategoria" class="form-control" required>
                            <option value="">-- Seleziona Categoria --</option>
                            <% if (categorie != null) {
                                   for (CategoriaBean cat : categorie) {
                                       boolean selected = (isEdit && cat.getId() == prodottoModifica.getIdCategoria()); %>
                                <option value="<%= cat.getId() %>" <%= selected ? "selected" : "" %>>
                                    <%= cat.getNome() %>
                                </option>
                            <%     }
                               } %>
                        </select>
                        <span class="field-error" id="admin-categoria-msg"></span>
                    </div>
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="admin-prezzo">Prezzo (&euro;) *</label>
                        <input type="number"
                               id="admin-prezzo"
                               name="prezzo"
                               class="form-control"
                               step="0.01"
                               min="0.01"
                               required
                               placeholder="0.00"
                               value="<%= isEdit ? String.format(java.util.Locale.US, "%.2f", prodottoModifica.getPrezzo()) : "" %>">
                        <span class="field-error" id="admin-prezzo-msg"></span>
                    </div>

                    <div class="form-group">
                        <label for="admin-quantita">Quantit&agrave; Scorta *</label>
                        <input type="number"
                               id="admin-quantita"
                               name="quantitaDisponibile"
                               class="form-control"
                               min="0"
                               step="1"
                               required
                               placeholder="10"
                               value="<%= isEdit ? prodottoModifica.getQuantitaDisponibile() : "10" %>">
                        <span class="field-error" id="admin-quantita-msg"></span>
                    </div>
                </div>

                <div class="form-group">
                    <label for="admin-immagine">Percorso Immagine</label>
                    <input type="text"
                           id="admin-immagine"
                           name="immagine"
                           class="form-control"
                           placeholder="es. images/scacchi-legno.jpg"
                           value="<%= (isEdit && prodottoModifica.getImmagine() != null) ? prodottoModifica.getImmagine() : "images/chess-default.svg" %>">
                    <small class="form-help">Inserisci il percorso relativo in images/ (es. images/scacchiera.jpg).</small>
                </div>

                <div class="form-group">
                    <label for="admin-descrizione">Descrizione Prodotto</label>
                    <textarea id="admin-descrizione"
                              name="descrizione"
                              class="form-control"
                              rows="4"
                              placeholder="Descrivi dettagli, materiali, dimensioni e finiture del prodotto..."><%= (isEdit && prodottoModifica.getDescrizione() != null) ? prodottoModifica.getDescrizione() : "" %></textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary" id="btn-salva-prodotto">
                        <%= isEdit ? "Salva Modifiche" : "Inserisci Prodotto" %>
                    </button>
                    <% if (isEdit) { %>
                        <a href="${pageContext.request.contextPath}/admin/catalogo" class="btn btn-outline">Annulla</a>
                    <% } %>
                </div>
            </form>
        </div>

        <%-- ===== LISTA PRODOTTI ATTUALI ===== --%>
        <div class="admin-card table-card">
            <h2>Prodotti nel Catalogo (<%= (prodotti != null) ? prodotti.size() : 0 %>)</h2>

            <% if (prodotti == null || prodotti.isEmpty()) { %>
                <p class="empty-state">Nessun prodotto presente nel catalogo.</p>
            <% } else { %>
                <div class="table-responsive">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th>ID</th>
                                <th>Immagine</th>
                                <th>Nome</th>
                                <th>Prezzo</th>
                                <th>Scorta</th>
                                <th>Azioni</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% for (ProductBean p : prodotti) { %>
                                <tr class="<%= (isEdit && p.getId() == prodottoModifica.getId()) ? "row-highlight" : "" %>">
                                    <td><strong>#<%= p.getId() %></strong></td>
                                    <td>
                                        <%
                                            String thumbPath = p.getImmagine();
                                            if (thumbPath != null && !thumbPath.startsWith("images/") && !thumbPath.startsWith("http")) {
                                                thumbPath = "images/" + thumbPath;
                                            }
                                        %>
                                        <img src="${pageContext.request.contextPath}/<%= thumbPath %>"
                                             alt="<%= p.getNome() %>"
                                             class="admin-thumb">
                                    </td>
                                    <td>
                                        <div class="product-title-cell">
                                            <strong><%= p.getNome() %></strong>
                                        </div>
                                    </td>
                                    <td>&euro; <%= String.format("%.2f", p.getPrezzo()) %></td>
                                    <td>
                                        <span class="stock-badge <%= p.getQuantitaDisponibile() > 0 ? "stock-in" : "stock-out" %>">
                                            <%= p.getQuantitaDisponibile() %> pz
                                        </span>
                                    </td>
                                    <td class="actions-cell">
                                        <a href="${pageContext.request.contextPath}/admin/catalogo?action=edit&id=<%= p.getId() %>"
                                           class="btn btn-sm btn-outline"
                                           id="btn-modifica-<%= p.getId() %>"
                                           title="Modifica">
                                            Modifica
                                        </a>
                                        <form action="${pageContext.request.contextPath}/admin/catalogo"
                                              method="post"
                                              class="form-inline-delete form-admin-delete">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id"     value="<%= p.getId() %>">
                                            <button type="submit"
                                                    class="btn btn-sm btn-danger"
                                                    id="btn-elimina-<%= p.getId() %>"
                                                    title="Elimina">
                                                Elimina
                                            </button>
                                        </form>
                                    </td>
                                </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
            <% } %>
        </div>

    </div><%-- fine admin-grid --%>

</section>

<%@ include file="../footer.jsp" %>
