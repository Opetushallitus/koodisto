describe("Edit codes test", function() {
    
    var model, scope, mockBackend, q;
    
    beforeEach(module("koodisto", function ($provide) {
	$provide.value('NoCacheInterceptor', {});
	$provide.value('AuthService', {
	    updateOph : function(parameter) {
		var deferred = q.defer();
		deferred.resolve();
		return deferred.promise;
	    }
	});
    }));

    beforeEach(inject(function ($controller, $injector, $rootScope, $routeParams, CodesEditorModel, $q) {	
	scope = $rootScope.$new();
	model = CodesEditorModel;
	q = $q;
	$routeParams.codesUri = "espoonoikeudet";
	$routeParams.codesVersion = 1;
	controller = $controller("CodesEditorController", {$scope: scope, CodesEditorModel : model});
	angular.mock.inject(function ($injector) {
	    mockBackend = $injector.get('$httpBackend');
	})        
    }));
    
    it("CodesEditorModel is defined and it is in scope", function() {
	expect(model).toBeDefined();
	expect(scope.model).toEqual(model);
    })
    
    describe("Relations", function() {
	
	var codesBeingEdited = { 
		"koodistoUri" : "espoonoikeudet",
		"resourceUri" : "http://koodistopalvelu.opintopolku.fi/espoonoikeudet",
		"omistaja" : null,
		"organisaatioOid" : "1.2.246.562.10.90008375488",
		"lukittu" : null,
		"codesGroupUri" : "876876",
		"version" : 3,
		"latestKoodistoVersio" : {
		    "koodistoUri": "espoonoikeudet",
		    "metadata" : [ {
			 "kieli" : "FI",
			 "nimi" : "Espoon oikeudet",
		    }]
		},
		"versio" : 1,
		"paivitysPvm" : 1397543271409,
		"voimassaAlkuPvm" : "2014-04-11",
		"voimassaLoppuPvm" : "2014-04-15",
		"tila" : "HYVAKSYTTY",
		"metadata" : [ {
		    "kieli" : "FI",
		    "nimi" : "Espoon oikeudet",
		    "kuvaus" : "faefaewf",
		    "kayttoohje" : null,
		    "kasite" : null,
		    "kohdealue" : null,
		    "sitovuustaso" : null,
		    "kohdealueenOsaAlue" : null,
		    "toimintaymparisto" : null,
		    "tarkentaaKoodistoa" : null,
		    "huomioitavaKoodisto" : null,
		    "koodistonLahde" : null
		} ],
		"codesVersions" : [ 1 ],
		"withinCodes" : [ ],
		"includesCodes" : [ ],
		"levelsWithCodes" : [ ],
		"uri" : "espoonoikeudet"
	}
	
	var relationCodes = jQuery.extend({}, codesBeingEdited); 
	
	expectGETS = function() {
	    mockBackend.expectGET(SERVICE_URL_BASE + "codes/espoonoikeudet/1").respond(codesBeingEdited);
	    mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]); //requests this twice, but why?
	    mockBackend.expectGET("/organisaatio-service/rest/organisaatio/1.2.246.562.10.90008375488").respond({"nimi" : {
		    "fi" : "Espoon kaupunki"
	    }});
	}
	
	beforeEach(function() {
	    mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]);
	    expectGETS();
	    mockBackend.flush();
	})
	
	it("Error message will be shown when relation is being removed", function() {
	    scope.model.removeFromWithinCodes(relationCodes);
	    scope.okconfirm();
	    mockBackend.expectGET("confirmModalContent.html").respond("<br />");
	    mockBackend.expectPOST(SERVICE_URL_BASE + "codes/removerelation/espoonoikeudet/espoonoikeudet/SISALTYY").respond(500, "");
	    expectGETS();
	    mockBackend.flush();	    
	    expect(scope.model.alerts.length).toEqual(1);
	})
	
	it("Error message will be shown when relation is being added", function() {
	    scope.addToWithinCodes(relationCodes);
	    mockBackend.expectPOST(SERVICE_URL_BASE + "codes/addrelation/espoonoikeudet/espoonoikeudet/SISALTYY").respond(500, "");
	    mockBackend.flush();
	    expect(scope.model.alerts.length).toEqual(1);
	})
    })
        
})