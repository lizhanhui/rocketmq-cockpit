var selectDefault = "----请选择-----";
var chart;

$(document).ready(function() {
    addcloud();
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


    $.get("cockpit/api/topic-progress", function(data) {
        var otext1 = document.createTextNode("Topic：");

        var selectT = document.createElement("select");
            selectT.id = "selectT";

        selectT.appendChild(createOption(null));

        data.forEach(function(topic) {
            selectT.appendChild(createOption(topic));
        });

        var selDiv = document.getElementById("selTopic");
            selDiv.appendChild(otext1);
            selDiv.appendChild(selectT);

    });

    $(".findTopicProgress").click(function() {
        addcloud();
        var x = [];
        var topic = "undefined" === typeof($("#selectT").children('option:selected').val()) ? "-1" : $("#selectT").children('option:selected').val();

        if (topic.indexOf("%") != -1){
                    topic = topic.replace(new RegExp("%","gm"), "%25");
                }

        if ($.trim(topic) === "" || "-1" === topic) {
            alert("consumer group should not be null");
            removecloud();
            return false;
        } else {
            $.ajax({
                async: false,
                url: "cockpit/api/topic-progress" + "/" + topic,
                type: "GET",
                contentType: "application/json; charset=UTF-8",
                dataType: "json",
                success: function(backdata) {
                    var line = topic;
                    var firstB = -1;
                    backdata.reverse();
                    backdata.forEach(function(consumeProgress){
                        var temp = [];
                        var time = consumeProgress.createTime.replace(new RegExp("-","gm"),"/");
                        temp.push((new Date(time)).getTime());
                        if (-1 === firstB){

                        }else{
                            temp.push(Math.round(100*(consumeProgress.brokerOffset - firstB)/(5*60))/100);

                            x.push(temp);
                        }

                        firstB = consumeProgress.brokerOffset;
                    });

                    showCharts(line, x);

                    removecloud();
                }
            });
        }
    });
    removecloud();
});

function createOption(text){
    var selOption = document.createElement("option");
    selOption.value = null === text ? -1 : text;
    selOption.innerHTML = null === text ? selectDefault : text;
    return selOption;
}

function showCharts(xsets, ysets) {
    $('#container').highcharts({
        chart: {
            type: 'spline'
        },
        title: {
            text: 'message tps'
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
                text: 'tps (producer per second)'
            },
            min: null,
            startOnTick: false
        },
        tooltip: {
            formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                    new Date(this.x) +': '+ this.y +' times/s';
            }
        },

        series: [{
            name: xsets,
            data:ysets
        }]
    });
}