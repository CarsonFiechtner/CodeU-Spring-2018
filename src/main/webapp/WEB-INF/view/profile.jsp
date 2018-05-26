<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="codeu.model.data.User" %>
<%@ page import="java.util.List"%>
<%@ page import="java.time.Instant"%>
<%@ page import="java.util.Date"%>
<%@ page import="java.text.SimpleDateFormat"%>
<%@ page import="java.time.format.DateTimeFormatter"%>
<%
String error = (String) request.getAttribute("Error");
		  	  
%>

<%@ page language="java" contentType="text/html; charset=UTF-8"  
    pageEncoding="UTF-8"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">  

<html>
<head>

<title>Profile</title>
<link rel="stylesheet" href="/css/main.css">
<script type="text/javascript"
	src="//ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>

</head>
<body  ">
	<nav>
		<% User currentUser = UserStore.getInstance().getUser(request.getParameter("value"));
		   if(currentUser == null){
			currentUser = UserStore.getInstance().getUser((String) request.getSession().getAttribute("user"));
		   }  %>
		<a id="navTitle" href="/">CodeU Chat App</a> <a href="/conversations">Conversations</a>
		<% if(request.getSession().getAttribute("user") != null){ %>
		<a>Hello <%= request.getSession().getAttribute("user") %>!</a> 
		<a href = "/logout">Logout</a>
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
			<h3>About <%= currentUser.getName() %></h3>
			<!--<p style="font-size: 16px"><%= currentUser.getAboutMe() %></p>-->
			 <div class="msg">
			<% if(request.getAttribute("aboutMe")==null || (String)request.getAttribute("aboutMe")==""|| (String)request.getAttribute("aboutMe")==" "){ %>
				
			     <a><%= "Hello, I am "+currentUser.getName() %></a> 
			     <%} else { %>
			     	
			   <%= request.getAttribute("aboutMe")%>
			 <% }%>
			 </div>
			   			
			   
		</a>

		    <% if(currentUser.getName().equals(request.getSession().getAttribute("user"))) { %>
			<!-- action="/profile" -->
		        <form id="form1"  method="POST" enctype="multipart/form-data">
				<br />
					<label for="aboutMe"></label>
					<h3>
					Edit your About Me (only you can see this)
					</h3>
					<textarea name="aboutMe" style="font-size: 16px" id="aboutMe" 
						placeholder="Write something about yourself" cols="72" rows="6"></textarea>
					<hr style="height: 0px; visibility: hidden;" />
					<button type="submit" onclick="submitAction('/profile/')">Submit</button>
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
 <script language="javascript" type="text/javascript">
	function submitAction(url) {
		  $('form').attr('action', url+$('#aboutMe').val());
		  //alert(url+$('#aboutMe').val());
		  $('form').submit();
		};
	
</script>
</body>
</html>
