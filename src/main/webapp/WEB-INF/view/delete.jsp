<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
 <title>Register</title>
 <link rel="stylesheet" href="/css/main.css">
 <style>
   label {
     display: inline-block;
     width: 100px;
   }
 </style>

 <script>
function myFunction() {
    document.getElementById("confirm2").style.display="block";
}
function myFunction2() {
    document.getElementById("confirm3").style.display="block";
}
</script>
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
   <% } %>

 </nav>
 <div id="container">
	<h1>Delete your account</h1>
    <form action="/delete" method="POST">
      <div id="confirm1">
        <p>Please confirm the removal of your account</p>
	<input type="radio" name="confirm1" id="confirm1_true" onchange="myFunction()" value="confirm1" />
	<label for="confirm1_true">Yes, delete my account</label>
	<input type="radio" name="confirm1" id="confirm1_false" value="cancel1" checked/>
	<label for="confirm1_false">Nevermind, I love your app!</label>
      </div>
      <div id="confirm2" style="display: none">
        <p>But are you sure?</p>
	<input type="radio" name="confirm2" id="confirm2_true" onchange="myFunction2()" value="confirm2" />
	<label for="confirm2_true">Yeah, I'm sure!</label>
	<input type="radio" name="confirm2" id="confirm2_false" value="cancel2" checked/>
	<label for="confirm2_false">You're right, this app is pretty great!</label>
      </div>
      <div id="confirm3" style="display: none">
        <p>Final answer?</p>
	<input type="radio" name="confirm3" id="confirm3_true" value="confirm3" />
	<label for="confirm3_true">YES!</label>
	<input type="radio" name="confirm3" id="confirm3_false" value="cancel3" checked/>
	<label for="confirm3_true">NO!</label>
      </div>
      <button type="submit" value="confirm" name="confirm">Confirm</button>
      <button type="submit" value="cancel" name="cancel">Cancel</button>
    </form>
  </div>
</body>
</html>
