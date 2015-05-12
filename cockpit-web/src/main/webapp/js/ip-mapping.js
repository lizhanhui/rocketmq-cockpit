$(document).ready(function() {

    $.get("cockpit/api/ip", function(data) {
        $(".table-content").children().remove();
        data.forEach(function(ip) {
            var operationLink = $("<a class='removeItem' href='javascript:;'>Remove</a>");
            operationLink.attr("rel", ip.id);
            var operation = $("<td></td>").append(operationLink);
            var item = $("<tr><td>" + ip.innerIP + "</td><td>" + ip.publicIP + "</td></tr>");
            item.append(operation);
            $(".table-content").append(item);
        });
    });


    $(".addMapping").click(function() {
        var innerIP = $("input.innerIP").val();
        var publicIP = $("input.publicIP").val();
        if ($.trim(innerIP) === "" || $.trim(publicIP) == "") {
            return false;
        } else {
            $.ajax({
                url: "cockpit/api/ip",
                type: "PUT",
                dataType: "json",
                contentType: "application/json;charset=UTF-8",
                data: JSON.stringify({innerIP: innerIP, publicIP:publicIP}),
                async: true,
                success: function(data) {
                    var item = $("<tr><td>" + data.innerIP + "</td><td>" + data.publicIP + "</td><td><a class='removeItem' rel='\'' + data.id + '\'' href='javascript:;'>Remove</a></td></tr>");
                    $(".table-content").append(item);
                    $("input.innerIP").val("");
                    $("input.publicIP").val("");
                }
            });
        }
    });

    $(".removeItem").live("click", function() {
        var row = $(this).parent().parent();
        var id = $(this).attr("rel");
        $.ajax({
            async: true,
            url: "cockpit/api/ip/id/" + id,
            type: "DELETE",
            success: function() {
                row.remove();
            },
            error: function() {
                alert("Error");
            }
        });
    });
});