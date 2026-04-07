<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>SnA - Carica Proposta</title>
	</head>
	<body>
		<div style="display: flex; justify-content: space-between">
			<div>
				<nav>
	            	<a href="benvenuto.jsp">Home</a> |
	            	<a href="ListaProposteServlet">Visualizza Proposte Progettuali Caricate</a>
            	</nav>		
			</div>
			<div>
	            <a href="LogoutServlet" class="btn-logout">Logout</a>
	        </div>
		</div>
		<div>
            <h3>Carica la tua Proposta Progettuale</h3>
            <p>Seleziona un file di testo contenente la tua proposta.</p>
            
            <% if (request.getAttribute("msgErrorUpload") != null) { %>
                <div style="color: red; font-weight: bold;">
                    <%= request.getAttribute("msgErrorUpload") %>
                </div>
            <% } %>

            <form method="post" action="UploadPropostaServlet" enctype="multipart/form-data">
            	<table>
			        <tr>
			        	<td>Proposta Progettuale (.txt)</td>
			        	<td><input type="file" id="proposta_file" name="proposta_file" accept=".txt" required></td>
		        	</tr>
			    </table>
	    	    <button style="margin-top: 20px" type="submit">Invia</button>
            </form>
        </div>
	</body>
</html>