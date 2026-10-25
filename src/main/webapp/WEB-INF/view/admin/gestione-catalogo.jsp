<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="it.unisa.model.ProductBean, it.unisa.model.CategoriaBean, java.util.List" %>
<%
    List<ProductBean> prodotti = (List<ProductBean>) request.getAttribute("prodotti");
    List<CategoriaBean> categorie = (List<CategoriaBean>) request.getAttribute("categorie");
    ProductBean prodottoModifica = (ProductBean) request.getAttribute("prodotto");
    Boolean isEditModeObj = (Boolean) request.getAttribute("isEditMode");
    boolean isEdit = (isEditModeObj != null && isEditModeObj && prodottoModifica != null);
    String formError = (String) request.getAttribute("formError");
    String msg = request.getParameter("msg");
%>
<%@ include file="../header.jsp" %>

<section class="admin-section">
    <div class="admin-header">
        <div>
            <h1>Gestione Catalogo Prodotti</h1>
            <p class="admin-subtitle">Pannello di controllo amministratore per la gestione e modifica del catalogo scacchi.</p>
        </div>
        <div class="admin-nav-actions">
            <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-outline" target="_blank">Vedi Negozio &rarr;</a>
        </div>
    </div>

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

    <% if (formError != null && !formError.trim().isEmpty()) { %>
        <div class="admin-alert admin-alert-error">
            &#9888; <%= formError %>
        </div>
    <% } %>

    <div class="admin-grid">
        <!-- FORM INSERIMENTO / MODIFICA -->
        <div class="admin-card form-card">
            <h2><%= isEdit ? "Modifica Prodotto #" + prodottoModifica.getId() : "Aggiungi Nuovo Prodotto" %></h2>
            
            <form action="${pageContext.request.contextPath}/admin/catalogo" method="post" class="admin-form">
                <input type="hidden" name="action" value="<%= isEdit ? "update" : "insert" %>">
                <% if (isEdit) { %>
                    <input type="hidden" name="id" value="<%= prodottoModifica.getId() %>">
                <% } %>

                <div class="form-group">
                    <label for="nome">Nome Prodotto *</label>
                    <input type="text" id="nome" name="nome" required
                           value="<%= (isEdit && prodottoModifica.getNome() != null) ? prodottoModifica.getNome() : "" %>"
                           placeholder="es. Scacchiera in Noce e Acero 50cm">
                </div>

                <div class="form-row">
                    <div class="form-group">
                        <label for="idCategoria">Categoria *</label>
                        <select id="idCategoria" name="idCategoria" required>
                            <option value="">-- Seleziona Categoria --</option>
                            <% if (categorie != null) { 
                                for (CategoriaBean cat : categorie) { 
                                    boolean selected = (isEdit && cat.getId() == prodottoModifica.getIdCategoria());
                            %>
                                <option value="<%= cat.getId() %>" <%= selected ? "selected" : "" %>>
                                    <%= cat.getNome() %>
                                </option>
                            <% } } %>
                        </select>
                    </div>

                    <div class="form-group">
                        <label for="prezzo">Prezzo (&euro;) *</label>
                        <input type="number" id="prezzo" name="prezzo" step="0.01" min="0.01" required
                               value="<%= isEdit ? String.format(java.util.Locale.US, "%.2f", prodottoModifica.getPrezzo()) : "" %>"
                               placeholder="0.00">
                    </div>

                    <div class="form-group">
                        <label for="quantitaDisponibile">Quantità Scorta *</label>
                        <input type="number" id="quantitaDisponibile" name="quantitaDisponibile" min="0" step="1" required
                               value="<%= isEdit ? prodottoModifica.getQuantitaDisponibile() : "10" %>"
                               placeholder="10">
                    </div>
                </div>

                <div class="form-group">
                    <label for="immagine">Percorso Immagine</label>
                    <input type="text" id="immagine" name="immagine"
                           value="<%= (isEdit && prodottoModifica.getImmagine() != null) ? prodottoModifica.getImmagine() : "images/chess-default.png" %>"
                           placeholder="es. images/scacchi-legno.jpg">
                    <small class="form-help">Inserisci il percorso relativo in images/ (es. images/scacchiera.jpg).</small>
                </div>

                <div class="form-group">
                    <label for="descrizione">Descrizione Prodotto</label>
                    <textarea id="descrizione" name="descrizione" rows="4"
                              placeholder="Descrivi dettagli, materiali, dimensioni e finiture del prodotto..."><%= (isEdit && prodottoModifica.getDescrizione() != null) ? prodottoModifica.getDescrizione() : "" %></textarea>
                </div>

                <div class="form-actions">
                    <button type="submit" class="btn btn-primary">
                        <%= isEdit ? "Salva Modifiche" : "Inserisci Prodotto" %>
                    </button>
                    <% if (isEdit) { %>
                        <a href="${pageContext.request.contextPath}/admin/catalogo" class="btn btn-outline">Annulla</a>
                    <% } %>
                </div>
            </form>
        </div>

        <!-- LISTA PRODOTTI ATTUALI -->
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
                                        <img src="${pageContext.request.contextPath}/<%= p.getImmagine() %>"
                                             alt="<%= p.getNome() %>"
                                             class="admin-thumb"
                                             onerror="this.src='${pageContext.request.contextPath}/images/chess-default.png'">
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
                                           class="btn btn-sm btn-outline" title="Modifica">
                                            Modifica
                                        </a>
                                        <form action="${pageContext.request.contextPath}/admin/catalogo" method="post"
                                              style="display:inline;"
                                              onsubmit="return confirm('Sei sicuro di voler eliminare \'<%= p.getNome().replace("'", "\\'") %>\' dal catalogo?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="<%= p.getId() %>">
                                            <button type="submit" class="btn btn-sm btn-danger" title="Elimina">
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
    </div>
</section>

<%@ include file="../footer.jsp" %>
