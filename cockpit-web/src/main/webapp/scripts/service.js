(function() {
    'use strict';

    angular
        .module('cockpit')
        .service('UserService', UserService);

    UserService.$inject = [];
    function UserService() {
        var user = {
            isLogin: false,
            username: ""
        };

        return user;
    }
})();
