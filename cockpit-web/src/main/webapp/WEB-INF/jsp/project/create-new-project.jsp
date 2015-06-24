    <%@ page contentType="text/html;charset=UTF-8" language="java" %>
        <html>
        <head>

        <jsp:include page="../include/html-title.jsp">
            <jsp:param name="pageTitle" value="Create New Project"/>
        </jsp:include>
        <script src="js/project.js" type="application/javascript"></script>
        </head>
        <body>
        <jsp:include page="../include/header.jsp"></jsp:include>

        <div class="container-fluid">
        <div class="row">
        <ol class="breadcrumb">
        <li class="active"><span id="step1" class="badge" style="background-color: red;">1</span> Launch New Project</li>
        <li><span id="step2" class="badge" style="background-color: red;">2</span> Create Consumer Groups</li>
        <li><span id="step3" class="badge" style="background-color: red;">3</span> Create Topics</li>
        </ol>
        </div>
        </div>

        <div id="addProjectDIV">
        <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
        <table width="100%" class="table table-bordered">
        <tr>
        <td width="30%"><label>Project Name</label></td>
        <td><input type="text" class="form-control projectName" placeholder="projectName" onblur="checkInfo(1)"><span><label id="projectNameCheck_label"></label></span></td>
        </tr>
        <tr>
        <td><label>Description</label></td>
        <td><input type="text" class="form-control projectDesc" placeholder="projectDesc"></td>
        </tr>
        <tr>
        <td align="right" colspan="2">
        <button type="submit" class="btn btn-primary addProject">next step</button></td>
        </tr>
        </table>
        </div>
        </div>

        <div id="addConsumerGroupDIV" style="display:none">
        <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
        <table width="100%" class="table table-bordered">
        <tr>
        <td width="30%">cluster_name:</td>
        <td><input type="text" class="form-control CGcluster_name" placeholder="cluster_name" onblur="checkInfo(20)"><span><label id="CGclusterNameCheck_label"></label></span></td>
        </tr>
        <tr style="display:none">
        <td>which_broker_when_consume_slowly:</td>
        <td><input type="text" class="form-control which_broker_when_consume_slowly" placeholder="which_broker_when_consume_slowly"></td>
        </tr>
        <tr>
        <td>group_name:</td>
        <td><input type="text" class="form-control CGgroup_name" placeholder="group_name" onblur="checkInfo(21)"><span><label id="CGgroupNameCheck_label"></label></span></td>
        </tr>
        <tr>
        <td>consume_enable:</td>
        <td><input type="text" class="form-control consume_enable" placeholder="consume_enable"></td>
        </tr>
        <tr>
        <td>consume_broadcast_enable:</td>
        <td><input type="text" class="form-control consume_broadcast_enable"
        placeholder="consume_broadcast_enable"></td>
        </tr>
        <tr style="display:none">
        <td>broker_address:</td>
        <td><input type="text" class="form-control broker_address" placeholder="broker_address"></td>
        </tr>
        <tr style="display:none">
        <td>broker_id:</td>
        <td><input type="text" class="form-control broker_id" placeholder="broker_id"></td>
        </tr>
        <tr style="display:none">
        <td>retry_max_times:</td>
        <td><input type="text" class="form-control retry_max_times" placeholder="retry_max_times"></td>
        </tr>
        <tr style="display:none">
        <td>retry_queue_num:</td>
        <td><input type="text" class="form-control retry_queue_num" placeholder="retry_queue_num"></td>
        </tr>
        <tr style="display:none">
        <td>consume_from_min_enable:</td>
        <td><input type="text" class="form-control consume_from_min_enable" placeholder="consume_from_min_enable"></td>
        </tr>
        <tr>
        <td>
        <div class="col-xs-2">
        <button type="submit" class="btn btn-primary backProject">back</button>
        </div>
        </td>
        <td>
        <div class="col-xs-2">
        <button type="submit" class="btn btn-primary addConsumerGroup">next step</button>
        </div>
        </td>
        </tr>
        </table>
        </div>
        </div>

        <div id="addTopicDIV" style="display:none">
        <div class="container-fluid">
        <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
        <table width="100%" class="table table-bordered">
        <tr>
        <td width="30%">Topic:</td>
        <td> <input type="text" class="form-control topic" placeholder="topic" onblur="checkInfo(30)"><span><label id="topicCheck_label"></label></span></td>
        </tr>
        <tr class="hide">
        <td>Write Queue Number:</td>
        <td> <input type="text" class="form-control writeQueueNum" placeholder="Write Queue Number, Default 16"></td>
        </tr>
        <tr class="hide">
        <td>Read Queue Number:</td>
        <td> <input type="text" class="form-control readQueueNum" placeholder="Read Queue Number, Default 16"></td>
        </tr>
        <tr>
        <td>Broker Address:</td>
        <td> <input type="text" class="form-control TbrokerAddress" placeholder="IP:10911"></td>
        </tr>
        <tr>
        <td>Cluster Name:</td>
        <td><input type="text" class="form-control TclusterName" placeholder="Cluster Name" value="DefaultCluster"></td>
        </tr>
        <tr class="hide">
        <td>Permission:</td>
        <td> <input type="text" class="form-control Tpermission" placeholder="Read: 2/Write: 4/Read&Write: 6"></td>
        </tr>
        <tr class="hide">
        <td>Unit:</td>
        <td> <input type="text" class="form-control unit" placeholder="true/false"></td>
        </tr>
        <tr class="hide">
        <td>Has Unit Subscription:</td>
        <td> <input type="text" class="form-control hasUnitSubscription" placeholder="true/false"></td>
        </tr>
        <tr>
        <td>Order:</td>
        <td> <input type="text" class="form-control order" placeholder="order"></td>
        </tr>
        <tr>
        <td>
        <div class="col-xs-2">
        <button type="submit" class="btn btn-primary backConsumer">back</button>
        </div>
        </td>
        <td>
        <div class="col-xs-2">
        <button type="submit" class="btn btn-primary addTopic">submit</button>
        </div>
        </td>
        </tr>
        </table>
        </div>
        </div>
        </body>
        </html>
