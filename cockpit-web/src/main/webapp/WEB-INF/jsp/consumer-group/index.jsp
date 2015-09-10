<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/html-title.jsp">
        <jsp:param name="pageTitle" value="Consumer Group Management" />
    </jsp:include>
    <script type="application/javascript" src="js/jquery.dataTables.min.js"></script>
    <link type="text/css" href="css/jquery.dataTables.min.css" rel="stylesheet">
    <script src="js/consumer-group.js" type="application/javascript"></script>
    <script src="js/onloading.js" type="application/javascript"></script>
</head>
<body>
    <jsp:include page="../include/header.jsp"></jsp:include>
<div class="container-fluid">
  <div class="row">
    <div class="col-md-8 col-md-offset-2 text-center">
      <h1>Consumer Group Catalog</h1>
    </div>
  </div>

  <div class="clear-both"></div>

  <table cellspacing="0" class="display" id="consumerGroup" width="100%">
          <thead>
             <tr>
                  <th>Cluster Name</th>
                  <th>Which Broker When Consume Slowly</th>
                  <th>Group Name</th>
                  <th>Consume Enabled</th>
                  <th>Consume Broadcast Enabled</th>
                  <th>Broker Address</th>
                  <th>Broker ID</th>
                  <th>Retry Max Times</th>
                  <th>Retry Queue Number</th>
                  <th>Consume From Min</th>
                  <th>Status</th>
                  <th>Create Time</th>
                  <th>Operation</th>
              </tr>
          </thead>
          <tbody>
          </tbody>
        </table>
      <div class="clear-both"></div>
    </div>
  </div>
</div>

  <div class="clear-both"></div>

</body>
</html>
