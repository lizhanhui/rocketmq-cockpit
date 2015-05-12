<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <jsp:include page="include/html-title.jsp">
        <jsp:param name="pageTitle" value="Cockpit Home" />
    </jsp:include>
</head>
<body>
    <jsp:include page="include/header.jsp"></jsp:include>
    <%
        String msg = "";
        Object errSMSG =  session.getAttribute("ACCESS_DENIED_MSG");
        if (null != errSMSG){
            msg = errSMSG.toString();
        }
    %>
    <span style="color:red"><%=msg%></span>

    <div class="container-fluid text-center">
        <h1>Welcome to use <strong>Cockpit</strong> :) </h1>
    </div>
</body>
</html>
