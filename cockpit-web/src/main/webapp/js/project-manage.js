$(document).ready(function() {
    $.get("cockpit/api/project", function(data) {
        $(".table-content").children().remove();
        data.forEach(function(project) {
            var operationLink = $("<a class='removeItem' href='javascript:;'>Remove</a>");
            var operation = $("<td></td>");
            operation.append(operationLink);
            var item = $("<tr><td style='display:none'>" + project.id + "</td><td>" + project.name + "</td><td>" + project.description + "</td></tr>");
            item.append(operation);
            $(".table-content").append(item);
        });
    });

    $(document).on("click", ".removeItem", function() {
        var row = $(this).parent().parent();
        var id = row.children().eq(0).html();

        $.ajax({
            async: false,
            url: "cockpit/api/project",
            type: "DELETE",
            dataType: "json",
            contentType: "application/json",
            data: id,
            success: function() {
                row.remove();
            },
            error: function() {
                location.reload(true);
            }
        });
    });

});