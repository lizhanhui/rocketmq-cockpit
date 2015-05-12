<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/html-title.jsp">
        <jsp:param name="pageTitle" value="IP Mapping Management"/>
    </jsp:include>
    <script src="js/ip-mapping.js" type="application/javascript"></script>
</head>
<body>
    <jsp:include page="../include/header.jsp"></jsp:include>
<div class="container-fluid">
  <div class="row">
    <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
      <table class="table table-condense table-hover table-bordered">
        <thead>
        <tr>
          <td>Inner IP Address</td>
          <td>Public IP Address</td>
          <td>Operation</td>
        </tr>
        </thead>
        <tbody class="table-striped table-content">
        </tbody>
      </table>

      <div class="clear-both"></div>

    </div>
    <div class="col-xs-4 col-xs-offset-2">
      <input type="text" class="form-control innerIP" placeholder="Inner IP Address">
    </div>

    <div class="col-xs-4">
      <input type="text" class="form-control publicIP" placeholder="Public IP Address">
    </div>

    <div class="col-xs-2">
      <button type="submit" class="btn btn-primary addMapping">Add</button>
    </div>
  </div>
</div>
</body>
</html>
