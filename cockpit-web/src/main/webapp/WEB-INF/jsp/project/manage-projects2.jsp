<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
    <head>

        <jsp:include page="../include/html-title.jsp">
            <jsp:param name="pageTitle" value="Create New Project"/>
        </jsp:include>
        <script src="js/project-manage2.js" type="application/javascript"></script>
        <script src="js/onloading.js" type="application/javascript"></script>
        <script type="text/javascript" src="js/highcharts/highcharts.js"></script>
        <script type="text/javascript" src="js/highcharts/modules/exporting.js"></script>
    </head>
    <body>
        <jsp:include page="../include/header.jsp"></jsp:include>
        <div class="container-fluid">
            <div class="row">
                <div class="col-md-8 col-md-offset-2 text-center">
                <h1>Project</h1>
                </div>
            </div>
            <div id="divPro"></div>
<br />
            <div class="">
                <table width="100%" class="table">
                    <tr>
                        <td width="20%">
                            <table class="table table-hover table-bordered">
                                <thead>
                                    <tr>
                                        <td>Consumer Group</td>
                                        <td>Operation</td>
                                    </tr>
                                </thead>
                                <tbody class="table-striped cTable-content">
                                </tbody>
                            </table>
                        </td>
                        <td>
                            <div id="cContainer" style="min-width:300px;height:400px"></div>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <table class="table table-hover table-bordered">
                                <thead>
                                    <tr>
                                        <td>Topic</td>
                                        <td>Operation</td>
                                    </tr>
                                </thead>
                                <tbody class="table-striped tTable-content">
                                </tbody>
                            </table>
                        </td>
                        <td>
                            <div id="tContainer" style="min-width:300px;height:400px"></div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </body>
</html>
