package uniba.sna.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import net.jcip.annotations.ThreadSafe;
import uniba.sna.dao.PropostaDAO;
import uniba.sna.model.File;
import uniba.sna.utils.AppProperties;
import uniba.sna.utils.FileHelper;

/**
 * Servlet implementation class VisualizzaPropostaServlet
 */
@ThreadSafe
@WebServlet("/VisualizzaPropostaServlet")
public class VisualizzaPropostaServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VisualizzaPropostaServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String idParam = request.getParameter("fileId");
        if (idParam == null || idParam.trim().isEmpty()) {
            return;
        }
		

        try {
        	PropostaDAO dao = new PropostaDAO();
        	int idProposta = Integer.parseInt(idParam);
        	File file = dao.getFileNamesById(idProposta);
        	
        	String uploadDirectory = AppProperties.getConfigProperty("upload_dir");
        	Path filePath = Paths.get(uploadDirectory, file.getNomeUnivoco());
        	
        	String contenutoGrezzo = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
            String contenutoSicuro = FileHelper.escapeHtml(contenutoGrezzo);
            
            request.setAttribute("contenutoProposta", contenutoSicuro);
            request.setAttribute("nomeProposta", file.getNomeOriginale());
            
            request.getRequestDispatcher("visualizza_proposta.jsp").forward(request, response);
            
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
