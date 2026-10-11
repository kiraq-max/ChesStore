package it.unisa.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Rappresenta un ordine effettuato da un utente in ChesStore.
 */
public class OrdineBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private Date data;
    private double totale;
    private String indirizzoSpedizione;
    private String metodoPagamento;
    private String utenteEmail;

    public OrdineBean() {
        this.totale = 0.0;
        this.data = new Date();
    }

    public OrdineBean(int id, Date data, double totale, String indirizzoSpedizione,
                      String metodoPagamento, String utenteEmail) {
        this.id = id;
        this.data = data;
        this.totale = totale;
        this.indirizzoSpedizione = indirizzoSpedizione;
        this.metodoPagamento = metodoPagamento;
        this.utenteEmail = utenteEmail;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public double getTotale() {
        return totale;
    }

    public void setTotale(double totale) {
        this.totale = totale;
    }

    public String getIndirizzoSpedizione() {
        return indirizzoSpedizione;
    }

    public void setIndirizzoSpedizione(String indirizzoSpedizione) {
        this.indirizzoSpedizione = indirizzoSpedizione;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getUtenteEmail() {
        return utenteEmail;
    }

    public void setUtenteEmail(String utenteEmail) {
        this.utenteEmail = utenteEmail;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrdineBean that = (OrdineBean) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "OrdineBean{" +
                "id=" + id +
                ", data=" + data +
                ", totale=" + totale +
                ", indirizzoSpedizione='" + indirizzoSpedizione + '\'' +
                ", metodoPagamento='" + metodoPagamento + '\'' +
                ", utenteEmail='" + utenteEmail + '\'' +
                '}';
    }
}