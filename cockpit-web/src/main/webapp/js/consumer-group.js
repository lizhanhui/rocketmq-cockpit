var oTable;

$(document).ready(function() {
    addcloud();

    oTable=initTable();

    $(".addConsumerGroup").click(function() {
        showCloud();
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
            hideCloud();
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
                    window.location.reload(true);
                },
                error: function() {
                    hideCloud();
                    alert(" ERROR ");
                }
            });
        }
    });

    hideCloud();
});


function initTable(){
    var table = $('#consumerGroup').dataTable({
        "processing": true,
        "sAjaxSource": "cockpit/api/consumer-group",
        "sAjaxDataProp": "data",
        "bPaginate": true,  //是否分页。
        "bFilter": true,  //是否可过滤。
        "bLengthChange": true, //是否允许自定义每页显示条数.
        "scrollX": true,
        "columns": [
            { "data": "clusterName" },
            { "data": "whichBrokerWhenConsumeSlowly" },
            { "data": "groupName" },
            { "data": "consumeEnable" },
            { "data": "consumeBroadcastEnable" },
            { "data": "brokerAddress" },
            { "data": "brokerId" },
            { "data": "retryMaxTimes" },
            { "data": "retryQueueNum" },
            { "data": "consumeFromMinEnable" },
            { "data": "status" },
            { "data": "createTime" },
            { "data": "id",
                "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                    if (oData.status != "ACTIVE"){
                        $(nTd).html("<a href='javascript:void(0);' " + "onclick='_editFun(\"" + oData.id + "\",\"" + oData.clusterName + "\",\"" + oData.whichBrokerWhenConsumeSlowly + "\",\"" + oData.groupName + "\",\"" + oData.consumeEnable + "\",\"" + oData.consumeBroadcastEnable + "\",\"" + oData.brokerAddress + "\",\"" + oData.brokerId + "\",\"" + oData.retryMaxTimes + "\",\"" + oData.retryQueueNum + "\",\"" + oData.consumeFromMinEnable + "\")'>Approve</a>&nbsp;&nbsp;")
                            .append("<a href='javascript:void(0);' onclick='_deleteFun(\"" + oData.id + "\",\"" + oData.clusterName + "\",\"" + oData.groupName +  "\")'>Delete</a>");
                    }else {
                        $(nTd).html("&nbsp;&nbsp;")
                            .append("<a href='javascript:void(0);' onclick='_deleteFun(\"" + oData.id+ "\",\"" + oData.clusterName + "\",\"" + oData.groupName + "\")'>Delete</a>");
                    }
                }
            }
        ],
        "fnCreatedRow": function (nRow, aData, iDataIndex) {
            //add selected class
            $(nRow).click(function () {
                if ($(this).hasClass('row_selected')) {
                    $(this).removeClass('row_selected');
                } else {
                    oTable.$('tr.row_selected').removeClass('row_selected');
                    $(this).addClass('row_selected');
                }
            });
        }
    });
    return table;
}


function _deleteFun(id, clusterName, groupName) {
    showCloud();
    $.ajax({
        async: false,
        url: "cockpit/manage/consumer-group/" + clusterName + "/" + groupName,
        type: "DELETE",
        dataType: "json",
        contentType: "application/json",
        success: function() {
            $.ajax({
                async: false,
                url: "cockpit/api/consumer-group/" + id,
                type: "DELETE",
                dataType: "json",
                contentType: "application/json",
                success: function() {
                    hideCloud();
                },
                error: function() {
                    location.reload(true);
                }
            });
        }
    });
}


function _editFun(id, clusterName, whichBrokerWhenConsumeSlowly, groupName, consumeEnable, consumeBroadcastEnable, brokerAddress, brokerId, retryMaxTimes, retryQueueNum, consumeFromMinEnable) {
    showCloud();
    var order = "ACTIVE";
    var ob = JSON.stringify({"id":id, "clusterName":clusterName,"whichBrokerWhenConsumeSlowly":whichBrokerWhenConsumeSlowly,
                             "groupName":groupName, "consumeEnable":consumeEnable, "consumeBroadcastEnable":consumeBroadcastEnable,
                             "brokerAddress":brokerAddress, "brokerId":brokerId, "retryMaxTimes":retryMaxTimes,
                             "retryQueueNum":retryQueueNum, "consumeFromMinEnable":consumeFromMinEnable,"order":order});

    if ($.trim(id) === "" ) {
        hideCloud();
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
                complete: function() {
                    location.reload(true);
                }
            });

            hideCloud();
        }
    });
}

