
<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="java.util.List"%>
<%@ page import="java.time.Instant"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"  
    pageEncoding="UTF-8"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">  

<html>
<head>
<title>Profile</title>
<link rel="stylesheet" href="/css/main.css">
</head>
<body>
	<nav>
		<% User currentUser = UserStore.getInstance().getUser(request.getParameter("value"));
		   if(currentUser == null){
			currentUser = UserStore.getInstance().getUser((String) request.getSession().getAttribute("user"));
		   }  %>
		<a id="navTitle" href="/">CodeU Chat App</a> <a href="/conversations">Conversations</a>
		<% if(request.getSession().getAttribute("user") != null){ %>
		<a>Hello <%= request.getSession().getAttribute("user") %>!
		</a>
		<a href="/profile/<%= request.getSession().getAttribute("user") %>"><%= request.getSession().getAttribute("user") %>'s Profile</a>  
		<% } else{ %>
		<a href="/login">Login</a> <a href="/register">Register</a>
		<% } %>
		<a href="/about.jsp">About</a>
		<a href="/testdata">Admin Page</a>

  </nav>
	<div id="container">
		<h1>
			<% if(currentUser != null){ %>
			<a><%= currentUser.getName() %>'s Profile
				Page</a>
		</h1>
		<hr>
		<a>
			<h3>
				About
				<%= currentUser.getName() %></h3>
			<p style="font-size: 16px"><%= currentUser.getAboutMe() %></p>
		</a>
		<h3>
		    <% if(currentUser.getName().equals(request.getSession().getAttribute("user"))) { %>
			Edit your About Me (only you can see this)
			<h3>
				<form action="/profile" method="POST">
					<label for="aboutMe"></label>
					<textarea name="aboutMe" style="font-size: 16px"
						placeholder="Write something about yourself" cols="72" rows="6"></textarea>
					<hr style="height: 0px; visibility: hidden;" />
					<button type="submit">Submit</button>
				</form>
				<hr>
		    <% } %>
				<a><h3><%= currentUser.getName() %>'s
						Recent Messages
					</h3></a>
				<% List<Message> messages = (List<Message>) request.getAttribute("authorMessages");
      if(messages == null | messages.size() == 0){ %>
				<p>No recent messages.</p>
				<% } else { %>
				<% int i = 0;
				   while(i < messages.size() && i < 10) { 
				       Date newTime = Date.from(messages.get(i).getCreationTime());
        			       SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        			       String messageTime = formatter.format(newTime); %>
				<p>
				<li><b><%= messageTime %>:</b> <%= messages.get(i).getContent()%></li>
				</p>
				<%     i++;
				   } %>
				<% } %>
				
				<% } else{ %>
				<h1>You must login to view this page.</h1>
				<% } %>
			
	</div>
</body>
</html>
