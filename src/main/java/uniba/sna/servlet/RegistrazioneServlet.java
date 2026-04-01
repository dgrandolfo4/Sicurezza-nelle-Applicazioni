package uniba.sna.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;

import uniba.sna.dao.RegistrazioneDAO;
import uniba.sna.utils.AppProperties;

/**
 * Servlet implementation class RegistrazioneServlet
 */
@MultipartConfig
@WebServlet("/RegistrazioneServlet")
public class RegistrazioneServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegistrazioneServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("registrazione.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {	        
		String email = request.getParameter("email").trim();
	    byte[] password = request.getParameter("password").getBytes();
	    byte[] retypePassword = request.getParameter("retype_password").getBytes();
	    
	    // Controllo valorizzazione dell'email
	    if (email == null || email.isEmpty()) {
	    	// svuoto le password
	    	Arrays.fill(password, (byte)0);
	    	Arrays.fill(retypePassword, (byte)0);
	    	request.setAttribute("msgError", "Il campo email è obbligatorio.");
	    	request.getRequestDispatcher("registrazione.jsp").forward(request, response);
	        return;
	    }

	    // Confronto corretto tra byte[] con Arrays.equals()
	    if (password == null || !Arrays.equals(password, retypePassword)) {
	    	// svuoto le password
	    	Arrays.fill(password, (byte)0);
	    	Arrays.fill(retypePassword, (byte)0);
	    	request.setAttribute("msgError", "Le password non coincidono o sono vuote.");
	    	request.getRequestDispatcher("registrazione.jsp").forward(request, response);
	        return;
	    }
	    
	    // INIZIO [Controllo file]
        Part filePart = request.getPart("profile_pic");
        
        if (filePart == null || filePart.getSize() == 0) {
        	// svuoto le password
	    	Arrays.fill(password, (byte)0);
	    	Arrays.fill(retypePassword, (byte)0);
	    	request.setAttribute("msgError", "Foto profilo mancante.");
	    	request.getRequestDispatcher("registrazione.jsp").forward(request, response);
            return;
        }
        
        String uploadDir = AppProperties.getConfigProperty("upload_dir");
        
        // Assicuriamoci che la cartella esista, altrimenti viene creata
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        String originalFileName = filePart.getSubmittedFileName();
        
        // Usiamo il path caricato dal config.ini
        File targetFile = new File(uploadDir, originalFileName);
        
        try (InputStream initialStream = filePart.getInputStream()) {
            Files.copy(initialStream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        
		long lastModifiedTimeAtCheck = targetFile.lastModified();
        
        String mimeType = "";
        try (InputStream content = new FileInputStream(targetFile)) {
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            Parser parser = new AutoDetectParser();
            
            parser.parse(content, handler, metadata, new ParseContext());
            mimeType = metadata.get(Metadata.CONTENT_TYPE);
            
            if(mimeType != null && mimeType.contains(";")) {
            	mimeType = mimeType.split(";")[0];
            }
        } catch (Exception e) {
            targetFile.delete();
            e.printStackTrace();
            request.setAttribute("msgError", "Errore nell'analisi del file.");
            request.getRequestDispatcher("registrazione.jsp").forward(request, response);
            return;
        }

        if (mimeType == null || !mimeType.startsWith("image/")) {
            targetFile.delete();
            request.setAttribute("msgError", "Errore: Formato file non consentito (solo immagini).");
            request.getRequestDispatcher("registrazione.jsp").forward(request, response);
            return;
        }
        // FINE [Controllo file]

        // INIZIO [Hash password]
        MessageDigest msgDigest;
		try {
			msgDigest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			request.setAttribute("msgError", "Errore di sistema nella crittografia.");
			request.getRequestDispatcher("registrazione.jsp").forward(request, response);
            return;
		}
		byte[] hashedPassword = msgDigest.digest(password);
        // FINE [Hash password]
		
		// INIZIO [Controllo TOCTOU]
		long currentModifiedTime = targetFile.lastModified();
        if (currentModifiedTime != lastModifiedTimeAtCheck) {
            // Il file è stato manomesso da un processo esterno tra il Check e l'Use!
            targetFile.delete(); // Cancelliamo il file compromesso
            
            // Programmazione difensiva: svuotiamo le password dalla memoria
            if (password != null) Arrays.fill(password, (byte)0);
            if (retypePassword != null) Arrays.fill(retypePassword, (byte)0);
            if (hashedPassword != null) Arrays.fill(hashedPassword, (byte)0);
            
            request.setAttribute("msgError", "Violazione di sicurezza: Il file è stato alterato durante l'elaborazione (TOCTOU).");
            request.getRequestDispatcher("registrazione.jsp").forward(request, response);
            return;
        }
		// FINE [Controllo TOCTOU]

        RegistrazioneDAO dao = new RegistrazioneDAO();
        boolean isRegistered = dao.registerUser(email, hashedPassword, originalFileName);
        
        if (isRegistered) {
        	request.setAttribute("msgSuccess", "Registrazione completata con successo! Ora puoi accedere.");
        	request.getRequestDispatcher("login.jsp").forward(request, response);
    	} else {
    		// elimino il file
            if (targetFile != null && targetFile.exists()) {
                targetFile.delete();
            }
            // svuoto le password
	    	Arrays.fill(password, (byte)0);
	    	Arrays.fill(retypePassword, (byte)0);
	    	Arrays.fill(hashedPassword, (byte)0);

            request.setAttribute("msgError", "Registrazione fallita. Riprovare.");
            request.getRequestDispatcher("registrazione.jsp").forward(request, response);
        }
	}

}
