<%@ page import="java.util.List" %>
<%@ page import="codeu.model.data.Message" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<!DOCTYPE html>
</html>
  <head>
  	<title>Profile</title>
  	<link rel="stylesheet" href="/css/main.css">
  </head>
  <body>
    <nav>
    <a id="navTitle" href="/">CodeU Chat App</a>
    <a href="/conversations">Conversations</a>
    <% if(request.getSession().getAttribute("user") != null){ %>
      <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
    <% } else{ %>
      <a href="/login">Login</a>
      <a href="/register">Register</a>
    <% } %>
    <a href="/about.jsp">About</a>
  </nav>
  <div id = "container">
  	<h1><% if(request.getSession().getAttribute("user") != null){ %>
      <a><%= request.getSession().getAttribute("user") %>'s Profile Page</a></h1>
      <hr>
      <a>
      	<h3>About <%= request.getSession().getAttribute("user") %></h3>
      	<p style="font-size:16px"><%= request.getAttribute("aboutMe")%></p>
      </a>
      <h3>Edit your About Me (only you can see this)<h3>
      <form action="/profile" method="POST">
        <label for="aboutMe"></label>
        <textarea name="aboutMe" style="font-size:16px" placeholder="Write something about yourself" cols="72" rows="6"></textarea>
        <hr style="height:0px; visibility:hidden;" />
        <button type="submit">Submit</button>
      </form>
      <hr>
      <a><h3><%= request.getSession().getAttribute("user") %>'s Sent Messages</h3></a>
      <% List<Message> messages = (List<Message>) request.getAttribute("authorMessages");
      if(messages == null | messages.size() == 0){ %>
        <p>You have not sent any messages.</p>
      <% } else { %>
        <% for(Message message: messages) { 
              Instant time = message.getCreationTime();
              DateTimeFormatter timeFormat = DataTimeFormatter.ofPattern("EEE mm/dd/yyyy HH:mm:ss").withZone(ZoneId.systemDefault());
              String timeS = timeFormat.format(time);
        %>
          <p><li><b><%= timeS %>:</b> <%= message.getContent()%></li></p>
        <% } %>
      <% } %>  
    <% } else{ %>
      <h1>You must login to view this page.</h1>
    <% } %>
  </div>
  </body>
</html>