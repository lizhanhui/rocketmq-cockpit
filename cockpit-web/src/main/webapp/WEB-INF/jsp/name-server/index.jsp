<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/html-title.jsp">
        <jsp:param name="pageTitle" value="Name Server Management" />
    </jsp:include>
    <title>Name Server Management</title>
    <script src="js/name-server.js" type="application/javascript"></script>
    <script src="js/onloading.js" type="application/javascript"></script>
</head>
<body>
    <jsp:include page="../include/header.jsp"></jsp:include>
    <div class="container-fluid">
        <div class="row">
            <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
                <table class="table table-condense table-hover table-bordered">
                    <thead>
                        <tr>
                            <td>Name Server Address</td>
                            <td>Date</td>
                            <td>Operation</td>
                        </tr>
                    </thead>
                    <tbody class="table-striped table-content">
                    </tbody>
                </table>

                <div class="clear-both"></div>

            </div>
            <div class="col-xs-7 col-xs-offset-2">
                <input type="text" class="form-control newNameServer" placeholder="Add New Name Server">
            </div>
            <div class="col-xs-2">
                <button type="submit" class="btn btn-primary addNameServer">Add</button>
            </div>
        </div>
    </div>
</body>
</html>
