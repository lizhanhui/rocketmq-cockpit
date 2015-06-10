var oTable;
var dTable;

$(document).ready(function() {
    addcloud();
    showCloud();
    oTable = initTable();
    document.getElementById("addTopicDIV").style.display="none";
    document.getElementById("sendMessageTestDIV").style.display="none";
    document.getElementById("topicDetailDIV").style.display="none";

    $(".addTopic").click(function() {
        document.getElementById("topicDetailDIV").style.display="none";
        showCloud();
        var topic = $("input.topic").val();
        var write_queue_num = $("input.writeQueueNum").val();
        var read_queue_num = $("input.readQueueNum").val();
        var broker_address = $("input.brokerAddress").val();
        var cluster_name = $("input.clusterName").val();
        var permission = $("input.permission").val();
        var unit = $("input.unit").val();
        var has_unit_subscription = $("input.hasUnitSubscription").val();
        var order = $("input.order").val();
        var allow = "DRAFT";
        var ob = JSON.stringify({"topic":topic,"writeQueueNum":write_queue_num,"readQueueNum":read_queue_num,
                                 "brokerAddress":broker_address, "clusterName":cluster_name, "permission":permission, "unit":unit, "hasUnitSubscription":has_unit_subscription, "order":order, "status":allow});
        if ($.trim(topic) === "") {
            alert(" no topic ?");
            hideCloud();
            return false;
        } else if ($.trim(cluster_name) === "" && $.trim(broker_address) == "") {
            alert("");
            hideCloud();
8            return false;
        } else {
            $.ajax({
                        async: false,
                        url: "cockpit/api/topic",
                        type: "PUT",
                        dataType: "json",
                        contentType: 'application/json',
                        data: ob,
                        success: function() {
                            location.reload(true);
                        },
                        error: function() {
                            hideCloud();
                        }
                    });
        }
    });

    $(".cancelTopic").click(function() {
        document.getElementById("addTopicDIV").style.display="none";
        document.getElementById("addButton").style.display="block";
        $("input.topic").val("");
        $("input.order").val("");
    });

    $(".cancelMes").click(function() {
        document.getElementById("sendMessageTestDIV").style.display="none";
        $("input.send_topic").val("");
        $("input.send_producerGroup").val("");
        $("input.send_tag").val("");
        $("input.send_key").val("");
        $("input.send_body").val("");
    });

    $(".send").click(function() {
        showCloud();
            var topic = $("input.send_topic").val();
            var producerGroup = $("input.send_producerGroup").val();
            var tag = $("input.send_tag").val();
            var key = $("input.send_key").val();
            var body = $("input.send_body").val();

            if ($.trim(topic) === "") {
                hideCloud();
                return false;
            } else {
                $.ajax({
                            async: false,
                            url: "cockpit/api/producer/" + producerGroup + "/" + topic + "/" + tag + "/" + key + "/" + body,
                            type: "GET",
                            dataType: "json",
                            contentType: 'application/json',
                            success: function(backdata) {
                                alert(backdata);
                                location.reload(true);
                            },
                            error: function() {
                                hideCloud();
                            }
                        });
            }
        });

    hideCloud();
});

function initTable(){
    var table = $('#topic').dataTable({
            "processing": true,
            "sAjaxSource": "cockpit/api/topic",
            "sAjaxDataProp": "data",
            "bPaginate": true,  //是否分页。
            "bFilter": true,  //是否可过滤。
            "bLengthChange": true, //是否允许自定义每页显示条数.
            "scrollX": true,
            "columns": [
                        { "data": "topic" },
                        { "data": "writeQueueNum" },
                        { "data": "readQueueNum" },
                        { "data": "permission" },
                        { "data": "unit" },
                        { "data": "hasUnitSubscription" },
                        { "data": "order" },
                        { "data": "createTime" },
                        { "data": "updateTime" },
                        { "data": "topic",
                          "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                                $(nTd).html("<a href='javascript:void(0);' " + "onclick='detailTable(\"" + oData.topic + "\")'>Detail</a>&nbsp;&nbsp;");
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


function detailTable(topic){
    var details = document.getElementById("topicDetailDIV");
    details.style.display = "none";
    if(undefined == dTable){

    }else{
        $('#topicDetail').dataTable().fnDestroy();
    }
    dTable = $('#topicDetail').dataTable({
        "processing": true,
        "sAjaxSource": "cockpit/api/topic/detail/" + topic,
        "sAjaxDataProp": "data",
        "bPaginate": true,  //是否分页。
        "bFilter": true,  //是否可过滤。
        "bLengthChange": true, //是否允许自定义每页显示条数.
        "scrollX": true,
        "columns": [
                    { "data": "topic" },
                    { "data": "clusterName" },
                    { "data": "brokerAddress" },
                    { "data": "status" },
                    { "data": "createTime" },
                    { "data": "updateTime" },
                    { "data": "id",
                      "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                        if (oData.status != "ACTIVE"){
                            $(nTd).html("<a href='javascript:void(0);' " + "onclick='_editFun(\"" + oData.id + "\",\"" + oData.topic + "\",\"" + oData.clusterName + "\",\"" + oData.brokerAddress + "\",\"" + oData.writeQueueNum + "\",\"" + oData.readQueueNum + "\",\"" + oData.permission + "\",\"" + oData.unit + "\",\"" + oData.hasUnitSubscription + "\",\"" + oData.order + "\")'>Approve</a>&nbsp;&nbsp;")
                            .append("<a href='javascript:void(0);' onclick='_deleteFun(\"" + oData.id + "\",\"" + oData.topic + "\",\"" + oData.clusterName + "\",\"" + oData.brokerAddress + "\")'>Delete</a>");
                         }else {
                            $(nTd).html("<a href='javascript:void(0);' " + "onclick='_sendFun(\"" + oData.topic + "\")'>test</a>&nbsp;&nbsp;")
                            .append("<a href='javascript:void(0);' onclick='_deleteFun(\"" + oData.id + "\",\"" + oData.topic + "\",\"" + oData.clusterName + "\",\"" + oData.brokerAddress + "\")'>Delete</a>");
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
    details.style.display = "block";
    details.style.position = "absolute";
    details.style.borderWidth = "thick";
    details.style.top = "150";
    details.style.background = "#FFCC80";
    details.style.opacity = "1";
    details.style.marginleft = "0";
    document.body.appendChild(details); //添加遮罩

}

function _deleteFun(id, topic, cluster_name, broker_address) {
    showCloud();
    $.ajax({
        async: false,
        data: JSON.stringify({"id":id, "topic":topic, "clusterName":cluster_name}),
        url: "cockpit/manage/topic/",
        type: "DELETE",
        dataType: "json",
        contentType: "application/json",
        success: function(backdata) {
            if (backdata){
                $.ajax({
                    async: false,
                        url: "cockpit/api/topic/" + id,
                        type: "DELETE",
                        dataType: "json",
                        contentType: "application/json; charset=UTF-8",
                        complete: function() {
                            hideCloud();
                            window.location.reload(true);
                        }
                    });
            }
        }
    });
}

function _editFun(id, topic, cluster_name, broker_address, write_queue_num, read_queue_num, permission, unit, has_unit_subscription,  order) {
        showCloud();
        var ob = JSON.stringify({"id":id, "topic":topic,"writeQueueNum":write_queue_num,"readQueueNum":read_queue_num,
                                 "brokerAddress":broker_address, "clusterName":cluster_name, "permission":permission,
                                 "unit":unit, "hasUnitSubscription":has_unit_subscription, "order":order});
        $.ajax({
            async: false,
            data: ob,
            url: "cockpit/manage/topic",
            type: "POST",
            dataType: "json",
            contentType: "application/json",
            success: function(backdata) {
                if (backdata) {
                    $.ajax({
                        async: false,
                        url: "cockpit/api/topic/" + id,
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json",
                        complete: function() {
                            window.location.reload(true);
                        }
                    });
                }

                hideCloud();
            }
        });
}

function _sendFun(topic) {
    document.getElementById("sendMessageTestDIV").style.display = "block";
    document.getElementById("topicDetailDIV").style.display="none";
    $("input.send_topic").val(topic);
}

function addButton(){
    document.getElementById("addButton").style.display="none";

    document.getElementById("addTopicDIV").style.display="block";
}

function closeDetail(){
    document.getElementById("topicDetailDIV").style.display="none";
}
