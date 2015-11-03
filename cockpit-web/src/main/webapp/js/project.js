var baseProject = [];

$(document).ready(function() {
    $.get("cockpit/api/project", function(data) {
        data.forEach(function(project) {
            baseProject.push(project.name);
        });
    });

    $(".addProject").click(function() {
        var projectName = $("input.projectName").val();

        if (projectName === ""){
            alert("your project need a name.");
            return false;
        }

        if (baseProject.indexOf($.trim(projectName)) > -1){
            if(window.confirm("there already had this project, do you want to add consumer group and topic for it ?")){

            }else{
                $("input.projectName").val("");
                return false;
            }
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
        var projectId = 0;
        var consumerGroupId = 0;
        var topicId = 0;
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
            "consumeFromBrokerId":brokerId, "retryMaxTimes":retryMaxTimes,
            "retryQueueNum":retryQueueNum, "consumeFromMinEnable":consumeFromMinEnable,"status":allow});

        var write_queue_num = $("input.writeQueueNum").val();
        var read_queue_num = $("input.readQueueNum").val();
        var permission = $("input.Tpermission").val();
        var unit = $("input.unit").val();
        var has_unit_subscription = $("input.hasUnitSubscription").val();
        var order = $("input.order").val();
        var ob = JSON.stringify({"topic":topic, "clusterName":cluster_name, "order":order, "status":allow});
        $.ajax({
            async: false,
            url: "cockpit/api/project",
            type: "PUT",
            dataType: "json",
            contentType: "application/json",
            data: project,
            success: function(dataP) {
                projectId = dataP;
                if(document.getElementById("CGaddExistGroup_label").innerHTML === "false"){
                    $.ajax({
                        async: false,
                        url: "cockpit/api/consumer-group/" + projectId,
                        type: "PUT",
                        dataType: "json",
                        contentType: 'application/json',
                        data: CG,
                        success: function() {

                        },
                        error: function() {
                            alert(" ADD CONSUMER GROUP ERROR ");
                        }
                    });
                }else{
                    consumerGroupId = document.getElementById("CGaddExistGroup_id").innerHTML;
                }
                if(document.getElementById("addExistTopic_label").innerHTML === "false"){
                    $.ajax({
                        async: false,
                        url: "cockpit/api/topic/" + projectId,
                        type: "PUT",
                        dataType: "json",
                        contentType: 'application/json',
                        data: ob,
                        success: function() {

                        },
                        error: function() {
                            alert(" ADD TOPIC ERROR ");
                        }
                    });
                }else{
                    topicId = document.getElementById("addExistTopic_id").innerHTML;
                }
                $.ajax({
                    async: false,
                    url: "cockpit/api/project/" + projectId + "/" + consumerGroupId + "/" + topicId,
                    type: "PUT",
                    dataType: "json",
                    contentType: "application/json",
                    complete: function() {
                        alert(" SUCCESS ");
                        location.href="cockpit/project/manage";
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
            var existGroup = document.getElementById("CGaddExistGroup_label");
            var groupId = document.getElementById("CGaddExistGroup_id");
            if (text === ""){
                check.style.color = "red";
                check.innerHTML = " group name can not be null.";
                existGroup.innerHTML = "false";
            }else{
                //check this consumer group maybe exist
                existGroup.innerHTML = "false";
                check.style.color = "green";
                check.innerHTML = "group name is ok.";
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
                            existGroup.innerHTML = "true";
                            groupId.innerHTML = consumerGroup.id;
                        }
                    }
                });
            }
            break;
        case 30 :
            text = $("input.topic").val();
            var check = document.getElementById("topicCheck_label");
            var existTopic = document.getElementById("addExistTopic_label");
            var topicId = document.getElementById("addExistTopic_id");
            if (text === ""){
                check.style.color = "red";
                check.innerHTML = "topic can not be null.";
                existTopic.innerHTML = "false";
            }else{
                existTopic.innerHTML = "false";
                check.style.color = "green";
                check.innerHTML = "topic name is ok.";
                $.ajax({
                    async: false,
                    url: "cockpit/api/topic/" + text,
                    type: "GET",
                    contentType: "application/json; charset=UTF-8",
                    dataType: "json",
                    success: function(topicMetadata) {
                        if (topicMetadata.topic === text) {
                            check.style.color = "blue";
                            check.innerHTML = "This topic is exist. Make sure you want make this topic connect to your project.";
                            existTopic.innerHTML = "true";
                            topicId.innerHTML = topicMetadata.id;
                        }
                    }
                });
            }
            break;
    }
}
