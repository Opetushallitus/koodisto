"use strict";

import angular from 'angular';

const mod = angular.module('loading', []);


class LoadingService {
    constructor() {
        "ngInject";
        this.requestCount = 0;
        this.isLoading = () => this.requestCount > 0;
    }
}

mod.service('loadingService', LoadingService);

mod.factory('onStartInterceptor', ['loadingService', function (loadingService) {
    return function (data, headersGetter) {
        loadingService.requestCount++;
        return data;
    };
}]);

mod.factory('onCompleteInterceptor', ['loadingService', '$q', function (loadingService, $q) {
    return {
        response: function (response) {
            loadingService.requestCount--;
            return response;
        },
        responseError: function (response) {
            loadingService.requestCount--;
            return $q.reject(response);
        },
    };
}]);

mod.config(['$httpProvider', function ($httpProvider) {
    $httpProvider.interceptors.push('onCompleteInterceptor');
}]);

mod.run(['$http', 'onStartInterceptor', function ($http, onStartInterceptor) {
    $http.defaults.transformRequest.push(onStartInterceptor);
}]);

class LoadingCtrl {
    constructor($scope, loadingService) {
        "ngInject";
        this.isLoading = loadingService.isLoading();
        $scope.$watch(() => loadingService.isLoading(), (value) => {
            this.isLoading = value;
        });
    }
}

mod.controller('LoadingCtrl', LoadingCtrl);

export default mod;