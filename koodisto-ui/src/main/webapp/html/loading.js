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

mod.factory('loadingInterceptor', ['loadingService', '$q', (loadingService, $q) => {
    return {
        request: (config) => {
            loadingService.requestCount++;
            return config;
        },
        requestError: (rejection) => {
            loadingService.requestCount--;
            return $q.reject(rejection);
        },
        response: (response) => {
            loadingService.requestCount--;
            return response;
        },
        responseError: (rejection) => {
            loadingService.requestCount--;
            return $q.reject(rejection);
        },
    };
}]);

mod.config(['$httpProvider', ($httpProvider) => {
    $httpProvider.interceptors.push('loadingInterceptor');
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