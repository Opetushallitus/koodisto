"use strict";

var SERVICE_NAME = "APP_KOODISTO";

var app = angular.module('koodisto', ['ngResource', 'loading', 'ngRoute', 'ngAnimate', 'localization','ui.bootstrap','ui.utils']);
//
// i18n toteutus kopioitu osittain http://jsfiddle.net/4tRBY/41/
//


angular.module('localization', [])
    .filter('i18n', ['$rootScope', '$locale', function ($rootScope, $locale) {
        var localeMapping = {"en-us": "en_US", "fi-fi": "fi_FI", "sv-se": "sv-SE"};

        jQuery.i18n.properties({
            name: 'messages',
            path: '../i18n/',
            mode: 'map',
            language: localeMapping[$locale.id],
            callback: function () {
            }
        });

        return function (text) {
            return jQuery.i18n.prop(text);
        };
    }]);

var SERVICE_URL_BASE = SERVICE_URL_BASE || "http://localhost:8180/koodisto-service/";
var ORGANIZATION_SERVICE_URL_BASE = ORGANIZATION_SERVICE_URL_BASE || "/organisaatio-service/";
var TEMPLATE_URL_BASE = TEMPLATE_URL_BASE || "";
var CAS_URL = CAS_URL || "/cas/myroles";

app.factory('NoCacheInterceptor', function () {
    return {
	request: function (config) {
	    if (config.method && config.method == 'GET' && config.url.indexOf('html') === -1 && config.url.indexOf(ORGANIZATION_SERVICE_URL_BASE) === -1){
		var separator = config.url.indexOf('?') === -1 ? '?' : '&';
		config.url = config.url+separator+'noCache=' + new Date().getTime();
	    }
	    return config;
	}
    };
});

//Route configuration
app.config(['$routeProvider', '$httpProvider', function ($routeProvider, $httpProvider) {
    $httpProvider.interceptors.push('NoCacheInterceptor');
    $routeProvider.
        //front page
        when('/etusivu', {controller: KoodistoTreeController, templateUrl: TEMPLATE_URL_BASE + 'codesmainpage.html'}).
        when('/lisaaKoodisto', {controller: CodesCreatorController, templateUrl:TEMPLATE_URL_BASE + 'codes/createcodes.html'}).
        when('/muokkaaKoodisto/:codesUri/:codesVersion', {controller: CodesEditorController, templateUrl:TEMPLATE_URL_BASE + 'codes/editcodes.html'}).
        when('/koodisto/:codesUri/:codesVersion', {controller: ViewCodesController, templateUrl:TEMPLATE_URL_BASE + 'codes/viewcodes.html'}).
        when('/koodi/:codeElementUri/:codeElementVersion', {controller: ViewCodeElementController, templateUrl:TEMPLATE_URL_BASE + 'codeelement/viewcodeelement.html'}).
        when('/lisaaKoodi/:codesUri/:codesVersion', {controller: CodeElementCreatorController, templateUrl:TEMPLATE_URL_BASE + 'codeelement/createcodeelement.html'}).
        when('/muokkaaKoodi/:codeElementUri/:codeElementVersion', {controller: CodeElementEditorController, templateUrl:TEMPLATE_URL_BASE + 'codeelement/editcodeelement.html'}).
        when('/lisaaKoodistoryhma', {controller: CodesGroupCreatorController, templateUrl:TEMPLATE_URL_BASE + 'codesgroup/createcodesgroup.html'}).
        when('/koodistoryhma/:id', {controller: ViewCodesGroupController, templateUrl:TEMPLATE_URL_BASE + 'codesgroup/viewcodesgroup.html'}).
        when('/muokkaaKoodistoryhma/:id', {controller: CodesGroupEditorController, templateUrl:TEMPLATE_URL_BASE + 'codesgroup/editcodesgroup.html'}).
         //else
        otherwise({redirectTo: '/etusivu'});
}]);

//rest resources

//Koodistot
app.factory('RootCodes', function ($resource) {
    return $resource(SERVICE_URL_BASE + "codes", {}, {
        get: {method: "GET", isArray: true, params: {} }
    });
});

app.factory('NewCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes", {}, {
        post: {method: "POST"}
    });
});

app.factory('DeleteCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/delete/:codesUri/:codesVersion",
        {codesUri: "@codesUri",codesVersion: "@codesVersion"}, {
            put: {method: "POST"}
        });
});

app.factory('NewCodesGroup', function($resource) {
    return $resource(SERVICE_URL_BASE + "codesgroup", {}, {
        post: {method: "POST"}
    });
});

app.factory('DeleteCodesGroup', function($resource) {
    return $resource(SERVICE_URL_BASE + "codesgroup/delete/:id", {id: "@id"}, {
        post: {method: "POST"}
    });
});

app.factory('UpdateCodesGroup', function($resource) {
    return $resource(SERVICE_URL_BASE + "codesgroup", {}, {
        put: {method: "PUT"}
    });
});

app.factory('UpdateCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes", {}, {
        put: {method: "PUT"}
    });
});

app.factory('CodesByUri', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/:codesUri", {codesUri: "@codesUri"}, {
        get: {method: "GET"} 
    });
});

app.factory('CodesByUriAndVersion', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/:codesUri/:codesVersion", {codesUri: "@codesUri",codesVersion: "@codesVersion"}, {
        get: {method: "GET"}
    });
});

app.factory('CodesGroupByUri', function($resource) {
    return $resource(SERVICE_URL_BASE + "codesgroup/:id", {id: "@id"}, {
        get: {method: "GET"}
    });
});

app.factory('AllCodes', function ($resource) {
    return $resource(SERVICE_URL_BASE + "codes/all", {}, {
        get: {method: "GET", isArray: true}
    });
});


app.factory('DownloadCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/download/:codesUri/:codesVersion",
        {codesUri: "@codesUri", codesVersion: "@codesVersion"}, {
        put: {method: "POST"}
    });
});

app.factory('MyRoles', function($resource) {
    return $resource(CAS_URL, {}, {
        get: {method: "GET", isArray: true}
    });
});


app.factory('CodeElementsByCodesUriAndVersion', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/codes/:codesUri/:codesVersion", {codesUri: "@codesUri", codesVersion: "@codesVersion"}, {
        get: {method: "GET", isArray: true}
    });
});

app.factory('CodeElementByUriAndVersion', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codeElementUri/:codeElementVersion", {codeElementUri: "@codeElementUri", codeElementVersion: "@codeElementVersion"}, {
        get: {method: "GET", isArray: false}
    });
});

app.factory('CodeElementByCodeElementUri', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codesUri/:codesVersion/:codeElementUri", {codesUri: "@codesUri", codesVersion: "@codesVersion", codeElementUri: "@codeElementUri"}, {
        get: {method: "GET", isArray: false}
    });
});

app.factory('CodeElementVersionsByCodeElementUri', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codeElementUri", {codeElementUri: "@codeElementUri"}, {
        get: {method: "GET", isArray: true}
    });
});

app.factory('LatestCodeElementVersionsByCodeElementUri', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/latest/:codeElementUri", {codeElementUri: "@codeElementUri"}, {
        get: {method: "GET", isArray: false}
    });
});

app.factory('NewCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/:codesUri", {}, {
        post: {method: "POST"}
    });
});

app.factory('DeleteCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/delete/:codeElementUri/:codeElementVersion",
        {codeElementUri: "@codeElementUri",codeElementVersion: "@codeElementVersion"}, {
            put: {method: "POST"}
        });
});

app.factory('RemoveRelationCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/removerelation/:codeElementUri/:codeElementUriToRemove/:relationType",
        {codeElementUri: "@codeElementUri",codeElementUriToRemove: "@codeElementUriToRemove",relationType: "@relationType"}, {
        put: {method: "POST"}
    });
});

app.factory('AddRelationCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement/addrelation/:codeElementUri/:codeElementUriToAdd/:relationType",
        {codeElementUri: "@codeElementUri",codeElementUriToAdd: "@codeElementUriToAdd",relationType: "@relationType"}, {
            put: {method: "POST"}
        });
});

app.factory('AddRelationCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/addrelation/:codesUri/:codesUriToAdd/:relationType",
        {codesUri: "@codesUri",codesUriToAdd: "@codesUriToAdd",relationType: "@relationType"}, {
            put: {method: "POST"}
        });
});

app.factory('RemoveRelationCodes', function($resource) {
    return $resource(SERVICE_URL_BASE + "codes/removerelation/:codesUri/:codesUriToRemove/:relationType",
        {codesUri: "@codesUri",codesUriToRemove: "@codesUriToRemove",relationType: "@relationType"}, {
            put: {method: "POST"}
        });
});

app.factory('UpdateCodeElement', function($resource) {
    return $resource(SERVICE_URL_BASE + "codeelement", {}, {
        put: {method: "PUT"}
    });
});

app.factory('Organizations', function ($resource) {
    return $resource(ORGANIZATION_SERVICE_URL_BASE + "rest/organisaatio/hae", {}, {
        get: {method: "GET"}
    });
});

app.factory('OrganizationChildrenByOid', function ($resource) {
    return $resource(ORGANIZATION_SERVICE_URL_BASE + "rest/organisaatio/hae?oidrestrictionlist=:oid&skipparents=true", {oid: "@oid"}, {
	get: {method: "GET"}
    });
});

app.factory('OrganizationByOid', function ($resource) {
    return $resource(ORGANIZATION_SERVICE_URL_BASE + "rest/organisaatio/:oid", {oid: "@oid"}, {
        get: {method: "GET"}
    });
});

app.filter('naturalSort', function() {
    return function(arrInput, field, reverse) {
	var arr = arrInput.sort(function(a, b) {
	    var valueA = field ? "a." + field : "a"; 
	    var valueB = field ? "b." + field : "b";
	    valueA = eval(valueA);
	    valueB = eval(valueB);
	    return naturalSort(isNaN(valueA) ? valueA.trim().toLowerCase() : valueA, isNaN(valueB) ? valueB.trim().toLowerCase() : valueB);
	});
	return reverse? arr.reverse(): arr;
    }
})

function getLanguageSpecificValue(fieldArray,fieldName,language) {
    var returnStr = "";
    if (fieldArray) {
        for (var i = 0; i < fieldArray.length; i++) {
            if (fieldArray[i].kieli === language) {
                var fieldName = "fieldArray[i]."+fieldName;
                return eval(fieldName);
            }
        }
    }
    return returnStr;
}

