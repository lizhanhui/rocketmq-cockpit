var selectDefault = "----请选择-----";

$(document).ready(function() {
    addcloud();
    $.get("cockpit/api/project", function(data) {
        var text = document.createTextNode("Project: ");
        var divP = document.getElementById("divPro");
        var selectP = document.createElement("select");
        selectP.id = "selectP";

        selectP.appendChild(createOption(null));
        data.forEach(function(project) {
            selectP.appendChild(createOption(project.name));
        });

        divP.appendChild(text);
        divP.appendChild(selectP);
    });
});


function createOption(text){
    var selOption = document.createElement("option");
    selOption.value = null === text ? -1 : text;
    selOption.innerHTML = null === text ? selectDefault : text;
    return selOption;
}
