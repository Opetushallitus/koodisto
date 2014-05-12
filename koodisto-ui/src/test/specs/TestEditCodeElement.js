describe("Code Element Edit test", function() {
    
    var model, scope, mockBackend;
    
    beforeEach(module("koodisto", function ($provide) {
	$provide.value('NoCacheInterceptor', {});
    }));

    beforeEach(inject(function ($controller, $injector, $rootScope, $routeParams, CodeElementEditorModel) {	
	scope = $rootScope.$new();
	model = CodeElementEditorModel;
	$routeParams.codeElementUri = "versiointitesti_uudi";
	$routeParams.codeElementVersion = 3;
	controller = $controller("CodeElementEditorController", {$scope: scope, CodeElementEditorModel : model});
	angular.mock.inject(function ($injector) {
	    mockBackend = $injector.get('$httpBackend');
	})        
    }));
    
    it("CodeElementEditorModel is defined and it is in scope", function() {
	expect(model).toBeDefined();
	expect(scope.model).toEqual(model);
    })
    
    describe("Relations", function() {
	
	var codesResponse = {
		"koodistoUri" : "versiointitesti",
		"resourceUri" : "http://koodistopalvelu.opintopolku.fi/versiointitesti",
		"omistaja" : null,
		"organisaatioOid" : "1.2.246.562.10.00000000001",
		"lukittu" : null,
		"latestKoodistoVersio" : {
		    "versio" : 5,
		    "paivitysPvm" : 1398346711637,
		    "voimassaAlkuPvm" : "2014-04-24",
		    "voimassaLoppuPvm" : null,
		    "tila" : "HYVAKSYTTY",
		    "version" : 2,
		    "metadata" : [ {
			"kieli" : "FI",
			"nimi" : "Versiointitesti",
			"kuvaus" : "Testataan versiointia",
			"kayttoohje" : null,
			"kasite" : null,
			"kohdealue" : null,
			"sitovuustaso" : null,
			"kohdealueenOsaAlue" : null,
			"toimintaymparisto" : null,
			"tarkentaaKoodistoa" : null,
			"huomioitavaKoodisto" : null,
			"koodistonLahde" : null
		    } ]
		}
	};
	
	givenCodeElementWithRelation = function() {
	    mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/latest/relaatiotesti").respond(codeElementRelation);
	    return {
		"koodiUri" : "versiointitesti_uudi",
		"resourceUri" : "http://koodistopalvelu.opintopolku.fi/versiointitesti/koodi/versiointitesti_uudi",
		"version" : 1,
		"versio" : 3,
		"koodisto" : {
		    "koodistoUri" : "versiointitesti",
		    "organisaatioOid" : "1.2.246.562.10.00000000001",
		    "koodistoVersios" : [ 3 ]
		},
		"koodiArvo" : "uudi",
		"paivitysPvm" : 1398346711458,
		"voimassaAlkuPvm" : "2014-04-14",
		"voimassaLoppuPvm" : null,
		"tila" : "HYVAKSYTTY",
		"metadata" : [ {
		    "nimi" : "Uusinkoodi",
		    "kuvaus" : "fawegag",
		    "lyhytNimi" : "fweafaw",
		    "kayttoohje" : null,
		    "kasite" : null,
		    "sisaltaaMerkityksen" : null,
		    "eiSisallaMerkitysta" : null,
		    "huomioitavaKoodi" : null,
		    "sisaltaaKoodiston" : null,
		    "kieli" : "FI"
		} ],
		"withinCodeElements" : [],
		"includesCodeElements" : [ "relaatiotesti" ],
		"levelsWithCodeElements" : [ ]
	    }
	};
	
	var codeElementRelation = {
		"koodisto" : {
		    "koodistoUri" : "relaatiotestikoodisto"
		},
		"metadata" : [ {
		    "nimi" : "ReferenssiKoodi",
		    "kieli" : "FI"
		}],
		"versio" : 3
	}
	
	beforeEach(function() {
	    mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]);
	    mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/versiointitesti_uudi/3").respond(givenCodeElementWithRelation())
	    mockBackend.flush();
	})
	
	it("Should contain version number of latest code element relation references", function() {
	    expect(model.includesCodeElements.length).toEqual(1);
	    expect(model.includesCodeElements[0].versio).toEqual(3);
	    expect(model.includesCodeElements[0].name).toEqual("ReferenssiKoodi");
	})
    })
})