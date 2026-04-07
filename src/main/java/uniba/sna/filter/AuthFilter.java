package uniba.sna.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import uniba.sna.utils.CookieHelper;

@WebFilter("/*")
public class AuthFilter implements Filter {

    public void init(FilterConfig fConfig) throws ServletException {
        // Inizializzazione del filtro
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String uri = req.getRequestURI();
        
        // ESCLUSIONE DELLE PAGINE PUBBLICHE
        if (uri.equals(req.getContextPath() + "/")
        		|| uri.endsWith("login.jsp") || uri.endsWith("LoginServlet")
        		|| uri.endsWith("registrazione.jsp") || uri.endsWith("RegistrazioneServlet")) {
            chain.doFilter(request, response);
            return;
        }

        // CONTROLLO: L'utente ha già una sessione attiva?
	    HttpSession session = req.getSession(false);
	    if (session != null && session.getAttribute("email") != null) {
            chain.doFilter(request, response);
	        return;
	    }

	    // CONTROLLO: L'utente ha il Cookie "Ricordami"?
	    Cookie[] cookies = req.getCookies();
	    if (cookies != null) {
	        for (Cookie c : cookies) {
	            if ("rememberme".equals(c.getName())) {
	                try {
	                    // Decifriamo il cookie con AES
	                    String decryptedEmail = CookieHelper.decrypt(c.getValue());
	                    
	                    if (decryptedEmail != null) {
	                    	HttpSession oldSession = req.getSession(false);
	        				if (oldSession != null) {
	        					oldSession.invalidate();
	        				}
	        				// Creiamo una nuova sessione
	        				HttpSession newSession = req.getSession(true);
	        				// Impostiamo un timeout di inattività (es. 15 minuti)
	        				newSession.setMaxInactiveInterval(60 * 15);
	        				// Salviamo l'utente nella sessione
	        				newSession.setAttribute("email", decryptedEmail);
	                        
	        				chain.doFilter(request, response);
	        				return;
	                    }
	                } catch (Exception e) {
	                    // Distruggiamo il cookie
	                    c.setMaxAge(0);
	                    c.setPath(req.getContextPath());
	                    res.addCookie(c);
	                }
	            }
	        }
	    }

        req.setAttribute("msgError", "Sessione scaduta o accesso non autorizzato. Effettua il login.");
        req.getRequestDispatcher("/login.jsp").forward(req, res);
    }

    public void destroy() {
        // Pulizia alla chiusura (puoi lasciarlo vuoto)
    }
}