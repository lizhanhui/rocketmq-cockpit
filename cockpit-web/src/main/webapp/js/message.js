var selectDefault = "----请选择-----";
$(document).ready(function() {

    var select = document.getElementById("queryType");

    select.onchange = function (){
        var queryType = $(this).children('option:selected').val();

        if ("0" === queryType){
            document.getElementById("queryKEY").style.display = "none";
            document.getElementById("queryID").style.display = "block";
        }

        if ("1" === queryType){
            document.getElementById("queryID").style.display = "none";
            document.getElementById("queryKEY").style.display = "block";
        }

        document.getElementById("flow").style.display = "none";
    } ;

    $(".queryByID").click(function() {
        var msgId = $("input.msgId").val();
        if (msgId.length != 32){
            alert(" wrong message id ");
            return;
        }
        $.ajax({
            async: false,
            url: "cockpit/api/message" + "/" + msgId,
            type: "GET",
            contentType: "application/json; charset=UTF-8",
            dataType: "json",
            success: function(message) {
                $(".itable-content").children().remove();
                document.getElementById("flow").style.display = "none";
                if (null != message){
                    var operationLink = $("<a class='flowItem' href='javascript:;'>flow</a>");
                    operationLink.attr("rel", message.msgId);
                    var operation = $("<td colspan='2'>message flow: </td>").append(operationLink);

                    var consumerLink = $("<a class='consumerItem' href='javascript:;'>find</a>")
                    consumerLink.attr("rel", message.topic);
                    var consumer = $("<td colspan='2'>Connect Consumer: </td>").append(consumerLink);
                    var co = $("<td colspan='2'><div id='connectConsumer'></div></td>")

                    var pro = message.properties;
                    var cons = getMapValue(pro);
                    var item = $("<tr><td  colspan='2'>Message ID:" + message.msgId + "</td></tr>" + "<tr><td colspan='2'>Topic:" + message.topic + "</td></tr>" + "<tr><td colspan='2'>Tag:" + message.tags + "</td></tr>" + "<tr><td colspan='2'>Key:" + message.keys + "</td></tr>" + "<tr><td colspan='2'>Userproperties:" + cons + "</td></tr>" + "<tr><td colspan='2'>Borntime:" + message.bornTime + "</td></tr>" + "<tr><td colspan='2'>BornHost:" + message.bornHost + "</td></tr>" + "<tr><td colspan='2'>Storetime:" + message.storTime + "</td></tr>" + "<tr><td colspan='2'>StoreHost:" + message.storeHost + "</td></tr>" + "<tr><td colspan='2'>Messagebody:[length]" + message.body.length + "   <a href='cockpit/api/message/download/" + message.msgId + "'>download</a></td></tr>");

                    $(".itable-content").append(item);
                    $(".itable-content").append($("<tr></tr>").append(operation));
                    $(".itable-content").append($("<tr></tr>").append(consumer));
                    $(".itable-content").append($("<tr></tr>").append(co));
                }

                    $.ajax({
                        async: false,
                        url: "cockpit/api/message" + "/" + msgId,
                        type: "POST",
                        contentType: "application/json; charset=UTF-8",
                        dataType: "json",
                        success: function(backdata) {
                            if (null != backdata){
                                var title = $("<tr><td>Consumer</td><td>投递状态</td></tr>");
                                $(".itable-content").append(title);
                                backdata.forEach(function(messageTrack) {
                                    var item = $("<tr><td>" + messageTrack.consumerGroup + "</td><td>" + messageTrack.trackType + "</td></tr>");
                                    $(".itable-content").append(item);
                               });
                               var backlog = $("<tr><td colspan='2'>订阅了，而且消费了SUBSCRIBED_AND_CONSUMED,<br /> 订阅了，但是被过滤掉了 SUBSCRIBED_BUT_FILTERD, <br /> 订阅了，但是是PULL，结果未知 SUBSCRIBED_BUT_PULL, <br /> 订阅了，但是没有消费 SUBSCRIBED_AND_NOT_CONSUME_YET, <br /> 未知异常 UNKNOW_EXCEPTION,</td></tr>");
                               $(".itable-content").append(backlog);
                            }
                        }
                    });
            }
        });
    });

    $(".queryByKEY").click(function() {
        var topic = $("input.msgTopic").val();
        var key = $("input.msgKey").val();

        if ("" === topic || "" === key){
            alert(" please input topic and key");
            return;
        }

        $.ajax({
            async: false,
            url: "cockpit/api/message" + "/" + topic + "/" + key,
            type: "GET",
            contentType: "application/json; charset=UTF-8",
            dataType: "json",
            success: function(backdata) {
                document.getElementById("flow").style.display = "none";
                $(".ktable-content").children().remove();
                if (null != backdata){
                    backdata.forEach(function(message) {
                        var operationLink = $("<a class='operationItem' href='javascript:;'>detail</a>    <a class='flowItem' href='javascript:;'>flow</a>");
                        operationLink.attr("rel", message.msgId);
                        var operation = $("<td></td>").append(operationLink);
                        var item = $("<tr><td>" + message.msgId + "</td><td>" + message.tags + "</td><td>" + message.keys + "</td><td>" + message.storTime + "</td></tr>");
                        item.append(operation);
                        $(".ktable-content").append(item);
                   });
                }
            }
        });

    });

    $(document).on("click", ".operationItem", function() {
        var msgId = $(this).attr("rel");
        $("input.msgId").val(msgId);
        $.ajax({
            async: false,
            url: "cockpit/api/message" + "/" + msgId,
            type: "GET",
            contentType: "application/json; charset=UTF-8",
            dataType: "json",
            success: function(message) {
                document.getElementById("queryKEY").style.display = "none";
                document.getElementById("flow").style.display = "none";
                document.getElementById("queryID").style.display = "block";
                document.getElementById("queryType").options[0].selected=true;
                $(".itable-content").children().remove();
                    var operationLink = $("<a class='flowItem' href='javascript:;'>flow</a>");
                    operationLink.attr("rel", message.msgId);
                    var operation = $("<td colspan='2'>message flow: </td>").append(operationLink);

                    var consumerLink = $("<a class='consumerItem' href='javascript:;'>find</a>")
                    consumerLink.attr("rel", message.topic);
                    var consumer = $("<td colspan='2'>Connect Consumer: </td>").append(consumerLink);
                    var co = $("<td colspan='2'><div id='connectConsumer'></div></td>")
                var pro = message.properties;
                var cons = getMapValue(pro);
                var item = $("<tr><td colspan='2'>Message ID:" + message.msgId + "</td></tr>" + "<tr><td colspan='2'>Topic:" + message.topic + "</td></tr>" + "<tr><td colspan='2'>Tag:" + message.tags + "</td></tr>" + "<tr><td colspan='2'>Key:" + message.keys + "</td></tr>" + "<tr><td colspan='2'>Userproperties:" + cons + "</td></tr>" + "<tr><td colspan='2'>Borntime:" + message.bornTime + "</td></tr>" + "<tr><td colspan='2'>BornHost:" + message.bornHost + "</td></tr>" + "<tr><td colspan='2'>Storetime:" + message.storTime + "</td></tr>" + "<tr><td colspan='2'>StoreHost:" + message.storeHost + "</td></tr>" + "<tr><td colspan='2'>Messagebody:[length]" + message.body.length + "   <a href='cockpit/api/message/download/" + message.msgId + "'>download</a></td></tr>");
                $(".itable-content").append(item);
                    $(".itable-content").append($("<tr></tr>").append(operation));
                    $(".itable-content").append($("<tr></tr>").append(consumer));
                    $(".itable-content").append($("<tr></tr>").append(co));


                    $.ajax({
                        async: false,
                        url: "cockpit/api/message" + "/" + msgId,
                        type: "POST",
                        contentType: "application/json; charset=UTF-8",
                        dataType: "json",
                        success: function(backdata) {
                            if (null != backdata){
                                var title = $("<tr><td>Consumer</td><td>投递状态</td></tr>");
                                $(".itable-content").append(title);
                                backdata.forEach(function(messageTrack) {
                                    var item = $("<tr><td>" + messageTrack.consumerGroup + "</td><td>" + messageTrack.trackType + "</td></tr>");
                                    $(".itable-content").append(item);
                               });
                               var backlog = $("<tr><td colspan='2'>订阅了，而且消费了SUBSCRIBED_AND_CONSUMED,<br /> 订阅了，但是被过滤掉了 SUBSCRIBED_BUT_FILTERD, <br /> 订阅了，但是是PULL，结果未知 SUBSCRIBED_BUT_PULL, <br /> 订阅了，但是没有消费 SUBSCRIBED_AND_NOT_CONSUME_YET, <br /> 未知异常 UNKNOW_EXCEPTION,</td></tr>");
                               $(".itable-content").append(backlog);
                            }
                        }
                    });

            },
            error: function() {
                alert("Error");
            }
        });
    });

    $(document).on("click", ".flowItem", function() {
        var msgId = $(this).attr("rel");
        $("input.msgId").val(msgId);
        $.ajax({
            async: false,
            url: "cockpit/api/message" + "/flow/" + msgId,
            type: "GET",
            contentType: "application/json; charset=UTF-8",
            dataType: "json",
            success: function(backdata) {
                document.getElementById("flow").style.display = "block";
                $(".ftable-content").children().remove();
                if (null != backdata){
                    backdata.forEach(function(cockpitMessageFlow) {
                        var item = $("<tr><td>source:" + cockpitMessageFlow.source + "</td><td>status:" + cockpitMessageFlow.status + "</td><td>from:" + cockpitMessageFlow.ipFrom + "</td><td>to:" + cockpitMessageFlow.ipTo + "</td><td>time:" + new Date(cockpitMessageFlow.timeStamp) + "</td></tr>");

                        $(".ftable-content").append(item);
                   });
                }
            },
            error: function() {
                alert("Error");
            }
        });
    });

    $(document).on("click", ".consumerItem", function() {
        var topic = $(this).attr("rel");
        $.ajax({
            async: false,
            url: "cockpit/api/message" + "/query/" + topic,
            type: "GET",
            contentType: "application/json; charset=UTF-8",
            dataType: "json",
            success: function(backdata) {
                if (null != backdata){
                    var text = document.createTextNode("Consumer Group：");
                    var selectC = document.createElement("select");
                    selectC.id = "selectC";
                    selectC.appendChild(createOption(null));
                    backdata.forEach(function(consumerGroup) {
                        selectC.appendChild(createOption(consumerGroup));
                    });
                    var selDiv = document.getElementById("connectConsumer");
                    selDiv.appendChild(text);
                    selDiv.appendChild(selectC);

                    selectC.onchange = function (){
                        var consumerGroup = "undefined" === typeof($("#selectC").children('option:selected').val()) ? "-1" : $("#selectC").children('option:selected').val();

                        if (-1 != consumerGroup){
                           $.ajax({
                               async: false,
                               url: "cockpit/api/consumer-group" + "/client/" + consumerGroup,
                               type: "GET",
                               contentType: "application/json; charset=UTF-8",
                               dataType: "json",
                               success: function(backdata) {
                                   if ( null != backdata){

                                       var selDiv = document.getElementById("connectConsumer");
                                       if (null != document.getElementById("selectT")){
                                            document.getElementById("selectT").innerHTML = "";
                                            var select = document.getElementById("selectT");
                                       }else{
                                            var textT = document.createTextNode(" Client: ");
                                            selDiv.appendChild(textT);
                                            var select = document.createElement("select");
                                            select.id = "selectT";
                                       }
                                       var selOption1 = document.createElement("option");
                                       selOption1.value = -1;
                                       selOption1.innerHTML = selectDefault;
                                       select.appendChild(selOption1);
                                       backdata.forEach(function(connection){
                                            var selOption2 = document.createElement("option");
                                            selOption2.value = connection.clientId;
                                            selOption2.innerHTML = connection.clientId;
                                            select.appendChild(selOption2);
                                       });
                                       if (null == document.getElementById("selectT")){
                                           selDiv.appendChild(select);
                                           var addB = document.createElement("input");
                                           addB.type = "button";
                                           addB.value = "resend";
                                           addB.onclick = function(){
                                                var consumerGroup = "undefined" === typeof($("#selectC").children('option:selected').val()) ? "-1" : $("#selectC").children('option:selected').val();
                                                var clientId = "undefined" === typeof($("#selectT").children('option:selected').val()) ? "-1" : $("#selectT").children('option:selected').val();
                                                var msgId = $("input.msgId").val();
                                                if ("-1" === consumerGroup || "-1" === clientId){
                                                    alert(" we need consumer group and client id ");
                                                    return;
                                                }

                                                $.ajax({
                                                    async: false,
                                                    url: "cockpit/api/message" + "/resend/" + consumerGroup + "/" + clientId + "/" + msgId,
                                                    type: "GET",
                                                    contentType: "application/json; charset=UTF-8",
                                                    dataType: "json",
                                                    success: function(backdata) {
                                                        alert(backdata);
                                                    }
                                                });

                                           };
                                           selDiv.appendChild(addB);

                                       }
                                   }
                               }
                           });

                        }else{
                        }
                    } ;
                }
            },
            error: function() {
                alert("Error");
            }
        });
    });
});

function getMapValue(sourceMap){
    var cons = "{";
    var index = 0;
    for (var i in sourceMap){
        if(index++ > 0){
            cons = cons + ","
        }
        cons = cons + i + "=" + sourceMap[i];
    }
    cons = cons + "}";
    return cons;
}

function createOption(text){
    var selOption = document.createElement("option");
    selOption.value = null === text ? -1 : text;
    selOption.innerHTML = null === text ? selectDefault : text;
    return selOption;
}