package uniba.sna.dao;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import net.jcip.annotations.NotThreadSafe;
import uniba.sna.utils.AppProperties;

@NotThreadSafe
abstract class DatabaseDAO {
    
    protected Connection connessione;

    protected boolean connect() throws ClassNotFoundException, SQLException, IOException {
        return connect(false);
    }
    
    protected boolean connect(boolean isAuthDb) throws ClassNotFoundException, SQLException, IOException {
        boolean connection = false;
        
        // Parametri generali di connessione
        String src = AppProperties.getConfigProperty("source"); 
        String connParams = AppProperties.getConfigProperty("connection_parameters");

        // Utenza di accesso al DB pubblico
        String db = AppProperties.getConfigProperty(isAuthDb ? "schema_auth_name" : "schema_name"); 
        String userDb = AppProperties.getConfigProperty(isAuthDb ? "user_auth_db" : "user_db");
        String passDb = AppProperties.getConfigProperty(isAuthDb ? "password_auth_db" : "password_db");
        
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String jdbc = (new StringBuilder("jdbc:mysql://"))
                            .append(src)
                            .append("/")
                            .append(db)
                            .append(connParams)
                            .toString();
                            
            connessione = DriverManager.getConnection(jdbc, userDb, passDb);
            connection = true;
        } catch (SQLException e) {
        	if (e.getErrorCode() == 1045) {
        		System.out.println("Username o password del DB errati! \n Controllare il file di configurazione e riprovare." + e);
    		} else {
        		System.out.println("Errore nella connessione al database!.");
    		}
        } catch (Exception e1) {
	    	System.out.println(e1);
	    }
        
        return connection;
    }
    
    protected void close() {
        try {
            if (connessione != null && !connessione.isClosed()) {
                connessione.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}