$(document).ready(function() {
    $.get("cockpit/api/project", function(data) {
        $(".table-content").children().remove();
        data.forEach(function(project) {
            var operationLink = $("<a class='removeItem' href='javascript:;'>Remove</a>");
            var addConsumerGroupLink = $("<a class='addCGItem' href='javascript:;'>Add Consumer Group</a>");
            var addTopicLink = $("<a class='addTItem' href='javascript:;'>Add Topic</a>");
            var jumpLink = $("<a class='jumpShow' href='javascript:;'>Project Dashboard</a>");
            var operation = $("<td></td>");
            operation.append(addConsumerGroupLink);
            var separator = "&nbsp;&nbsp;|&nbsp;&nbsp;&nbsp;";
            operation.append(separator);
            operation.append(addTopicLink);
            operation.append(separator);
            operation.append(operationLink);
            operation.append(separator);
            operation.append(jumpLink);
            var item = $("<tr><td style='display:none'>" + project.id + "</td><td>" + project.name + "</td><td>" + project.description + "</td></tr>");
            item.append(operation);
            $(".table-content").append(item);
        });
    });

    $(document).on("click", ".removeItem", function() {
        var row = $(this).parent().parent();
        var id = row.children().eq(0).html();

        $.ajax({
            async: false,
            url: "cockpit/api/project",
            type: "DELETE",
            dataType: "json",
            contentType: "application/json",
            data: id,
            success: function() {
                row.remove();
            },
            error: function() {
                location.reload(true);
            }
        });
    });

    $(document).on("click", ".addCGItem", function() {
        var row = $(this).parent().parent();
        var name = row.children().eq(1).html();

        document.getElementById("addConsumerGroupDIV").style.display = "block";
        $("input.cgProject").val(name);
    });

    $(document).on("click", ".addTItem", function() {
        var row = $(this).parent().parent();
        var name = row.children().eq(1).html();

        document.getElementById("addTopicDIV").style.display = "block";
        $("input.tProject").val(name);
    });

    $(document).on("click", ".jumpShow", function() {
        var row = $(this).parent().parent();
        var name = row.children().eq(1).html();

        document.cookie="projectName=" + name;
        location.href="cockpit/project/manage2";
    });

    $(".cancelConsumer").click(function() {
        document.getElementById("addConsumerGroupDIV").style.display = "none";
    });

    $(".addConsumerGroup").click(function() {
        var project = $("input.cgProject").val();
        var topic = "$EMPTY$";

        var clusterName = $("input.CGcluster_name").val();
        var whichBrokerWhenConsumeSlowly = $("input.which_broker_when_consume_slowly").val();
        var groupName = $("input.CGgroup_name").val();
        var consumeEnable = $("input.consume_enable").val();
        var consumeBroadcastEnable = $("input.consume_broadcast_enable").val();
        var brokerAddress = $("input.broker_address").val();
        var brokerId = $("input.broker_id").val();
        var retryMaxTimes = $("input.retry_max_times").val();
        var retryQueueNum = $("input.retry_queue_num").val();
        var consumeFromMinEnable = $("input.consume_from_min_enable").val();
        var allow = "DRAFT";
        var CG = JSON.stringify({"clusterName":clusterName,"whichBrokerWhenConsumeSlowly":whichBrokerWhenConsumeSlowly,
            "groupName":groupName,"consumeEnable":consumeEnable, "consumeBroadcastEnable":consumeBroadcastEnable,
            "brokerAddress":brokerAddress, "brokerId":brokerId, "retryMaxTimes":retryMaxTimes,
            "retryQueueNum":retryQueueNum, "consumeFromMinEnable":consumeFromMinEnable,"status":allow});

        $.ajax({
            async: false,
            url: "cockpit/api/consumer-group",
            type: "PUT",
            dataType: "json",
            contentType: 'application/json',
            data: CG,
            success: function() {
                $.ajax({
                    async: false,
                    //TODO we use ID to build the reference
                    url: "cockpit/api/project/" + project + "/" + groupName + "/" + topic,
                    type: "PUT",
                    dataType: "json",
                    contentType: "application/json",
                    complete: function() {
                        alert(" SUCCESS ");
                        location.reload(true);
                    }
                });
            },
            error: function() {
                alert(" ERROR ");
            }
        });
    });

    $(".cancelTopic").click(function() {
        document.getElementById("addTopicDIV").style.display = "none";
    });

    $(".addTopic").click(function() {
        var project = $("input.tProject").val();
        var groupName = "$EMPTY$";
        var topic = $("input.topic").val();
        if ($.trim(topic) === ""){
            alert("your topic need a name");
            return false;
        }

        var broker_address = $("input.TbrokerAddress").val();
        var cluster_name = $("input.TclusterName").val();
        if ($.trim(broker_address) === "" && $.trim(cluster_name) === ""){
            alert(" your topic need a cluster or broker to build on.");
            return false;
        }

        var write_queue_num = $("input.writeQueueNum").val();
        var read_queue_num = $("input.readQueueNum").val();
        var permission = $("input.Tpermission").val();
        var unit = $("input.unit").val();
        var has_unit_subscription = $("input.hasUnitSubscription").val();
        var order = $("input.order").val();
        var allow = "DRAFT";

        var ob = JSON.stringify({"topic":topic,"writeQueueNum":write_queue_num,"readQueueNum":read_queue_num,
            "brokerAddress":broker_address, "clusterName":cluster_name, "permission":permission, "unit":unit, "hasUnitSubscription":has_unit_subscription, "order":order, "status":allow});

        $.ajax({
            async: false,
            url: "cockpit/api/topic",
            type: "PUT",
            dataType: "json",
            contentType: 'application/json',
            data: ob,
            success: function() {
                $.ajax({
                    async: false,
                    //TODO we use ID to build the reference
                    url: "cockpit/api/project/" + project + "/" + groupName + "/" + topic,
                    type: "PUT",
                    dataType: "json",
                    contentType: "application/json",
                    complete: function() {
                        alert(" SUCCESS ");
                        location.reload(true);
                    }
                });
            },
            error: function() {
                alert(" ERROR ");
            }
        });
    });

});


function checkInfo(num){
    var text;
    switch (num){
        case 0 :
            break;
        case 1 :
            text = $("input.projectName").val();
            var check = document.getElementById("projectNameCheck_label");
            if (text === ""){
                check.style.color = "red";
                check.innerHTML = "project name can not be null.";
            }else if (baseProject.indexOf(text) > -1){
                check.style.color = "orange";
                check.innerHTML = "this project is already exist, please make sure you want to use it.";
            }else {
                check.style.color = "green";
                check.innerHTML = "you can use this project name.";
            }
            break;
        case 20 :
            text = $("input.CGcluster_name").val();
            var check = document.getElementById("CGclusterNameCheck_label");
            if (text === ""){
                check.style.color = "red";
                check.innerHTML = " cluster name can not be null."
            }else{
                check.style.color = "green";
                check.innerHTML = "cluster name is ok.";
            }
            break;
        case 21 :
            text = $("input.CGgroup_name").val();
            var check = document.getElementById("CGgroupNameCheck_label");
            if (text === ""){
                check.style.color = "red";
                check.innerHTML = " group name can not be null."
            }else{
                //check this consumer group maybe exist
                $.ajax({
                    async: false,
                    url: "cockpit/api/consumer-group/consumer-group-name/" + text,
                    type: "GET",
                    dataType: "json",
                    contentType: 'application/json',
                    success: function(consumerGroup){
                        if (consumerGroup.groupName === text){
                            check.style.color = "blue";
                            check.innerHTML = "This group is exist. Make sure you want make this group connect to your project.";
                        }else{
                            check.style.color = "green";
                            check.innerHTML = "group name is ok.";
                        }
                    }
                });
            }
            break;
        case 30 :
            text = $("input.topic").val();
            var check = document.getElementById("topicCheck_label");
            if (text === ""){
                check.style.color = "red";
                check.innerHTML = "topic can not be null.";
            }else{
                $.ajax({
                    async: false,
                    url: "cockpit/api/topic/" + text,
                    type: "GET",
                    contentType: "application/json; charset=UTF-8",
                    dataType: "json",
                    success: function(data) {
                        if (null == data || data.length == 0) {
                            check.style.color = "green";
                            check.innerHTML = "topic name is ok.";
                        } else {
                            check.style.color = "blue";
                            check.innerHTML = "This topic is exist. Make sure you want make this topic connect to your project.";
                        }
                    }
                });
            }
            break;
    }
}
