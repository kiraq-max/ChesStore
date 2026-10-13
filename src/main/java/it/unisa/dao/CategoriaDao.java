package it.unisa.dao;

import it.unisa.model.CategoriaBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CategoriaDao {

    private static final String SQL_FIND_ALL =
            "SELECT id, nome, descrizione FROM categoria ORDER BY nome";

    private static final String SQL_FIND_BY_KEY =
            "SELECT id, nome, descrizione FROM categoria WHERE id = ?";

    public List<CategoriaBean> doRetrieveAll() throws SQLException {
        List<CategoriaBean> lista = new ArrayList<>();
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public CategoriaBean doRetrieveByKey(int id) throws SQLException {
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

    private CategoriaBean mapRow(ResultSet rs) throws SQLException {
        return new CategoriaBean(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("descrizione")
        );
    }
}
