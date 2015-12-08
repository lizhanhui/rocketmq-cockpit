'use strict'

/**
 *
 *
 */

 var cockpit = angular.module('cockpit');
 cockpit.factory('MAdminSvc', ['$http','$q',function ($http,$q) {
    return {
        query:function(admin){
            var deferred=$q.defer();
            if(admin==null||admin==""){
                return deferred.promise;
            }

            $http({
                method:'POST',
                //headers: {'Content-type': 'application/json;charset=UTF-8'},
                url:'/admin/query',
                responseType:'json',
                params: {'admin':admin}
            }).success(function(data,status,headers,config){
                console.log(data+"==="+data.admin);
                deferred.resolve(data);//成功，返回数据
            }).error(function(data,status,headers,config){
                console.log("status="+status);
                deferred.reject(data);//失败，返回错误信息
            });
            return deferred.promise;// 返回承诺，这里并不是最终数据，而是访问最终数据的API
        }
    };
}]) ;


(function() {
    'use strict';

    angular
        .module('cockpit')
        .service('ChartData', ChartData);

    ChartData.$inject = ['$http'];
    function ChartData($http) {
        this.load = load;

        var opts = {
            get: { method: 'GET', isArray: true }
        };

        function load(source) {
            $http({
                method:'GET',
                //headers: {'Content-type': 'application/json;charset=UTF-8'},
                url: source,
                responseType: 'json'
            }).success(function(data, status, headers, config) {
                console.log(data+"/r/n"+status+'/r/n'+headers+'/r/n'+config);
                return data;
            }).error(function(data, status, headers, config) {
                console.log(data+"/r/n"+status+'/r/n'+headers+'/r/n'+config);
                return false;
            });

        }
    }
})();
