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
                scope.showPassive = false;
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

                model.namefi = getLanguageSpecificValue(result.metadata, 'nimi', 'FI');
                model.namesv = getLanguageSpecificValue(result.metadata, 'nimi', 'SV');
                model.nameen = getLanguageSpecificValue(result.metadata, 'nimi', 'EN');
                model.name = getLanguageSpecificValueOrValidValue(result.metadata, 'nimi', 'FI');
                
                model.shortnamefi = getLanguageSpecificValue(result.metadata, 'lyhytNimi', 'FI');
                model.shortnamesv = getLanguageSpecificValue(result.metadata, 'lyhytNimi', 'SV');
                model.shortnameen = getLanguageSpecificValue(result.metadata, 'lyhytNimi', 'EN');
                
                model.descriptionfi = getLanguageSpecificValue( model.codeElement.metadata , 'kuvaus', 'FI');
                model.descriptionsv = getLanguageSpecificValue( model.codeElement.metadata , 'kuvaus', 'SV');
                model.descriptionen = getLanguageSpecificValue( model.codeElement.metadata , 'kuvaus', 'EN');
                
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
                scope.loadingReady = true;
            });
        };

        this.extractAndPushCodeElementInformation = function(codeElement, list) {
            var ce = {};
            ce.uri = codeElement.codeElementUri;
            ce.name = getLanguageSpecificValueOrValidValue(codeElement.relationMetadata, 'nimi', 'FI');
            ce.description = getLanguageSpecificValueOrValidValue(codeElement.relationMetadata, 'kuvaus', 'FI');
            ce.versio = codeElement.codeElementVersion;
            ce.codesname = getLanguageSpecificValueOrValidValue(codeElement.parentMetadata, 'nimi', 'FI');
            ce.active = !codeElement.passive;
            list.push(ce);
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
    $scope.model.forceRefresh = $routeParams.forceRefresh || $routeParams.edited;
    $scope.model.codeElementEdited = $routeParams.edited == true;
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
    
    $scope.showRelation = function(codeElement) {
	return codeElement.active || $scope.showPassive
    }

}