describe("Codes Tree test", function() {
    
    var codesArray = [{
	  "id" : 0,
	  "koodistoRyhmaUri" : "http://kaikkikoodistot",
	  "metadata" : [ {
	    "nimi" : "Kaikki koodistot"
	  } ],
	  "koodistos" : [ {
	    "koodistoUri" : "tutkinto",
	    "organisaatioOid" : "1.2.246.562.10.00000000001",
	    "latestKoodistoVersio" : {
	      "versio" : 1,
	      "voimassaAlkuPvm" : "2013-01-01",
	      "voimassaLoppuPvm" : null,
	      "metadata" : [ {
	        "kieli" : "FI",
	        "nimi" : "tutkinto"
	      }, {
	        "kieli" : "SV",
	        "nimi" : "tutkinto"
	      }, {
	        "kieli" : "EN",
	        "nimi" : "tutkinto"
	      }, {
	        "kieli" : "FI",
	        "nimi" : "tutkinto"
	      }, {
	        "kieli" : "SV",
	        "nimi" : "tutkinto"
	      }, {
	        "kieli" : "EN",
	        "nimi" : "tutkinto"
	      } ]
	    }
	  }, {
	    "koodistoUri" : "eutukialue",
	    "organisaatioOid" : "1.2.246.562.10.00000000001",
	    "latestKoodistoVersio" : {
	      "versio" : 6,
	      "voimassaAlkuPvm" : "2014-03-31",
	      "voimassaLoppuPvm" : null,
	      "metadata" : [ {
	        "kieli" : "FI",
	        "nimi" : "eututkinto"
	      }, {
	        "kieli" : "SV",
	        "nimi" : "utomlands"
	      }, {
	        "kieli" : "EN",
	        "nimi" : "foreign"
	      }, {
	        "kieli" : "FI",
	        "nimi" : "eutukialue"
	      }, {
	        "kieli" : "SV",
	        "nimi" : "eutukialue"
	      }, {
	        "kieli" : "EN",
	        "nimi" : "eutukialue"
	      } ]
	    }
	  }, {
	    "koodistoUri" : "hakukohteet",
	    "organisaatioOid" : "1.2.246.562.10.00000000001",
	    "latestKoodistoVersio" : {
	      "versio" : 1,
	      "voimassaAlkuPvm" : "2013-01-01",
	      "voimassaLoppuPvm" : null,
	      "metadata" : [ {
	        "kieli" : "FI",
	        "nimi" : "hakukohteet"
	      }, {
	        "kieli" : "SV",
	        "nimi" : "hakukohteet"
	      }, {
	        "kieli" : "EN",
	        "nimi" : "hakukohteet"
	      }, {
	        "kieli" : "FI",
	        "nimi" : "hakukohteet"
	      }, {
	        "kieli" : "SV",
	        "nimi" : "hakukohteet"
	      }, {
	        "kieli" : "EN",
	        "nimi" : "hakukohteet"
	      } ]
	    }
	  }, {
	    "koodistoUri" : "aluehallintovirasto",
	    "organisaatioOid" : "1.2.246.562.10.378471054110",
	    "latestKoodistoVersio" : {
	      "versio" : 48,
	      "voimassaAlkuPvm" : "2014-03-28",
	      "voimassaLoppuPvm" : null,
	      "metadata" : [ {
	        "kieli" : "FI",
	        "nimi" : "aluehallinto"
	      }, {
	        "kieli" : "SV",
	        "nimi" : "aluehallintovirastof6b0"
	      }, {
	        "kieli" : "EN",
	        "nimi" : "aluehallintovirastoi9hdgl"
	      }, {
	        "kieli" : "FI",
	        "nimi" : "aluehallinto"
	      }, {
	        "kieli" : "SV",
	        "nimi" : "aluehallintovirastof6b0"
	      }, {
	        "kieli" : "EN",
	        "nimi" : "aluehallintovirastoi9hdgl"
	      } ]
	    }
	  }, {
	    "koodistoUri" : "uuu",
	    "organisaatioOid" : "1.2.246.562.10.23429950928",
	    "latestKoodistoVersio" : {
	      "versio" : 1,
	      "voimassaAlkuPvm" : "2013-11-30",
	      "voimassaLoppuPvm" : null,
	      "metadata" : [ {
	        "kieli" : "FI",
	        "nimi" : "Henkselit hallussa"
	      } ]
	    }
	  }, {
	    "koodistoUri" : "koulutustyyppi",
	    "organisaatioOid" : "1.2.246.562.10.00000000001",
	    "latestKoodistoVersio" : {
	      "versio" : 1,
	      "voimassaAlkuPvm" : "2013-01-01",
	      "voimassaLoppuPvm" : null,
	      "metadata" : [ {
	        "kieli" : "FI",
	        "nimi" : "koulutustyyppi"
	      }, {
	        "kieli" : "SV",
	        "nimi" : "koulutustyyppi"
	      }, {
	        "kieli" : "EN",
	        "nimi" : "koulutustyyppi"
	      }, {
	        "kieli" : "FI",
	        "nimi" : "koulutustyyppi"
	      }, {
	        "kieli" : "SV",
	        "nimi" : "koulutustyyppi"
	      }, {
	        "kieli" : "EN",
	        "nimi" : "koulutustyyppi"
	      } ]
	    }
	  }]
    }];
    
    var controller, treeModel, scope, mockBackend;
    
    beforeEach(module("koodisto", function ($provide) {
	  $provide.value('NoCacheInterceptor', {});
    }));

    beforeEach(inject(function ($controller, $injector, $rootScope, Treemodel) {	
	scope = $rootScope.$new();
	treeModel = Treemodel;
	controller = $controller("KoodistoTreeController", {$scope: scope, Treemodel : treeModel});
	angular.mock.inject(function ($injector) {
            mockBackend = $injector.get('$httpBackend');
        })        
    }));
    
    it("Treemodel is defined and it is in scope", function() {
	expect(treeModel).toBeDefined();
	expect(scope.domain).toEqual(treeModel);
    });       
    
    describe("Search functionality", function() {
	
	function assertFilter(filterString, expectedAmount) {
	    scope.domain.filter = {name: filterString};
	    scope.domain.update();
	    expect(scope.domain.search.codesfound).toEqual(expectedAmount);
	}
	
	beforeEach(function() {
	    mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond(codesArray);
	    mockBackend.flush();	    
	});
		
	it("filter uses finnish name", function() {
	    assertFilter('tutkin', 2);
	});
	
	it("filter uses swedish name", function() {
	    assertFilter('utoml', 1);
	});
	
	it("filter uses english name", function() {
	    assertFilter('foreign', 1);
	});
	
	it("displays none of the codes when none matches filter", function() {
	    assertFilter('mahdotonrimpsu', 0);
	});
	
	it("filter ignores spaces and capitals", function() {
	   assertFilter('ELIThal', 1);
	});

    });
    
});