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
<script type="text/javascript"
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.3/jquery.min.js"></script>
<style type="text/css">

.Profile_img{
		    width: 100%;
		    background-color: #fff;
		    padding: 10px;
		}
	 	.Profile_img>div{
		    width: 78px;
		    height: 78px;
		    border: 1px solid #ddd;
		    margin-top: 10px;
		    line-height: 78px;
		    text-align: center;
		}
		#Profile_addImg>img{
		    width: 100%;
   		    height: 100%;
		}
		.msg{
		    font-size: 15px;
                    color: red;
                    padding-left: 5px;
		}
		.tip{
		    position: relative;
		    margin-top: 4px;
             margin-left: 4px;
		}
		.tip>#file_input{
		    position: absolute;
		    top: 0;
		    left: -120px;
		    z-index: 3;
		    opacity: 0;
		}

</style>
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

		    <% if(currentUser.getName().equals(request.getSession().getAttribute("user"))) { %>
			
		
				<form action="/profile" method="POST" enctype="multipart/form-data">
				<!-- <label>Upload your profile picture</label> <br /> 
				     <input id="fileupload" type="file" name="profilePic"
						accept=".jpg, .jpeg, .png"> <br /> 
						<b>Live Preview</b> 
						<br />
						
			
					<img id="myImg" style="width: 190px;" src="#" alt="your image" /> <br />
			 -->
				
					<div class="Profile_img">
				        <span>Your profile picture</span>
				        <div class="Profile_addImg" id="Profile_addImg">
				          <!-- <img id="myImg" style="width: 190px;" src="#" alt="your image" /> -->
				        <img id="myImg" src="/upload/<%= request.getSession().getAttribute("user") %>.png" alt="your image"  onError='this.src="http://hanslodge.com/data_images/326062.jpg"' />
				        	
				    	</div>
					</div>
					<span class="msg"></span>
					<div class="tip">Select your profile picture
						 <input id="fileupload" type="file" name="profilePic"
						accept=".jpg, .jpeg, .png">
					</div> 
				</form>
					 <br />
				<form action="/profile" method="POST">
					<label for="aboutMe"></label>
					<h3>
					Edit your About Me (only you can see this)
					</h3>
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
<script language="javascript" type="text/javascript">
	 $(function() {
		$(":file").change(function() {
			if (this.files && this.files[0]) {
				var reader = new FileReader();
				reader.onload = imageIsLoaded;
				reader.readAsDataURL(this.files[0]);
			}
		});
	});
	function imageIsLoaded(e) {
		$('#myImg').attr('src', e.target.result);
	}; 
	
	
</script>
</body>
</html>
