(function() {
    'use strict';

    angular
        .module('cockpit')
        .factory('UserService', UserService);

    UserService.$inject = [];
    function UserService() {
        var user = {
            isLogin: false,
            username: ""
        };

        return user;
    }
})();
