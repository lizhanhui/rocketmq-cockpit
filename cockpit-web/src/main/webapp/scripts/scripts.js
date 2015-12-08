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
    'cockpitServices',
    'highcharts-ng',
    'ui.router',
    'ngAnimate',
   // 'ngStorage',
   // 'ngCookies',
    'ngResource'
  ])
  .config(function($stateProvider, $urlRouterProvider) {

    $urlRouterProvider.when('/dashboard', '/dashboard/message', '/dashboard/project','/dashboard/projectA');
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
        .state('message', {
        url: '/message',
        parent: 'dashboard',
        templateUrl: 'views/dashboard/message.html',
        controller: 'MessageCtrl'
        })
        .state('project', {
        url: '/project',
        parent: 'dashboard',
        templateUrl: 'views/dashboard/project.html',
        controller: 'ProjectCtrl'
        }).state('projectA', {
            url: '/projectA',
            parent: 'dashboard',
            templateUrl: 'views/dashboard/projectA.html',
            controller: 'ProjectAddCtrl'
        });

  });
