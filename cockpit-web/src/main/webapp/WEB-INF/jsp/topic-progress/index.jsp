<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <jsp:include page="../include/html-title.jsp">
        <jsp:param name="pageTitle" value="Topic Progress" />
    </jsp:include>
    <script type="text/javascript" src="js/highcharts/highcharts.js"></script>
    <script type="text/javascript" src="js/highcharts/modules/exporting.js"></script>
    <script src="js/topic-progress.js" type="application/javascript"></script>
    <script src="js/onloading.js" type="application/javascript"></script>
</head>
<body>
    <jsp:include page="../include/header.jsp"></jsp:include>
    <div class="container-fluid">
        <table class="table table-bordered">
            <tr>
                <td>
                <div id="selTopic" style="display:block"></div>
                </td>
            </tr>
            <tr>
                <td>
                    <div class="col-xs-2">
                      <button type="submit" class="btn btn-primary findTopicProgress">find</button>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <div id="container" style="min-width:700px;height:400px"></div>
                </td>
            </tr>
        </table>
    </div>
</body>
</html>
