(function(){
    'use strict';

    /**
    * @ngdoc function
    * @name cockpit.controller:MainCtrl
    * @description
    * # MainCtrl
    * Controller of cockpit
    */
    angular
    .module('cockpit')
    .controller('LoginCtrl', LoginController);

    LoginController.$inject = ['$scope', '$location', '$http', '$window', 'UserService', '$cookieStore'];
    function LoginController($scope, $location, $http, $window, UserService, $cookieStore) {
        if ("yes" == $cookieStore.get("isLogin")) {
            UserService.isLogin = true;
        }

        if (UserService.isLogin) {
            $location.path('/dashboard');
        }else{
            $scope.kaptchaImage = function(){
                document.getElementById("kaptchaImage").src = "cockpit/captcha-image?"  + Math.floor(Math.random() * 100);
            };
            $scope.message = "";

            $scope.submit = function() {
                var loginLoad = 'j_username=' + $scope.j_username + '&j_password=' + $scope.j_password + '&kaptcha=' + $scope.kaptcha;
                var config = {
                    headers: {'Content-Type':'application/x-www-form-urlencoded; charset=UTF-8'}
                };
                $http.post('j_spring_security_check', loginLoad, config)
                .success(function(data, status, headers, config){
                    UserService.isLogin = true;
                    $cookieStore.put("isLogin", "yes");
                    $location.path('/dashboard');
                }).error(function(data, status, headers, config){
                    $cookieStore.remove("isLogin");
                    $scope.message = "login failed."
                });

                return false;
            }
        }
    }
})();

(function() {
    'use strict';

    angular
    .module('cockpit')
    .controller('DashboardCtrl', DashboardController);

    DashboardController.$inject = ['$scope', '$state','$location', 'UserService'];
    function DashboardController($scope, $state, $location, UserService) {
        if (!UserService.isLogin) {
            $location.path('/login');
        }else{
            $scope.$state = $state;
        }
    }
})();

(function() {
    'use strict';

    angular.module('cockpit')
    .controller('ProjectCtrl', ProjectController);
    ProjectController.$inject = ['$scope', '$http' , '$location', 'UserService', '$cookieStore'];

    function ProjectController($scope, $http, $location, UserService, $cookieStore ){
        if (!UserService.isLogin) {
            $location.path('/login');
        }else {
            var dateB = new Date();
            var offset = dateB.getTimezoneOffset() * 60000;
            Highcharts.setOptions({
                global : {
                    useUTC : false
                }
            });
    		$scope.set = function() {
                reDrow();
    		};

            function reDrow(){
                initAdd();
                clearGroups();
                clearTopics();
    			activate();
            }

            function initAdd() {
                $scope.visibleG = false;
                $scope.visibleT = false;
                $scope.groupMessage = "";
                $scope.topicMessage = "";
                $scope.consumerGroups = null;
                $scope.consumerGroup = null;
                $scope.topics = null;
                $scope.topic = null;
            }

            $scope.showGroups = function(projectId) {
                $http({
                    url: 'cockpit/api/project/' + projectId + "/unconsumer-groups",
                    method: 'GET',
                    responseType: 'json'
                }).success(function(data, status, headers, config) {
                    $scope.groupMessage = "";
                    $scope.visibleG = !$scope.visibleG;
                    $scope.consumerGroups = data;
                    if (null != data) {
                        $scope.consumerGroup = data[0];
                    }
                }).error(function(data, status, headers, config) {

                });
            };

            $scope.showTopics = function(projectId){
                $http({
                    url: 'cockpit/api/project/' + projectId + "/untopics",
                    method: 'GET',
                    responseType: 'json'
                }).success(function(data, status, headers, config) {
                    $scope.topicMessage = "";
                    $scope.visibleT = !$scope.visibleT;
                    $scope.topics = data;
                    if (null != data) {
                        $scope.topic = data[0];
                    }
                }).error(function(data, status, headers, config) {

                });
            }

            $scope.addGroup = function(projectId){
                if (null != $scope.consumerGroup){
                    $http({
                        url: 'cockpit/api/project/' + projectId + "/" + $scope.consumerGroup.id + "/0",
                        method: 'PUT'
                    }).success(function (data, status, headers, config) {
                        $scope.visibleG = !$scope.visibleG;
                        $scope.groupMessage = 'add group ok.';
                        reDrow();
                    }).error(function (data, status, headers, config) {
                        $scope.visibleG = !$scope.visibleG;
                        $scope.groupMessage = 'add group error.';
                    });
                }else {
                    $scope.visibleG = !$scope.visibleG;
                    $scope.groupMessage = 'no group to add.';
                }
            }

            $scope.addTopic = function(projectId){
                if (null != $scope.topic) {
                    $http({
                        url: 'cockpit/api/project/' + projectId + "/0/" + $scope.topic.id,
                        method: 'PUT'
                    }).success(function (data, status, headers, config) {
                        $scope.visibleT = !$scope.visibleT;
                        $scope.topicMessage = 'add group ok.';
                        reDrow();
                    }).error(function (data, status, headers, config) {
                        $scope.visibleT = !$scope.visibleT;
                        $scope.topicMessage = 'add group error.';
                    });
                }else {
                    $scope.visibleT = !$scope.visibleT;
                    $scope.topicMessage = 'no topic to add';
                }
            }

            $http({
                method:'GET',
                //headers: {'Content-type': 'application/json;charset=UTF-8'},
                url: 'cockpit/api/project',
                responseType: 'json'
            }).success(function(data, status, headers, config) {
                $scope.projects=data;
                $scope.project = $scope.projects[0];
                activate();
            }).error(function(data, status, headers, config) {

            });

            function activate() {
                $http({
                    method:'GET',
                    //headers: {'Content-type': 'application/json;charset=UTF-8'},
                    url: 'cockpit/api/project/' + $scope.project.id + "/consumer-groups",
                    responseType: 'json'
                }).success(function(data, status, headers, config) {
    				if (data != null) {
    					data.forEach(function(consumerGroup){
    			            var x = [];
    			            var y = [];
    			            var yin = []
    			                $http({
    			                    method:'GET',
    			                    //headers: {'Content-type': 'application/json;charset=UTF-8'},
    			                    url: "cockpit/api/consume-progress" + "/" + consumerGroup.groupName + "/" + "-1" + "/" + "-1" + "/" + "-1",
    			                    responseType: 'json'
    			                }).success(function(data, status, headers, config) {
    			                    if (data != null) {
    									data.forEach(function (consumeProgress) {
    				                        var temp = [];
    				                        var time = consumeProgress.createTime.replace(new RegExp("-", "gm"), "/");
    				                        temp.push((new Date(time)).getTime() -  offset);
    				                        temp.push(consumeProgress.diff);
    				                        yin.push(temp);
    				                    });
    				                    if (yin.length > 0 ) {
            								yin.reverse();
    				                        if ("undefined" ===  typeof($scope.chartConfig) ){
    				                            activate1(consumerGroup.groupName, yin);
                                            }else if($scope.chartConfig.series.length === 0) {
                                                activate1(consumerGroup.groupName, yin);
    				                        }else {
    				                            addLine(consumerGroup.groupName, yin);
    				                        }
    				                    }
    			                    }
    			                }).error(function(data, status, headers, config) {

    			                });

    	                });
    				}
                }).error(function(data, status, headers, config) {
                    console.log(status);
                });

                $http({
                    url: 'cockpit/api/project/' + $scope.project.id + '/topics',
                    method: "GET",
                    responseType: 'json'
                }).success(function(data, status, headers, config) {
                    data.forEach(function(topicMetadata) {
    					var x = [];
    					var y = [];
    					var yin = []
    						$http({
    							method:'GET',
    							//headers: {'Content-type': 'application/json;charset=UTF-8'},
    							url: "cockpit/api/topic-progress" + "/" + topicMetadata.topic,
    							responseType: 'json'
    						}).success(function(data, status, headers, config) {
    							data.forEach(function (topicPerSecond) {
    								var temp = [];
    								temp.push(topicPerSecond.timeStamp);
    								temp.push(topicPerSecond.tps);
    								yin.push(temp);
    							});
    							if (yin.length > 0 ) {
    								yin.reverse();
    								if ("undefined" ===  typeof($scope.chartConfig2) ){
    									activate2(topicMetadata.topic, yin);
    								}else if ($scope.chartConfig2.series.length === 0) {
    								    activate2(topicMetadata.topic, yin);
    								}else {
    									addLine2(topicMetadata.topic, yin);
    								}
    							}

    						}).error(function(data, status, headers, config) {

    						});

                    });
                }).error(function(data, status, headers, config) {

                });
            };

            function activate1(x, y) {
                $scope.chartConfig = {
                    options: {
                        chart: {
                            type: 'line'
                        },
                        xAxis: {
                            type: 'datetime',
                            dateTimeLabelFormats: { // don't display the dummy year
                                second: '%H:%M:%S',
                                day: '%e. %b',
                                month: '%b \'%y',
                                year: '%Y'
                            }
                        },
                        yAxis: {
                            title: {
                                text: 'diff(times)'
                            },
                            min: null,
                            startOnTick: false
                        },
                        tooltip: {
                            formatter: function () {
                                return '<b>' + this.series.name + '</b><br/>' +
                                new Date(this.x) + ': ' + this.y + ' times';
                            }
                        }
                    },
                    series: [{
                        name: x,
                        data: y
                    }],
                    title: {
                        text: $scope.project.name
                    },
                    subtitle: {
                        text: 'diff'
                    }
                }
            };

            function addLine(x, y){
                $scope.chartConfig.series.push({name:x, data: y});
            };

            function clearGroups(){
                var series = [];
                $scope.chartConfig.series = series;
            }

            function activate2(x, y) {
                $scope.chartConfig2 = {
                    options: {
                        chart: {
                            type: 'line'
                        },
                        xAxis: {
                            type: 'datetime',
                            dateTimeLabelFormats: { // don't display the dummy year
                                second: '%H:%M:%S',
                                day: '%e. %b',
                                month: '%b \'%y',
                                year: '%Y'
                            }
                        },
                        yAxis: {
                            title: {
                                text: 'tps(times)'
                            },
                            min: null,
                            startOnTick: false
                        },
                        tooltip: {
                            formatter: function () {
                                return '<b>' + this.series.name + '</b><br/>' +
                                new Date(this.x) + ': ' + this.y + ' times';
                            }
                        }
                    },
                    series: [{
                        name: x,
                        data: y
                    }],
                    title: {
                        text: $scope.project.name
                    },
                    subtitle: {
                        text: 'tps'
                    }
                }
            };

            function addLine2(x, y){
                $scope.chartConfig2.series.push({name:x, data: y});
            }

            function clearTopics() {
                var series = [];
                $scope.chartConfig2.series = series;

            }
        }
    }

})();

(function() {
    'use strict';

    angular.module('cockpit')
    .controller('MessageCtrl', MessageController);

    MessageController.$inject = ['$scope', '$http', '$location', 'UserService', '$stateParams'];
    function MessageController($scope, $http, $location, UserService, $stateParams){

        if (!UserService.isLogin) {
            $location.path('/login');
        }else {
            if (null != $stateParams.msgId) {
                $scope.msgID = $stateParams.msgId;
                searchMsg();
            }

            $scope.showProperties = function(properties) {
                var resultString = "";
                for (var key in properties) {
                    resultString = resultString + "[" + key + "]-[" + properties[key] + "]  " ;
                }
                return resultString;
            }

            $scope.searchID = function() {
                searchMsg();
            };


            function searchMsg(){
                var msgId = $scope.msgID;
                if ("undedined" != typeof(msgId) && msgId != "" && msgId.length === 32) {
                    $http({
                        url: 'cockpit/api/message/' + msgId,
                        method: 'GET',
                        responseType: 'json'
                    }).success(function(data, status, headers, config) {
                        $scope.message = data;

                        $http({
                            url: 'cockpit/api/message/' + msgId,
                            method: "POST",
                            responseType: 'json'
                        }).success(function(data, status, headers, config) {
                            $scope.statuses = data;
                        }).error(function(data, status, headers, config) {

                        });
                    }).error(function(data, status, headers, config){

                    });
                }
            };

            $scope.findConnectConsumer = function() {
                if ("undefined" != typeof($scope.message) && "undedined" != typeof($scope.message.topic) && null != $scope.message.topic) {
                    $http({
                        url: "cockpit/api/message/query/" + $scope.message.topic,
                        method: "GET",
                        responseType: "json"
                    }).success(function(data, status, headers, config) {
                        $scope.consumerGroups = data;
                        document.getElementById("findConsumer").style.display = "block";
                    }).error(function(data, status, headers, config) {

                    });
                }
            };

            $scope.findConnectClient = function() {
                if (null != $scope.consumerGroup) {
                    $http({
                        url: "cockpit/api/consumer-group" + "/client/" + $scope.consumerGroup,
                        method: "GET",
                        responseType: "json"
                    }).success(function(data, status, headers, config) {
                        $scope.clients = data;
                        document.getElementById("findClient").style.display = "block";
                    }).error(function(data, status, headers, config) {

                    });
                }
            };

            $scope.checkClient = function (){
                if ("undefined" != typeof($scope.consumerGroup) && "undefined" != typeof($scope.client) && null != $scope.client.clientId) {
                    $http({
                        url: "cockpit/api/message" + "/resend/" + $scope.consumerGroup + "/" + $scope.client.clientId + "/" + $scope.msgID,
                        method: "GET"
                    }).success(function(data, status, headers, config) {
                        alert(data);
                    }).error(function(data, status, headers, config){

                    });

                }
            };
        }
    }
})();

(function() {
    'use strict';

    angular.module('cockpit')
    .controller('MessageKEYCtrl', MessageKEYController);

    MessageKEYController.$inject = ['$scope', '$http', '$location', 'UserService', '$state'];
    function MessageKEYController($scope, $http, $location, UserService, $state){
        if (!UserService.isLogin) {
            $location.path('/login');
        }else {
            $scope.jumpDetail = function(msgId) {
                $state.go('messageID', {'msgId': msgId});
            };

            $scope.searchKEY = function() {
                var topic = $scope.messageTopic;
                var key = $scope.messageKey;
                if (null != topic && null != key) {
                    $http({
                        url: "cockpit/api/message" + "/" + topic + "/" + key,
                        method: "GET",
                        responseType: "json"
                    }).success(function (data, status, headers, config) {
                        $scope.messages = data;
                    }).error(function(data, status, headers, config) {

                    });

                }
            };
        }
    }
})();

(function() {
    'use strict';

    angular.module('cockpit')
    .controller('ProjectLCtrl', ProjectListController);

    ProjectListController.$inject = ['$scope', '$http', '$location', 'UserService', '$element'];
    function ProjectListController($scope, $http, $location, UserService, $element) {
        if (!UserService.isLogin) {
            $location.path('/login');
        }else{
            $http({
                url: 'cockpit/api/project',
                method: 'GET',
                responseType: 'json'
            }).success(function(data, status, headers, config) {
                $scope.projects = data;
            }).error(function(data, status, headers, config) {

            });

            $scope.showGroups = function(consumerGroups) {
                var resultString = "";

                consumerGroups.forEach(function(consumerGroup) {
                    resultString = resultString + consumerGroup.groupName + '\r\n';
                });

                return resultString;
            };

            $scope.showTopics = function(topics){
                var resultString = "";

                topics.forEach(function(topic){
                    resultString = resultString + topic.topic + " ";
                });

                return resultString;
            }
        }
    }
})();

(function() {
    'use strict';

    angular.module('cockpit')
    .controller('ProjectAddCtrl', ProjectAddController);

    ProjectAddController.$inject = ['$scope', '$http', '$location', 'UserService'];
    function ProjectAddController($scope, $http, $location, UserService) {
        if (!UserService.isLogin) {
            $location.path('/login');
        }else{
            $scope.submit = function() {
                var project = $scope.project;
                $http({
                    url: 'cockpit/api/project',
                    method: 'PUT',
                    data: project,
                    responseType: 'json'
                }).success(function(data, status, headers, config) {
                    if (data == 0) {
                        $scope.message = "this project is already~~~~~exist";
                    }else{
                        $scope.message = "SUCCESS";
                    }
                }).error(function(data, status, headers, config) {

                });
            }
        }
    }
})();
