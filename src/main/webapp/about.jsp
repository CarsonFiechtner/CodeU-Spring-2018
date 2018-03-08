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
  <title>CodeU Chat App</title>
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
    <% } %>
    <a href="/about.jsp">About</a>
  </nav>

  <div id="container">
    <div
      style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">

      <h1>About the CodeU Chat App</h1>
      <p>
        This is an example chat application designed to be a starting point
        for your CodeU project team work. Here's some stuff to think about:
      </p>

      <ul>
        <li><strong>Algorithms and data structures:</strong> We've made the app
            and the code as simple as possible. You will have to extend the
            existing data structures to support your enhancements to the app,
            and also make changes for performance and scalability as your app
            increases in complexity.</li>
        <li><strong>Look and feel:</strong> The focus of CodeU is on the Java
          side of things, but if you're particularly interested you might use
          HTML, CSS, and JavaScript to make the chat app prettier.</li>
        <li><strong>Customization:</strong> Think about a group you care about.
          What needs do they have? How could you help? Think about technical
          requirements, privacy concerns, and accessibility and
          internationalization.</li>
      </ul>

      <p>
        This is your code now. Get familiar with it and get comfortable
        working with your team to plan and make changes. Start by updating the
        homepage and this about page to tell your users more about your team.
        This page should also be used to describe the features and improvements
        you've added.
      </p>
    </div>
  </div>
  
  <div id="container">
    <div 
         style="width:75%; margin-left:auto; margin-right:auto; margin-top: 50px;">
      <h1>About CodeU Team 34</h1>
      <p>This is Team 34. ...</p>

      <h2>Introduction of Members</h2>
      <ul>
      	<li><strong>Volodya Shtenovych -PA: </strong>
      		Introduction about Volodya...</li>

      	<li><strong>Jiaxin Du:</strong>
      		I am from China and a student studying Computer Science at University of 
          California, Irvine. I love traveling and swimming. </li>

      	<li><strong>AJ Phillips: </strong>
      		I am from Las Cruces, New Mexico. I am 19 and a student at New Mexico 
          State University, double majoring in electrical engineering and computer 
          science. In my free time, I like to compete in triathlons and play music.
        </li>

      	<li><strong>Carson Fiechtner:</strong>
      		I am a software engineering major at Montana Tech in Butte, MT.</li>

      	<li><strong>Casey Chien:</strong>
      		I am from California. I am a third year at UC Santa Cruz majoring in 
          computer science and minoring in linguistics. </li>
      </ul>
  	</div>
  </div>
  
</body>
</html>
