package it.unisa.dao;

import it.unisa.model.UtenteBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class UtenteDao {

    private static final String SQL_FIND_BY_EMAIL_PASSWORD =
        "SELECT id, nome, cognome, email, password, ruolo " +
        "FROM utente WHERE email = ? AND password = SHA2(?, 256)";

    private static final String SQL_FIND_BY_EMAIL =
            "SELECT id, nome, cognome, email, password, ruolo " +
            "FROM utente WHERE email = ?";

    private static final String SQL_INSERT =
        "INSERT INTO utente (nome, cognome, email, password, ruolo) " +
        "VALUES (?, ?, ?, SHA2(?, 256), ?)";

    
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
