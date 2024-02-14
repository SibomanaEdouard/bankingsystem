<%--
  Created by IntelliJ IDEA.
  User: edouard
  Date: 2024-02-14
  Time: 21:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>BankW</title>
</head>
<body>
<h5>Keep your money in peace!</h5>

<form action="deposit" method="post">
    <label for="amount">Amount</label><br/>
    <input type= "text" name="amount" id="amount"><br/>
    <button type="submit" >Deposit</button>
</form>
</body>
</html>
