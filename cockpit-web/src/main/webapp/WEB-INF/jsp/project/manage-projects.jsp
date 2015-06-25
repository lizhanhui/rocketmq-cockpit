<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <jsp:include page="../include/html-title.jsp">
        <jsp:param name="pageTitle" value="Create New Project" />
    </jsp:include>
    <script src="js/project-manage.js" type="application/javascript"></script>
</head>
<body>
    <jsp:include page="../include/header.jsp"></jsp:include>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-8 col-md-offset-2 text-center">
            <h1>Project Catalog</h1>
        </div>
    </div>

    <div class="clear-both"></div>

    <div class="row">
        <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
            <table class="table table-hover table-bordered">
                <thead>
                    <tr>
                        <td style="display:none;">id</td>
                        <td>Project Name</td>
                        <td>Project desc</td>
                        <td>Operation</td>
                    </tr>
                </thead>
                <tbody class="table-striped table-content">
                </tbody>
            </table>

            <div class="clear-both"></div>
        </div>
    </div>
</div>
</body>
</html>
