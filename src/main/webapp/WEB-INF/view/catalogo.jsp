<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ page import="it.unisa.model.ProductBean, it.unisa.model.CategoriaBean" %>
        <%@ page import="java.util.List" %>
            <% Integer categoriaSelezionata=(Integer) request.getAttribute("categoriaSelezionata"); List<CategoriaBean>
                categorie = (List<CategoriaBean>) request.getAttribute("categorie");
                    List<ProductBean> prodotti = (List<ProductBean>) request.getAttribute("prodotti");
                            %>
                            <%@ include file="header.jsp" %>

                                <section class="catalog-section">
                                    <aside class="category-filter">
                                        <h2>Categorie</h2>
                                        <ul>
                                            <li>
                                                <a href="${pageContext.request.contextPath}/catalogo"
                                                    class="<%= (categoriaSelezionata == null) ? " active" : "" %>">
                                                    Tutti i Prodotti
                                                </a>
                                            </li>
                                            <% if (categorie !=null) { for (CategoriaBean cat : categorie) { String
                                                cssClass=(categoriaSelezionata !=null &&
                                                categoriaSelezionata.equals(cat.getId())) ? "active" : "" ; %>
                                                <li>
                                                    <a href="${pageContext.request.contextPath}/catalogo?categoria=<%= cat.getId() %>"
                                                        class="<%= cssClass %>">
                                                        <%= cat.getNome() %>
                                                    </a>
                                                </li>
                                                <% } } %>
                                        </ul>
                                    </aside>
                                    <div class="product-grid">
                                        <% if (prodotti==null || prodotti.isEmpty()) { %>
                                            <p class="no-products-msg">Nessun prodotto disponibile per questa categoria
                                            </p>
                                            <% } else { for (ProductBean p : prodotti) { %>
                                                <article class="product-card">
                                                    <a href="${pageContext.request.contextPath}/catalogo?id=<%= p.getId() %>"
                                                        class="product-card-link">
                                                        <div class="product-image-wrapper">
                                                            <img src="${pageContext.request.contextPath}/images/<%= p.getImmagine() %>"
                                                                alt="<%= p.getNome() %>">
                                                        </div>
                                                        <div class="product-info">
                                                            <h3 class="product-name">
                                                                <%= p.getNome() %>
                                                            </h3>
                                                            <p class="product-price">&euro; <%= String.format("%.2f",
                                                                    p.getPrezzo()) %>
                                                            </p>
                                                            <% if (p.getQuantitaDisponibile()> 0) { %>
                                                                <span class="in-stock">Disponibile</span>
                                                                <% } else { %>
                                                                    <span class="out-of-stock">Esaurito</span>
                                                                    <% } %>
                                                        </div>
                                                    </a>
                                                    <% if (p.getQuantitaDisponibile()> 0) { %>
                                                        <button class="btn btn-primary btn-add-cart"
                                                            id="btn-add-<%= p.getId() %>"
                                                            data-product-id="<%= p.getId() %>">
                                                            Aggiungi al Carrello
                                                        </button>
                                                        <% } %>
                                                </article>
                                                <% } } %>
                                    </div>
                                </section>
                                <%@ include file="footer.jsp" %>