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
    <h1>Load Test Data</h1>
    <!-- <p>This will load a number of users, conversations, and messages for testing -->
        <!-- purposes.</p> -->
    <!-- <form action="/testdata" method="POST"> -->
      <!-- <button type="submit" value="confirm" name="confirm">Confirm</button> -->
      <!-- <button type="submit" value="cancel" name="cancel">Do Nothing</button> -->
    <!-- </form> -->
    <p>Select your preferences in the dropdown menus below.</p>
    <form action="/testdata" method="POST">
      <p>From source:  
      <select name="source">
        <option value="Romeo and Juliet">Romeo and Juliet</option>
        <option value="Hamilton">Hamilton</option>
      </select></p>
      <p>Number of Messages:
      <select name="numMess">
        <option value="10">10</option>
        <option value="20">20</option>
      </select></p>
      <p>Number of Users:
      <select name="numUsers">
        <option value="1">1</option>
        <option value="2">2</option>
        <option value="3">3</option>
        <option value="4">4</option>
        <option value="5">5</option>
      </select></p>
      <button type="submit" value="confirm" name="confirm">Load</button>
    </form>
  </div>
</body>
</html>
