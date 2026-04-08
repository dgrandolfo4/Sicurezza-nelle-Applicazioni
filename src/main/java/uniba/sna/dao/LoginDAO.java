package uniba.sna.dao;

import java.security.MessageDigest;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Base64;
import net.jcip.annotations.NotThreadSafe;
import uniba.sna.utils.AppProperties;

@NotThreadSafe
public class LoginDAO extends DatabaseDAO {
	public boolean isUserValid(String email, byte[] plainPassword) {
		PreparedStatement psApp = null;
		PreparedStatement psAuth = null;
        ResultSet rsApp = null;
        ResultSet rsAuth = null;
        
        int userId = -1;
        String dbStoredHash = null;
        String dbStoredSalt = null;

        // OTTENGO L'ID DELL'UTENTE
        try {
            if (connect()) {
                String sqlApp = AppProperties.getQueryProperty("selectAppUserIdQuery");
    			// oggetto preparedstatement che consente di eseguire una query al db...
                psApp = connessione.prepareStatement(sqlApp);
    			// ... a partire dall'email
                psApp.setString(1, email);
                
                // esegue effettivamente la query ed ottiene un oggetto ResultSet che contiene la risposta del db
                rsApp = psApp.executeQuery();
    			// il next() prende la prima riga del risultato della query
    			// restituisce true se c'è almeno una riga, altrimenti false
    			if (rsApp.next()) {
                    userId = rsApp.getInt(1); // Salviamo l'ID
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Svuotiamo l'array contenente la password
            if (plainPassword != null) Arrays.fill(plainPassword, (byte) 0);
        } finally {
            // Chiusura sicura delle risorse JDBC
            try { if (rsApp != null) rsApp.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psApp != null) psApp.close(); } catch (SQLException e) { e.printStackTrace(); }
            
            close();
        }
        
        if (userId == -1) return false; // Utente inesistente
        
        // OTTENGO LA PASSWORD ED IL SALT DELL'UTENTE
        try {
            if (connect(true)) {
                String sqlAuth = AppProperties.getQueryProperty("selectAuthUserQuery");
                psAuth = connessione.prepareStatement(sqlAuth);
                psAuth.setInt(1, userId);
                
                rsAuth = psAuth.executeQuery();
                if (rsAuth.next()) {
                    dbStoredHash = rsAuth.getString(1);
                    dbStoredSalt = rsAuth.getString(2);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Svuotiamo l'array contenente la password
            if (plainPassword != null) Arrays.fill(plainPassword, (byte) 0);
        } finally {
        	try { if (rsAuth != null) rsAuth.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psAuth != null) psApp.close(); } catch (SQLException e) { e.printStackTrace(); }
            
            close();
        }
        
        if (dbStoredHash == null || dbStoredSalt == null) return false;

        // CONFRONTO
        try {
            byte[] saltBytes = Base64.getDecoder().decode(dbStoredSalt);
            MessageDigest msgDigest = MessageDigest.getInstance("SHA-256");
            msgDigest.update(saltBytes); 
            byte[] hashedInputPassword = msgDigest.digest(plainPassword);
            
            String base64HashedInputPassword = Base64.getEncoder().encodeToString(hashedInputPassword);
            return dbStoredHash.equals(base64HashedInputPassword);
        } catch (Exception e) { 
            e.printStackTrace();
        }
        
        return false;
    }
}