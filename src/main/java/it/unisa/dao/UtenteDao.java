package it.unisa.dao;

import it.unisa.model.UtenteBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DAO per la gestione della tabella {@code utente}.
 * Fornisce le operazioni di autenticazione, ricerca e registrazione utente.
 */
public class UtenteDao {

    // -------------------------------------------------------------------------
    // Query SQL
    // -------------------------------------------------------------------------

    private static final String SQL_FIND_BY_EMAIL_PASSWORD =
            "SELECT id, nome, cognome, email, password, ruolo " +
            "FROM utente WHERE email = ? AND password = ?";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT id, nome, cognome, email, password, ruolo " +
            "FROM utente WHERE email = ?";

    private static final String SQL_INSERT =
            "INSERT INTO utente (nome, cognome, email, password, ruolo) " +
            "VALUES (?, ?, ?, ?, ?)";

    // -------------------------------------------------------------------------
    // Metodi pubblici
    // -------------------------------------------------------------------------

    /**
     * Verifica le credenziali dell'utente per il login.
     *
     * @param email    email inserita nel form di login
     * @param password password inserita nel form di login
     * @return il {@link UtenteBean} corrispondente, oppure {@code null} se le
     *         credenziali non corrispondono ad alcun utente
     * @throws SQLException in caso di errore JDBC
     */
    public UtenteBean doRetrieveByEmailPassword(String email, String password)
            throws SQLException {
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_EMAIL_PASSWORD)) {

            ps.setString(1, email);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Recupera un utente tramite la sola email.
     * Utilizzato per controllare i duplicati durante la registrazione.
     *
     * @param email email da cercare
     * @return il {@link UtenteBean} corrispondente, oppure {@code null} se non trovato
     * @throws SQLException in caso di errore JDBC
     */
    public UtenteBean doRetrieveByEmail(String email) throws SQLException {
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_EMAIL)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    /**
     * Inserisce un nuovo utente nella tabella {@code utente}.
     * Al termine dell'operazione, aggiorna l'id del bean con la chiave
     * generata automaticamente dal DB (AUTO_INCREMENT).
     *
     * @param utente il bean da persistere (il campo {@code ruolo} deve essere
     *               già valorizzato, di default {@code "CLIENTE"})
     * @throws SQLException in caso di errore JDBC o se la email è già presente
     */
    public void doSave(UtenteBean utente) throws SQLException {
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, utente.getNome());
            ps.setString(2, utente.getCognome());
            ps.setString(3, utente.getEmail());
            ps.setString(4, utente.getPassword());
            ps.setString(5, utente.getRuolo());

            ps.executeUpdate();

            // Aggiorna l'id nel bean con la chiave generata dal DB
            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    utente.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // Helper privati
    // -------------------------------------------------------------------------

    /**
     * Mappa una riga del {@link ResultSet} corrente in un {@link UtenteBean}.
     * Il cursore del ResultSet deve essere già posizionato sulla riga da leggere.
     */
    private UtenteBean mapRow(ResultSet rs) throws SQLException {
        UtenteBean u = new UtenteBean();
        u.setId(rs.getInt("id"));
        u.setNome(rs.getString("nome"));
        u.setCognome(rs.getString("cognome"));
        u.setEmail(rs.getString("email"));
        u.setPassword(rs.getString("password"));
        u.setRuolo(rs.getString("ruolo"));
        return u;
    }
}
