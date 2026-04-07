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
        boolean connection = false;
        
        // Parametri di connessione
        String src = AppProperties.getConfigProperty("source"); 
        String db = AppProperties.getConfigProperty("schema_name"); 
        String connParams = AppProperties.getConfigProperty("connection_parameters");
        // Utenza di accesso al DB
        String userDb = AppProperties.getConfigProperty("user_db");
        String passDb = AppProperties.getConfigProperty("password_db");
        
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