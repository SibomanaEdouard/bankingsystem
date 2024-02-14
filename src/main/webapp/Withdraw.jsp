<%--
  Created by IntelliJ IDEA.
  User: edouard
  Date: 2024-02-14
  Time: 21:40
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>BankW</title>
</head>
<body>
<h5>Withdraw your money from BankW !</h5>
<form action="withdraw" method="post">
    <label for="amount">Amount</label><br/>
    <input type= "text" name="amount" id="amount"><br/>
    <button type="submit" >Withdraw</button>
</form>
</body>
</html>
