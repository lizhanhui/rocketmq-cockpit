'use strict';

/**
 * @ngdoc
 * @name cockpit
 * @description
 * # cockpit
 *
 * Main module of the application.
 */
angular
  .module('cockpit', [
    'highcharts-ng',
    'ui.router',
    'ngAnimate',
    'ngCookies',
    'ngResource'
  ])
  .config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.when('/dashboard', '/dashboard/messageID', '/dashboard/messageKEY', '/dashboard/project','/dashboard/projectA', '/dashboard/projectL');
    $urlRouterProvider.otherwise('/login');

    $stateProvider
        .state('base', {
            abstract: true,
            url: '',
            templateUrl: 'views/base.html'
        })
        .state('login', {
          url: '/login',
          parent: 'base',
          templateUrl: 'views/login.html',
          controller: 'LoginCtrl'
        })
        .state('dashboard', {
          url: '/dashboard',
          parent: 'base',
          templateUrl: 'views/dashboard.html',
          controller: 'DashboardCtrl'
        })
        .state('messageID', {
            url: '/messageID',
            parent: 'dashboard',
            templateUrl: 'views/dashboard/message.html',
            controller: 'MessageCtrl'
        })
        .state('messageKEY', {
            url: '/messageKEY',
            parent: 'dashboard',
            templateUrl: 'views/dashboard/messageKEY.html',
            controller: 'MessageKEYCtrl'
        })
        .state('project', {
            url: '/project',
            parent: 'dashboard',
            templateUrl: 'views/dashboard/project.html',
            controller: 'ProjectCtrl'
        })
        .state('projectA', {
            url: '/projectA',
            parent: 'dashboard',
            templateUrl: 'views/dashboard/projectA.html',
            controller: 'ProjectAddCtrl'
        })
        .state('projectL', {
            url: 'projectL',
            parent: 'dashboard',
            templateUrl: 'views/dashboard/projectL.html',
            controller: 'ProjectLCtrl'
        });

  });
