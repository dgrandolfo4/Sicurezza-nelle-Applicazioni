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
	public boolean isUserValid(String email, byte[] pass) {
		boolean status = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        byte[] hashedBytes = null;

        try {
            if (connect()) {
                MessageDigest md = MessageDigest.getInstance("SHA-256");
                hashedBytes = md.digest(pass);
                String base64Password = Base64.getEncoder().encodeToString(hashedBytes);

                String sql = AppProperties.getQueryProperty("loginUserQuery");
    			// oggetto preparedstatement che consente di eseguire una query al db...
                ps = connessione.prepareStatement(sql);
    			// ... a partire dal nome e ...
                ps.setString(1, email);
    			// ... password date in input dall'utente alla jsp, dalla jsp alla servlet e dalla servlet al DAO
                ps.setString(2, base64Password);
                
                // esegue effettivamente la query ed ottiene un oggetto ResultSet che contiene la risposta del db
    			rs = ps.executeQuery();
    			// il next() prende la prima riga del risultato della query
    			// restituisce true se c'è almeno una riga, altrimenti false
    			status = rs.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // Chiusura sicura delle risorse JDBC
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            close();
            
            // Svuotiamo gli array contenenti la password
            if (pass != null) Arrays.fill(pass, (byte) 0);
            if (hashedBytes != null) Arrays.fill(hashedBytes, (byte) 0);
        }

        return status;
    }
}