import {expect} from 'chai';
import {ViewCodeElementController, ViewCodeElementModel} from "../../main/webapp/html/codeelement/viewCodeElement";

describe("Code Element View test", function() {

    let model, controller, CodeElementByUriAndVersion, routeParams, SaveCodeElement, codesByUri;
    const emptyCodeElement = {
        koodisto: {
            koodistoUri: '',
            koodistoVersios: [],
        },
        withinCodeElements: [],
        includesCodeElements: [],
        levelsWithCodeElements: [],

    };

    const codesResponse = {
        "koodistoUri": "versiointitesti",
        "resourceUri": "http://koodistopalvelu.opintopolku.fi/versiointitesti",
        "omistaja": null,
        "organisaatioOid": "1.2.246.562.10.00000000001",
        "lukittu": null,
        "latestKoodistoVersio": {
            "versio": 5,
            "paivitysPvm": 1398346711637,
            "voimassaAlkuPvm": "2014-04-24",
            "voimassaLoppuPvm": null,
            "tila": "HYVAKSYTTY",
            "version": 2,
            "metadata": [{
                "kieli": "FI",
                "nimi": "Versiointitesti",
                "kuvaus": "Testataan versiointia",
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
        }
    };

    beforeEach(function() {
        const scope = {$watch: () => {}};
        codesByUri = {get: ({}, fun) => {fun(codesResponse);}};
        CodeElementByUriAndVersion = CodeElementByUriAndVersion || {get: ({}, fun) => {fun(emptyCodeElement);}};
        SaveCodeElement = SaveCodeElement || {put: () => 'empty'};
        model = new ViewCodeElementModel({}, {}, CodeElementByUriAndVersion, codesByUri, {});
        model.shownCodeElements = [];

        routeParams = {};
        routeParams.codeElementUri = "versiointitesti_uudi";
        routeParams.codeElementVersion = 3;
        controller = new ViewCodeElementController(scope, {}, routeParams, model, {}, {});
    });

    it("viewCodeElementModel is defined and it is in scope", function() {
        expect(model).to.not.be.undefined;
        expect(controller.model).to.equal(model);
    });

    describe("Versioning", function() {

        const givenResponseCodeElement = function(old) {
            codesByUri = {get: ({}, fun) => {fun(codesResponse);}};
            return {
                "koodiUri" : "versiointitesti_uudi",
                "resourceUri" : "http://koodistopalvelu.opintopolku.fi/versiointitesti/koodi/versiointitesti_uudi",
                "version" : 1,
                "versio" : (old ? 2 : 3),
                "koodisto" : {
                    "koodistoUri" : "versiointitesti",
                    "organisaatioOid" : "1.2.246.562.10.00000000001",
                    "koodistoVersios" : [ (old ? 4 : 5) ]
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
                "includesCodeElements" : [],
                "levelsWithCodeElements" : []
            };
        };

        before(function() {
            // in order to get rid of controller's initialization
            CodeElementByUriAndVersion = {get: ({}, fun) => {fun(givenResponseCodeElement(true));}};
        });

        it("Editing old version of code element is prevented", function() {
            CodeElementByUriAndVersion = {get: ({}, fun) => {fun(givenResponseCodeElement(false));}};
            controller.model.init(controller, "versiointitesti_uudi", 2);
            expect(controller.model.editState).to.equal("disabled");
        });

        it("Editing latest version of code element is permitted", function() {
            expect(controller.model.editState).to.equal("");
        });
    });

    describe("Relations", function() {
        const givenResponseCodeElementWithRelation = function() {
            codesByUri = {get: ({}, fun) => {fun(codesResponse);}};
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
                "withinCodeElements" : [ {
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
                "includesCodeElements" : [],
                "levelsWithCodeElements" : []
            };
        };

        before(function() {
            CodeElementByUriAndVersion = {get: ({}, fun) => {fun(givenResponseCodeElementWithRelation());}};
        });

        it("Should contain version number of code element relation references", function() {
            expect(model.withinCodeElements.length).to.equal(1);
            expect(model.withinCodeElements[0].versio).to.equal(2);
            expect(model.withinCodeElements[0].name).to.equal("2");
        });
    });

    describe("Caching", function() {

        const givenResponseCodeElementCaching = function() {
            codesByUri = {get: ({}, fun) => {fun(codesResponse);}};
            return {
                "koodiUri" : "versiointitesti_uudi",
                "resourceUri" : "http://koodistopalvelu.opintopolku.fi/versiointitesti/koodi/versiointitesti_uudi",
                "version" : 1,
                "versio" : 3,
                "koodisto" : {
                    "koodistoUri" : "versiointitesti",
                    "organisaatioOid" : "1.2.246.562.10.00000000001",
                    "koodistoVersios" : [ 5 ]
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
                "includesCodeElements" : [],
                "levelsWithCodeElements" : []
            };
        };

        beforeEach(function() {
            // in order to get rid of controller's initialization
            CodeElementByUriAndVersion = {get: ({}, fun) => {fun(givenResponseCodeElementCaching());}};
        });

        it("Calling subsequent inits should make no calls to backend", function() {
            controller.model.init(controller, "versiointitesti_uudi", 3);
            controller.model.init(controller, "versiointitesti_uudi", 3);
            controller.model.init(controller, "versiointitesti_uudi", 3);
        });

        it("Calling init after forcerefresh should load eveything", function() {
            controller.model.init(controller, "versiointitesti_uudi", 3);

            model.forceRefresh = true; // simulates clicking the edit link
            controller.model.init(controller, "versiointitesti_uudi", 3);
        });

    });

});