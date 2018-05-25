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
<%@ page language="java" contentType="text/html; charset=UTF-8"  
    pageEncoding="UTF-8"%> 
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">  

<html>
<head>
  <title>CodeU Chat App</title>
  <link rel="stylesheet" href="/css/main.css">
</head>
<body>

  <nav>
    <a id="navTitle" href="/">CodeU Chat App</a>
    <a href="/conversations">Conversations</a>
    <% if(request.getSession().getAttribute("user") != null){ %>
      <a>Hello <%= request.getSession().getAttribute("user") %>!</a>
      <a href="/profile/<%= request.getSession().getAttribute("user") %>"><%= request.getSession().getAttribute("user") %>'s Profile</a>   
    <% } else{ %>
      <a href="/login">Login</a>
      <a href="/register">Register</a>
    <% } %>
    <a href="/about.jsp">About</a>
    <a href="/testdata">Admin Page</a>

<!--    <% if(request.getSession().getAttribute("user") != null && (request.getSession().getAttribute("user").toString().equals("CarsonFiechtner") || 
	request.getSession().getAttribute("user").toString().equals("AJPhillips") || request.getSession().getAttribute("user").toString().equals("CaseyChien") ||
        request.getSession().getAttribute("user").toString().equals("JiaxinDu") || request.getSession().getAttribute("user").toString().equals("VolodyaShtenovych"))){ %>
    <% } %> -->
  </nav>

  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>Presented by The Hippopotami</h1>

      <ul>
        <li><a href="/login">Login</a> to get started.</li>
        <li>Go to the <a href="/conversations">conversations</a> page to
            create or join a conversation.</li>
        <li>View the <a href="/about.jsp">about</a> page to learn more about the
            project.</li>
        <li>Check out the <a href="/testdata">admin page</a> to learn about site statistics and test out our conversations by loading your choice of test data!</li>
        <li>Don't forget to take a look at our user profile pages and create your own!</li>
        <% if(request.getSession().getAttribute("user") != null){ %>
        <li>Tired of chatting? <a href="/delete">Delete your account</a></li>
        <% } %>
      </ul>
    </div>
  </div>
</body>
</html>
