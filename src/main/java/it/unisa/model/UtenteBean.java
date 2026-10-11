package it.unisa.model;

import java.io.Serializable;
import java.util.Objects;

public class UtenteBean implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private int id;
    private String nome;
    private String cognome;
    private String email;
    private String password;
    private String ruolo;

    public UtenteBean() {
        this.ruolo = "Cliente";
    }

    public UtenteBean(int id, String nome, String cognome, String email, String password, String ruolo){
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
        this.email = email;
        this.password = password;
        this.ruolo = (ruolo != null && ruolo.trim().isEmpty()) ? ruolo : "Cliente";
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getCognome() { return cognome; }
    public void setCognome(String cognome) { this.cognome = cognome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRuolo() { return ruolo; }
    public void setRuolo(String ruolo) { this.ruolo = ruolo; }
    
    public boolean isAdmin() { return "admin".equalsIgnoreCase(this.ruolo); }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UtenteBean that = (UtenteBean) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return "UtenteBean{" +
               "id=" + id +
               ", nome='" + nome + '\'' +
               ", cognome='" + cognome + '\'' +
               ", email='" + email + '\'' +
               ", ruolo='" + ruolo + '\'' +
               "}";
    }
}