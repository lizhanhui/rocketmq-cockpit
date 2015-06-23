$(document).ready(function() {
    $(".addProject").click(function() {
        var projectName = $("input.projectName").val();
        if ($.trim(projectName) === "") {
            alert("we need a name for your project.");
            return false;
        }
        document.getElementById("addProjectDIV").style.display="none";
        document.getElementById("addConsumerGroupDIV").style.display="block";
        document.getElementById("step1").style.backgroundColor="blue";
    });

    $(".addConsumerGroup").click(function() {
        var cluster_name = $("input.CGcluster_name").val();
        if ($.trim(cluster_name) === ""){
            alert(" we need a cluster to build your consumer group");
            return false;
        }

        var group_name = $("input.CGgroup_name").val();
        if ($.trim(group_name) === ""){
            alert(" your consumer group need a name.");
            return false;
        }

        document.getElementById("addConsumerGroupDIV").style.display="none";
        document.getElementById("addTopicDIV").style.display="block";
        document.getElementById("step2").style.backgroundColor="blue";
    });

    $(".backProject").click(function() {
        document.getElementById("addConsumerGroupDIV").style.display="none";
        document.getElementById("addProjectDIV").style.display="block";
    });

    $(".addTopic").click(function() {
        var topic = $("input.Ttopic").val();
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

        var projectName = $("input.projectName").val();
        var projectDesc = $("input.projectDesc").val();

        var project = JSON.stringify({"name":projectName, "description":projectDesc});

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

        var write_queue_num = $("input.writeQueueNum").val();
        var read_queue_num = $("input.readQueueNum").val();
        var permission = $("input.Tpermission").val();
        var unit = $("input.unit").val();
        var has_unit_subscription = $("input.hasUnitSubscription").val();
        var order = $("input.order").val();
        var ob = JSON.stringify({"topic":topic,"writeQueueNum":write_queue_num,"readQueueNum":read_queue_num,
                                 "brokerAddress":broker_address, "clusterName":cluster_name, "permission":permission, "unit":unit, "hasUnitSubscription":has_unit_subscription, "order":order, "status":allow});
        $.ajax({
            async: false,
            url: "cockpit/api/project",
            type: "PUT",
            dataType: "json",
            contentType: "application/json",
            data: project,
            success: function() {
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
                            url: "cockpit/api/topic",
                            type: "PUT",
                            dataType: "json",
                            contentType: 'application/json',
                            data: ob,
                            success: function() {
                                $.ajax({
                                    async: false,
                                    url: "cockpit/api/project/" + projectName + "/" + groupName + "/" + topic,
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
                    },
                    error: function() {
                        alert(" ERROR ");
                    }
                });
            }
        });
    });

    $(".backConsumer").click(function() {
        document.getElementById("addConsumerGroupDIV").style.display="block";
        document.getElementById("addTopicDIV").style.display="none";
    });

});
