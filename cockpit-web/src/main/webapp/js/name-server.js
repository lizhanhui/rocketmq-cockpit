$(document).ready(function() {

    $.get("cockpit/api/name-server", function(data) {
        $(".table-content").children().remove();
        data.forEach(function(nameServerItem) {
            var operationLink = $("<a class='removeItem' href='javascript:;'>Remove</a>");
            operationLink.attr("rel", nameServerItem.id);
            var operation = $("<td></td>").append(operationLink);
            var item = $("<tr><td>" + nameServerItem.ip + ":" + nameServerItem.port + "</td><td>" + nameServerItem.createTime + "</td></tr>");
            item.append(operation);
            $(".table-content").append(item);
        });

    });


    $(".addNameServer").click(function() {
        var nameServer = $("input.newNameServer").val();
        if ($.trim(nameServer) === "") {
            return false;
        } else {
            sections = nameServer.split(":");
            $.ajax({
                async: false,
                url: "cockpit/api/name-server",
                type: "PUT",
                contentType: "application/json; charset=UTF-8",
                dataType: "json",
                data: JSON.stringify({ip: sections[0], port: sections[1]}),
                complete: function(data) {
                    if (data.responseText != "") {
                        var nsItem = $.parseJSON(data.responseText);
                        var operationLink = $("<a class='removeItem' href='javascript:;'>Remove</a>");
                        operationLink.attr("rel", nsItem.id);
                        var operation = $("<td></td>").append(operationLink);
                        var item = $("<tr><td>" + nsItem.ip + ":" + nsItem.port + "</td><td>" + nsItem.createTime + "</td></tr>");
                        item.append(operation);
                        $(".table-content").append(item);
                        $("input.newNameServer").val("");
                    } else {
                        alert("Error!");
                    }

                }
            });
        }
    });

    $(".removeItem").live("click", function() {
        var row = $(this).parent().parent();
        $.ajax({
            async: true,
            url: "cockpit/api/name-server/id/" + $(this).attr("rel"),
            type: "DELETE",
            dataType: "json",
            complete: function() {
                row.remove();
            }
        });
    });
});