package it.unisa.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Rappresenta una categoria di prodotti (es. Scacchiere, Pezzi, Orologi, Libri) in ChesStore.
 */
public class CategoriaBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String nome;
    private String descrizione;

    // 1. Costruttore vuoto (JavaBean spec)
    public CategoriaBean() {
    }

    // 2. Costruttore completo
    public CategoriaBean(int id, String nome, String descrizione) {
        this.id = id;
        this.nome = nome;
        this.descrizione = descrizione;
    }

    // Costruttore di comodo senza id (per inserimenti prima di generare l'id con AUTO_INCREMENT)
    public CategoriaBean(String nome, String descrizione) {
        this(0, nome, descrizione);
    }

    // 3. Getter e Setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }

    // 4. equals e hashCode basati su id
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CategoriaBean that = (CategoriaBean) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // 5. toString
    @Override
    public String toString() {
        return "CategoriaBean{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                '}';
    }
}
