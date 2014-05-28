describe("Code Element Edit test", function() {
    
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

    beforeEach(inject(function ($controller, $injector, $rootScope, $routeParams, CodeElementEditorModel, $q) {
	q = $q;
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
    	
    	var codeElementWithRelation = {
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
    		"withinCodeElements" : [ ],
    		"includesCodeElements" : [{
    		    "codeElementUri" : "relaatiotesti",
    		    "codeElementVersion" : 3
    		}],
    		"levelsWithCodeElements" : [ ]
    	};
    	
    	var codeElementRelation = {
    		"koodiUri" : "relaatiotesti",
    		"koodisto" : {
    		    "koodistoUri" : "relaatiotestikoodisto"
    		},
    		"metadata" : [ {
    		    "nimi" : "ReferenssiKoodi",
    		    "kieli" : "FI"
    		}],
    		"versio" : 3
    	};
    	
    	beforeEach(function() {
    	    mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]);
    	    mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/versiointitesti_uudi/3").respond(codeElementWithRelation);
    	    mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/latest/relaatiotesti").respond(codeElementRelation);
    	    mockBackend.flush();
    	});
    	
        it("Should contain version number of latest code element relation references", function() {
            expect(model.includesCodeElements.length).toEqual(1);
            expect(model.includesCodeElements[0].versio).toEqual(3);
            expect(model.includesCodeElements[0].name).toEqual("ReferenssiKoodi");
        });
    
    });
    
    describe("Adding relations", function() {
        var addToListName, shownCodeElements, withinCodeElements, codeElement1, codeElement2;
        
        beforeEach(function() {
            addToListName = "withincodes";
        
            shownCodeElements = [ {
                "checked" : true,
                "name" : "nimi",
                "uri" : "2organisaatiotesti_arvonen",
                "value" : "arvonen"
            }, {
                "checked" : true,
                "name" : "kakkonen",
                "uri" : "2organisaatiotesti_kakkonen",
                "value" : "kakkonen"
            } ];
        
            withinCodeElements = [];
            
            codeElement1 = {
                    "koodiUri" : "1organisaatiotesti_ykkonen",
                    "withinCodeElements" : [ {
                        "codeElementUri" : "2organisaatiotesti_arvonen",
                        "codeElementVersion" : 1
                    } ],
                    "includesCodeElements" : [],
                    "levelsWithCodeElements" : []
                };

            codeElement2 = {
                    "koodiUri" : "2organisaatiotesti_arvonen",
                    "withinCodeElements" : [],
                    "includesCodeElements" : [ {
                        "codeElementUri" : "1organisaatiotesti_ykkonen",
                        "codeElementVersion" : 1
                    } ],
                    "levelsWithCodeElements" : []
                };

            scope.model.codeelementmodalInstance = {
                    close: jasmine.createSpy('modalInstance.close')
                }
            scope.model.addToListName = addToListName;
            scope.model.shownCodeElements = shownCodeElements;
            scope.model.withinCodeElements = withinCodeElements;
            scope.model.codeElement = codeElement1;
            
            mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]);
            mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/versiointitesti_uudi/3").respond(codeElement1);
            mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/latest/2organisaatiotesti_arvonen").respond(codeElement2);
            mockBackend.flush();
        });

        
        it("should add code to relation list", function() {
            expect(scope.model.withinCodeElements.length).toEqual(1);

            scope.okcodeelement();
            mockBackend.expectPOST(SERVICE_URL_BASE + "codeelement/addrelation/2organisaatiotesti_kakkonen/1organisaatiotesti_ykkonen/SISALTYY").respond(200,
                    "");
            mockBackend.flush();
            expect(scope.model.alerts.length).toEqual(0);
            expect(scope.model.withinCodeElements.length).toEqual(2); // added to list
            expect(scope.model.codeelementmodalInstance.close).toHaveBeenCalledWith();
        });

        it("should add alert if backend fails", function() {
            expect(scope.model.withinCodeElements.length).toEqual(1);
            scope.okcodeelement();
            mockBackend.expectPOST(SERVICE_URL_BASE + "codeelement/addrelation/2organisaatiotesti_kakkonen/1organisaatiotesti_ykkonen/SISALTYY").respond(500,
                    "");
            mockBackend.flush();
            expect(scope.model.withinCodeElements.length).toEqual(1); // alert
            expect(scope.model.alerts.length).toEqual(1); // no change to list
            expect(scope.model.codeelementmodalInstance.close).toHaveBeenCalledWith();
        });
    
    });
});