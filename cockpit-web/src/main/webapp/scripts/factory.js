
(function() {
    'use strict';
	var module = angular.module('cockpit', []);
    module.factory('sessionInjector',[ function() {
		    var sessionInjector = {
		        request: function(config) {

		            return config;
		        },
				response: function(respone) {
					return response;
				}
		    };
		    return sessionInjector;
		}]);
})();
