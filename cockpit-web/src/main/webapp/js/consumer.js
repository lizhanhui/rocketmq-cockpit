$(document).ready(function() {
    $(".queryTopic").click(function() {
        var groupName = $("input.groupName").val();
        var topic = $("input.topic").val();
        var broker = $("input.broker").val();
        if ($.trim(groupName) === "") {
            return false;
        } else {
            $.post("rocketmq/cpJson", {groupName:groupName}, function(data) {

                $(".table-content").children().remove();
                data.forEach(function(ConsumerProgress) {
                    var item = $("<tr><td><a class='queryItem' href='javascript:;'>" + ConsumerProgress.topic + "<a></td><td>" + ConsumerProgress.brokerName + "</td><td>" + ConsumerProgress.queueId + "</td><td>" + ConsumerProgress.diff + "</td></tr>");
                    $(".table-content").append(item);
                });
            });
        }
    });

        $(".queryItem").live("click", function() {
                var row = $(this).parent().parent();
                var topic = row.children().eq(0).html();
                var brokerName = row.children().eq(1).html();
                var queueId = row.children().eq(2).html();
                if ($.trim(topic) === "" ) {
                            return false;
                }
                $.ajax({
                    async: false,
                    data: JSON.stringify({topic: topic, brokerName: brokerName, queueId: queueId}),
                    url: "rocketmq/topicP",
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json",
                    complete: function() {
                        $('#container').highcharts({
                                title: {
                                    text: 'Monthly Average Temperature',
                                    x: -20 //center
                                },
                                subtitle: {
                                    text: 'Source: WorldClimate.com',
                                    x: -20
                                },
                                xAxis: {
                                    categories: ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun','Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec']
                                },
                                yAxis: {
                                    title: {
                                        text: 'Temperature (°C)'
                                    },
                                    plotLines: [{
                                        value: 0,
                                        width: 1,
                                        color: '#808080'
                                    }]
                                },
                                tooltip: {
                                    valueSuffix: '°C'
                                },
                                legend: {
                                    layout: 'vertical',
                                    align: 'right',
                                    verticalAlign: 'middle',
                                    borderWidth: 0
                                },
                                series: [{
                                    name: 'Tokyo',
                                    data: [7.0, 6.9, 9.5, 14.5, 18.2, 21.5, 25.2, 26.5, 23.3, 18.3, 13.9, 9.6]
                                }, {
                                    name: 'New York',
                                    data: [-0.2, 0.8, 5.7, 11.3, 17.0, 22.0, 24.8, 24.1, 20.1, 14.1, 8.6, 2.5]
                                }, {
                                    name: 'Berlin',
                                    data: [-0.9, 0.6, 3.5, 8.4, 13.5, 17.0, 18.6, 17.9, 14.3, 9.0, 3.9, 1.0]
                                }, {
                                    name: 'London',
                                    data: [3.9, 4.2, 5.7, 8.5, 11.9, 15.2, 17.0, 16.6, 14.2, 10.3, 6.6, 4.8]
                                }]
                            });
                    }
                });
            });
});