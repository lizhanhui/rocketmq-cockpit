<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <!-- The above 3 meta tags *must* come first in the head; any other head content must come *after* these tags -->

    <title>
        <c:choose>
            <c:when test="${not empty param.pageTitle}"><c:out value="${param.pageTitle}" /></c:when>
            <c:otherwise>Welcome to use Cockpit</c:otherwise>
        </c:choose>
    </title>

    <%@include file="base-path.jsp"%>
    <base href="<%=basePath%>">
    <link rel="shortcut icon" href="favicon.ico" />
    <jsp:include page="html-static-resources.jsp" />