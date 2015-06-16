$(document).ready(function() {
    $(".addProject").click(function() {
        var projectName = $("input.projectName").val();
        if ($.trim(projectName) === "") {
            alert("we need a name for your project.");
            return false;
        }
        document.getElementById("addProjectDIV").style.display="none";
        document.getElementById("addConsumerGroupDIV").style.display="block";
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
    });

    $(".backProject").click(function() {
        document.getElementById("addConsumerGroupDIV").style.display="none";
        document.getElementById("addProjectDIV").style.display="block";
    });

    $(".addTopic").click(function() {
        var t_topic = $("input.Ttopic").val();
        if ($.trim(t_topic) === ""){
            alert("your topic need a name");
            return false;
        }

        var t_cluster = $("input.TclusterName").val();
        var t_broker = $("input.TbrokerAddress").val();
        if ($.trim(t_cluster) === "" && $.trim(t_broker) === ""){
            alert(" your topic need a cluster or broker to build on.");
            return false;
        }
    });

    $(".backConsumer").click(function() {
        document.getElementById("addConsumerGroupDIV").style.display="block";
        document.getElementById("addTopicDIV").style.display="none";
    });

});
