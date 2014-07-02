app.factory('ViewCodeElementModel', function($location, $modal, CodeElementByUriAndVersion, CodesByUri, LatestCodeElementVersionsByCodeElementUri) {
    var model;
    model = new function() {

        this.withinCodeElements = [];
        this.includesCodeElements = [];
        this.levelsWithCodeElements = [];
        this.deleteState = "disabled";
        this.alerts = [];

        this.init = function(scope, codeElementUri, codeElementVersion) {
            if (model.forceRefresh || !(model.codeElement && model.codeElement.koodiUri == codeElementUri && model.codeElement.versio == codeElementVersion)) {
                model.codeElement = null;
                this.withinCodeElements = [];
                this.includesCodeElements = [];
                this.levelsWithCodeElements = [];
                this.deleteState = "disabled";
                this.editState = "";
                this.alerts = [];

                model.getCodeElement(scope, codeElementUri, codeElementVersion);
            }
        };

        this.getCodeElement = function(scope, codeElementUri, codeElementVersion) {
            CodeElementByUriAndVersion.get({
                codeElementUri : codeElementUri,
                codeElementVersion : codeElementVersion
            }, function(result) {
                model.codeElement = result;

                scope.instructionsfi = model.languageSpecificValue(result.metadata, 'kayttoohje', 'FI');
                scope.instructionssv = model.languageSpecificValue(result.metadata, 'kayttoohje', 'SV');
                scope.instructionsen = model.languageSpecificValue(result.metadata, 'kayttoohje', 'EN');

                scope.conceptfi = model.languageSpecificValue(result.metadata, 'kasite', 'FI');
                scope.conceptsv = model.languageSpecificValue(result.metadata, 'kasite', 'SV');
                scope.concepten = model.languageSpecificValue(result.metadata, 'kasite', 'EN');

                scope.totakenoticeoffi = model.languageSpecificValue(result.metadata, 'huomioitavaKoodi', 'FI');
                scope.totakenoticeofsv = model.languageSpecificValue(result.metadata, 'huomioitavaKoodi', 'SV');
                scope.totakenoticeofen = model.languageSpecificValue(result.metadata, 'huomioitavaKoodi', 'EN');

                scope.containssignificancefi = model.languageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'FI');
                scope.containssignificancesv = model.languageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'SV');
                scope.containssignificanceen = model.languageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'EN');

                scope.doesnotcontainsignificancefi = model.languageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'FI');
                scope.doesnotcontainsignificancesv = model.languageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'SV');
                scope.doesnotcontainsignificanceen = model.languageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'EN');

                scope.containscodesfi = model.languageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'FI');
                scope.containscodessv = model.languageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'SV');
                scope.containscodesen = model.languageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'EN');

                if (model.codeElement.tila === "PASSIIVINEN") {
                    model.deleteState = "";
                }

                CodesByUri.get({
                    codesUri : result.koodisto.koodistoUri
                }, function(codes) {
                    var inLatestCodes = jQuery.inArray(codes.latestKoodistoVersio.versio, model.codeElement.koodisto.koodistoVersios) != -1;
                    model.editState = inLatestCodes ? "" : "disabled";
                });

                model.codeElement.withinCodeElements.forEach(function(codeElement) {
                    model.extractAndPushCodeElementInformation(codeElement, model.withinCodeElements);
                });
                model.codeElement.includesCodeElements.forEach(function(codeElement) {
                    model.extractAndPushCodeElementInformation(codeElement, model.includesCodeElements);
                });
                model.codeElement.levelsWithCodeElements.forEach(function(codeElement) {
                    model.extractAndPushCodeElementInformation(codeElement, model.levelsWithCodeElements);
                });
            });
        };

        this.extractAndPushCodeElementInformation = function(codeElement, list) {
            var ce = {};
            ce.uri = codeElement.codeElementUri;
            ce.name = model.languageSpecificValue(codeElement.relationMetadata, 'nimi', 'FI');
            ce.description = model.languageSpecificValue(codeElement.relationMetadata, 'kuvaus', 'FI');
            ce.versio = codeElement.codeElementVersion;
            ce.codesname = model.languageSpecificValue(codeElement.parentMetadata, 'nimi', 'FI');
            list.push(ce);
        };

        this.languageSpecificValue = function(fieldArray, fieldName, language) {
            return getLanguageSpecificValue(fieldArray, fieldName, language);
        };

        this.removeCodeElement = function() {
            model.deleteCodeElementModalInstance = $modal.open({
                templateUrl : 'confirmDeleteCodeElementModalContent.html',
                controller : ViewCodeElementController,
                resolve : {}
            });
        };

    };

    return model;
});

function ViewCodeElementController($scope, $location, $routeParams, ViewCodeElementModel, DeleteCodeElement, RemoveRelationCodeElement) {
    $scope.model = ViewCodeElementModel;
    $scope.codeElementUri = $routeParams.codeElementUri;
    $scope.codeElementVersion = $routeParams.codeElementVersion;
    $scope.model.forceRefresh = $routeParams.forceRefresh;
    $scope.model.codeElementEdited = $routeParams.forceRefresh;
    ViewCodeElementModel.init($scope, $scope.codeElementUri, $scope.codeElementVersion);

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

}