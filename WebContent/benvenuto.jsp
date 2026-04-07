<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<title>SnA - Welcome Page</title>
	</head>
	<body>
		<div style="display: flex; justify-content: space-between">
			<div>
				<nav>
	            	<a href="ListaProposteServlet">Visualizza Proposte Progettuali Caricate</a> |
	            	<a href="carica_proposta.jsp">Carica Proposta Progettuale</a>
            	</nav>		
			</div>
			<div>
	            <a href="LogoutServlet" class="btn-logout">Logout</a>
	        </div>
		</div>
		<div>
			<h1>Benvenuto!</h1>
			<h3>Email: ${email}</h3>
			
			<% 
			    String successo = (String) request.getAttribute("msgSuccessHome");
			    if (successo != null) { 
			%>
			    <div style="color: green; font-weight: bold;">
			        <%= successo %>
			    </div>
			<% } %>
			
			<% 
			    String info = (String) request.getAttribute("msgInfoHome");
			    if (info != null) { 
			%>
			    <div style="color: blue; font-weight: bold;">
			        <%= info %>
			    </div>
			<% } %>
			
			<% 
			    String errore = (String) request.getAttribute("msgErrorHome");
			    if (errore != null) { 
			%>
			    <div style="color: red; font-weight: bold;">
			        <%= errore %>
			    </div>
			<% } %>
		</div>
	</body>
</html>