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
				<h1>Benvenuto!</h1>
				<h3>Email: ${email}</h3>			
			</div>
			<div>
	            <a href="LogoutServlet" class="btn-logout">Logout</a>
	        </div>
		</div>
	</body>
</html>