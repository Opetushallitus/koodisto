"use strict";

var SERVICE_NAME = "APP_KOODISTO";

var app = angular.module('koodisto', [
    'ngResource',
    'loading',
    'ngRoute',
    'ngAnimate',
    'ngCookies',
    'localization',
    'ui.bootstrap',
    'ui.utils',
    'ui.select',
    'ngIdle',
    'pasvaz.bindonce',
    'ngUpload']);

// Käytössä vain suomenkieliset "käännökset"
angular.module('localization', []).filter('i18n', [ '$rootScope', '$locale', function($rootScope, $locale) {
    jQuery.i18n.properties({
        name : 'messages',
        path : '../i18n/',
        mode : 'map',
        language : 'fi_FI',
        callback : function() {
        }
    });

    return function(text) {
        return jQuery.i18n.prop(text);
    };
} ]);

var SERVICE_URL_BASE = SERVICE_URL_BASE || "http://localhost:8180/koodisto-service/";
var SESSION_KEEPALIVE_INTERVAL_IN_SECONDS = SESSION_KEEPALIVE_INTERVAL_IN_SECONDS || 30;
var MAX_SESSION_IDLE_TIME_IN_SECONDS = MAX_SESSION_IDLE_TIME_IN_SECONDS || 1800;

app.factory('NoCacheInterceptor', function() {
    return {
        request : function(config) {
            if (config.method && config.method === 'GET' && config.url.indexOf('html') === -1 && config.url.indexOf("/organisaatio-service/") === -1) {
                var separator = config.url.indexOf('?') === -1 ? '?' : '&';
                config.url = config.url + separator + 'noCache=' + new Date().getTime();
            }
            return config;
        }
    };
});

app.run(function($http, $cookies) {
    $http.defaults.headers.common['clientSubSystemCode'] = "koodisto.koodisto-ui.frontend";
    if($cookies['CSRF']) {
        $http.defaults.headers.common['CSRF'] = $cookies['CSRF'];
    }
});

// Route configuration
app.config([ '$routeProvider', '$httpProvider', '$locationProvider', function($routeProvider, $httpProvider, $locationProvider) {
    $httpProvider.interceptors.push('NoCacheInterceptor');
    $locationProvider.html5Mode(true);
    $routeProvider.
    // front page
    when('/etusivu', {
        controller : KoodistoTreeController,
        templateUrl : 'codesmainpage.html'
    }).when('/lisaaKoodisto', {
        controller : CodesCreatorController,
        templateUrl : 'codes/createcodes.html',
        resolve : {
            isModalController : function() {
                return false;
            }
        }
    }).when('/muokkaaKoodisto/:codesUri/:codesVersion', {
        controller : CodesEditorController,
        templateUrl : 'codes/editcodes.html',
        resolve : {
            isModalController : function() {
                return false;
            }
        }
    }).when('/koodisto/:codesUri/:codesVersion', {
        controller : ViewCodesController,
        templateUrl : 'codes/viewcodes.html',
        resolve : {
            isModalController : function() {
                return false;
            }
        }
    }).when('/koodi/:codeElementUri/:codeElementVersion', {
        controller : ViewCodeElementController,
        templateUrl : 'codeelement/viewcodeelement.html'
    }).when('/lisaaKoodi/:codesUri/:codesVersion', {
        controller : CodeElementCreatorController,
        templateUrl : 'codeelement/createcodeelement.html',
        resolve : {
            isModalController : function() {
                return false;
            }
        }
    }).when('/muokkaaKoodi/:codeElementUri/:codeElementVersion', {
        controller : CodeElementEditorController,
        templateUrl : 'codeelement/editcodeelement.html',
        resolve : {
            isModalController : function() {
                return false;
            }
        }
    }).when('/lisaaKoodistoryhma', {
        controller : CodesGroupCreatorController,
        templateUrl : 'codesgroup/createcodesgroup.html'
    }).when('/koodistoryhma/:id', {
        controller : ViewCodesGroupController,
        templateUrl : 'codesgroup/viewcodesgroup.html'
    }).when('/muokkaaKoodistoryhma/:id', {
        controller : CodesGroupEditorController,
        templateUrl : 'codesgroup/editcodesgroup.html'
    }).
    // else
    otherwise({
        redirectTo : '/etusivu'
    });
} ]);

// rest resources

// Koodistot
app.factory('RootCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes", {}, {
        get : {
            method : "GET",
            isArray : true,
            params : {}
        }
    });
});

app.factory('NewCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes", {}, {
        post : {
            method : "POST"
        }
    });
});

app.factory('DeleteCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/delete/:codesUri/:codesVersion", {
        codesUri : "@codesUri",
        codesVersion : "@codesVersion"
    }, {
        put : {
            method : "POST"
        }
    });
});

app.factory('NewCodesGroup', function($resource) {
    return $resource(window.url("koodisto-service.codesgroup"), {}, {
        post : {
            method : "POST"
        }
    });
});

app.factory('DeleteCodesGroup', function($resource) {
    return $resource(SERVICE_URL_BASE + "codesgroup/delete/:id", {
        id : "@id"
    }, {
        post : {
            method : "POST"
        }
    });
});

app.factory('UpdateCodesGroup', function($resource) {
    return $resource(SERVICE_URL_BASE + "codesgroup", {}, {
        put : {
            method : "PUT"
        }
    });
});

app.factory('UpdateCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes", {}, {
        put : {
            method : "PUT"
        }
    });
});

app.factory('SaveCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/save", {}, {
        put : {
            method : "PUT"
        }
    });
});

app.factory('CodesByUri', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/:codesUri", {
        codesUri : "@codesUri"
    }, {
        get : {
            method : "GET"
        }
    });
});

app.factory('CodesByUriAndVersion', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/:codesUri/:codesVersion", {
        codesUri : "@codesUri",
        codesVersion : "@codesVersion"
    }, {
        get : {
            method : "GET"
        }
    });
});

app.factory('CodesGroupByUri', function($resource) {
    return $resource(SERVICE_URL_BASE + "codesgroup/:id", {
        id : "@id"
    }, {
        get : {
            method : "GET"
        }
    });
});

app.factory('AllCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/all", {}, {
        get : {
            method : "GET",
            isArray : true
        }
    });
});

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

app.factory('MyRoles', function($resource) {
    return $resource(window.url("cas.myroles"), {}, {
        get : {
            method : "GET",
            isArray : true
        }
    });
});

app.factory('CodeElementsByCodesUriAndVersion', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/codes/:codesUri/:codesVersion", {
        codesUri : "@codesUri",
        codesVersion : "@codesVersion"
    }, {
        get : {
            method : "GET",
            isArray : true
        }
    });
});

app.factory('CodeElementByUriAndVersion', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codeElementUri/:codeElementVersion", {
        codeElementUri : "@codeElementUri",
        codeElementVersion : "@codeElementVersion"
    }, {
        get : {
            method : "GET",
            isArray : false
        }
    });
});

app.factory('CodeElementByCodeElementUri', function($resource) {
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
});

app.factory('CodeElementVersionsByCodeElementUri', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codeElementUri", {
        codeElementUri : "@codeElementUri"
    }, {
        get : {
            method : "GET",
            isArray : true
        }
    });
});

app.factory('LatestCodeElementVersionsByCodeElementUri', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/latest/:codeElementUri", {
        codeElementUri : "@codeElementUri"
    }, {
        get : {
            method : "GET",
            isArray : false
        }
    });
});

app.factory('NewCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codesUri", {}, {
        post : {
            method : "POST"
        }
    });
});

app.factory('DeleteCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/delete/:codeElementUri/:codeElementVersion", {
        codeElementUri : "@codeElementUri",
        codeElementVersion : "@codeElementVersion"
    }, {
        put : {
            method : "POST"
        }
    });
});

app.factory('RemoveRelationCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/removerelation/:codeElementUri/:codeElementUriToRemove/:relationType", {
        codeElementUri : "@codeElementUri",
        codeElementUriToRemove : "@codeElementUriToRemove",
        relationType : "@relationType"
    }, {
        put : {
            method : "POST"
        }
    });
});

app.factory('MassRemoveRelationCodeElements', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/removerelations", {
    }, {
        remove : {
            method : "POST"
        }
    });
});

app.factory('AddRelationCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/addrelation/:codeElementUri/:codeElementUriToAdd/:relationType", {
        codeElementUri : "@codeElementUri",
        codeElementUriToAdd : "@codeElementUriToAdd",
        relationType : "@relationType"
    }, {
        put : {
            method : "POST"
        }
    });
});

app.factory('MassAddRelationCodeElements', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/addrelations", {

    }, {
        put : {
            method : "POST"
        }
    });
});

app.factory('AddRelationCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/addrelation/:codesUri/:codesUriToAdd/:relationType", {
        codesUri : "@codesUri",
        codesUriToAdd : "@codesUriToAdd",
        relationType : "@relationType"
    }, {
        put : {
            method : "POST"
        }
    });
});

app.factory('RemoveRelationCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/removerelation/:codesUri/:codesUriToRemove/:relationType", {
        codesUri : "@codesUri",
        codesUriToRemove : "@codesUriToRemove",
        relationType : "@relationType"
    }, {
        put : {
            method : "POST"
        }
    });
});

app.factory('SaveCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/save", {}, {
        put : {
            method : "PUT"
        }
    });
});

app.factory('UpdateCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement", {}, {
        put : {
            method : "PUT"
        }
    });
});

app.factory('Organizations', function($resource) {
    return $resource(window.url("organisaatio-service.hae"), {}, {
        get : {
            method : "GET"
        }
    });
});

app.factory('OrganizationChildrenByOid', function($resource) {
    return $resource(window.url("organisaatio-service.hae") + "?oidrestrictionlist=:oid&skipparents=true", {
        oid : "@oid"
    }, {
        get : {
            method : "GET"
        }
    });
});

app.factory('OrganizationByOid', function($resource) {
    return $resource(window.url("organisaatio-service.byOid"), {
        oid : "@oid"
    }, {
        get : {
            method : "GET"
        }
    });
});

app.factory('SessionPoll', function($resource) {
    return $resource(SERVICE_URL_BASE + "session/maxinactiveinterval", {}, {
        get: {method:   "GET"}
    });
});

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

function getLanguageSpecificValue(fieldArray, fieldName, language) {
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

function getLanguageSpecificValueOrValidValue(fieldArray, fieldName, language) {
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
app.config(function(datepickerConfig) {
    datepickerConfig.startingDay = 1;
});

app.run(["SessionPoll", function(SessionPoll) {
    SessionPoll.get({});
}]);
