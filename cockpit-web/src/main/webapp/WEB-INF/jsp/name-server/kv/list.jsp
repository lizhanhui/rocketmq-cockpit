<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../../include/html-title.jsp">
        <jsp:param name="pageTitle" value="Name Server Key-Value Management" />
    </jsp:include>
    <script src="js/name-server-kv.js" type="application/javascript"></script>
</head>
<body>
    <jsp:include page="../../include/header.jsp"></jsp:include>
    <div class="container-fluid">
        <div class="row">
            <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
                <table class="table table-condense table-hover table-bordered">
                    <thead>
                        <tr>
                            <td>Name Space</td>
                            <td>Key</td>
                            <td>Value</td>
                            <td>Status</td>
                            <td>Operation</td>
                        </tr>
                    </thead>
                    <tbody class="table-striped table-content">
                    </tbody>
                </table>
                <div class="clear-both"></div>
            </div>
        </div>

        <div class="clear-both"></div>

        <div class="row">
            <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
                <div class="col-xs-3">
                    <input type="text" class="form-control nameSpace" placeholder="Name Space">
                </div>

                <div class="col-xs-3">
                    <input type="text" class="form-control key" placeholder="Key">
                </div>

                <div class="col-xs-4">
                    <input type="text" class="form-control value" placeholder="Value">
                </div>

                <div class="col-xs-2">
                    <button type="submit" class="btn btn-primary addKV">Add</button>
                </div>
            </div>
        </div>
    </div>
</body>
</html>
