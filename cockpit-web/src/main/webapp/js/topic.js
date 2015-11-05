var oTable;
var dTable;

$(document).ready(function() {
    addcloud();
    showCloud();
    oTable = initTable();
    document.getElementById("sendMessageTestDIV").style.display="none";
    document.getElementById("topicDetailDIV").style.display="none";

    $(".addTopic").click(function() {
        showCloud();
        var brokerId = "undefined" === typeof($("#brokerList").children('option:selected').val()) ? "-1" : $("#brokerList").children('option:selected').val();

        var topic = $("input.topic").val();
        var write_queue_num = $("input.writeQueueNum").val();
        var read_queue_num = $("input.readQueueNum").val();

        var cluster_name = $("input.clusterName").val();
        var permission = $("input.permission").val();
        var allow = "DRAFT";
        var broker_address = "undefined" === typeof($("#brokerList").children('option:selected').val()) ? "-1" : $("#brokerList").children('option:selected').text();
        var topicMetadata = {"topic":topic,"clusterName":cluster_name};
        var broker = {"id": brokerId, "clusterName": cluster_name, "address": broker_address};
        var ob = JSON.stringify({"topicMetadata": topicMetadata, "broker": broker, "writeQueueNum":write_queue_num,"readQueueNum":read_queue_num,
                 "permission":permission});

        if ($.trim(topic) === "") {
             alert(" no topic ?");
             hideCloud();
             return false;
        } else if ($.trim(cluster_name) === "" && $.trim(broker_address) == "") {
             alert("error");
             hideCloud();
             return false;
        } else {
            if ("-1" === brokerId){
                if (window.confirm("no broker ? add it to all ?")){
                    alert(" will add this topic to all brokers .");
                }else {
                    alert(" add your broker.");
                    hideCloud();
                    return;
                }
            }
            $.ajax({
                async: false,
                url: "cockpit/manage/topic",
                type: "POST",
                dataType: "json",
                contentType : "application/json; charset=utf-8",
                data: ob,
                success: function(data) {
                    if (data){
                        $.ajax({
                            async: false,
                            url: "cockpit/api/topic",
                            type: "POST",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            data: ob,
                            success: function(data) {
                                if (data){
                                    alert("SUCCESS");
                                }
                            },
                            error: function(e) {
                                alert(e.statusText);
                                hideCloud();
                            }
                        });
                    }
                    location.reload(true);
                },
                error: function(msg) {
                    alert(msg.statusText);
                    hideCloud();
                }
            });
        }

    });

    $(".cancelTopic").click(function() {
        document.getElementById("addTopicDIV").style.display="none";
        document.getElementById("topicDetailButton").style.display="block";
        document.getElementById("topicDetailTable").style.display="block";
    });

    $(".cancelMes").click(function() {
        document.getElementById("sendMessageTestDIV").style.display="none";
        document.getElementById("topicDIV").style.display="block";
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
            { "data": "order" },
            { "data": "status"},
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
    $("input.topic").val(topic);
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
            { "data": "topicMetadata.topic" },
            { "data": "broker.clusterName" },
            { "data": "broker.address" },
            { "data": "status" },
            { "data": "writeQueueNum" },
            { "data": "readQueueNum" },
            { "data": "permission" },
            { "data": "createTime" },
            { "data": "updateTime" },
            { "data": "syncTime" },
            { "data": "topicMetadata.topic",
                "fnCreatedCell": function (nTd, sData, oData, iRow, iCol) {
                    if (oData.status != "ACTIVE"){
                        $(nTd).html("<a href='javascript:void(0);' " + "onclick='_editFun(\"" + oData.broker.id + "\",\"" + oData.topicMetadata.topic + "\",\"" + oData.broker.clusterName + "\",\"" + oData.broker.address  + "\",\"" + oData.writeQueueNum + "\",\"" + oData.readQueueNum + "\",\"" + oData.permission + "\",\"" + oData.topicMetadata.order + "\")'>Approve</a>&nbsp;&nbsp;")
                            .append("<a href='javascript:void(0);' onclick='_deleteFun(\"" + oData.broker.id + "\",\"" + oData.topicMetadata.id + "\",\"" + oData.topicMetadata.topic + "\",\"" + oData.broker.clusterName + "\",\"" + oData.broker.address + "\")'>Delete</a>");
                    }else {
                        $(nTd).html("<a href='javascript:void(0);' " + "onclick='_sendFun(\"" + oData.topicMetadata.topic + "\")'>test</a>&nbsp;&nbsp;")
                            .append("<a href='javascript:void(0);' onclick='_deleteFun(\"" + oData.broker.id + "\",\"" + oData.topicMetadata.id + "\",\"" + oData.topicMetadata.topic + "\",\"" + oData.broker.clusterName + "\",\"" + oData.broker.address + "\")'>Delete</a>");
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

function _deleteFun(brokerId, topicId, topic, cluster_name, broker_address) {
    showCloud();
    $.ajax({
        async: false,
        url: "cockpit/manage/topic/" + brokerId + "/" + topicId,
        type: "DELETE",
        dataType: "json",
        contentType: "application/json",
        success: function(backdata) {
            if (backdata){
                $.ajax({
                    async: false,
                    url: "cockpit/api/topic/" + topicId + "/" + brokerId,
                    type: "DELETE",
                    dataType: "json",
                    contentType: "application/json; charset=UTF-8",
                    complete: function() {
                        alert("SUCCESS");
                        hideCloud();
                    }
                });
            }
        },
        error: function(){
            alert("delete topic on broker error");
            hideCloud();
        }
    });


    var cells = dTable.children().eq(1).children();
    var index = 0;
    var max = cells.length
    while (index < max){
        cell = cells.eq(index);
        if (cell.children().eq(0).html() === topic && cell.children().eq(1).html() === cluster_name && cell.children().eq(2).html() === broker_address){
            cell.children().eq(3).html("DELETE");
        }
        index++;
    }

    if (dTable.children().eq(1).children().length === 0){
        window.location.reload(true);
    }

    hideCloud();
}

function _editFun(id, topic, cluster_name, broker_address, write_queue_num, read_queue_num, permission, order) {
    showCloud();
    var topicMetadata = {"topic":topic,"clusterName":cluster_name};
    var broker = {"id": id, "clusterName": cluster_name, "address": broker_address};
    var ob = JSON.stringify({"topicMetadata": topicMetadata, "broker": broker, "writeQueueNum":write_queue_num,"readQueueNum":read_queue_num,
                     "permission":permission});

    $.ajax({
        async: false,
        url: "cockpit/manage/topic",
        type: "POST",
        dataType: "json",
        contentType : "application/json; charset=utf-8",
        data: ob,
        success: function(data) {
            if (data){
                $.ajax({
                    async: false,
                    url: "cockpit/api/topic/activate",
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: ob,
                    success: function(data) {
                        if (data){
                            alert("SUCCESS");
                            var cells = dTable.children().eq(1).children();
                            var index = 0;
                            var max = cells.length
                            while (index < max){
                                cell = cells.eq(index);
                                if (cell.children().eq(0).html() === topic && cell.children().eq(1).html() === cluster_name && cell.children().eq(2).html() === broker_address){
                                    cell.children().eq(3).html("ACTIVE");
                                }
                                index++;
                            }
                        hideCloud();
                        }
                    },
                    error: function(e) {
                        alert(e.statusText);
                        hideCloud();
                    }
                });
            }
        },
        error: function(msg) {
            alert(msg.statusText);
            hideCloud();
        }
    });
}

function _sendFun(topic) {
    document.getElementById("sendMessageTestDIV").style.display = "block";
    document.getElementById("topicDetailDIV").style.display="none";
    document.getElementById("topicDIV").style.display="none";
    $("input.send_topic").val(topic);
}


function closeDetail(){
    document.getElementById("topicDetailDIV").style.display="none";
}

function addDetail(){
    $.ajax({
        async: false,
        url: "cockpit/api/broker",
        type: "GET",
        dataType: "json",
        contentType: "application/json",
        success: function(brokers){
            var selectB = document.getElementById("brokerList");
            brokers.forEach(function(broker) {
                var option = document.createElement("option");
                option.value = broker.id;
                option.innerHTML = broker.address;
                selectB.appendChild(option);
            });
        }
    });
    document.getElementById("addTopicDIV").style.display="block";
    document.getElementById("topicDetailButton").style.display="none";
    document.getElementById("topicDetailTable").style.display="none";
}
