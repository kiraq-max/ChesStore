package it.unisa.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Gestisce il carrello della spesa in memoria durante la sessione utente.
 */
public class CartBean implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Rappresenta un singolo elemento all'interno del carrello (prodotto + quantità).
     */
    public static class CartItem implements Serializable {
        private static final long serialVersionUID = 1L;

        private ProductBean product;
        private int quantita;

        public CartItem() {
        }

        public CartItem(ProductBean product, int quantita) {
            this.product = product;
            this.quantita = quantita;
        }

        public ProductBean getProduct() {
            return product;
        }

        public void setProduct(ProductBean product) {
            this.product = product;
        }

        public int getQuantita() {
            return quantita;
        }

        public void setQuantita(int quantita) {
            this.quantita = quantita;
        }

        public double getSubtotale() {
            return (product != null) ? product.getPrezzo() * quantita : 0.0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            CartItem cartItem = (CartItem) o;
            return Objects.equals(product, cartItem.product);
        }

        @Override
        public int hashCode() {
            return Objects.hash(product);
        }
    }

    // Mappa con chiave l'ID del prodotto e valore l'elemento CartItem
    // LinkedHashMap garantisce l'ordine di inserimento dei prodotti a schermo
    private final Map<Integer, CartItem> items;

    public CartBean() {
        this.items = new LinkedHashMap<>();
    }

    /**
     * Aggiunge 1 unità di un prodotto al carrello (o ne incrementa la quantità se già presente).
     */
    public void addProduct(ProductBean product) {
        addProduct(product, 1);
    }

    /**
     * Aggiunge una quantità specificata di un prodotto al carrello.
     */
    public void addProduct(ProductBean product, int quantita) {
        if (product == null || quantita <= 0) {
            return;
        }

        int productId = product.getId();
        if (items.containsKey(productId)) {
            CartItem existingItem = items.get(productId);
            existingItem.setQuantita(existingItem.getQuantita() + quantita);
        } else {
            items.put(productId, new CartItem(product, quantita));
        }
    }

    /**
     * Aggiorna la quantità di un prodotto. Se la quantità è <= 0, il prodotto viene rimosso.
     */
    public void updateQuantity(int productId, int quantita) {
        if (quantita <= 0) {
            deleteProduct(productId);
        } else if (items.containsKey(productId)) {
            items.get(productId).setQuantita(quantita);
        }
    }

    /**
     * Rimuove un prodotto dal carrello tramite il suo ID.
     */
    public void deleteProduct(int productId) {
        items.remove(productId);
    }

    /**
     * Restituisce tutti gli elementi presenti nel carrello (comodo per il ciclo JSTL <c:forEach>).
     */
    public Collection<CartItem> getItems() {
        return items.values();
    }

    /**
     * Restituisce il CartItem per l'ID di prodotto indicato, o null se non è nel carrello.
     * Usato da CartServlet per controllare la quantità già presente prima di aggiungerne altra.
     */
    public CartItem getItemById(int productId) {
        return items.get(productId);
    }

    /**
     * Calcola il prezzo totale dell'intero carrello.
     */
    public double getTotale() {
        double totale = 0.0;
        for (CartItem item : items.values()) {
            totale += item.getSubtotale();
        }
        return totale;
    }

    /**
     * Restituisce il numero totale di pezzi (somma delle quantità), utile per il badge nel menu/header.
     */
    public int getNumeroElementi() {
        int count = 0;
        for (CartItem item : items.values()) {
            count += item.getQuantita();
        }
        return count;
    }

    /**
     * Verifica se il carrello è vuoto.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Svuota completamente il carrello (es. dopo il checkout o su richiesta dell'utente).
     */
    public void clear() {
        items.clear();
    }

    @Override
    public String toString() {
        return "CartBean{" +
                "numeroElementi=" + getNumeroElementi() +
                ", totale=" + getTotale() +
                ", items=" + items.values() +
                '}';
    }
}
