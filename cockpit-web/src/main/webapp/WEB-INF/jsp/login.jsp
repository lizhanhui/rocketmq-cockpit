<%@ page language="java" contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ include file="include/base-path.jsp" %>
<html>
	<head>
		<title>Login Page</title>
		<script src="../js/jquery.min.js" type="text/javascript"></script>
		<script src="../js/bootstrap.min.js" type="text/javascript"></script>
		<link href="../css/bootstrap.min.css" type="text/css" rel="stylesheet">
		<style>
		    table td {
		        padding: 10px;
		    }
		</style>
		<base href="<%=basePath%>">
		<script type="text/javascript">
			function check() {
				var username = document.getElementById("j_username").value;
				var password = document.getElementById("j_password").value;
				var kaptcha = document.getElementById("kaptcha").value;
				if (!username) {
					alert(" user name can not be null !");
					return false;
				}
				if (!password) {
					alert(" password can not be null !");
					return false;
				}
				if (!kaptcha) {
					alert(" kaptcha can not be null !");
					return false;
				}
				return true;
			};

			function loading() {
			    var bodyWidth = document.documentElement.clientWidth;
                var bodyHeight = Math.max(document.documentElement.clientHeight, document.body.scrollHeight);
                var lh = bodyHeight/7;
                document.getElementById("titles").style.top = lh + "px";
                document.getElementById("titles").align = "center";
			};
		</script>
	</head>
	<body onload='document.f.j_username.focus();loading();' style="background-color:#EEFfeE">
	<h3 id="titles" >Login with Username and Password</h3>

	<form name='f' action='j_spring_security_check' method='POST'>
    <%
        String msg = "";
        Object errMSG = session.getAttribute("errorMSG");
        Object errSMSG = session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION");
        if (null != errMSG) {
            msg = (String) errMSG;
        } else if (null != errSMSG) {
            msg = errSMSG.toString();
        }
    %>
    <table align="center" style="background-color:#ccffcc" border="2">
        <tr>
            <td colspan='2' style="padding:0px;"><span style="color:red"><%=msg%></span></td>
        </tr>
        <tr>
            <td><label for="j_username">User:</label></td>
            <td><input type='text' id="j_username" name='j_username' value=''></td>
        </tr>
        <tr>
            <td><label for="j_password">Password:</label></td>
            <td><input type='password' id="j_password" name='j_password'/></td>
        </tr>
        <tr>
            <td><label for="j_remember-me">Remember Me</label></td>
            <td><input type="checkbox" id="j_remember-me" name="remember-me"></td>
        </tr>
        <tr>
            <td>
                <label for="kaptcha">verification code：</label>
            </td>
            <td>
                <input name="kaptcha" type="text" id="kaptcha" maxlength="20" class="chknumber_input">
                <img src="cockpit/captcha-image" width="170" height="40" id="kaptchaImage" style="margin-bottom: -3px"  alt="Captcha">
                <script type="text/javascript">
                     document.getElementById("kaptchaImage").onclick = function() {
                        document.getElementById("kaptchaImage").src = "cockpit/captcha-image?"  + Math.floor(Math.random() * 100);
                    };
                </script>

            </td>
        </tr>
        <tr>
            <td colspan='2' align="right"><input name="submit" type="submit" value="Login" onclick="return check()" /></td>
        </tr>
    </table>
</form>
    <div align="center">
    Do not have an account yet? <a href="cockpit/register">Register</a>
    <div>
</body>
</html>