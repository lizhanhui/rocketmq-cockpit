
(function (){
	'use strict';

	var cockpitServices = angular.module('cockpitServices', ['ngResource']);

	cockpitServices.factory('Project',['$http, $q',function($http, $q){
		
		var service = {};

		service.query = function(source){
			var def = $q.defer();

			$http({
                method:'GET',
                //headers: {'Content-type': 'application/json;charset=UTF-8'},
                url: source,
                responseType: 'json'
            }).success(function(data, status, headers, config) {
                console.log(data);
                def.resolve(data);
            }).error(function(data, status, headers, config) {
                console.log(status);
                def.reject(false);
            });
		};

		return service;
	}]);
})();