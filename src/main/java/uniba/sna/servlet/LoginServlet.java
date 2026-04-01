package uniba.sna.servlet;
import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uniba.sna.dao.LoginDAO;
import uniba.sna.utils.CookieHelper;

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
		// CONTROLLO: L'utente ha già una sessione attiva?
	    HttpSession session = request.getSession(false); // false = non crearne una nuova se non esiste
	    if (session != null && session.getAttribute("email") != null) {
	        response.sendRedirect("benvenuto.jsp");
	        return;
	    }

	    // CONTROLLO: L'utente ha il Cookie "Ricordami"?
	    Cookie[] cookies = request.getCookies();
	    if (cookies != null) {
	        for (Cookie c : cookies) {
	            if ("rememberme".equals(c.getName())) {
	                try {
	                    // Decifriamo il cookie con AES
	                    String decryptedEmail = CookieHelper.decrypt(c.getValue());
	                    
	                    if (decryptedEmail != null) {
	                        // Il cookie è autentico! Ricreiamo la sessione e lo facciamo entrare
	                        session.setAttribute("email", decryptedEmail);
	                        session.setMaxInactiveInterval(60 * 15); // Rimettiamo il timeout di sicurezza
	                        
	                        response.sendRedirect("benvenuto.jsp");
	                        return; // Ferma il caricamento della pagina di login
	                    }
	                } catch (Exception e) {
	                    // Distruggiamo il cookie
	                    c.setMaxAge(0);
	                    c.setPath(request.getContextPath());
	                    response.addCookie(c);
	                }
	            }
	        }
	    }
	    
	    request.getRequestDispatcher("login.jsp").forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
		String email = request.getParameter("email");
		// Memorizziamo la password in un array di byte per garantire
		// maggiore sicurezza rispetto a memorizzarla in una stringa.
		byte[] password = request.getParameter("password").getBytes();
		
		try {
			LoginDAO dao = new LoginDAO();
			if(dao.isUserValid(email, password)) {
				// INIZIO [Gestione Sessione]
				
				// Invalidiamo un'eventuale sessione preesistente creata prima
				HttpSession oldSession = request.getSession(false);
				if (oldSession != null) {
					oldSession.invalidate();
				}
				// Creiamo una nuova sessione
				HttpSession session = request.getSession(true);
				// Impostiamo un timeout di inattività (es. 15 minuti)
				session.setMaxInactiveInterval(60 * 15);
				// Salviamo l'utente nella sessione
				session.setAttribute("email", email);
				
				// FINE [Gestione Sessione]
				
				// INIZIO [Gestione Cookie]

				boolean rememberMe = request.getParameter("rememberMe") != null;
				if (rememberMe) {
					String encryptedEmail = CookieHelper.encrypt(email);
					
					Cookie loginCookie = new Cookie("rememberme", encryptedEmail);
					loginCookie.setMaxAge(60 * 60 * 24 * 30); // Scadenza: 30 giorni
					loginCookie.setPath(request.getContextPath()); // Limita il cookie a questa webapp
					
					// Flag di sicurezza obbligatori
					loginCookie.setHttpOnly(true); // Previene furto tramite JavaScript (XSS)
					loginCookie.setSecure(true);   // Forza l'invio solo su HTTPS (no intercettazioni)
					
					response.addCookie(loginCookie);
				}
				
				// FINE [Gestione Cookie]

				response.sendRedirect("benvenuto.jsp");
			} else {
				response.sendRedirect("errore.jsp");
			}
		} catch(Exception e) {
			e.printStackTrace();
			response.sendRedirect("errore.jsp");
		} finally {
			if (password != null) {
				Arrays.fill(password, (byte) 0);
			}
		}
	}
}
