import {CodesEditorController, CodesEditorModel} from "../../main/webapp/html/codes/editCodes";
import {CodesMatcher} from "../../main/webapp/html/codes/codesMatcher";
import {expect} from 'chai';

describe("Edit codes test", function() {

    let model, controller, CodesByUriAndVersion, SaveCodes;

    const emptyCodes = {
        withinCodes: [],
        includesCodes: [],
        levelsWithCodes: [],
    };

    beforeEach(function () {
        const authService = {
            updateOph: (parameter) => Promise.resolve(),
        };
        const rootCodes = {get: ({}, fun) => {fun([]);}};
        CodesByUriAndVersion = CodesByUriAndVersion || {get: ({}, fun) => {fun(emptyCodes);}};
        SaveCodes = SaveCodes || {put: () => 'empty'};
        const organizationByOid = {get: ({}, fun) => {fun({nimi: {fi: 'nimi'}});}};
        model = new CodesEditorModel({}, rootCodes, {}, CodesByUriAndVersion, organizationByOid, CodesByUriAndVersion, authService, {});
        const routeParams = {
            codesUri: "espoonoikeudet",
            codesVersion: 1,
        };
        const matcher = new CodesMatcher();
        const filterMock = (type) => (() => 'mock_text');
        controller = new CodesEditorController({}, {}, {}, {}, routeParams, filterMock, model, {}, matcher, SaveCodes, false, {});
    });

    it("codesEditorModel is defined and it is in scope", function() {
        expect(model).to.not.be.undefined;
        expect(controller.model).to.equal(model);
    });

    describe("Relations", function() {
        const givenCodesWithRelationsResponse = {
            "koodistoUri": "espoonoikeudet",
            "resourceUri": "http://koodistopalvelu.opintopolku.fi/espoonoikeudet",
            "omistaja": null,
            "organisaatioOid": "1.2.246.562.10.90008375488",
            "lukittu": null,
            "codesGroupUri": "876876",
            "version": 3,
            "versio": 1,
            "paivitysPvm": 1397543271409,
            "voimassaAlkuPvm": "2014-04-11",
            "voimassaLoppuPvm": "2014-04-15",
            "tila": "HYVAKSYTTY",
            "latestKoodistoVersio": {
                "koodistoUri": "espoonoikeudet",
                "metadata": [{
                    "kieli": "FI",
                    "nimi": "Espoon oikeudet"
                }]
            },
            "metadata": [{
                "kieli": "FI",
                "nimi": "Espoon oikeudet",
                "kuvaus": "faefaewf",
                "kayttoohje": null,
                "kasite": null,
                "kohdealue": null,
                "sitovuustaso": null,
                "kohdealueenOsaAlue": null,
                "toimintaymparisto": null,
                "tarkentaaKoodistoa": null,
                "huomioitavaKoodisto": null,
                "koodistonLahde": null
            }],
            "codesVersions": [1],
            "withinCodes": [{
                "codesUri": "kauniaisenkoodit",
                "codesVersion": 2,
                "nimi": {
                    "FI": "Kauniaisen koodit"
                }
            }],
            "includesCodes": [],
            "levelsWithCodes": [],
            "uri": "espoonoikeudet"
        };

        const relationCodes = Object.assign({}, givenCodesWithRelationsResponse);

        before(function() {
            CodesByUriAndVersion = {get: ({}, fun) => {fun(givenCodesWithRelationsResponse);}};
        });

        it("should show error message when removerelation is rejected", async function() {
            const relationCount = controller.model.withinCodes.length;
            controller.model.withinRelationToRemove = relationCodes;
            controller.okconfirm();
            SaveCodes.put = () => ({$promise: Promise.reject({})});
            controller.submit();
            // Wait for promise execution.
            await new Promise(resolve => setTimeout(resolve, 10));
            expect(controller.model.alerts.length).to.equal(1);
            expect(controller.model.withinCodes.length).to.equal(relationCount);
        });

        it("should show error message when addrelation is rejected", async function() {
            const relationCount = controller.model.withinCodes.length;
            controller.addToWithinCodes(relationCodes);
            SaveCodes.put = () => ({$promise: Promise.reject({})});
            controller.submit();
            // Wait for promise execution.
            await new Promise(resolve => setTimeout(resolve, 10));
            expect(controller.model.withinCodes.length).to.equal(relationCount+1); // The changes are not reverted when failing update.
            expect(controller.model.alerts.length).to.equal(1);
        });

        it("Should contain version number of codes relation references", function() {
            expect(model.withinCodes.length).to.equal(1);
            expect(model.withinCodes[0].versio).to.equal(2);
        });

        it("Should prevent manual versioning", function() {
            expect(model.states.filter(function(item) { return item.key==='LUONNOS'; }).length).to.equal(0);
        });

    });

});
