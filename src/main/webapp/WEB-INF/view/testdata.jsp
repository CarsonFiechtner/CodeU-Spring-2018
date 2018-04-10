
<%--
  Copyright 2017 Google Inc.

  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
--%>

<%@ page import="codeu.model.store.basic.UserStore" %>
<%@ page import="codeu.model.store.basic.ConversationStore" %>
<%@ page import="codeu.model.store.basic.MessageStore" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>

<head>
  <title>Load Test Data</title>
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

  <div id="container">
     <h1>Statistics</h1>
    <p>This provides an overview of data relevant to our chat app.</p>
     <% int numUsers = UserStore.getInstance().getNumUsers();
	int numConversations = ConversationStore.getInstance().getNumConversations();
	int numMessages = MessageStore.getInstance().getNumMessages();
	String newUser = UserStore.getInstance().getNewestUser();
	String oldUser = UserStore.getInstance().getOldestUser();
	String newMessage = MessageStore.getInstance().getNewestMessage();
     %>
	<a>Users: <%= numUsers %></a></br>
	<a>Conversations: <%= numConversations %></a></br>
	<a>Messages: <%= numMessages %></a></br>
	<a>Oldest User: <%= oldUser %></a></br>
	<a>Newest User: <%= newUser %></a></br>
	<a>Most Recent Message Sent: <%= newMessage %></a></br>

  </div>

  <div id="messageChart" style="display:block; margin:0 auto; width:450px; height:250px"></div>

    <script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
    <script type="text/javascript">
      google.charts.load('current', {'packages':['line']});
      google.charts.setOnLoadCallback(drawChart);

      function drawChart() {

      var data = new google.visualization.DataTable();
      data.addColumn('number', 'Day');
      data.addColumn('number', 'Messages');

      <% Integer [] activeUsers = MessageStore.getInstance().activeUserInfo(); %>

      <% for(int i = 0; i < 30; i++){ %>
	data.addRow([<%= i %>, <%= activeUsers[i] %>]);
      <% } %>
	
      var options = {
        chart: {
          title: 'Number of Messages Sent in the Last 30 Days',
        },
        width: 450,
        height: 250,
	vAxis: {
          title: 'Messages'
        },
	legend: {position: 'none'}
      };

      var chart = new google.charts.Line(document.getElementById('messageChart'));

      chart.draw(data, google.charts.Line.convertOptions(options));
    }
  </script>

  <div id="container">
	<h1>Load Test Data</h1>
    <p>This will load a number of users, conversations, and messages for testing
        purposes.</p>
    <form action="/testdata" method="POST">
      <button type="submit" value="confirm" name="confirm">Confirm</button>
      <button type="submit" value="cancel" name="cancel">Do Nothing</button>
    </form>
  </div>
</body>
</html>
