<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>SnA - Registrazione</title>
</head>
<body>
	<h2>Registrazione Nuovo Utente</h2>
	
	<% 
	    String errore = (String) request.getAttribute("msgError");
	    if (errore != null) { 
	%>
	    <div style="margin-bottom: 10px; color: red; font-weight: bold;">
	        <%= errore %>
	    </div>
	<% } %>

	<form method="post" action="RegistrazioneServlet" enctype="multipart/form-data">
	    <table>
	        <tr>
	        	<td>Email:</td>
	        	<td><input type="email" id="email" name="email" required></td>
        	</tr>
	        <tr>
	        	<td>Password:</td>
	        	<td><input type="password" id="password" name="password" required></td>
        	</tr>
        	<tr>
	        	<td>Conferma Password:</td>
	        	<td><input type="password" id="retype_password" name="retype_password" required></td>
        	</tr>
        	<tr>
	        	<td>Foto Profilo (.png, .jpeg):</td>
	        	<td><input type="file" id="profile_pic" name="profile_pic" accept=".png, .jpeg, .jpg" size="50"></td>
        	</tr>
	    </table>
	    <button style="margin-top: 20px" type="submit">Registrati</button>
	</form>
</body>
</html>