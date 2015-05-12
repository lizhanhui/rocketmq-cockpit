$(document).ready(function() {
    $.get("cockpit/api/name-server-kv", function(data) {
        $(".table-content").children().remove();

        data.forEach(function(item) {
            var applyLink = $("<a href='javascript:;'  class='applyKV'>Apply</a>");
            var deleteLink = $("<a href='javascript:;' style='margin-left: 20px'  class='deleteKV'>Delete</a>");
            applyLink.attr("rel", item.id);
            deleteLink.attr("rel", item.id);
            var operation = $("<td></td>");

            if (item.status == "ACTIVE") {
                operation.append(deleteLink);
            } else {
                operation.append(applyLink).append(deleteLink);
            }

            var item = $("<tr><td>" + item.nameSpace + "</td><td>" + item.key + "</td><td>" + item.value + "</td><td>"
            + item.status + "</td></tr>");
            item.append(operation);
            $(".table-content").append(item);

        });
    });

    $(".addKV").click(function() {
        var nameSpace = $(".nameSpace").val();
        var key = $(".key").val();
        var value = $(".value").val();

        $.ajax({
            url: "cockpit/api/name-server-kv",
            type: "PUT",
            contentType: "application/json; charset=UTF-8",
            dataType: "json",
            data: JSON.stringify({nameSpace: nameSpace, key: key, value: value}),
            complete: function(data) {
                if (data.responseText != "") {
                    var dataJson = $.parseJSON(data.responseText);
                    var applyLink = $("<a href='javascript:;' class='applyKV'>Apply</a>");
                    var deleteLink = $("<a href='javascript:;' style='margin-left: 20px;' class='deleteKV'>Delete</a>");
                    applyLink.attr("rel", dataJson.id);
                    deleteLink.attr("rel", dataJson.id);
                    var operation = $("<td></td>").append(applyLink).append(deleteLink);
                    var item = $("<tr><td>" + dataJson.nameSpace + "</td><td>" + dataJson.key + "</td><td>" + dataJson.value + "</td><td>"
                    + dataJson.status + "</td></tr>");
                    item.append(operation);
                    $(".table-content").append(item);
                    $(".nameSpace").val("");
                    $(".key").val("");
                    $(".value").val("");
                }
            }
        });
    });

    $(".deleteKV").live("click", function() {
        var id = $(this).attr("rel");
        var row = $(this).parent().parent();
        $.ajax({
            async: false,
            url: "cockpit/manage/name-server-kv/delete/" + id,
            type: "DELETE",
            dataType: "json",
            contentType: "application/json",
            success: function(backdata) {
                if (backdata){
                   $.ajax({
                       url: "cockpit/api/name-server-kv/id/" + id,
                       type: "DELETE",
                       dataType: "json",
                       success: function(data) {
                           row.remove();
                       },
                       error: function(data) {
                           alert(data);
                       }
                   });
                }
            }
        });

    });

    $(".applyKV").live("click", function() {
        var id = $(this).attr("rel");

        var $status = $(this).parent().prev();
        $.get("cockpit/manage/name-server-kv/id/" + id, function(data) {
            $status.text(data.status);
            id.remove();
        });
    });

});