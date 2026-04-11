package uniba.sna.servlet;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import net.jcip.annotations.ThreadSafe;
import uniba.sna.dao.PropostaDAO;
import uniba.sna.utils.AppProperties;

/**
 * Servlet implementation class UploadPropostaServlet
 */
@ThreadSafe
@MultipartConfig
@WebServlet("/UploadPropostaServlet")
public class UploadPropostaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadPropostaServlet() {
        super();
        // TODO Auto-generated constructor stub
    }
    
    /**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		request.getRequestDispatcher("carica_proposta.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
        String emailUtente = (String) session.getAttribute("email");
        
		// INIZIO [Controllo file]
        Part filePart = request.getPart("proposta_file");
        
        if (filePart == null || filePart.getSize() == 0) {
	    	request.setAttribute("msgErrorUpload", "File non caricato.");
	    	request.getRequestDispatcher("carica_proposta.jsp").forward(request, response);
            return;
        }
        
        String uploadDir = AppProperties.getConfigProperty("upload_dir");
        
        // Assicuriamoci che la cartella esista, altrimenti viene creata
        File uploadDirectory = new File(uploadDir);
        if (!uploadDirectory.exists()) {
            uploadDirectory.mkdirs();
        }

        String originalFileName = filePart.getSubmittedFileName();
        
        // INIZIO [Creazione Nome Univoco per il File]
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String dateOggi = LocalDateTime.now().format(formatter);
	    String nomeSenzaEstensione = "";
	    String estensione = "";
	    
	    int lastDotIndex = originalFileName.lastIndexOf('.');
	    if (lastDotIndex > 0) {
	        nomeSenzaEstensione = originalFileName.substring(0, lastDotIndex);
	        estensione = originalFileName.substring(lastDotIndex);
	    } else {
	        nomeSenzaEstensione = originalFileName;
	    }
	    
	    String uniqueFileName = nomeSenzaEstensione + "_" + dateOggi + estensione;
        // FINE [Creazione Nome Univoco per il File]
        
        // Usiamo il path caricato dal config.ini
        File targetFile = new File(uploadDir, uniqueFileName);
        
        try (InputStream initialStream = filePart.getInputStream()) {
            Files.copy(initialStream, targetFile.toPath());
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
            request.setAttribute("msgErrorUpload", "Errore nell'analisi del file.");
            request.getRequestDispatcher("carica_proposta.jsp").forward(request, response);
            return;
        }

        if (mimeType == null || !mimeType.equals("text/plain")) {
            targetFile.delete();
            request.setAttribute("msgErrorUpload", "Errore: Il file non è un documento di testo valido.");
            request.getRequestDispatcher("carica_proposta.jsp").forward(request, response);
            return;
        }
        // FINE [Controllo file]
        
        // INIZIO [Controllo TOCTOU]
 		long currentModifiedTime = targetFile.lastModified();
         if (currentModifiedTime != lastModifiedTimeAtCheck) {
             targetFile.delete(); // Cancelliamo il file compromesso
             
             request.setAttribute("msgErrorUpload", "Violazione di sicurezza: Il file è stato alterato durante l'elaborazione (TOCTOU).");
             request.getRequestDispatcher("carica_proposta.jsp").forward(request, response);
             return;
         }
 		// FINE [Controllo TOCTOU]
        
         PropostaDAO dao = new PropostaDAO();
         
         // Passiamo sia l'email, sia il nome originale, sia il nome univoco appena generato
         boolean isSaved = dao.uploadProposta(emailUtente, originalFileName, uniqueFileName); 

         if (isSaved) {
             // In caso di successo, mandiamo a benvenuto.jsp con il messaggio verde
             request.setAttribute("msgSuccessHome", "Proposta caricata e salvata con successo!");
             request.getRequestDispatcher("benvenuto.jsp").forward(request, response);
         } else {
             // In caso di errore DB, cancelliamo il file e rimaniamo sulla pagina di caricamento
             targetFile.delete(); 
             request.setAttribute("msgErrorUpload", "Errore nel salvataggio sul database. Riprova.");
             request.getRequestDispatcher("carica_proposta.jsp").forward(request, response);
         }
	}
}
