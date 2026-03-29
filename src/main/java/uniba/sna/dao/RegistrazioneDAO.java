package uniba.sna.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;

import uniba.sna.utils.AppProperties;

public class RegistrazioneDAO extends DatabaseDAO {

    /**
     * Registra un nuovo utente e salva le informazioni della sua foto profilo.
     */
    public boolean registerUser(String email, byte[] hashedPassword, 
                                             String fileName) {
        boolean success = false;
        PreparedStatement psUser = null;
        PreparedStatement psFile = null;
        ResultSet rs = null;

        try {
            if (connect()) {
            	// Convertiamo in una stringa Base64
            	String base64Password = Base64.getEncoder().encodeToString(hashedPassword);
                // DISABILITIAMO L'AUTO-COMMIT PER AVVIARE LA TRANSAZIONE
                connessione.setAutoCommit(false);
                

                String sqlUser = AppProperties.getQueryProperty("registerUserQuery");
                
                // (Statement.RETURN_GENERATED_KEYS ci permette di recuperare l'ID appena creato)
                psUser = connessione.prepareStatement(sqlUser, Statement.RETURN_GENERATED_KEYS);
                psUser.setString(1, email);
                psUser.setString(2, base64Password);
                
                int userRowsAffected = psUser.executeUpdate();
                
                if (userRowsAffected > 0) {
                    // Recuperiamo l'ID dell'utente appena inserito
                    rs = psUser.getGeneratedKeys();
                    if (rs.next()) {
                        int generatedUserId = rs.getInt(1);

                        String sqlFile = AppProperties.getQueryProperty("insertFileQuery");
                        psFile = connessione.prepareStatement(sqlFile);
                        psFile.setString(1, fileName);
                        psFile.setInt(2, 1); // Category = 1 ==> Immagine Profilo
                        psFile.setInt(3, generatedUserId);

                        int fileRowsAffected = psFile.executeUpdate();
                        
                        if (fileRowsAffected > 0) {
                            connessione.commit();
                            success = true;
                        } else {
                            connessione.rollback();
                        }
                    } else {
                        connessione.rollback();
                    }
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
            // Pulizia delle risorse (Fondamentale per la sicurezza e stabilità)
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psUser != null) psUser.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (psFile != null) psFile.close(); } catch (SQLException e) { e.printStackTrace(); }
            
            // Ripristiniamo l'auto-commit
            try { 
                if (connessione != null) connessione.setAutoCommit(true); 
            } catch (SQLException e) { e.printStackTrace(); }
            
            // Chiudiamo la connessione
            close();
        }
        
        return success;
    }
}