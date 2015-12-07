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

    LoginController.$inject = ['$scope', '$location', '$http', '$window'];
    function LoginController($scope, $location, $http, $window) {
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
                $location.path('/dashboard');
            }).error(function(data, status, headers, config){
                delete $window.sessionStorage.token;
                $scope.message = "login failed."
            });

            return false;
        }

    }
})();

(function() {
    'use strict';

    angular
    .module('cockpit')
    .controller('DashboardCtrl', DashboardController);

    DashboardController.$inject = ['$scope', '$state'];
    function DashboardController($scope, $state) {

        $scope.$state = $state;

    }
})();

(function() {
    'use strict';

    angular.module('cockpit')
    .controller('ProjectCtrl', ProjectController);
    ProjectController.$inject = ['$scope', '$http' ];

    function ProjectController($scope, $http ){

		$scope.set = function() {
			activate();
		};

        $http({
            method:'GET',
            //headers: {'Content-type': 'application/json;charset=UTF-8'},
            url: 'cockpit/api/project',
            responseType: 'json'
        }).success(function(data, status, headers, config) {
            $scope.projects=data;
            $scope.project = $scope.projects[0].id;
            activate();
        }).error(function(data, status, headers, config) {

        });

        function activate() {
            $http({
                method:'GET',
                //headers: {'Content-type': 'application/json;charset=UTF-8'},
                url: 'cockpit/api/project/' + $scope.project + "/consumer-groups",
                responseType: 'json'
            }).success(function(data, status, headers, config) {
				var indexG = 0;
				if (data != null) {
					data.forEach(function(consumerGroup){
			            var x = [];
			            var y = [];
			            var yin = []
			            if (indexG < 20) {
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
				                        temp.push((new Date(time)).getTime());
				                        temp.push(consumeProgress.diff);
				                        yin.push(temp);
				                    });
				                    if (yin.length > 0 ) {
				                        yin.reverse();
				                        if (indexG === 0){
				                            activate1(consumerGroup.groupName, yin);
				                        }else {
				                            addLine(consumerGroup.groupName, yin);
				                        }

				                        indexG++;
				                    }
			                    }
			                }).error(function(data, status, headers, config) {

			                });
			            }
	                });
				}
            }).error(function(data, status, headers, config) {
                console.log(status);
            });

            $http({
                url: 'cockpit/api/project/' + $scope.project + '/topics',
                method: "GET",
                responseType: 'json'
            }).success(function(data, status, headers, config) {
				var indexT = 0;
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
								if (indexT === 0){
									activate2(topicMetadata.topic, yin);
								}else {
									addLine2(topicMetadata.topic, yin);
								}

								indexT++;
							}

						}).error(function(data, status, headers, config) {

						});

                });
            }).error(function(data, status, headers, config) {

            });
        };

        function showTopics (topic, indexT) {

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
                            text: '(times)'
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
                    text: $scope.project
                }
            }
        };

        function addLine(x, y){
            $scope.chartConfig.series.push({name:x, data: y});
        };

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
                            text: '(times)'
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
                    text: $scope.project
                }
            }
        };

        function addLine2(x, y){
            $scope.chartConfig2.series.push({name:x, data: y});
        }

    }

})();

(function() {
    'use strict';

    angular.module('cockpit')
    .controller('MessageCtrl', function(){

    });
})();
