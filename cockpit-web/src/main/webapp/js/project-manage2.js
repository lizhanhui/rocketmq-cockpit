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

        selectP.onchange = function() {
            showCloud();
            var project = $("#selectP").children('option:selected').val();
            if (-1 != project){
                $.ajax({
                    async:false,
                    url: "",//查询该project对应的Consumer Group
                    type: "",
                    data: project,
                    dataType: "",
                    contentType: "",
                    success: function(datas){
                        //获取结果集 插入表格
                        //获取第一个结果，并展示其积压曲线
                    }
                });

                $.ajax({
                    async: false,
                    url: "",//查询该project对应的Topic
                    type: "",
                    data: project,
                    dataType: "",
                    contentType: "",
                    success: function(){
                        //获取结果集 插入表格
                        //获取第一个结果，并展示其积压曲线
                    }
                });
            }else {
                hideCloud();
            }

        };
    });
});


function createOption(text){
    var selOption = document.createElement("option");
    selOption.value = null === text ? -1 : text;
    selOption.innerHTML = null === text ? selectDefault : text;
    return selOption;
}
