package it.unisa.dao;

import java.sql.Connection;
import java.sql.SQLException;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * Gestore centralizzato del pool di connessioni tramite DataSource JNDI su Tomcat.
 */
public class DBConnectionPool {

    private static DataSource ds;

    static {
        try {
            InitialContext ctx = new InitialContext();
            ds = (DataSource) ctx.lookup("java:comp/env/jdbc/chesstore_db");
        } catch (NamingException e) {
            System.err.println("Errore ricerca DataSource JNDI: " + e.getMessage());
        }
    }

    /**
     * Restituisce un'istanza del DataSource configurato su Tomcat.
     */
    public static synchronized DataSource getDataSource() {
        if (ds == null) {
            try {
                InitialContext ctx = new InitialContext();
                ds = (DataSource) ctx.lookup("java:comp/env/jdbc/chesstore_db");
            } catch (NamingException e) {
                System.err.println("Errore ricerca DataSource JNDI: " + e.getMessage());
            }
        }
        return ds;
    }

    /**
     * Estrae una connessione attiva dal pool del DataSource.
     */
    public static Connection getConnection() throws SQLException {
        DataSource dataSource = getDataSource();
        if (dataSource == null) {
            throw new SQLException("DataSource JNDI non disponibile o non configurato.");
        }
        return dataSource.getConnection();
    }
}
