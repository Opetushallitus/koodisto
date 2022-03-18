import {ViewCodesController, ViewCodesModel} from "../../main/webapp/html/codes/viewCodes";
import {expect} from 'chai';

describe("Codes View test", function() {

    let model, controller, CodesByUriAndVersion, CodeElementsByCodesUriAndVersion;

    const emptyCodes = {
        codesVersions: [],
        withinCodes: [],
        includesCodes: [],
        levelsWithCodes: [],
    };

    const emptyCodeElement = {
        koodisto: {
            koodistoUri: '',
            koodistoVersios: [],
        },
        withinCodeElements: [],
        includesCodeElements: [],
        levelsWithCodeElements: [],

    };

    beforeEach(function() {
        const scope = {$watch: () => {}};
        const rootCodes = {get: ({}, fun) => {fun([]);}};
        CodesByUriAndVersion = CodesByUriAndVersion || {get: ({}, fun) => {fun(emptyCodes);}};
        const organizationByOid = {get: ({}, fun) => {fun({nimi: {fi: 'nimi'}});}};
        CodeElementsByCodesUriAndVersion = CodeElementsByCodesUriAndVersion  || {get: ({}, fun) => {fun(emptyCodeElement);}};
        model = new ViewCodesModel({}, rootCodes, CodesByUriAndVersion, CodeElementsByCodesUriAndVersion, {}, organizationByOid, {});
        const routeParams = {
            codesUri: "espoonoikeudet",
            codesVersion: 1,
        };
        const filterMock = (type) => (() => 'mock_text');
        controller = new ViewCodesController(scope, {}, filterMock, routeParams, {}, model, {}, {}, {}, {}, false);
    });

    it("viewCodesModel is defined and it is in scope", function() {
        expect(model).to.not.be.undefined;
        expect(controller.model).to.equal(model);
    });

    describe("Versioning", function() {

        function givenCodesResponse(first) {
            CodeElementsByCodesUriAndVersion = {get: ({}, fun) => {fun([]);}};
            return {
                "koodistoUri" : "espoonoikeudet",
                "resourceUri" : "http://koodistopalvelu.opintopolku.fi/espoonoikeudet",
                "omistaja" : null,
                "organisaatioOid" : "1.2.246.562.10.90008375488",
                "lukittu" : null,
                "codesGroupUri" : "876876",
                "version" : 3,
                "versio" : first ? 1 : 2,
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
                "codesVersions" : [ first ? 2 : 1 ],
                "withinCodes" : [],
                "includesCodes" : [],
                "levelsWithCodes" : []
            };

        }

        before(function() {
            // in order to get rid of controller's initialization
            CodesByUriAndVersion = {get: ({}, fun) => {fun(givenCodesResponse(true));}};
        });

        it("Editing old version of codes is prevented", function() {
            expect(model.editState).to.equal("disabled");
        });

        it("Editing latest version of codes is permitted", function() {
            CodesByUriAndVersion = {get: ({}, fun) => {fun(givenCodesResponse(false));}};
            model.init(controller, "espoonoikeudet", 2);
            expect(model.editState).to.equal("");
        });
    });

    describe("Relations", function() {
        function givenCodesWithRelationsResponse() {
            CodeElementsByCodesUriAndVersion = {get: ({}, fun) => {fun([]);}};
            return {
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
                    "codesUri" : "kauniaisenkoodit",
                    "codesVersion" : 2,
                    "nimi": {
                        "FI": "Kauniaisen koodit"
                    }
                } ],
                "includesCodes" : [],
                "levelsWithCodes" : []
            };
        }

        before(function() {
            CodesByUriAndVersion = {get: ({}, fun) => {fun(givenCodesWithRelationsResponse());}};
        });

        it("Should contain version number of codes relation references", function() {
            expect(model.withinCodes.length).to.equal(1);
            expect(model.withinCodes[0].versio).to.equal(2);
        });
    });

});
