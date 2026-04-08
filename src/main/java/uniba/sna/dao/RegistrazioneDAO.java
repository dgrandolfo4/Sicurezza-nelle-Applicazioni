package uniba.sna.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import net.jcip.annotations.NotThreadSafe;
import uniba.sna.utils.AppProperties;

@NotThreadSafe
public class RegistrazioneDAO extends DatabaseDAO {

    /**
     * Registra un nuovo utente e salva le informazioni della sua foto profilo.
     */
	/* public boolean registerUser(String email, byte[] hashedPassword, String salt, String originalName, String uniqueName) {
        int generatedUserId = -1;
        boolean isSuccess = false;

        try {
            if (connect()) {
                String sqlUser = AppProperties.getQueryProperty("insertAppUserQuery");
                PreparedStatement psUser = connessione.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
                psUser.setString(1, email);
                int rowsAffected = psUser.executeUpdate();
                if (rowsAffected > 0) {
                    ResultSet rs = psUser.getGeneratedKeys();
                    if (rs.next()) {
                    	generatedUserId = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
        	e.printStackTrace(); 
        	return false; 
    	} 
        finally { 
        	close(); 
    	}

        if (generatedUserId == -1) return false;

        try {
            if (connect(true)) { // DB Auth
            	String base64Password = Base64.getEncoder().encodeToString(hashedPassword);

                String sqlAuth = AppProperties.getQueryProperty("insertAuthUserQuery");
                PreparedStatement psAuth = connessione.prepareStatement(sqlAuth);
                psAuth.setInt(1, generatedUserId);
                psAuth.setString(2, base64Password);
                psAuth.setString(3, salt);
                if (psAuth.executeUpdate() == 0) return false;
            }
        } catch (Exception e) { 
        	e.printStackTrace(); 
        	return false; 
    	} 
        finally { 
        	close(); 
    	}

        // FASE 3: Salvataggio File Profilo su DB App
        try {
            if (connect(false)) { // DB App
                String sqlFile = AppProperties.getQueryProperty("insertFileQuery");
                try (PreparedStatement psFile = connessione.prepareStatement(sqlFile)) {
                    psFile.setString(1, originalName);
                    psFile.setString(2, uniqueName);
                    psFile.setInt(3, 1); // 1 = Categoria Immagine Profilo
                    psFile.setInt(4, generatedUserId);
                    if (psFile.executeUpdate() > 0) isSuccess = true;
                }
            }
        } catch (Exception e) { e.printStackTrace(); } 
        finally { close(); }

        return isSuccess;
    } */
	
    public boolean registerUser(String email, 
    							byte[] hashedPassword, 
								byte[] salt,
                                String fileName,
                                String uniqueFileName) {
        PreparedStatement psUserAuth = null;
        PreparedStatement psUser = null;
        PreparedStatement psFile = null;
        ResultSet rsAuth = null;
        ResultSet rs = null;
        
        int generatedUserId = -1;
        boolean success = false;

        // INSERIMENTO PASSWORD E SALT
        try {
            if (connect(true)) {
            	// Convertiamo la password in una stringa Base64
            	String base64HashedPassword = Base64.getEncoder().encodeToString(hashedPassword);
            	// Convertiamo la password in una stringa Base64
            	String base64Salt = Base64.getEncoder().encodeToString(salt);
            	
                String sqlUser = AppProperties.getQueryProperty("insertAuthUserQuery");
                psUserAuth = connessione.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
                psUserAuth.setString(1, base64HashedPassword);
                psUserAuth.setString(2, base64Salt);
                int rowsAffected = psUserAuth.executeUpdate();
                if (rowsAffected > 0) {
                    rsAuth = psUserAuth.getGeneratedKeys();
                    if (rsAuth.next()) {
                    	generatedUserId = rsAuth.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
        	e.printStackTrace(); 
    	} 
        finally { 
        	try { if (rsAuth != null) rsAuth.close(); } catch (SQLException e) { e.printStackTrace(); }
        	try { if (psUserAuth != null) psUserAuth.close(); } catch (SQLException e) { e.printStackTrace(); }
        	
        	close(); 
    	}
        
        if (generatedUserId == -1) return false;
        
        // INSERIMENTO ID E EMAIL
        try {
            if (connect()) {
            	
                // Avviamo la transazione (disabilitando l'auto-commit)
                connessione.setAutoCommit(false);                

                String sqlUser = AppProperties.getQueryProperty("insertAppUserQuery");
                psUser = connessione.prepareStatement(sqlUser);
                psUser.setInt(1, generatedUserId);
                psUser.setString(2, email);
                int userRowsAffected = psUser.executeUpdate();
                
            	String sqlFile = AppProperties.getQueryProperty("insertFileQuery");
                psFile = connessione.prepareStatement(sqlFile);
                psFile.setString(1, fileName);
                psFile.setString(2, uniqueFileName);
                psFile.setInt(3, 1); // Category = 1 ==> Immagine Profilo
                psFile.setString(4, email);
                int fileRowsAffected = psFile.executeUpdate();

                if (userRowsAffected > 0 && fileRowsAffected > 0) {
                    connessione.commit();
                    success = true;
                } else {
                    connessione.rollback();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // In caso di qualsiasi eccezione (es. email duplicata), annulliamo tutto
            try {
                if (connessione != null) {
                    connessione.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        } finally {
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psUser != null) psUser.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psFile != null) psFile.close(); } catch (SQLException e) { e.printStackTrace(); }
            
            // Ripristiniamo l'auto-commit
            try { 
                if (connessione != null) connessione.setAutoCommit(true); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            close();
        }
        
        return success;
    }
}