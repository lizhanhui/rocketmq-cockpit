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

<div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
    <table class="table table-bordered">
        <tr><td>cluster_name:</td><td>  <input type="text" class="form-control cluster_name" placeholder="cluster_name"></td></tr>
        <tr style="display:none"><td>which_broker_when_consume_slowly:</td><td>  <input type="text" class="form-control which_broker_when_consume_slowly"
        placeholder="which_broker_when_consume_slowly"></td></tr>
        <tr><td>group_name:</td><td>  <input type="text" class="form-control group_name" placeholder="group_name"></td></tr>
        <tr><td>consume_enable:</td><td>  <input type="text" class="form-control consume_enable"
        placeholder="consume_enable"></td></tr>
        <tr><td>consume_broadcast_enable:</td><td>  <input type="text" class="form-control consume_broadcast_enable"
        placeholder="consume_broadcast_enable"></td></tr>
        <tr style="display:none"><td>broker_address:</td><td>  <input type="text" class="form-control broker_address"
                placeholder="broker_address"></td></tr>
        <tr style="display:none"><td>broker_id:</td><td>  <input type="text" class="form-control broker_id"
                placeholder="broker_id"></td></tr>
        <tr style="display:none"><td>retry_max_times:</td><td>  <input type="text" class="form-control retry_max_times"
                placeholder="retry_max_times" value="3"></td></tr>
        <tr style="display:none"><td>retry_queue_num:</td><td>  <input type="text" class="form-control retry_queue_num" placeholder="retry_queue_num" value="1"></td></tr>
        <tr style="display:none"><td>consume_from_min_enable:</td><td>  <input type="text" class="form-control consume_from_min_enable" placeholder="consume_from_min_enable"></td></tr>
        <tr>
            <td colspan="2">
                <div class="col-xs-2">
                  <button type="submit" class="btn btn-primary addConsumerGroup">Add</button>
                </div>
            </td>
        </tr>
    </table>
</div>

</body>
</html>
