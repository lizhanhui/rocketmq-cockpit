var selectDefault = "----请选择-----";
var chart;

$(document).ready(function() {
/** * Grid theme for Highcharts JS * @author Torstein Honsi */
    Highcharts.theme = {
        colors: ['#058DC7', '#50B432', '#ED561B', '#DDDF00', '#24CBE5', '#64E572', '#FF9655', '#FFF263', '#6AF9C4'],
        chart: {
            backgroundColor: {
                linearGradient: { x1: 0, y1: 0, x2: 1, y2: 1 },
                stops: [ [0, 'rgb(255, 255, 255)'], [1, 'rgb(240, 240, 255)'] ]
            },
            borderWidth: 2,
            plotBackgroundColor: 'rgba(255, 255, 255, .9)',
            plotShadow: true,
            plotBorderWidth: 1
        },
        title: {
            style: {
                color: '#000',
                font: 'bold 16px "Trebuchet MS", Verdana, sans-serif'
            }
        },
        subtitle: {
            style: {
                color: '#666666',
                font: 'bold 12px "Trebuchet MS", Verdana, sans-serif'
            }
        },
        xAxis: {
            gridLineWidth: 1,
            lineColor: '#000',
            tickColor: '#000',
            labels: {
                style: {
                color: '#000',
                font: '11px Trebuchet MS, Verdana, sans-serif'
                }
            },
            title: {
                style: { color: '#333', fontWeight: 'bold', fontSize: '12px', fontFamily: 'Trebuchet MS, Verdana, sans-serif' }
            }
        },
        yAxis: {
            minorTickInterval: 'auto',
            lineColor: '#000',
            lineWidth: 1,
            tickWidth: 1,
            tickColor: '#000',
            labels: {
                style: { color: '#000', font: '11px Trebuchet MS, Verdana, sans-serif' }
            },
            title: {
                style: { color: '#333', fontWeight: 'bold', fontSize: '12px', fontFamily: 'Trebuchet MS, Verdana, sans-serif' }
            }
        },
        legend: {
            itemStyle: { font: '9pt Trebuchet MS, Verdana, sans-serif', color: 'black' },
            itemHoverStyle: { color: '#039' },
            itemHiddenStyle: { color: 'gray' }
        },
        labels: {
            style: { color: '#99b' }
        },
        navigation: {
            buttonOptions: { theme: { stroke: '#CCCCCC' } }
        }
    };
    // Apply the theme
    Highcharts.setOptions({ global: { useUTC: false } });
    var highchartsOptions = Highcharts.setOptions(Highcharts.theme);


    $.get("cockpit/api/consume-progress", function(data) {
        var otext1 = document.createTextNode("Consumer Group：");
        var otext2 = document.createTextNode("Topic：");
        var otext3 = document.createTextNode("Broker：");
        var otext4 = document.createTextNode("Queue：");

        var selectC = document.createElement("select");
            selectC.id = "selectC";
        var selectT = document.createElement("select");
            selectT.id = "selectT";
        var selectB = document.createElement("select");
            selectB.id = "selectB";
        var selectQ = document.createElement("select");
            selectQ.id = "selectQ";

        selectC.appendChild(createOption(null));

        data.forEach(function(consumerGroup) {
            selectC.appendChild(createOption(consumerGroup));
        });

        var selDiv = document.getElementById("selConsumerGroup");
            selDiv.appendChild(otext1);
            selDiv.appendChild(selectC);
        var selDiv = document.getElementById("selTopic");
            selDiv.appendChild(otext2);
            selDiv.appendChild(selectT);
        var selDiv = document.getElementById("selBroker");
            selDiv.appendChild(otext3);
            selDiv.appendChild(selectB);
        var selDiv = document.getElementById("selQueue");
            selDiv.appendChild(otext4);
            selDiv.appendChild(selectQ);

        selectC.onchange = function (){
            var consumerGroup = $(this).children('option:selected').val();
            hideTopic();
            if (-1 != consumerGroup){
               $.ajax({
                   async: false,
                   url: "cockpit/api/consume-progress" + "/" + consumerGroup,
                   type: "GET",
                   contentType: "application/json; charset=UTF-8",
                   dataType: "json",
                   success: function(backdata) {
                       var selOption1 = document.createElement("option");
                       selOption1.value = -1;
                       selOption1.innerHTML = selectDefault;
                       var select = document.getElementById("selectT");
                       document.getElementById("selTopic").style.display = "inline";
                       select.appendChild(selOption1);
                       backdata.forEach(function(topic){
                            var selOption2 = document.createElement("option");
                            selOption2.value = topic;
                            selOption2.innerHTML = topic;
                            select.appendChild(selOption2);
                       });
                   }
               });
            }else{
            }
        } ;

        selectT.onchange = function (){
            var consumerGroup = $("#selectC").children('option:selected').val();
            var topic = $(this).children('option:selected').val();
            if (topic.indexOf("%") != -1){
                        topic = topic.replace(new RegExp("%","gm"), "%25");
                    }
            hideBroker();
            if (-1 != topic){
               $.ajax({
                   async: false,
                   url: "cockpit/api/consume-progress" + "/" + consumerGroup + "/" + topic,
                   type: "GET",
                   contentType: "application/json; charset=UTF-8",
                   dataType: "json",
                   success: function(backdata) {
                       var selOption1 = document.createElement("option");
                       selOption1.value = -1;
                       selOption1.innerHTML = selectDefault;
                       var select = document.getElementById("selectB");
                       document.getElementById("selBroker").style.display = "inline";
                       select.appendChild(selOption1);
                       backdata.forEach(function(broker){
                            var selOption2 = document.createElement("option");
                            selOption2.value = broker;
                            selOption2.innerHTML = broker;
                            select.appendChild(selOption2);
                       });
                   }
               });
            }else{
            }
        } ;

        selectB.onchange = function (){
            var consumerGroup = $("#selectC").children('option:selected').val();
            var topic = $("#selectT").children('option:selected').val();
            if (topic.indexOf("%") != -1){
                        topic = topic.replace(new RegExp("%","gm"), "%25");
                    }
            var broker = $(this).children('option:selected').val();
            hideQueue();
            if (-1 != broker){
               $.ajax({
                   async: false,
                   url: "cockpit/api/consume-progress" + "/" + consumerGroup + "/" + topic + "/" + broker,
                   type: "GET",
                   contentType: "application/json; charset=UTF-8",
                   dataType: "json",
                   success: function(backdata) {
                       var select = document.getElementById("selectQ");
                       document.getElementById("selQueue").style.display = "inline";
                       select.appendChild(createOption(null));
                       backdata.forEach(function(queue){
                            select.appendChild(createOption(queue));
                       });

                   }
               });
            }else{
            }
        } ;
    });

    $(".findConsumeProgress").click(function() {
        var x = [];
        var consumerGroup = "undefined" === typeof($("#selectC").children('option:selected').val()) ? "-1" : $("#selectC").children('option:selected').val();
        var topic = "undefined" === typeof($("#selectT").children('option:selected').val()) ? "-1" : $("#selectT").children('option:selected').val();

        if (topic.indexOf("%") != -1){
            topic = topic.replace(new RegExp("%","gm"), "%25");
        }

        var broker = "undefined" === typeof($("#selectB").children('option:selected').val()) ? "-1" : $("#selectB").children('option:selected').val();
        var queue = "undefined" === typeof($("#selectQ").children('option:selected').val()) ? "-1" : $("#selectQ").children('option:selected').val();
        if ($.trim(consumerGroup) === "" || "-1" === consumerGroup) {
            alert("consumer group should not be null");
            return false;
        } else {
            $.ajax({
                async: false,
                url: "cockpit/api/consume-progress" + "/" + consumerGroup + "/" + topic + "/" + broker + "/" + queue,
                type: "GET",
                contentType: "application/json; charset=UTF-8",
                dataType: "json",
                success: function(backdata) {
                    var line = consumerGroup;
                    if ("-1" === topic){
                        line = line + "" ;
                    }else{
                        line = line + " - " + topic;
                    }
                    if ("-1" === broker){
                        line = line + "";
                    }else{
                        line = line + " - " + broker;
                    }
                    if ("-1" === queue){
                        line = line + "";
                    }else{
                        line = line + " - " + queue;
                    }

                    backdata.forEach(function(consumeProgress){
                        var temp = [];
                        var time = consumeProgress.createTime.replace(new RegExp("-","gm"),"/");
                        temp.push((new Date(time)).getTime());
                        temp.push(consumeProgress.diff);
                        x.push(temp);
                    });

                    x.reverse();
                    showCharts(line, x);
                }
            });
        }
    });

});

function createOption(text){
    var selOption = document.createElement("option");
    selOption.value = null === text ? -1 : text;
    selOption.innerHTML = null === text ? selectDefault : text;
    return selOption;
}

function hideTopic(){
    document.getElementById("selTopic").style.display = "none";
    document.getElementById("selectT").innerHTML = "";
    hideBroker();
}

function hideBroker(){
    document.getElementById("selBroker").style.display = "none";
    document.getElementById("selectB").innerHTML = "";
    hideQueue();
}

function hideQueue(){
    document.getElementById("selQueue").style.display = "none";
    document.getElementById("selectQ").innerHTML = "";
}

function showCharts(xsets, ysets) {
    $('#container').highcharts({
        chart: {
            type: 'spline'
        },
        title: {
            text: 'message diff'
        },
        subtitle: {
            text: xsets
        },
        xAxis: {
            type: 'datetime',
            dateTimeLabelFormats: { // don't display the dummy year
                second:'%H:%M:%S',
                day:'%e. %b',
                month:'%b \'%y',
                year:'%Y'
            }
        },
        yAxis: {
            title: {
                text: 'diff (times)'
            },
            min: null,
            startOnTick: false
        },
        tooltip: {
            formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                    new Date(this.x) +': '+ this.y +' times';
            }
        },

        series: [{
            name: xsets,
            data:ysets
        }]
    });
}