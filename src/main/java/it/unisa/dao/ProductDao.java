package it.unisa.dao;

import it.unisa.model.ProductBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDao {

    private static final String SQL_FIND_ALL =
            "SELECT id, nome, descrizione, prezzo, quantita_disponibile, immagine, id_categoria " +
            "FROM prodotto ORDER BY nome";

    private static final String SQL_FIND_BY_KEY =
            "SELECT id, nome, descrizione, prezzo, quantita_disponibile, immagine, id_categoria " +
            "FROM prodotto WHERE id = ?";

    private static final String SQL_FIND_BY_CATEGORIA =
            "SELECT id, nome, descrizione, prezzo, quantita_disponibile, immagine, id_categoria " +
            "FROM prodotto WHERE id_categoria = ? ORDER BY nome";

    private static final String SQL_INSERT =
            "INSERT INTO prodotto (nome, descrizione, prezzo, quantita_disponibile, immagine, id_categoria) " +
            "VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_UPDATE =
            "UPDATE prodotto SET nome = ?, descrizione = ?, prezzo = ?, " +
            "quantita_disponibile = ?, immagine = ?, id_categoria = ? " +
            "WHERE id = ?";

    private static final String SQL_DELETE =
            "DELETE FROM prodotto WHERE id = ?";

    public List<ProductBean> doRetrieveAll() throws SQLException {
        List<ProductBean> lista = new ArrayList<>();
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    public ProductBean doRetrieveByKey(int id) throws SQLException {
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

    public List<ProductBean> doRetrieveByCategoria(int idCategoria) throws SQLException {
        List<ProductBean> lista = new ArrayList<>();
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_CATEGORIA)) {

            ps.setInt(1, idCategoria);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    public void doSave(ProductBean product) throws SQLException {
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getNome());
            ps.setString(2, product.getDescrizione());
            ps.setDouble(3, product.getPrezzo());
            ps.setInt(4, product.getQuantitaDisponibile());
            ps.setString(5, product.getImmagine());
            ps.setInt(6, product.getIdCategoria());

            ps.executeUpdate();

            try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    public void doUpdate(ProductBean product) throws SQLException {
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_UPDATE)) {

            ps.setString(1, product.getNome());
            ps.setString(2, product.getDescrizione());
            ps.setDouble(3, product.getPrezzo());
            ps.setInt(4, product.getQuantitaDisponibile());
            ps.setString(5, product.getImmagine());
            ps.setInt(6, product.getIdCategoria());
            ps.setInt(7, product.getId());

            ps.executeUpdate();
        }
    }

    public void doDelete(int id) throws SQLException {
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_DELETE)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    private ProductBean mapRow(ResultSet rs) throws SQLException {
        return new ProductBean(
                rs.getInt("id"),
                rs.getString("nome"),
                rs.getString("descrizione"),
                rs.getDouble("prezzo"),
                rs.getInt("quantita_disponibile"),
                rs.getString("immagine"),
                rs.getInt("id_categoria")
        );
    }
}
