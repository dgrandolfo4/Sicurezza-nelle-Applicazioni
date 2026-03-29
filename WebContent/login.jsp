<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SnA - Login</title>
</head>
<body>
	<h2>Login</h2>

	<% 
	    String successo = (String) request.getAttribute("msgSuccess");
	    if (successo != null) { 
	%>
	    <div style="color: green; font-weight: bold;">
	        <%= successo %>
	    </div>
	<% } %>
	
	<form method="post" action="LoginServlet">
	    <table>
	        <tr>
	        	<td>Username</td>
	        	<td><input type="text" name="username"></td>
        	</tr>
	        <tr>
	        	<td>Password</td>
	        	<td><input type="password" name="pass"></td>
        	</tr>
	        <tr>
	        	<td></td>
	        	<td><button type="submit">Accedi</button></td>
        	</tr>
	    </table>
	</form>
	
	<div style="margin-top: 20px;">
		<p>Non hai ancora un account? <a href="registrazione.jsp">Registrati qui</a></p>
	</div>
</body>
</html>