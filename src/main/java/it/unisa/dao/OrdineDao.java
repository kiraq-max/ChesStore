package it.unisa.dao;

import it.unisa.model.OrdineBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OrdineDao {

    private static final String SQL_FIND_ALL =
            "SELECT id, data, totale, indirizzo_spedizione, metodo_pagamento, utente_email " +
            "FROM ordine ORDER BY data DESC";

    private static final String SQL_FIND_BY_KEY =
            "SELECT id, data, totale, indirizzo_spedizione, metodo_pagamento, utente_email " +
            "FROM ordine WHERE id = ?";

    private static final String SQL_FIND_BY_UTENTE =
            "SELECT id, data, totale, indirizzo_spedizione, metodo_pagamento, utente_email " +
            "FROM ordine WHERE utente_email = ? ORDER BY data DESC";

    private static final String SQL_INSERT =
            "INSERT INTO ordine (data, totale, indirizzo_spedizione, metodo_pagamento, utente_email) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_DELETE =
            "DELETE FROM ordine WHERE id = ?";

    public List<OrdineBean> doRetrieveAll() throws SQLException {
        List<OrdineBean> lista = new ArrayList<>();
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public OrdineBean doRetrieveByKey(int id) throws SQLException {
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_KEY)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public List<OrdineBean> doRetrieveByUtente(String utenteEmail) throws SQLException {
        List<OrdineBean> lista = new ArrayList<>();
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_UTENTE)) {

            ps.setString(1, utenteEmail);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    public void doSave(OrdineBean ordine) throws SQLException {
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, new java.sql.Timestamp(ordine.getData().getTime()));
            ps.setDouble(2, ordine.getTotale());
            ps.setString(3, ordine.getIndirizzoSpedizione());
            ps.setString(4, ordine.getMetodoPagamento());
            ps.setString(5, ordine.getUtenteEmail());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    ordine.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void doDelete(int id) throws SQLException {
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private OrdineBean mapRow(ResultSet rs) throws SQLException {
        OrdineBean ordine = new OrdineBean();
        ordine.setId(rs.getInt("id"));
        ordine.setData(rs.getTimestamp("data"));
        ordine.setTotale(rs.getDouble("totale"));
        ordine.setIndirizzoSpedizione(rs.getString("indirizzo_spedizione"));
        ordine.setMetodoPagamento(rs.getString("metodo_pagamento"));
        ordine.setUtenteEmail(rs.getString("utente_email"));
        return ordine;
    }
}