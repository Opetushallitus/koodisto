describe("Codes View test", function() {

    var model, scope, mockBackend;

    beforeEach(module("koodisto", function($provide) {
        $provide.value('NoCacheInterceptor', {});
    }));

    beforeEach(inject(function($controller, $injector, $rootScope, $routeParams, ViewCodesModel) {
        scope = $rootScope.$new();
        model = ViewCodesModel;
        $routeParams.codesUri = "espoonoikeudet";
        $routeParams.codesVersion = 1;
        controller = $controller("ViewCodesController", {
            $scope : scope,
            ViwCodesModel : model
        });
        angular.mock.inject(function($injector) {
            mockBackend = $injector.get('$httpBackend');
        });
        mockBackend.whenGET(SERVICE_URL_BASE + "session/maxinactiveinterval").respond(1);
    }));

    it("ViewCodesModel is defined and it is in scope", function() {
        expect(model).toBeDefined();
        expect(scope.model).toEqual(model);
    });

    describe("Versioning", function() {

        function givenCodesResponse(first) {
            mockBackend.expectGET("/organisaatio-service/rest/organisaatio/1.2.246.562.10.90008375488").respond({
                "nimi" : {
                    "fi" : "Espoon kaupunki"
                }
            });
            mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/codes/espoonoikeudet/" + (first ? 1 : 2)).respond([]);
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

        beforeEach(function() {
            // in order to get rid of controller's initialization
            mockBackend.expectGET(SERVICE_URL_BASE + "codes/espoonoikeudet/1").respond(givenCodesResponse(true));
            mockBackend.flush();
        });

        it("Editing old version of codes is prevented", function() {
            expect(model.editState).toEqual("disabled");
        });

        it("Editing latest version of codes is permitted", function() {
            mockBackend.expectGET(SERVICE_URL_BASE + "codes/espoonoikeudet/2").respond(givenCodesResponse(false));
            model.init(scope, "espoonoikeudet", 2);
            mockBackend.flush();
            expect(model.editState).toEqual("");
        });
    });

    describe("Relations", function() {

        function givenCodesWithRelationsResponse() {
            mockBackend.expectGET(SERVICE_URL_BASE + "codes/kauniaisenkoodit").respond({
                "latestKoodistoVersio" : [ {
                    "metadata" : [ {
                        "kieli" : "FI",
                        "nimi" : "Kauniaisen koodit"
                    } ]
                } ]
            });
            mockBackend.expectGET("/organisaatio-service/rest/organisaatio/1.2.246.562.10.90008375488").respond({
                "nimi" : {
                    "fi" : "Espoon kaupunki"
                }
            });
            mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/codes/espoonoikeudet/1").respond([]);
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
                    "codesVersion" : 2
                } ],
                "includesCodes" : [],
                "levelsWithCodes" : []
            };
        }

        beforeEach(function() {
            mockBackend.expectGET(SERVICE_URL_BASE + "codes/espoonoikeudet/1").respond(givenCodesWithRelationsResponse());
            mockBackend.flush();
        });

        it("Should contain version number of codes relation references", function() {
            expect(model.withinCodes.length).toEqual(1);
            expect(model.withinCodes[0].versio).toEqual(2);
        });
    });

    describe("Caching", function() {

        function givenCodesWithRelationsResponse() {
            mockBackend.expectGET(SERVICE_URL_BASE + "codes/kauniaisenkoodit").respond({
                "latestKoodistoVersio" : [ {
                    "metadata" : [ {
                        "kieli" : "FI",
                        "nimi" : "Kauniaisen koodit"
                    } ]
                } ]
            });
            mockBackend.expectGET("/organisaatio-service/rest/organisaatio/1.2.246.562.10.90008375488").respond({
                "nimi" : {
                    "fi" : "Espoon kaupunki"
                }
            });
            mockBackend.expectGET(SERVICE_URL_BASE + "codeelement/codes/espoonoikeudet/1").respond([]);

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
                    "codesVersion" : 2
                } ],
                "includesCodes" : [],
                "levelsWithCodes" : []
            };
        }

        beforeEach(function() {
            mockBackend.expectGET(SERVICE_URL_BASE + "codes/espoonoikeudet/1").respond(givenCodesWithRelationsResponse());
            mockBackend.flush();
        });

        it("Calling subsequent inits should make no calls to backend", function() {
            model.init(scope, "espoonoikeudet", 1);
            model.init(scope, "espoonoikeudet", 1);
            model.init(scope, "espoonoikeudet", 1);
        });

        it("Calling init after forcerefresh should load eveything", function() {
            model.init(scope, "espoonoikeudet", 1);

            model.forceRefresh = true; // simulates clicking the edit link
            mockBackend.expectGET(SERVICE_URL_BASE + "codes/espoonoikeudet/1").respond(givenCodesWithRelationsResponse());
            model.init(scope, "espoonoikeudet", 1);
            mockBackend.flush();
        });
    });
});