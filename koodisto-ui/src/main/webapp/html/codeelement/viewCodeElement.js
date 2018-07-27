import {getLanguageSpecificValue, getLanguageSpecificValueOrValidValue} from "../app";

export class ViewCodeElementModel {
    constructor($location, $modal, CodeElementByUriAndVersion, CodesByUri, LatestCodeElementVersionsByCodeElementUri) {
        "ngInject";
        this.$location = $location;
        this.$modal = $modal;
        this.CodeElementByUriAndVersion = CodeElementByUriAndVersion;
        this.CodesByUri = CodesByUri;
        this.LatestCodeElementVersionsByCodeElementUri = LatestCodeElementVersionsByCodeElementUri;

        this.withinCodeElements = [];
        this.includesCodeElements = [];
        this.levelsWithCodeElements = [];
        this.deleteState = "disabled";
        this.alerts = [];
    }

    init(scope, codeElementUri, codeElementVersion) {
        if (this.forceRefresh || !(this.codeElement && this.codeElement.koodiUri === codeElementUri && this.codeElement.versio === codeElementVersion)) {
            this.codeElement = null;
            scope.showPassive = false;
            this.withinCodeElements = [];
            this.includesCodeElements = [];
            this.levelsWithCodeElements = [];
            this.deleteState = "disabled";
            this.editState = "";
            this.alerts = [];

            this.getCodeElement(scope, codeElementUri, codeElementVersion);
        }
    }

    getCodeElement(scope, codeElementUri, codeElementVersion) {
        this.CodeElementByUriAndVersion.get({
            codeElementUri : codeElementUri,
            codeElementVersion : codeElementVersion
        }, (result) => {
            this.codeElement = result;

            this.namefi = getLanguageSpecificValue(result.metadata, 'nimi', 'FI');
            this.namesv = getLanguageSpecificValue(result.metadata, 'nimi', 'SV');
            this.nameen = getLanguageSpecificValue(result.metadata, 'nimi', 'EN');
            this.name = getLanguageSpecificValueOrValidValue(result.metadata, 'nimi', 'FI');

            this.shortnamefi = getLanguageSpecificValue(result.metadata, 'lyhytNimi', 'FI');
            this.shortnamesv = getLanguageSpecificValue(result.metadata, 'lyhytNimi', 'SV');
            this.shortnameen = getLanguageSpecificValue(result.metadata, 'lyhytNimi', 'EN');

            this.descriptionfi = getLanguageSpecificValue( this.codeElement.metadata , 'kuvaus', 'FI');
            this.descriptionsv = getLanguageSpecificValue( this.codeElement.metadata , 'kuvaus', 'SV');
            this.descriptionen = getLanguageSpecificValue( this.codeElement.metadata , 'kuvaus', 'EN');

            scope.instructionsfi = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'FI');
            scope.instructionssv = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'SV');
            scope.instructionsen = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'EN');

            scope.conceptfi = getLanguageSpecificValue(result.metadata, 'kasite', 'FI');
            scope.conceptsv = getLanguageSpecificValue(result.metadata, 'kasite', 'SV');
            scope.concepten = getLanguageSpecificValue(result.metadata, 'kasite', 'EN');

            scope.totakenoticeoffi = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodi', 'FI');
            scope.totakenoticeofsv = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodi', 'SV');
            scope.totakenoticeofen = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodi', 'EN');

            scope.containssignificancefi = getLanguageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'FI');
            scope.containssignificancesv = getLanguageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'SV');
            scope.containssignificanceen = getLanguageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'EN');

            scope.doesnotcontainsignificancefi = getLanguageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'FI');
            scope.doesnotcontainsignificancesv = getLanguageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'SV');
            scope.doesnotcontainsignificanceen = getLanguageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'EN');

            scope.containscodesfi = getLanguageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'FI');
            scope.containscodessv = getLanguageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'SV');
            scope.containscodesen = getLanguageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'EN');

            if (this.codeElement.tila === "PASSIIVINEN") {
                this.deleteState = "";
            }

            this.CodesByUri.get({
                codesUri : result.koodisto.koodistoUri
            }, (codes) => {
                var inLatestCodes = jQuery.inArray(codes.latestKoodistoVersio.versio, this.codeElement.koodisto.koodistoVersios) !== -1;
                this.editState = inLatestCodes ? "" : "disabled";
            });

            this.codeElement.withinCodeElements.forEach((codeElement) => {
                this.extractAndPushCodeElementInformation(codeElement, this.withinCodeElements);
            });
            this.codeElement.includesCodeElements.forEach((codeElement) => {
                this.extractAndPushCodeElementInformation(codeElement, this.includesCodeElements);
            });
            this.codeElement.levelsWithCodeElements.forEach((codeElement) => {
                this.extractAndPushCodeElementInformation(codeElement, this.levelsWithCodeElements);
            });
            scope.loadingReady = true;
        });
    }

    extractAndPushCodeElementInformation(codeElement, list) {
        var ce = {};
        ce.uri = codeElement.codeElementUri;
        ce.name = getLanguageSpecificValueOrValidValue(codeElement.relationMetadata, 'nimi', 'FI');
        ce.description = getLanguageSpecificValueOrValidValue(codeElement.relationMetadata, 'kuvaus', 'FI');
        ce.versio = codeElement.codeElementVersion;
        ce.codesname = getLanguageSpecificValueOrValidValue(codeElement.parentMetadata, 'nimi', 'FI');
        ce.active = !codeElement.passive;
        list.push(ce);
    }

    removeCodeElement() {
        this.deleteCodeElementModalInstance = this.$modal.open({
            templateUrl : 'confirmDeleteCodeElementModalContent.html',
            controller : 'viewCodeElementController',
            resolve : {}
        });
    }
}

export class ViewCodeElementController {
    constructor($scope, $location, $routeParams, viewCodeElementModel, DeleteCodeElement, RemoveRelationCodeElement) {
        "ngInject";
        $scope.model = viewCodeElementModel;
        $scope.codeElementUri = $routeParams.codeElementUri;
        $scope.codeElementVersion = $routeParams.codeElementVersion;
        $scope.model.forceRefresh = $routeParams.forceRefresh || $routeParams.edited;
        $scope.model.codeElementEdited = $routeParams.edited === true;
        viewCodeElementModel.init($scope, $scope.codeElementUri, $scope.codeElementVersion);

        $scope.closeAlert = function(index) {
            $scope.model.alerts.splice(index, 1);
        };

        $scope.cancel = function() {
            $location.path("/koodisto/" + $scope.model.codeElement.koodisto.koodistoUri + "/"
                + $scope.model.codeElement.koodisto.koodistoVersios[$scope.model.codeElement.koodisto.koodistoVersios.length - 1]).search({forceRefresh: $scope.model.codeElementEdited});
        };

        $scope.editCodeElement = function() {
            $location.path("/muokkaaKoodi/" + $scope.codeElementUri + "/" + $scope.codeElementVersion);
        };

        $scope.okconfirmdeletecodeelement = function() {
            DeleteCodeElement.put({
                codeElementUri : $scope.codeElementUri,
                codeElementVersion : $scope.codeElementVersion
            }, function(success) {
                $location.path("/koodisto/" + $scope.model.codeElement.koodisto.koodistoUri + "/"
                    + $scope.model.codeElement.koodisto.koodistoVersios[$scope.model.codeElement.koodisto.koodistoVersios.length - 1]).search({forceRefresh: true});
            }, function(error) {
                var alert = {
                    type : 'danger',
                    msg : 'Koodin poisto ep\u00E4onnistui.'
                };
                $scope.model.alerts.push(alert);
            });

            $scope.model.deleteCodeElementModalInstance.close();
        };

        $scope.cancelconfirmdeletecodeelement = function() {
            $scope.model.deleteCodeElementModalInstance.dismiss('cancel');
        };

        $scope.showRelation = function(codeElement) {
            return codeElement.active || $scope.showPassive
        }

    }
}
