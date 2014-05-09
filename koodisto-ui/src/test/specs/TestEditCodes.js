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
	
	var givenCodesWithRelationsResponse = {	   
		"koodistoUri" : "espoonoikeudet",
		"resourceUri" : "http://koodistopalvelu.opintopolku.fi/espoonoikeudet",
		"omistaja" : null,
		"organisaatioOid" : "1.2.246.562.10.90008375488",
		"lukittu" : null,
		"codesGroupUri" : "876876",
		"version" : 3,
		"versio" : 1,
		"paivitysPvm" : 1397543271409,
		"voimassaAlkuPvm" : "2014-04-11",
		"voimassaLoppuPvm" : "2014-04-15",
		"tila" : "HYVAKSYTTY",
		"latestKoodistoVersio" : {
		    "koodistoUri" : "espoonoikeudet",
		    "metadata" : [ {
			"kieli" : "FI",
			"nimi" : "Espoon oikeudet"
		    }]
		}, 
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
		"withinCodes" : [ {
		    "codesUri": "kauniaisenkoodit",
		    "codesVersion": 2
		} ],
		"includesCodes" : [ ],
		"levelsWithCodes" : [ ],
		"uri" : "espoonoikeudet"
	}
	
	var relationCodes = jQuery.extend({}, givenCodesWithRelationsResponse); 
	
	beforeEach(function() {
	    mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]);
	    mockBackend.expectGET(SERVICE_URL_BASE + "codes/espoonoikeudet/1").respond(givenCodesWithRelationsResponse);
	    mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]); //requests this twice, but why?
	    mockBackend.expectGET(SERVICE_URL_BASE + "codes/kauniaisenkoodit").respond( {
		"latestKoodistoVersio" : [{
		    "metadata": [{
			"kieli" : "FI",
			"nimi" : "Kauniaisen koodit"
		    }]
		}]
	    })
	    mockBackend.expectGET("/organisaatio-service/rest/organisaatio/1.2.246.562.10.90008375488").respond({"nimi" : {
		"fi" : "Espoon kaupunki"
	    }});
	    mockBackend.flush();
	})
	
	it("Error message will be shown when relation is being removed", function() {
	    scope.model.modalInstance = {
		    close: jasmine.createSpy('modalInstance.close')
	    }
	    scope.model.withinRelationToRemove = relationCodes;
	    scope.okconfirm();
	    mockBackend.expectPOST(SERVICE_URL_BASE + "codes/removerelation/espoonoikeudet/espoonoikeudet/SISALTYY").respond(500, "");
	    mockBackend.flush();	    
	    expect(scope.model.alerts.length).toEqual(1);
	    expect(scope.model.modalInstance.close).toHaveBeenCalledWith();
	})
	
	it("Error message will be shown when relation is being added", function() {
	    console.log(relationCodes);
	    scope.addToWithinCodes(relationCodes);
	    mockBackend.expectPOST(SERVICE_URL_BASE + "codes/addrelation/espoonoikeudet/espoonoikeudet/SISALTYY").respond(500, "");
	    mockBackend.flush();
	    expect(scope.model.alerts.length).toEqual(1);
	})
	
	it("Should contain version number of codes relation references", function() {
	    expect(model.withinCodes.length).toEqual(1);
	    expect(model.withinCodes[0].versio).toEqual(2);
	})
    })
        
})