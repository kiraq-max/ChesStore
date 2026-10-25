package it.unisa.dao;

import it.unisa.model.ComposizioneOrdineBean;
import it.unisa.model.OrdineBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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

    private static final String SQL_FIND_BY_DATA_RANGE =
            "SELECT id, data, totale, indirizzo_spedizione, metodo_pagamento, utente_email " +
            "FROM ordine WHERE data >= ? AND data <= ? ORDER BY data DESC";

    private static final String SQL_INSERT_ORDINE =
            "INSERT INTO ordine (data, totale, indirizzo_spedizione, metodo_pagamento, utente_email) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_INSERT_COMPOSIZIONE =
            "INSERT INTO composizione_ordine " +
            "(id_ordine, id_prodotto, nome_prodotto_storico, quantita, prezzo_unitario_storico) " +
            "VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_DECREMENTA_SCORTA =
            "UPDATE prodotto SET quantita_disponibile = quantita_disponibile - ? " +
            "WHERE id = ? AND quantita_disponibile >= ?";

    private static final String SQL_FIND_COMPOSIZIONE_BY_ORDINE =
            "SELECT id, id_ordine, id_prodotto, nome_prodotto_storico, quantita, prezzo_unitario_storico " +
            "FROM composizione_ordine WHERE id_ordine = ?";

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

    public List<OrdineBean> doRetrieveAllOrdini() throws SQLException {
        return doRetrieveAll();
    }

    public List<OrdineBean> doRetrieveByCliente(String email) throws SQLException {
        return doRetrieveByUtente(email);
    }

    public List<OrdineBean> doRetrieveByDataRange(Date daData, Date aData) throws SQLException {
        List<OrdineBean> lista = new ArrayList<>();
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_BY_DATA_RANGE)) {

            java.sql.Timestamp tsDa = (daData != null) ? new java.sql.Timestamp(daData.getTime()) : new java.sql.Timestamp(0);
            java.sql.Timestamp tsA = (aData != null) ? new java.sql.Timestamp(aData.getTime()) : new java.sql.Timestamp(System.currentTimeMillis());

            ps.setTimestamp(1, tsDa);
            ps.setTimestamp(2, tsA);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    public List<OrdineBean> doRetrieveFiltered(String clienteEmail, Date daData, Date aData) throws SQLException {
        StringBuilder sql = new StringBuilder(
                "SELECT id, data, totale, indirizzo_spedizione, metodo_pagamento, utente_email FROM ordine WHERE 1=1"
        );
        List<Object> params = new ArrayList<>();

        if (clienteEmail != null && !clienteEmail.trim().isEmpty()) {
            sql.append(" AND utente_email LIKE ?");
            params.add("%" + clienteEmail.trim() + "%");
        }

        if (daData != null) {
            sql.append(" AND data >= ?");
            params.add(new java.sql.Timestamp(daData.getTime()));
        }

        if (aData != null) {
            sql.append(" AND data <= ?");
            params.add(new java.sql.Timestamp(aData.getTime()));
        }

        sql.append(" ORDER BY data DESC");

        List<OrdineBean> lista = new ArrayList<>();
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String) {
                    ps.setString(i + 1, (String) p);
                } else if (p instanceof java.sql.Timestamp) {
                    ps.setTimestamp(i + 1, (java.sql.Timestamp) p);
                }
            }

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
                     SQL_INSERT_ORDINE, Statement.RETURN_GENERATED_KEYS)) {

            long time = (ordine.getData() != null) ? ordine.getData().getTime() : System.currentTimeMillis();
            ps.setTimestamp(1, new java.sql.Timestamp(time));
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

    public void doSave(OrdineBean ordine, List<ComposizioneOrdineBean> righeComposizione) throws SQLException {
        if (ordine == null || righeComposizione == null || righeComposizione.isEmpty()) {
            throw new IllegalArgumentException("Impossibile salvare un ordine vuoto.");
        }

        Connection con = null;
        PreparedStatement psOrdine = null;
        PreparedStatement psComposizione = null;
        PreparedStatement psScorte = null;
        ResultSet rsChiavi = null;

        try {
            con = DBConnectionPool.getConnection();
            con.setAutoCommit(false);

            psOrdine = con.prepareStatement(SQL_INSERT_ORDINE, Statement.RETURN_GENERATED_KEYS);
            long time = (ordine.getData() != null) ? ordine.getData().getTime() : System.currentTimeMillis();
            psOrdine.setTimestamp(1, new java.sql.Timestamp(time));
            psOrdine.setDouble(2, ordine.getTotale());
            psOrdine.setString(3, ordine.getIndirizzoSpedizione());
            psOrdine.setString(4, ordine.getMetodoPagamento());
            psOrdine.setString(5, ordine.getUtenteEmail());

            psOrdine.executeUpdate();

            rsChiavi = psOrdine.getGeneratedKeys();
            if (!rsChiavi.next()) {
                throw new SQLException("Creazione ordine fallita: nessun ID restituito dal database.");
            }
            int idOrdineGenerato = rsChiavi.getInt(1);
            ordine.setId(idOrdineGenerato);

            psComposizione = con.prepareStatement(SQL_INSERT_COMPOSIZIONE);
            psScorte = con.prepareStatement(SQL_DECREMENTA_SCORTA);

            for (ComposizioneOrdineBean riga : righeComposizione) {
                if (riga.getIdProdotto() != null) {
                    psScorte.setInt(1, riga.getQuantita());
                    psScorte.setInt(2, riga.getIdProdotto());
                    psScorte.setInt(3, riga.getQuantita());
                    int righeAggiornate = psScorte.executeUpdate();

                    if (righeAggiornate == 0) {
                        throw new SQLException("Disponibilita insufficiente a magazzino per il prodotto ID: " + riga.getIdProdotto());
                    }
                }

                psComposizione.setInt(1, idOrdineGenerato);
                if (riga.getIdProdotto() != null) {
                    psComposizione.setInt(2, riga.getIdProdotto());
                } else {
                    psComposizione.setNull(2, java.sql.Types.INTEGER);
                }
                psComposizione.setString(3, riga.getNomeProdottoStorico());
                psComposizione.setInt(4, riga.getQuantita());
                psComposizione.setDouble(5, riga.getPrezzoUnitarioStorico());

                psComposizione.executeUpdate();
            }

            con.commit();
        } catch (SQLException e) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                }
            }
            throw e;
        } finally {
            if (rsChiavi != null) try { rsChiavi.close(); } catch (SQLException ignored) {}
            if (psOrdine != null) try { psOrdine.close(); } catch (SQLException ignored) {}
            if (psComposizione != null) try { psComposizione.close(); } catch (SQLException ignored) {}
            if (psScorte != null) try { psScorte.close(); } catch (SQLException ignored) {}
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {}
            }
        }
    }

    public List<ComposizioneOrdineBean> doRetrieveDettaglioOrdine(int idOrdine) throws SQLException {
        List<ComposizioneOrdineBean> lista = new ArrayList<>();
        try (Connection con = DBConnectionPool.getConnection();
             PreparedStatement ps = con.prepareStatement(SQL_FIND_COMPOSIZIONE_BY_ORDINE)) {

            ps.setInt(1, idOrdine);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ComposizioneOrdineBean c = new ComposizioneOrdineBean();
                    c.setId(rs.getInt("id"));
                    c.setIdOrdine(rs.getInt("id_ordine"));
                    int idProd = rs.getInt("id_prodotto");
                    c.setIdProdotto(rs.wasNull() ? null : idProd);
                    c.setNomeProdottoStorico(rs.getString("nome_prodotto_storico"));
                    c.setQuantita(rs.getInt("quantita"));
                    c.setPrezzoUnitarioStorico(rs.getDouble("prezzo_unitario_storico"));
                    lista.add(c);
                }
            }
        }
        return lista;
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