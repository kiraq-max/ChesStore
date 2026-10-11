package it.unisa.model;

import java.io.Serializable;
import java.util.Objects;


public class ComposizioneOrdineBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private int idOrdine;
    private Integer idProdotto; // Integer nullable per gestire prodotti eliminati
    private String nomeProdottoStorico;
    private int quantita;
    private double prezzoUnitarioStorico;

    // 1. Costruttore vuoto (standard JavaBean)
    public ComposizioneOrdineBean() {
    }

    // 2. Costruttore completo
    public ComposizioneOrdineBean(int id, int idOrdine, Integer idProdotto, 
                                  String nomeProdottoStorico, int quantita, double prezzoUnitarioStorico) {
        this.id = id;
        this.idOrdine = idOrdine;
        this.idProdotto = idProdotto;
        this.nomeProdottoStorico = nomeProdottoStorico;
        this.quantita = quantita;
        this.prezzoUnitarioStorico = prezzoUnitarioStorico;
    }

    // Costruttore di comodo senza id (utile per inserimenti con AUTO_INCREMENT)
    public ComposizioneOrdineBean(int idOrdine, Integer idProdotto, 
                                  String nomeProdottoStorico, int quantita, double prezzoUnitarioStorico) {
        this(0, idOrdine, idProdotto, nomeProdottoStorico, quantita, prezzoUnitarioStorico);
    }

    // 3. Getter e Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdOrdine() {
        return idOrdine;
    }

    public void setIdOrdine(int idOrdine) {
        this.idOrdine = idOrdine;
    }

    public Integer getIdProdotto() {
        return idProdotto;
    }

    public void setIdProdotto(Integer idProdotto) {
        this.idProdotto = idProdotto;
    }

    public String getNomeProdottoStorico() {
        return nomeProdottoStorico;
    }

    public void setNomeProdottoStorico(String nomeProdottoStorico) {
        this.nomeProdottoStorico = nomeProdottoStorico;
    }

    public int getQuantita() {
        return quantita;
    }

    public void setQuantita(int quantita) {
        this.quantita = quantita;
    }

    public double getPrezzoUnitarioStorico() {
        return prezzoUnitarioStorico;
    }

    public void setPrezzoUnitarioStorico(double prezzoUnitarioStorico) {
        this.prezzoUnitarioStorico = prezzoUnitarioStorico;
    }

    // Metodo di utilità: subtotale della riga d'ordine
    public double getSubtotale() {
        return this.quantita * this.prezzoUnitarioStorico;
    }

    // 4. equals e hashCode basati su id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComposizioneOrdineBean that = (ComposizioneOrdineBean) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // 5. toString
    @Override
    public String toString() {
        return "ComposizioneOrdineBean{" +
                "id=" + id +
                ", idOrdine=" + idOrdine +
                ", idProdotto=" + idProdotto +
                ", nomeProdottoStorico='" + nomeProdottoStorico + '\'' +
                ", quantita=" + quantita +
                ", prezzoUnitarioStorico=" + prezzoUnitarioStorico +
                '}';
    }
}
