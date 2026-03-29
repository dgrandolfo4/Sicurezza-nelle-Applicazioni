package uniba.sna.dao;
import java.sql.*;
import java.util.Arrays;

public class LoginDAO {
	public static boolean isUserValid(String name, byte[] pass) {
		boolean status= false;
		try {
			// inizializza il driver per comunicare con il db
			Class.forName("com.mysql.cj.jdbc.Driver");
			// stringa di connessione: indirizzo -porta -nome db
			String url = "jdbc:mysql://localhost:3306/sna_grandolfo";
			// oggetto connessione al dbtramite inserimento di credenziali: stringa di connessione -nome utente -password
			Connection con = DriverManager.getConnection(url, "my_user", "my_password");
			// oggetto preparedstatementche consente di eseguire una query al db...
			PreparedStatement ps = con.prepareStatement("SELECT * FROM user WHERE username=? AND password=?");
			// ... a partire dal nome e ...
			ps.setString(1, name);
			// ... password date in input dall'utente alla jsp, dalla jspalla servlete dalla servletal DAO
			ps.setBytes(2, pass);
			// svuoto la password
			Arrays.fill(pass, (byte)0);
			// esegue effettivamente la query ed ottiene un oggetto ResultSetche contiene la risposta del db
			ResultSet rs = ps.executeQuery();
			// il next() prende la prima riga del risultato della query
			// restituisce truese c'è almeno una riga altrimenti false
			status = rs.next();
		} 
		catch(Exception e) {
			e.printStackTrace();
		}
		
		return status;
	}
}