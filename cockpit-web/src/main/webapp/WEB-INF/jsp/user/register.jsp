<%@ page contentType="text/html;charset=UTF-8" language="java" isELIgnored="false" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <jsp:include page="../include/html-title.jsp">
        <jsp:param name="pageTitle" value="User Registration" />
    </jsp:include>
    <script type="text/javascript" src="js/register.js"></script>
</head>
<body>
    <div class="container-fluid">
        <table>
            <tr>
               <td>Team</td>
               <td>
                   <select id="team">
                       <c:if test="${not empty teamList}">
                           <c:forEach items="${teamList}" var="team">
                               <option value="${team.id}">${team.name}</option>
                           </c:forEach>
                       </c:if>
                   </select>
               </td>
            </tr>

            <tr>
                <td>User Name</td>
                <td><input type="text" id="userName"></td>
            </tr>

            <tr>
                <td>Password</td>
                <td><input type="password" id="password"></td>
            </tr>

            <tr>
                <td>Confirm Password</td>
                <td>
                    <input type="password" id="confirmPassword">
                </td>
            </tr>

            <tr>
                <td>Email</td>
                <td>
                    <input type="text" id="email">
                </td>
            </tr>

            <tr>
                <td colspan="2" style="text-align: center;">
                    <input type="button" value="Register" id="registerButton">
                </td>
            </tr>
        </table>
        Already Registered? <a href="cockpit/login">Login</a>
    </div>
</body>
</html>
