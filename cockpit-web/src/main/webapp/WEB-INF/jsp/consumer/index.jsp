<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Consumer Management</title>
    <%@include file="../include/base-path.jsp"%>
    <base href="<%=basePath%>%">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <script type="text/javascript" src="http://cdn.hcharts.cn/jquery/jquery-1.8.3.min.js"></script>
    <script type="text/javascript" src="http://cdn.hcharts.cn/highcharts/highcharts.js"></script>
    <script type="text/javascript" src="http://cdn.hcharts.cn/highcharts/exporting.js"></script>
    <script src="http://libs.baidu.com/bootstrap/3.0.3/js/bootstrap.min.js"></script>
    <link href="http://libs.baidu.com/bootstrap/3.0.3/css/bootstrap.min.css" rel="stylesheet">
    <script src="js/consumer.js" type="application/javascript"></script>
    <script src="js/onloading.js" type="application/javascript"></script>
</head>
<body>
<div class="container-fluid">
  <div class="row">
    <div class="col-md-8 col-md-offset-2 text-center">
      <h1>Consumer Catalog</h1>
    </div>
  </div>
  <div class="clear-both"></div>

    <div class="row">
      <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
    <table class="table table-hover table-bordered">
        <thead>
                <tr>
                  <td width="30%">which Consumer you want to know ?</td>
                  <td colspan='3'><input type="text" class="form-control groupName" placeholder="groupName"><input type="text" class="form-control topic" style="display:none" placeholder="topic"><input type="text" class="form-control broker" style="display:none" placeholder="broker"><button type="submit" class="btn btn-primary queryTopic">Query</button></td>
                </tr>
                <tr>
                    <td width="30%">topic</td>
                    <td>broker</td>
                    <td>queue</td>
                    <td>diff</td>
                <tr>
        </thead>
        <div id="container" style="min-width:700px;height:400px; margin: 0 auto"></div>
        <tbody class="table-striped table-content">

        </tbody>
    </table>
<div class="clear-both"></div>

    </div>
  </div>
</div>

</body>
</html>
