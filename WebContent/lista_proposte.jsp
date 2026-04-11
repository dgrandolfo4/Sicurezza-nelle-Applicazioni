<%
    if (request.getAttribute("listaProposte") == null) {
        response.sendRedirect("ListaProposteServlet");
        return; // Blocca il rendering della pagina vuota
    }
%>

<%@ page import="java.util.List" %>
<%@ page import="uniba.sna.model.Proposta" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>SnA - Visualizza Proposte</title>
	</head>
	<body>
		<div style="display: flex; justify-content: space-between">
			<div>
				<nav>
	            	<a href="benvenuto.jsp">Home</a> |
	            	<a href="carica_proposta.jsp">Carica Proposta Progettuale</a>
            	</nav>		
			</div>
			<div>
	            <a href="LogoutServlet" class="btn-logout">Logout</a>
	        </div>
		</div>
		<div>
            <h3>Visualizza tutte le Proposte Progettuali Caricate</h3>
            <p>La seguente tabella contiene le proposte progettuali caricate dagli utenti.</p>

            <table border="1" style="width:100%; margin-top:20px;">
	            <tr style="background-color: #f2f2f2;">
	                <th>Nome File Caricato</th>
	                <th>Data Caricamento</th>
	                <th>Autore</th>
	                <th>Azioni</th>
	            </tr>
            
	            <% 
	            @SuppressWarnings("unchecked")
	            List<Proposta> lista = (List<Proposta>) request.getAttribute("listaProposte");
	            if (lista != null && !lista.isEmpty()) {
	                for (Proposta p : lista) { 
	            %>
		                <tr>
		                    <td><%= p.getNomeFile() %></td>
		                    <td><%= p.getAutoreEmail() %></td>
		                    <td><%= p.getDataUpload() %></td>
		                    <td style="text-align:center;">
		                        <a href="VisualizzaPropostaServlet?fileId=<%= p.getId() %>">Visualizza</a>
		                    </td>
		                </tr>
	            <%  }
	            } else { %>
	                <tr>
	                	<td colspan="4" style="text-align:center;">Nessuna proposta caricata al momento.</td>
	               	</tr>
	            <% } %>
	        </table>
        </div>
	</body>
</html>