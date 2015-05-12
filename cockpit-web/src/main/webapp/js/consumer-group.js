$(document).ready(function() {
    addcloud();
    $.get("cockpit/api/consumer-group", function(data) {
        $(".table-content").children().remove();
        data.forEach(function(consumerGroup) {
            var operationLink = $("<a class='removeItem' href='javascript:;'>Remove</a>");
            var approveLink = $("<a class='approveItem' href='javascript:;'>Approve</a>");

            var operation = $("<td></td>");
            if (consumerGroup.status != "ACTIVE"){
                operation.append(approveLink);
                operation.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            }
            operation.append(operationLink);
            var item = $("<tr><td style='display:none'>" + consumerGroup.id + "</td><td>" + consumerGroup.clusterName + "</td><td>" + consumerGroup.whichBrokerWhenConsumeSlowly + "</td><td>" + consumerGroup.groupName + "</td><td>" + consumerGroup.consumeEnable + "</td><td>" + consumerGroup.consumeBroadcastEnable + "</td><td>" + consumerGroup.brokerAddress + "</td><td>" + consumerGroup.brokerId + "</td><td>" + consumerGroup.retryMaxTimes + "</td><td>" + consumerGroup.retryQueueNum + "</td><td>" + consumerGroup.consumeFromMinEnable + "</td><td>" + consumerGroup.status + "</td><td>" + consumerGroup.createTime + "</td></tr>");
            item.append(operation);
            $(".table-content").append(item);
        });
    });

    $(".addConsumerGroup").click(function() {
        addcloud();
        var clusterName = $("input.cluster_name").val();
        var whichBrokerWhenConsumeSlowly = $("input.which_broker_when_consume_slowly").val();
        var groupName = $("input.group_name").val();
        var consumeEnable = $("input.consume_enable").val();
        var consumeBroadcastEnable = $("input.consume_broadcast_enable").val();
        var brokerAddress = $("input.broker_address").val();
        var brokerId = $("input.broker_id").val();
        var retryMaxTimes = $("input.retry_max_times").val();
        var retryQueueNum = $("input.retry_queue_num").val();
        var consumeFromMinEnable = $("input.consume_from_min_enable").val();
        var allow = "DRAFT";
        var ob = JSON.stringify({"clusterName":clusterName,"whichBrokerWhenConsumeSlowly":whichBrokerWhenConsumeSlowly,
                                 "groupName":groupName,"consumeEnable":consumeEnable, "consumeBroadcastEnable":consumeBroadcastEnable,
                                 "brokerAddress":brokerAddress, "brokerId":brokerId, "retryMaxTimes":retryMaxTimes,
                                 "retryQueueNum":retryQueueNum, "consumeFromMinEnable":consumeFromMinEnable,"status":allow});
        if ($.trim(groupName) === "") {
            alert(" no group name ?");
            removecloud();
            return false;
        } else {
            $.ajax({
                        async: false,
                        url: "cockpit/api/consumer-group",
                        type: "PUT",
                        dataType: "json",
                        contentType: 'application/json',
                        data: ob,
                        success: function() {
                            location.reload(true);
                        },
                        error: function() {
                            removecloud();
                            alert(" ERROR ");
                        }
                    });
        }
    });

    $(document).on("click", ".removeItem", function() {
        addcloud();
        var row = $(this).parent().parent();
        var id = row.children().eq(0).html();
        if ($.trim(id) === "" ) {
            removecloud();
            return false;
        }
        $.ajax({
            async: false,
            url: "cockpit/api/consumer-group/" + id,
            type: "DELETE",
            dataType: "json",
            contentType: "application/json",
            success: function() {
                row.remove();
                removecloud();
            },
            error: function() {
                location.reload(true);
            }
        });
    });

    $(document).on("click", ".approveItem", function() {
        addcloud();
            var row = $(this).parent().parent();
            var id = row.children().eq(0).html();
            var clusterName = row.children().eq(1).html();
            var whichBrokerWhenConsumeSlowly = row.children().eq(2).html();
            var groupName = row.children().eq(3).html();
            var consumeEnable = row.children().eq(4).html();
            var consumeBroadcastEnable = row.children().eq(5).html();
            var brokerAddress = row.children().eq(6).html();
            var brokerId = row.children().eq(7).html();
            var retryMaxTimes = row.children().eq(8).html();
            var retryQueueNum = row.children().eq(9).html();
            var consumeFromMinEnable = row.children().eq(10).html();
            var order = "ACTIVE";
            var ob = JSON.stringify({"id":id, "clusterName":clusterName,"whichBrokerWhenConsumeSlowly":whichBrokerWhenConsumeSlowly,
                                     "groupName":groupName, "consumeEnable":consumeEnable, "consumeBroadcastEnable":consumeBroadcastEnable,
                                     "brokerAddress":brokerAddress, "brokerId":brokerId, "retryMaxTimes":retryMaxTimes,
                                     "retryQueueNum":retryQueueNum, "consumeFromMinEnable":consumeFromMinEnable,"order":order});

            if ($.trim(id) === "" ) {
                removecloud();
                return false;
            }

            $.ajax({
                async: false,
                data: ob,
                url: "cockpit/manage/consumer-group",
                type: "POST",
                dataType: "json",
                contentType: "application/json",
                success: function() {
                    $.ajax({
                        async: false,
                        url: "cockpit/api/consumer-group/" + id,
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json",
                        success: function() {
                            location.reload(true);
                        }
                    });

                    removecloud();
                }
            });
    });

    removecloud();
});