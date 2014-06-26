describe(
        "Code Element Edit test",
        function() {

            var model, scope, mockBackend, q;

            beforeEach(module("koodisto", function($provide) {
                $provide.value('NoCacheInterceptor', {});
                $provide.value('AuthService', {
                    updateOph : function(parameter) {
                        var deferred = q.defer();
                        deferred.resolve();
                        return deferred.promise;
                    }
                });
            }));

            beforeEach(inject(function($controller, $injector, $rootScope, $routeParams, CodeElementEditorModel, $q) {
                q = $q;
                scope = $rootScope.$new();
                model = CodeElementEditorModel;
                model.shownCodeElements = [];
                $routeParams.codeElementUri = "versiointitesti_uudi";
                $routeParams.codeElementVersion = 3;
                controller = $controller("CodeElementEditorController", {
                    $scope : scope,
                    CodeElementEditorModel : model,
                    isModalController : false
                });
                angular.mock.inject(function() {
                    mockBackend = $injector.get('$httpBackend');
                });
            }));

            it("CodeElementEditorModel is defined and it is in scope", function() {
                expect(model).toBeDefined();
                expect(scope.model).toEqual(model);
            });

            describe("Relations", function() {

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
                    "withinCodeElements" : [],
                    "includesCodeElements" : [ {
                        "codeElementUri" : "hep2_2",
                        "codeElementVersion" : 2,
                        "relationMetadata" : [ {
                            "nimi" : "2",
                            "kuvaus" : "2",
                            "lyhytNimi" : "2",
                            "kayttoohje" : null,
                            "kasite" : null,
                            "sisaltaaMerkityksen" : null,
                            "eiSisallaMerkitysta" : null,
                            "huomioitavaKoodi" : null,
                            "sisaltaaKoodiston" : null,
                            "kieli" : "FI"
                        } ],
                        "parentMetadata" : [ {
                            "kieli" : "FI",
                            "nimi" : "hep2",
                            "kuvaus" : "hep2",
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
                    } ],
                    "levelsWithCodeElements" : []
                };

                beforeEach(function() {
                    mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]);
                    mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/versiointitesti_uudi/3").respond(codeElementWithRelation);
                    mockBackend.flush();
                });

                it("Should contain version number of latest code element relation references", function() {
                    expect(model.includesCodeElements.length).toEqual(1);
                    expect(model.includesCodeElements[0].versio).toEqual(2);
                    expect(model.includesCodeElements[0].name).toEqual("2");
                });

            });

            describe("Adding relations", function() {
                var addToListName, shownCodeElements, withinCodeElements, codeElement1;

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
                        close : jasmine.createSpy('modalInstance.close')
                    };
                    scope.model.addToListName = addToListName;
                    scope.model.shownCodeElements = shownCodeElements;
                    scope.model.withinCodeElements = withinCodeElements;
                    scope.model.codeElement = codeElement1;

                    mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]);
                    mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/versiointitesti_uudi/3").respond(codeElement1);
                    mockBackend.flush();
                });

                it("should add code to relation list", function() {
                    expect(scope.model.withinCodeElements.length).toEqual(1);

                    scope.okcodeelement();
                    mockBackend.expectPOST(SERVICE_URL_BASE + "codeelement/addrelations/1organisaatiotesti_ykkonen/SISALTYY")
                            .respond(200, "");
                    mockBackend.flush();
                    expect(scope.model.alerts.length).toEqual(0);
                    expect(scope.model.withinCodeElements.length).toEqual(2); // added to list
                    expect(scope.model.codeelementmodalInstance.close).toHaveBeenCalledWith();
                });

                it("should add alert if backend fails", function() {
                    expect(scope.model.withinCodeElements.length).toEqual(1);
                    scope.okcodeelement();
                    mockBackend.expectPOST(SERVICE_URL_BASE + "codeelement/addrelations/1organisaatiotesti_ykkonen/SISALTYY")
                            .respond(500, "");
                    mockBackend.flush();
                    expect(scope.model.withinCodeElements.length).toEqual(1); // alert
                    expect(scope.model.alerts.length).toEqual(1); // no change to list
                    expect(scope.model.codeelementmodalInstance.close).toHaveBeenCalledWith();
                });

            });

            describe(
                    "Removing relations",
                    function() {

                        var codeWithLotsOfRelations = {
                            "koodiUri" : "posti",
                            "withinCodeElements" : [ {
                                "codeElementUri" : "postimerkki",
                                "codeElementVersion" : 1
                            }, {
                                "codeElementUri" : "kirjekuori",
                                "codeElementVersion" : 2
                            }, {
                                "codeElementUri" : "osoitetarra",
                                "codeElementVersion" : 3
                            } ],
                            "includesCodeElements" : [ {
                                "codeElementUri" : "sulkakyna",
                                "codeElementVersion" : 1
                            }, {
                                "codeElementUri" : "mustepullo",
                                "codeElementVersion" : 2
                            } ],
                            "levelsWithCodeElements" : [ {
                                "codeElementUri" : "postiluukku",
                                "codeElementVersion" : 1
                            }, {
                                "codeElementUri" : "postilaatikko",
                                "codeElementVersion" : 2
                            } ]
                        };

                        beforeEach(function() {
                            mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]);
                            mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/versiointitesti_uudi/3").respond(codeWithLotsOfRelations);
                            mockBackend.flush();
                            scope.model.codeelementmodalInstance = {
                                close : jasmine.createSpy('modalInstance.close')
                            };
                        });

                        it(
                                "should remove multiple relations with relation type *within*",
                                function() {
                                    scope.model.shownCodeElements = [ {
                                        "uri" : "postimerkki",
                                        "checked" : false
                                    }, {
                                        "uri" : "kirjekuori",
                                        "checked" : false
                                    }, {
                                        "uri" : "osoitetarra",
                                        "checked" : false
                                    } ];
                                    scope.model.addToListName = "withincodes";
                                    mockBackend
                                            .expectDELETE(
                                                    SERVICE_URL_BASE
                                                            + "codeelement/removerelations/posti/SISALTYY?isChild=true&relationsToRemove=postimerkki&relationsToRemove=kirjekuori&relationsToRemove=osoitetarra")
                                            .respond();
                                    scope.okcodeelement();
                                    mockBackend.flush();
                                    expect(scope.model.withinCodeElements.length).toEqual(0);
                                });

                        it(
                                "should remove multiple relations with relation type *includes*",
                                function() {
                                    scope.model.shownCodeElements = [ {
                                        "uri" : "sulkakyna",
                                        "checked" : false
                                    }, {
                                        "uri" : "mustepullo",
                                        "checked" : false
                                    } ];
                                    scope.model.addToListName = "includescodes";
                                    mockBackend
                                            .expectDELETE(
                                                    SERVICE_URL_BASE
                                                            + "codeelement/removerelations/posti/SISALTYY?isChild=false&relationsToRemove=sulkakyna&relationsToRemove=mustepullo")
                                            .respond();
                                    scope.okcodeelement();
                                    mockBackend.flush();
                                    expect(scope.model.includesCodeElements.length).toEqual(0);
                                });

                        it(
                                "should remove multiple relations with relation type *levelswith*",
                                function() {
                                    scope.model.shownCodeElements = [ {
                                        "uri" : "postiluukku",
                                        "checked" : false
                                    }, {
                                        "uri" : "postilaatikko",
                                        "checked" : false
                                    } ];
                                    scope.model.addToListName = "levelswithcodes";
                                    mockBackend
                                            .expectDELETE(
                                                    SERVICE_URL_BASE
                                                            + "codeelement/removerelations/posti/RINNASTEINEN?isChild=true&relationsToRemove=postiluukku&relationsToRemove=postilaatikko")
                                            .respond();
                                    scope.okcodeelement();
                                    mockBackend.flush();
                                    expect(scope.model.levelsWithCodeElements.length).toEqual(0);
                                });

                        afterEach(function() {
                            expect(scope.model.codeelementmodalInstance.close).toHaveBeenCalledWith();
                        });
                    });

            describe("Removing and adding multiple relations", function() {

                var codeWithSomeRelations = {
                    "koodiUri" : "posti",
                    "withinCodeElements" : [ {
                        "codeElementUri" : "postimerkki",
                        "codeElementVersion" : 1
                    }, {
                        "codeElementUri" : "kirjekuori",
                        "codeElementVersion" : 2
                    } ],
                    "includesCodeElements" : [ {
                        "codeElementUri" : "sulkakyna",
                        "codeElementVersion" : 1
                    }, {
                        "codeElementUri" : "mustepullo",
                        "codeElementVersion" : 2
                    } ],
                    "levelsWithCodeElements" : [ {
                        "codeElementUri" : "postiluukku",
                        "codeElementVersion" : 1
                    } ]
                };

                beforeEach(function() {
                    mockBackend.expectGET(SERVICE_URL_BASE + "codes").respond([]);
                    mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/versiointitesti_uudi/3").respond(codeWithSomeRelations);
                    mockBackend.flush();
                    scope.model.codeelementmodalInstance = {
                        close : jasmine.createSpy('modalInstance.close')
                    }
                });

                it("should add and remove multiple relations with relation type *within*", function() {
                    scope.model.shownCodeElements = [ {
                        "uri" : "postimerkki",
                        "checked" : false
                    }, {
                        "uri" : "kirjekuori",
                        "checked" : false
                    }, {
                        "uri" : "osoitetarra",
                        "checked" : true
                    } ];
                    scope.model.addToListName = "withincodes";
                    mockBackend.expectPOST(SERVICE_URL_BASE + "codeelement/addrelations/posti/SISALTYY").respond(200, "");
                    mockBackend.expectDELETE(
                            SERVICE_URL_BASE
                                    + "codeelement/removerelations/posti/SISALTYY?isChild=true&relationsToRemove=postimerkki&relationsToRemove=kirjekuori")
                            .respond();
                    scope.okcodeelement();
                    mockBackend.flush();
                    expect(scope.model.withinCodeElements.length).toEqual(1);
                });

                it("should add and remove multiple relations with relation type *includes*", function() {
                    scope.model.shownCodeElements = [ {
                        "uri" : "sulkakyna",
                        "checked" : false
                    }, {
                        "uri" : "mustepullo",
                        "checked" : true
                    }, {
                        "uri" : "mustetahra",
                        "checked" : true
                    } ];
                    scope.model.addToListName = "includescodes";
                    mockBackend.expectPOST(SERVICE_URL_BASE + "codeelement/addrelations/posti/SISALTYY").respond(200, "");
                    mockBackend.expectDELETE(SERVICE_URL_BASE + "codeelement/removerelations/posti/SISALTYY?isChild=false&relationsToRemove=sulkakyna")
                            .respond();
                    scope.okcodeelement();
                    mockBackend.flush();
                    expect(scope.model.includesCodeElements.length).toEqual(2);
                });

                it("should add and remove multiple relations with relation type *levelswith*", function() {
                    scope.model.shownCodeElements = [ {
                        "uri" : "postiluukku",
                        "checked" : false
                    }, {
                        "uri" : "postiauto",
                        "checked" : true
                    } ];
                    scope.model.addToListName = "levelswithcodes";
                    mockBackend.expectPOST(SERVICE_URL_BASE + "codeelement/addrelations/posti/RINNASTEINEN").respond(200, "");
                    mockBackend.expectDELETE(SERVICE_URL_BASE + "codeelement/removerelations/posti/RINNASTEINEN?isChild=true&relationsToRemove=postiluukku")
                            .respond();
                    scope.okcodeelement();
                    mockBackend.flush();
                    expect(scope.model.levelsWithCodeElements.length).toEqual(1);
                });

                afterEach(function() {
                    expect(scope.model.codeelementmodalInstance.close).toHaveBeenCalledWith();
                });
            });
        });