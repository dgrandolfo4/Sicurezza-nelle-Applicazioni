<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>SnA - Visualizza Proposta</title>
	</head>
	<body>
		<div style="display: flex; justify-content: space-between">
			<div>
				<nav>
	            	<a href="benvenuto.jsp">Home</a> |
	            	<a href="ListaProposteServlet">Torna alla Lista</a> |
	            	<a href="carica_proposta.jsp">Carica Proposta Progettuale</a>
            	</nav>		
			</div>
			<div>
	            <a href="LogoutServlet" class="btn-logout">Logout</a>
	        </div>
		</div>
		
		<div>
            <h3>Visualizza la Proposta Progettuale</h3>
			<p>Contenuto della Proposta: <%= request.getAttribute("nomeProposta") %></p>  
			          
            <div style="border: 1px solid #ccc; padding: 15px;">
            	<%= request.getAttribute("contenutoProposta") %>
		    </div>
        </div>
	</body>
</html>