<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>

    <jsp:include page="../include/html-title.jsp">
        <jsp:param name="pageTitle" value=" Project Management" />
    </jsp:include>
    <script src="js/project-manage.js" type="application/javascript"></script>
</head>
<body>
    <jsp:include page="../include/header.jsp"></jsp:include>
<div class="container-fluid">
    <div class="row">
        <div class="col-md-8 col-md-offset-2 text-center">
            <h1>Project Catalog</h1>
        </div>
    </div>

        <div id="addConsumerGroupDIV" style="display:none">
            <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
                <table width="100%" class="table table-bordered">
                    <tr style="display:none">
                        <td width="30%">Project</td>
                        <td><input type="text" class="form-control cgProject" placeholder="project"></td>
                    </tr>
                    <tr>
                        <td width="30%">cluster_name:</td>
                        <td><input type="text" class="form-control CGcluster_name" placeholder="cluster_name" onblur="checkInfo(20)"><span>
                            <label id="CGclusterNameCheck_label"></label></span>
                        </td>
                    </tr>
                    <tr style="display:none">
                        <td>which_broker_when_consume_slowly:</td>
                        <td><input type="text" class="form-control which_broker_when_consume_slowly" placeholder="which_broker_when_consume_slowly"></td>
                    </tr>
                    <tr>
                        <td>group_name:</td>
                        <td><input type="text" class="form-control CGgroup_name" placeholder="group_name" onblur="checkInfo(21)"><span>
                            <label id="CGgroupNameCheck_label"></label></span>
                            <span style="display:none"><label id="CGaddExistGroup_label"></label></span>
                            <span style="display:none"><label id="CGaddExistGroup_id"></label></span>
                        </td>
                    </tr>
                    <tr>
                        <td>consume_enable:</td>
                        <td><input type="text" class="form-control consume_enable" placeholder="consume_enable"></td>
                    </tr>
                    <tr>
                        <td>consume_broadcast_enable:</td>
                        <td><input type="text" class="form-control consume_broadcast_enable" placeholder="consume_broadcast_enable"></td>
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
                        <td><input type="text" class="form-control retry_max_times" placeholder="retry_max_times" value="3"></td>
                    </tr>
                    <tr style="display:none">
                        <td>retry_queue_num:</td>
                        <td><input type="text" class="form-control retry_queue_num" placeholder="retry_queue_num" value="3"></td>
                    </tr>
                    <tr style="display:none">
                        <td>consume_from_min_enable:</td>
                        <td><input type="text" class="form-control consume_from_min_enable" placeholder="consume_from_min_enable"></td>
                    </tr>
                    <tr>
                        <td>
                            <div class="col-xs-2">
                                <button type="submit" class="btn btn-primary cancelConsumer">cancel</button>
                            </div>
                        </td>
                        <td>
                            <div class="col-xs-2">
                                <button type="submit" class="btn btn-primary addConsumerGroup">add</button>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>

        <div id="addTopicDIV" style="display:none">
            <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
                <table width="100%" class="table table-bordered">
                    <tr class="hide">
                        <td width="30%">Project: </td>
                        <td><input type="text" class="form-control tProject" placeholder="project"></td>
                    </tr>
                    <tr>
                        <td width="30%">Topic:</td>
                        <td> <input type="text" class="form-control topic" placeholder="topic" onblur="checkInfo(30)"><span>
                             <label id="topicCheck_label"></label></span>
                             <span style="display:none"><label id="addExistTopic_label"></label></span>
                             <span style="display:none"><label id="addExistTopic_id"></label></span>
                        </td>
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
                                <button type="submit" class="btn btn-primary cancelTopic">cancel</button>
                            </div>
                        </td>
                        <td>
                            <div class="col-xs-2">
                                <button type="submit" class="btn btn-primary addTopic">add</button>
                            </div>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    <div class="clear-both"></div>

    <div class="row">
        <div class="col-xs-8 col-xs-offset-2 text-left table-responsive">
            <table class="table table-hover table-bordered">
                <thead>
                    <tr>
                        <td style="display:none;">id</td>
                        <td class="text-center">Project Name</td>
                        <td class="text-center">Project desc</td>
                        <td class="text-center">Operation</td>
                    </tr>
                </thead>
                <tbody class="table-striped table-content">
                </tbody>
            </table>

            <div class="clear-both"></div>
        </div>
    </div>
</div>
</body>
</html>