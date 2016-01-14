<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <jsp:include page="../include/html-title.jsp">
        <jsp:param name="pageTitle" value="Topic Management" />
    </jsp:include>

    <script type="application/javascript" src="js/jquery/jquery.dataTables.min.js"></script>
    <link type="text/css" href="css/jquery.dataTables.min.css" rel="stylesheet">
    <script src="js/topic.js" type="application/javascript"></script>
    <script src="js/onloading.js" type="application/javascript"></script>
</head>
<body>
    <jsp:include page="../include/header.jsp"></jsp:include>
<div class="container-fluid">
<div class="row">
    <div class="col-md-8 col-md-offset-2 text-center">
      <h1>TOPIC</h1>
    </div>
  </div>
<div id="sendMessageTestDIV" class="col-xs-8 col-xs-offset-2 text-left table-responsive">
    <table class="table table-bordered">
        <tr><td>Topic:</td><td>  <input type="text" class="form-control send_topic" placeholder="topic"></td></tr>
        <tr><td>Producer Group:</td><td>  <input type="text" class="form-control send_producerGroup"
        placeholder="Producer Group"></td></tr>
        <tr><td>tag:</td><td>  <input type="text" class="form-control send_tag" placeholder="tag"></td></tr>
        <tr><td>key:</td><td>  <input type="text" class="form-control send_key" placeholder="key"></td></tr>
        <tr><td>Message Body:</td><td>  <input type="text" class="form-control send_body" placeholder=""
        value="test context"></td></tr>
        <tr>
            <td colspan="2">
                <div class="col-xs-2">
                  <button type="submit" class="btn btn-primary send">send</button>
                </div>
                <div class="col-xs-2">
                  <button type="submit" class="btn btn-primary cancelMes">cancel</button>
                </div>
            </td>
        </tr>
    </table>
</div>
<button id="addTopicM" onclick="addTopic();">add topic</button>
<div id="topicDIV">
      <table cellspacing="0" class="display" id="topic" width="100%">
        <thead>
           <tr>
                  <th>Topic</th>
                  <th>Order</th>
                  <th>Status</th>
                  <th>Create Time</th>
                  <th>Update Time</th>
                  <th>Operation</th>
            </tr>
        </thead>
        <tbody>
        </tbody>
      </table>
</div>
<div id="addTopicMeta" class="col-xs-8 col-xs-offset-2 text-left table-responsive" style="padding:5px;display:none">
    <table cellspacing="1" class="" id="addTopicMetaTable" style="padding-left: 50px;">
        <tr><td>Topic:</td><td><input type="text" class="form-control topicA" placeholder="Topic name"></input></td></tr>
        <tr><td>Cluster:</td><td><input type="text" class="form-control clusterA" placeholder="Which Cluster ?"></input></td></tr>
        <tr><td>Order:</td><td><input type="text" class="form-control orderA" placeholder="order or not ?"></input></td></tr>
        <tr><td colspan="2"><button type="submit" class="btn btn-primary addTopicM"> add topic meta</button>
        <button type="submit" class="btn btn-primary cancelTopicM">cancel</button>
    </td></tr>
    </table>
</div>
<div id="topicDetailDIV" class="col-xs-8 col-xs-offset-2 text-left table-responsive" style="padding:20px">
<div id="topicDetailButton" style="padding:5px">
<button id="addDetail" onclick="addDetail();">Add Details</button>
<button id="closeDetail" onclick="closeDetail();">close</button>
</div>
    <div id="addTopicDIV" style="display:none">
        <table cellspacing="1" class="col-xs-8 col-xs-offset-2 text-left table-responsive" id="addDetailTable" style="padding-left: 50px;">
            <tr class="hide">
                <td>Topic:</td>
                <td><input type="text" class="form-control topic" placeholder="topic"></td>
            </tr>
            <tr>
                <td>Write Queue Number:</td>
                <td>  <input type="text" class="form-control writeQueueNum" placeholder="Write Queue Number, Default 16"></td>
            </tr>
            <tr>
                <td>Read Queue Number:</td>
                <td>  <input type="text" class="form-control readQueueNum" placeholder="Read Queue Number, Default 16"></td>
            </tr>
            <tr>
                <td>Broker Address:</td>
                <td><select id="brokerList"><option value="-1">please choose your broker</option></select></td>
            </tr>
            <tr>
                <td>Cluster Name:</td>
                <td>  <input type="text" class="form-control clusterName" placeholder="Cluster Name" value="DefaultCluster"></td>
            </tr>
            <tr>
                <td>Permission:</td>
                <td>  <input type="text" class="form-control permission" placeholder="Read: 2/Write: 4/Read&amp;Write: 6"></td>
            </tr>

            <tr>
                <td colspan="2">
                    <div class="col-xs-2">
                      <button type="submit" class="btn btn-primary addTopic">Add</button>
                    </div>
                    <div class="col-xs-2">
                      <button type="submit" class="btn btn-primary cancelTopic">cancel</button>
                    </div>
                </td>
            </tr>
        </table>
    </div>

    <div align="center" id="topicDetailTable">
      <table cellspacing="0" class="display" id="topicDetail" width="100%" style="margin-left: 0px;">
        <thead>
           <tr>
                  <th>Topic</th>
                  <th>Cluster Name</th>
                  <th>Broker Address</th>
                  <th>Status</th>
                  <th>Write Queue Num</th>
                  <th>Read Queue Num</th>
                  <th>Permission</th>
                  <th>Create Time</th>
                  <th>Update Time</th>
                  <th>SyncTime</th>
                  <th>Operation</th>
            </tr>
        </thead>
        <tbody>
        </tbody>
      </table>
    </div>
</div>

</div>
</body>
</html>
