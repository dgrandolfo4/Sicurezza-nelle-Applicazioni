package uniba.sna.servlet;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import uniba.sna.dao.LoginDAO;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public LoginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String nomeUtente = request.getParameter("username");
		// Memorizziamo la password in un array di byte per garantire
		// maggiore sicurezza rispetto a memorizzarla in una stringa.
		byte[] password = request.getParameter("pass").getBytes();
		try
		{
			if(LoginDAO.isUserValid(nomeUtente, password))
			{
				response.sendRedirect("benvenuto.jsp");
			}
			else
			{
				response.sendRedirect("errore.jsp");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
