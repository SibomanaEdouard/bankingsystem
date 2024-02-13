<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>BankW</title>
</head>
<body>
<h1>Bank with us</h1>
<form action="register" method="post">
    <label for="username">Username</label><br/>
    <input type="text" name="username" id="username" required/><br/>
    <label for="age">Age</label><br/>
    <input type="text" name="age" id="age" required/><br/>
    <label for="email">Email</label><br/>
    <input type="email" name="email" id="email" required/><br/>
    <label for="phone">Phone</label><br/>
    <input type="tel" name="phone" id="phone" required/><br/>
    <label for="password">Password</label><br/>
    <input type="password" name="password" id="password" required/><br/>
    <button type="submit" name="signup">SIGNUP</button>
</form>
<h6>Already have account ? <a href="index.jsp">Login</a></h6>
</body>
</html>
