import {CodeElementEditorController, CodeElementEditorModel} from "../../main/webapp/html/codeelement/editCodeElement";
import {expect} from 'chai';
import {NaturalSortFilter} from "../../main/webapp/html/app.utils";

describe(
    "Code Element Edit test",
    function() {

        let model, controller, CodeElementByUriAndVersion, routeParams, SaveCodeElement;
        const emptyCodeElement = {
            withinCodeElements: [],
            includesCodeElements: [],
            levelsWithCodeElements: [],
        };

        beforeEach(function() {
            const authService = {
                updateOph: (parameter) => Promise.resolve(),
            };
            const scope = {$watch: () => {}};
            const location = {path: () => ({search: () => {}})};
            const codesByUriAndVersion = {};
            const rootCodes = {get: ({}, fun) => {fun([]);}};
            CodeElementByUriAndVersion = CodeElementByUriAndVersion || {get: ({}, fun) => {fun(emptyCodeElement);}};
            SaveCodeElement = SaveCodeElement || {put: () => 'empty'};
            const CodeElementByCodesUriAndVersion = {get: ({}, fun) => {fun(emptyCodeElement);}};
            model = new CodeElementEditorModel({}, location, rootCodes, CodeElementByUriAndVersion, {}, CodeElementByCodesUriAndVersion, {}, authService);
            model.shownCodeElements = [];

            routeParams = {};
            const filterMock = (type) => (type === 'naturalSort' ? NaturalSortFilter : () => 'mock_text');
            routeParams.codeElementUri = "versiointitesti_uudi";
            routeParams.codeElementVersion = 3;
            controller = new CodeElementEditorController(scope, location, routeParams, filterMock, model, codesByUriAndVersion, SaveCodeElement, {}, {}, false, {});
        });

        it("codeElementEditorModel is defined and it is in scope", function() {
            expect(model).to.not.be.undefined;
            expect(controller.model).to.equal(model);
        });

        describe("Relations", function() {
            const codeElementWithRelation = {
                "koodiUri": "versiointitesti_uudi",
                "resourceUri": "http://koodistopalvelu.opintopolku.fi/versiointitesti/koodi/versiointitesti_uudi",
                "version": 1,
                "versio": 3,
                "koodisto": {
                    "koodistoUri": "versiointitesti",
                    "organisaatioOid": "1.2.246.562.10.00000000001",
                    "koodistoVersios": [3]
                },
                "koodiArvo": "uudi",
                "paivitysPvm": 1398346711458,
                "voimassaAlkuPvm": "2014-04-14",
                "voimassaLoppuPvm": null,
                "tila": "HYVAKSYTTY",
                "metadata": [{
                    "nimi": "Uusinkoodi",
                    "kuvaus": "fawegag",
                    "lyhytNimi": "fweafaw",
                    "kayttoohje": null,
                    "kasite": null,
                    "sisaltaaMerkityksen": null,
                    "eiSisallaMerkitysta": null,
                    "huomioitavaKoodi": null,
                    "sisaltaaKoodiston": null,
                    "kieli": "FI"
                }],
                "withinCodeElements": [],
                "includesCodeElements": [{
                    "codeElementUri": "hep2_2",
                    "codeElementVersion": 2,
                    "relationMetadata": [{
                        "nimi": "2",
                        "kuvaus": "2",
                        "lyhytNimi": "2",
                        "kayttoohje": null,
                        "kasite": null,
                        "sisaltaaMerkityksen": null,
                        "eiSisallaMerkitysta": null,
                        "huomioitavaKoodi": null,
                        "sisaltaaKoodiston": null,
                        "kieli": "FI"
                    }],
                    "parentMetadata": [{
                        "kieli": "FI",
                        "nimi": "hep2",
                        "kuvaus": "hep2",
                        "kayttoohje": null,
                        "kasite": null,
                        "kohdealue": null,
                        "sitovuustaso": null,
                        "kohdealueenOsaAlue": null,
                        "toimintaymparisto": null,
                        "tarkentaaKoodistoa": null,
                        "huomioitavaKoodisto": null,
                        "koodistonLahde": null
                    }]
                }],
                "levelsWithCodeElements": []
            };

            before(function() {
                CodeElementByUriAndVersion = {get: ({}, fun) => {fun(codeElementWithRelation);}};
            });

            it("Should contain version number of latest code element relation references", function() {
                expect(model.includesCodeElements.length).to.equal(1);
                expect(model.includesCodeElements[0].versio).to.equal(2);
                expect(model.includesCodeElements[0].name).to.equal("2");
            });

        });

        describe("Adding relations", function() {
            let addToListName, shownCodeElements, withinCodeElements;
            const codeElement1 = {
                "koodiUri" : "1organisaatiotesti_ykkonen",
                "withinCodeElements" : [ {
                    "codeElementUri" : "2organisaatiotesti_arvonen",
                    "codeElementVersion" : 1
                } ],
                "includesCodeElements" : [],
                "levelsWithCodeElements" : []
            };

            before(function() {
                CodeElementByUriAndVersion = {get: ({}, fun) => {fun(codeElement1);}};
            });

            it("should add code to relation list", function() {
                expect(controller.model.withinCodeElements.length).to.equal(1);

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
                controller.model.codeelementmodalInstance = {
                    close: () => {},
                };
                controller.model.addToListName = addToListName;
                controller.model.shownCodeElements = shownCodeElements;
                controller.model.withinCodeElements = withinCodeElements;
                controller.model.codeElement = codeElement1;
                controller.okcodeelement();
                expect(controller.model.withinCodeElements.length).to.equal(2); // added to list
            });

        });

        describe(
            "Removing relations",
            function() {
                const codeWithLotsOfRelations = {
                    "koodiUri": "posti",
                    "withinCodeElements": [{
                        "codeElementUri": "postimerkki",
                        "codeElementVersion": 1
                    }, {
                        "codeElementUri": "kirjekuori",
                        "codeElementVersion": 2
                    }, {
                        "codeElementUri": "osoitetarra",
                        "codeElementVersion": 3
                    }],
                    "includesCodeElements": [{
                        "codeElementUri": "sulkakyna",
                        "codeElementVersion": 1
                    }, {
                        "codeElementUri": "mustepullo",
                        "codeElementVersion": 2
                    }],
                    "levelsWithCodeElements": [{
                        "codeElementUri": "postiluukku",
                        "codeElementVersion": 1
                    }, {
                        "codeElementUri": "postilaatikko",
                        "codeElementVersion": 2
                    }]
                };

                before(function() {
                    CodeElementByUriAndVersion = {get: ({}, fun) => {fun(codeWithLotsOfRelations);}};
                });

                beforeEach(function () {
                    controller.model.codeelementmodalInstance = {
                        close: () => {},
                    };
                });

                it("should remove multiple relations with relation type *within*", function() {
                    controller.model.shownCodeElements = [ {
                        "uri" : "postimerkki",
                        "checked" : false
                    }, {
                        "uri" : "kirjekuori",
                        "checked" : false
                    }, {
                        "uri" : "osoitetarra",
                        "checked" : false
                    } ];
                    controller.model.addToListName = "withincodes";
                    controller.okcodeelement();
                    expect(controller.model.withinCodeElements.length).to.equal(0);
                });

                it("should remove multiple relations with relation type *includes*", function() {
                    controller.model.shownCodeElements = [ {
                        "uri" : "sulkakyna",
                        "checked" : false
                    }, {
                        "uri" : "mustepullo",
                        "checked" : false
                    } ];
                    controller.model.addToListName = "includescodes";
                    controller.okcodeelement();
                    expect(controller.model.includesCodeElements.length).to.equal(0);
                });

                it("should remove multiple relations with relation type *levelswith*", function() {
                    controller.model.shownCodeElements = [ {
                        "uri" : "postiluukku",
                        "checked" : false
                    }, {
                        "uri" : "postilaatikko",
                        "checked" : false
                    } ];
                    controller.model.addToListName = "levelswithcodes";
                    controller.okcodeelement();
                    expect(controller.model.levelsWithCodeElements.length).to.equal(0);
                });
            });

        describe("Removing and adding multiple relations", function() {
            const codeWithSomeRelations = {
                "koodiUri": "posti",
                "withinCodeElements": [{
                    "codeElementUri": "postimerkki",
                    "codeElementVersion": 1
                }, {
                    "codeElementUri": "kirjekuori",
                    "codeElementVersion": 2
                }],
                "includesCodeElements": [{
                    "codeElementUri": "sulkakyna",
                    "codeElementVersion": 1
                }, {
                    "codeElementUri": "mustepullo",
                    "codeElementVersion": 2
                }],
                "levelsWithCodeElements": [{
                    "codeElementUri": "postiluukku",
                    "codeElementVersion": 1
                }]
            };

            before(function() {
                CodeElementByUriAndVersion = {get: ({}, fun) => {fun(codeWithSomeRelations);}};
            });

            beforeEach(function() {
                controller.model.codeelementmodalInstance = {
                    close: () => {},
                };
            });

            it("should add and remove multiple relations with relation type *within*", function() {
                controller.model.shownCodeElements = [ {
                    "uri" : "postimerkki",
                    "checked" : false
                }, {
                    "uri" : "kirjekuori",
                    "checked" : false
                }, {
                    "uri" : "osoitetarra",
                    "checked" : true
                } ];
                controller.model.addToListName = "withincodes";
                controller.okcodeelement();
                expect(controller.model.withinCodeElements.length).to.equal(1);
            });

            it("should add and remove multiple relations with relation type *includes*", function() {
                controller.model.shownCodeElements = [ {
                    "uri" : "sulkakyna",
                    "checked" : false
                }, {
                    "uri" : "mustepullo",
                    "checked" : true
                }, {
                    "uri" : "mustetahra",
                    "checked" : true
                } ];
                controller.model.addToListName = "includescodes";
                controller.okcodeelement();
                expect(controller.model.includesCodeElements.length).to.equal(2);
            });

            it("should add and remove multiple relations with relation type *levelswith*", function() {
                controller.model.shownCodeElements = [ {
                    "uri" : "postiluukku",
                    "checked" : false
                }, {
                    "uri" : "postiauto",
                    "checked" : true
                } ];
                controller.model.addToListName = "levelswithcodes";
                controller.okcodeelement();
                expect(controller.model.levelsWithCodeElements.length).to.equal(1);
            });

        });

        describe("Saving code element", function() {
            const codeWithSomeRelations = {
                "koodiUri": "posti",
                "withinCodeElements": [{
                    "codeElementUri": "postimerkki",
                    "codeElementVersion": 1
                }, {
                    "codeElementUri": "kirjekuori",
                    "codeElementVersion": 2
                }],
                "includesCodeElements": [{
                    "codeElementUri": "sulkakyna",
                    "codeElementVersion": 1
                }, {
                    "codeElementUri": "mustepullo",
                    "codeElementVersion": 2
                }],
                "levelsWithCodeElements": [{
                    "codeElementUri": "postiluukku",
                    "codeElementVersion": 1
                }]
            };


            before(function() {
                CodeElementByUriAndVersion = {get: ({}, fun) => {fun(codeWithSomeRelations);}};
            });

            beforeEach(function() {
                controller.model.codeelementmodalInstance = {
                    close: () => {},
                };
            });

            it("should save valid codeelement.", async function() {
                controller.model.shownCodeElements = [ {
                    "uri" : "postimerkki",
                    "checked" : false
                }, {
                    "uri" : "kirjekuori",
                    "checked" : false
                }, {
                    "uri" : "osoitetarra",
                    "checked" : true
                } ];
                expect(controller.model.alerts.length).to.equal(0);
                controller.model.addToListName = "withincodes";
                controller.okcodeelement();
                expect(controller.model.withinCodeElements.length).to.equal(1);

                SaveCodeElement.put = () => ({$promise: Promise.resolve('4')});
                controller.submit();
                // Wait for promise execution.
                await new Promise(resolve => setTimeout(resolve, 10));
                expect(controller.model.alerts.length).to.equal(0); // no change to list
            });

            it("should show alert if save does not succeed.", async function() {
                controller.model.shownCodeElements = [ {
                    "uri" : "postimerkki",
                    "checked" : false
                }, {
                    "uri" : "kirjekuori",
                    "checked" : false
                }, {
                    "uri" : "osoitetarra",
                    "checked" : true
                } ];
                expect(controller.model.alerts.length).to.equal(0);
                controller.model.addToListName = "withincodes";
                controller.okcodeelement();
                expect(controller.model.withinCodeElements.length).to.equal(1);

                const promise = Promise.reject({});
                SaveCodeElement.put = () => ({$promise: promise});
                controller.submit();
                // Wait for promise execution.
                await new Promise(resolve => setTimeout(resolve, 10));
                expect(controller.model.alerts.length).to.equal(1);
            });
        });
    });
