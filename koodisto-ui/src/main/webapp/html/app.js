"use strict";

import '../css/font-awesome-4.0.3/css/font-awesome.css';
import '../css/bootstrap.css';
import '../css/virkailija.css';
import '../css/other.css';
import '../css/selectize.default.css';
import '../css/select-0.8.3.css';

import codesMainPage from './codesmainpage.html';
import createCodes from './codes/createcodes.html';
import editCodes from './codes/editcodes.html';
import viewCodes from './codes/viewcodes.html';
import viewCodeelement from './codeelement/viewcodeelement.html';
import createCodeelement from './codeelement/createcodeelement.html';
import editCodeelement from './codeelement/editcodeelement.html';
import createCodesgroup from './codesgroup/createcodesgroup.html';
import viewCodesgroup from './codesgroup/viewcodesgroup.html';
import editCodesgroup from './codesgroup/editcodesgroup.html';

import angular from 'angular';
import ngResource from 'angular-resource';
import ngRoute from 'angular-route';
import ngAnimate from 'angular-animate';
import ngCookies from 'angular-cookies';
import uiBootstrap from 'angular-ui-bootstrap';
// import 'angular-ui-utils/modules/ie-shiv/ie-shiv';
// import 'angular-ui-event'; // ui-utils required this
import 'ui-select';
import ngIdle from 'ng-idle';
import 'angular-bindonce';
import 'ng-upload';
import 'jquery-i18n-properties';
import naturalSort from 'javascript-natural-sort';

import loading from './loading';
import localization from './localization';

import {urls} from 'oph-urls-js';
import frontUrls from './koodisto-ui-web-oph';
import {EventsCtrl, idleConfig, Idler, idleRun, SessionExpiresCtrl} from "./idler";
import {CsrfHeaderInterceptor} from "./app-csrf-header";
import {AuthService, MyRolesModel} from "./auth";
import {KoodistoTreeController, Treemodel} from "./codesTree";
import {Auth, IeSelectFix} from "./directives";
import {
    ChildOpener, ModalInstanceCtrl,
    OrganisaatioOPHTreeModel,
    OrganisaatioTreeController,
    OrganisaatioTreeModel
} from "./organizationTree";
import {CodeElementCreatorController, CodeElementCreatorModel} from "./codeelement/createCodeElement";
import {CodeElementEditorController, CodeElementEditorModel} from "./codeelement/editCodeElement";
import {ViewCodeElementController, ViewCodeElementModel} from "./codeelement/viewCodeElement";
import {CodesMatcher} from "./codes/codesMatcher";
import {CodesCreatorController, CodesCreatorModel} from "./codes/createCodes";
import {CodesEditorController, CodesEditorModel} from "./codes/editCodes";
import {ViewCodesController, ViewCodesModel} from "./codes/viewCodes";
import {CodesGroupCreatorController, CodesGroupCreatorModel} from "./codesgroup/createCodesGroup";
import {CodesGroupEditorController, CodesGroupEditorModel} from "./codesgroup/editCodesGroup";
import {ViewCodesGroupController, ViewCodesGroupModel} from "./codesgroup/viewCodesGroup";

export const SERVICE_NAME = "APP_KOODISTO";

const app = angular.module('koodisto', [
    ngResource,
    loading.name,
    ngRoute,
    ngAnimate,
    ngCookies,
    localization.name,
    uiBootstrap,
    // 'ui.utils',
    'ui.select',
    ngIdle,
    'pasvaz.bindonce',
    'ngUpload',
]);


export let SERVICE_URL_BASE;
export const SESSION_KEEPALIVE_INTERVAL_IN_SECONDS = window.SESSION_KEEPALIVE_INTERVAL_IN_SECONDS || 30;
export const MAX_SESSION_IDLE_TIME_IN_SECONDS = window.MAX_SESSION_IDLE_TIME_IN_SECONDS || 1800;

app.factory('NoCacheInterceptor', function() {
    return {
        request: function(config) {
            if (config.method && config.method === 'GET' && config.url.indexOf('html') === -1 && config.url.indexOf("/organisaatio-service/") === -1) {
                var separator = config.url.indexOf('?') === -1 ? '?' : '&';
                config.url = config.url + separator + 'noCache=' + new Date().getTime();
            }
            return config;
        }
    };
});

app.run(['$http', '$cookies', function( $http, $cookies) {
    $http.defaults.headers.common['clientSubSystemCode'] = "koodisto.koodisto-ui.frontend";
    if($cookies['CSRF']) {
        $http.defaults.headers.common['CSRF'] = $cookies['CSRF'];
    }
}]);

// Route configuration
app.config(['$windowProvider', '$routeProvider', '$httpProvider', '$locationProvider', function($windowProvider, $routeProvider, $httpProvider, $locationProvider) {
    $httpProvider.interceptors.push('csrfHeaderInterceptor');
    urls.addProperties(frontUrls);
    if (window.urlProperties) {
        urls.addOverrides(window.urlProperties);
    }
    SERVICE_URL_BASE = urls.url("koodisto-service.base");

    $httpProvider.interceptors.push('NoCacheInterceptor');
    $locationProvider.html5Mode(true);
    $routeProvider.
    // front page
    when('/etusivu', {
        controller: 'koodistoTreeController',
        template: codesMainPage,
    }).when('/lisaaKoodisto', {
        controller: 'codesCreatorController',
        template: createCodes,
        resolve: {
            isModalController : function() {
                return false;
            }
        }
    }).when('/muokkaaKoodisto/:codesUri/:codesVersion', {
        controller: 'codesEditorController',
        template: editCodes,
        resolve: {
            isModalController : function() {
                return false;
            }
        }
    }).when('/koodisto/:codesUri/:codesVersion', {
        controller: 'viewCodesController',
        template: viewCodes,
        resolve: {
            isModalController : function() {
                return false;
            }
        }
    }).when('/koodi/:codeElementUri/:codeElementVersion', {
        controller: 'viewCodeElementController',
        template: viewCodeelement,
    }).when('/lisaaKoodi/:codesUri/:codesVersion', {
        controller: 'codeElementCreatorController',
        template: createCodeelement,
        resolve: {
            isModalController : function() {
                return false;
            }
        }
    }).when('/muokkaaKoodi/:codeElementUri/:codeElementVersion', {
        controller: 'codeElementEditorController',
        template: editCodeelement,
        resolve: {
            isModalController : function() {
                return false;
            }
        }
    }).when('/lisaaKoodistoryhma', {
        controller: 'codesGroupCreatorController',
        template: createCodesgroup,
    }).when('/koodistoryhma/:id', {
        controller: 'ViewCodesGroupController',
        template: viewCodesgroup,
    }).when('/muokkaaKoodistoryhma/:id', {
        controller: 'codesGroupEditorController',
        template: editCodesgroup,
    }).
    // else
    otherwise({
        redirectTo : '/etusivu'
    });
} ]);

// rest resources

// Koodistot
app.factory('RootCodes', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes", {}, {
        get : {
            method : "GET",
            isArray : true,
            params : {}
        }
    });
}]);

app.factory('NewCodes', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes", {}, {
        post : {
            method : "POST"
        }
    });
}]);

app.factory('DeleteCodes', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/delete/:codesUri/:codesVersion", {
        codesUri : "@codesUri",
        codesVersion : "@codesVersion"
    }, {
        put : {
            method : "POST"
        }
    });
}]);

app.factory('NewCodesGroup', ['$resource', function($resource) {
    return $resource(urls.url("koodisto-service.codesgroup"), {}, {
        post : {
            method : "POST"
        }
    });
}]);

app.factory('DeleteCodesGroup', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codesgroup/delete/:id", {
        id : "@id"
    }, {
        post : {
            method : "POST"
        }
    });
}]);

app.factory('UpdateCodesGroup', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codesgroup", {}, {
        put : {
            method : "PUT"
        }
    });
}]);

app.factory('UpdateCodes', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes", {}, {
        put : {
            method : "PUT"
        }
    });
}]);

app.factory('SaveCodes', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/save", {}, {
        put : {
            method : "PUT",
            transformResponse: function (data, headersGetter, status) {
                return {content: data};
            }
        }
    });
}]);

app.factory('CodesByUri', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/:codesUri", {
        codesUri : "@codesUri"
    }, {
        get : {
            method : "GET"
        }
    });
}]);

app.factory('CodesByUriAndVersion', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/:codesUri/:codesVersion", {
        codesUri : "@codesUri",
        codesVersion : "@codesVersion"
    }, {
        get : {
            method : "GET"
        }
    });
}]);

app.factory('CodesGroupByUri', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codesgroup/:id", {
        id : "@id"
    }, {
        get : {
            method : "GET"
        }
    });
}]);

app.factory('AllCodes', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/all", {}, {
        get : {
            method : "GET",
            isArray : true
        }
    });
}]);

app.factory('DownloadCodes', function() {
    return function(codesUri, codesVersion, format, encoding) {
        var urlArray = [];
        urlArray.push(
                SERVICE_URL_BASE + "codes/download",
                codesUri,
                codesVersion,
                format);
        var urlStr = urlArray.join("/") + "?encoding=" + encoding;

        return urlStr;
    };
});

app.factory('MyRoles', ['$resource', function($resource) {
    return $resource(urls.url("cas.myroles"), {}, {
        get : {
            method : "GET",
            isArray : true
        }
    });
}]);

app.factory('CodeElementsByCodesUriAndVersion', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/codes/:codesUri/:codesVersion", {
        codesUri : "@codesUri",
        codesVersion : "@codesVersion"
    }, {
        get : {
            method : "GET",
            isArray : true
        }
    });
}]);

app.factory('CodeElementByUriAndVersion', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codeElementUri/:codeElementVersion", {
        codeElementUri : "@codeElementUri",
        codeElementVersion : "@codeElementVersion"
    }, {
        get : {
            method : "GET",
            isArray : false
        }
    });
}]);

app.factory('CodeElementByCodeElementUri', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codesUri/:codesVersion/:codeElementUri", {
        codesUri : "@codesUri",
        codesVersion : "@codesVersion",
        codeElementUri : "@codeElementUri"
    }, {
        get : {
            method : "GET",
            isArray : false
        }
    });
}]);

app.factory('CodeElementVersionsByCodeElementUri', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codeElementUri", {
        codeElementUri : "@codeElementUri"
    }, {
        get : {
            method : "GET",
            isArray : true
        }
    });
}]);

app.factory('LatestCodeElementVersionsByCodeElementUri', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/latest/:codeElementUri", {
        codeElementUri : "@codeElementUri"
    }, {
        get : {
            method : "GET",
            isArray : false
        }
    });
}]);

app.factory('NewCodeElement', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codesUri", {}, {
        post : {
            method : "POST"
        }
    });
}]);

app.factory('DeleteCodeElement', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/delete/:codeElementUri/:codeElementVersion", {
        codeElementUri : "@codeElementUri",
        codeElementVersion : "@codeElementVersion"
    }, {
        put : {
            method : "POST"
        }
    });
}]);

app.factory('RemoveRelationCodeElement', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/removerelation/:codeElementUri/:codeElementUriToRemove/:relationType", {
        codeElementUri : "@codeElementUri",
        codeElementUriToRemove : "@codeElementUriToRemove",
        relationType : "@relationType"
    }, {
        put : {
            method : "POST"
        }
    });
}]);

app.factory('MassRemoveRelationCodeElements', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/removerelations", {
    }, {
        remove : {
            method : "POST"
        }
    });
}]);

app.factory('AddRelationCodeElement', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/addrelation/:codeElementUri/:codeElementUriToAdd/:relationType", {
        codeElementUri : "@codeElementUri",
        codeElementUriToAdd : "@codeElementUriToAdd",
        relationType : "@relationType"
    }, {
        put : {
            method : "POST"
        }
    });
}]);

app.factory('MassAddRelationCodeElements', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/addrelations", {

    }, {
        put : {
            method : "POST"
        }
    });
}]);

app.factory('AddRelationCodes', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/addrelation/:codesUri/:codesUriToAdd/:relationType", {
        codesUri : "@codesUri",
        codesUriToAdd : "@codesUriToAdd",
        relationType : "@relationType"
    }, {
        put : {
            method : "POST"
        }
    });
}]);

app.factory('RemoveRelationCodes', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/removerelation/:codesUri/:codesUriToRemove/:relationType", {
        codesUri : "@codesUri",
        codesUriToRemove : "@codesUriToRemove",
        relationType : "@relationType"
    }, {
        put : {
            method : "POST"
        }
    });
}]);

app.factory('SaveCodeElement', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/save", {}, {
        put : {
            method : "PUT",
            transformResponse: function (data, headersGetter, status) {
                return {content: data};
            }
        }
    });
}]);

app.factory('UpdateCodeElement', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement", {}, {
        put : {
            method : "PUT"
        }
    });
}]);

app.factory('Organizations', ['$resource', function($resource) {
    return $resource(urls.url("organisaatio-service.hae"), {}, {
        get : {
            method : "GET"
        }
    });
}]);

app.factory('OrganizationChildrenByOid', ['$resource', function($resource) {
    return $resource(urls.url("organisaatio-service.hae") + "?oidrestrictionlist=:oid&skipparents=true", {
        oid : "@oid"
    }, {
        get : {
            method : "GET"
        }
    });
}]);

app.factory('OrganizationByOid', ['$resource', function($resource) {
    return $resource(urls.url("organisaatio-service.byOid"), {
        oid : "@oid"
    }, {
        get : {
            method : "GET"
        }
    });
}]);

app.factory('SessionPoll', ['$resource', function($resource) {
    return $resource(SERVICE_URL_BASE + "session/maxinactiveinterval", {}, {
        get: {method:   "GET"}
    });
}]);

app.filter('naturalSort', function() {
    return function(arrInput, field, reverse) {
        var arr = arrInput.sort(function(a, b) {
            var valueA = field ? a[field] : a;
            var valueB = field ? b[field] : b;
            var aIsString = typeof valueA === 'string';
            var bIsString = typeof valueB === 'string';
            return naturalSort(aIsString ? valueA.trim().toLowerCase() : valueA, bIsString ? valueB.trim().toLowerCase() : valueB);
        });
        return reverse ? arr.reverse() : arr;
    };
});

export function getLanguageSpecificValue(fieldArray, fieldName, language) {
    if (fieldArray) {
        for (var i = 0; i < fieldArray.length; i++) {
            if (fieldArray[i].kieli === language) {
                var result = eval("fieldArray[i]." + fieldName);
                return result === null ? "" : result;
            }
        }
    }
    return "";
}

export function getLanguageSpecificValueOrValidValue(fieldArray, fieldName, language) {
    var specificValue = getLanguageSpecificValue(fieldArray, fieldName, language);

    if (specificValue === "" && language !== "FI"){
        specificValue = getLanguageSpecificValue(fieldArray, fieldName, "FI");
    }
    if (specificValue === "" && language !== "SV"){
        specificValue = getLanguageSpecificValue(fieldArray, fieldName, "SV");
    }
    if (specificValue === "" && language !== "EN"){
        specificValue = getLanguageSpecificValue(fieldArray, fieldName, "EN");
    }
    return specificValue;
}

// Pagination

// Filter used to slice array to start pagination from correct location
app.filter('startFrom', function() {
    return function(input, start) {
        start = +start; // parse to int
        return input.slice(start);
    };
});

// Forloop for angularjs
app.filter('forLoop', function() {
    return function(input, start, end) {
        input = new Array(end - start);
        for (var i = 0; start < end; start++, i++) {
            input[i] = start;
        }
        return input;
    };
});

// Konfiguroidaan DatePicker alkamaan viikon maanantaista (default = sunnuntai)
app.config(['datepickerConfig', function(datepickerConfig) {
    datepickerConfig.startingDay = 1;
}]);

app.run(["SessionPoll", function(SessionPoll) {
    SessionPoll.get({});
}]);

// app.directive('idler', () => new Idler);

app.controller('sessionExpiresCtrl', SessionExpiresCtrl);

app.controller('eventsCtrl', EventsCtrl);

app.config(idleConfig);

app.run(idleRun);

app.service('csrfHeaderInterceptor', CsrfHeaderInterceptor);

app.service('myRolesModel', MyRolesModel);

app.service('authService', AuthService);

app.service('treemodel', Treemodel);

app.controller('koodistoTreeController', KoodistoTreeController);

// app.directive('auth', () => new Auth);

// app.directive('ieSelectFix', () => new IeSelectFix);

app.service('childOpener', ChildOpener);

app.service('OrganisaatioTreeModel', OrganisaatioTreeModel);

app.service('organisaatioOPHTreeModel', OrganisaatioOPHTreeModel);

app.controller('organisaatioTreeController', OrganisaatioTreeController);

app.controller('modalInstanceCtrl', ModalInstanceCtrl);

app.service('codeElementCreatorModel', CodeElementCreatorModel);

app.controller('codeElementCreatorController', CodeElementCreatorController);

app.service('codeElementEditorModel', CodeElementEditorModel);

app.controller('codeElementEditorController', CodeElementEditorController);

app.service('viewCodeElementModel', ViewCodeElementModel);

app.controller('viewCodeElementController', ViewCodeElementController);

app.service('codesMatcher', CodesMatcher);

app.service('codesCreatorModel', CodesCreatorModel);

app.controller('codesCreatorController', CodesCreatorController);

app.service('codesEditorModel', CodesEditorModel);

app.controller('codesEditorController', CodesEditorController);

app.service('viewCodesModel', ViewCodesModel);

app.controller('viewCodesController', ViewCodesController);

app.service('codesGroupCreatorModel', CodesGroupCreatorModel);

app.controller('codesGroupCreatorController', CodesGroupCreatorController);

app.service('CodesGroupEditorModel', CodesGroupEditorModel);

app.controller('codesGroupEditorController', CodesGroupEditorController);

app.service('viewCodesGroupModel', ViewCodesGroupModel);

app.controller('ViewCodesGroupController', ViewCodesGroupController);
