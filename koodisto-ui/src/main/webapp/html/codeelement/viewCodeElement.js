
app.factory('ViewCodeElementModel', function($location, $modal, CodeElementByUriAndVersion,
                                             CodesByUri, LatestCodeElementVersionsByCodeElementUri) {
    var model;
    model = new function() {

        this.withinCodeElements = [];
        this.includesCodeElements = [];
        this.levelsWithCodeElements = [];
        this.deleteState = "disabled";
        this.alerts = [];

        this.init = function(scope, codeElementUri, codeElementVersion) {

            this.withinCodeElements = [];
            this.includesCodeElements = [];
            this.levelsWithCodeElements = [];
            this.deleteState = "disabled";
            this.editState = "";
            this.alerts = [];

            model.getCodeElement(scope, codeElementUri, codeElementVersion);
        };

        this.getCodeElement = function(scope, codeElementUri, codeElementVersion) {
            CodeElementByUriAndVersion.get({codeElementUri: codeElementUri, codeElementVersion: codeElementVersion}, function (result) {
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
                
                LatestCodeElementVersionsByCodeElementUri.get({codeElementUri: codeElementUri}, function (result) {
                   model.editState = result.versio > codeElementVersion ? "disabled" : ""; 
                });

                model.codeElement.withinCodeElements.forEach(function(codelement){
                    model.getLatestCodeElementVersionsByCodeElementUri(codelement,model.withinCodeElements);
                });
                model.codeElement.includesCodeElements.forEach(function(codelement){
                    model.getLatestCodeElementVersionsByCodeElementUri(codelement,model.includesCodeElements);
                });
                model.codeElement.levelsWithCodeElements.forEach(function(codelement){
                    model.getLatestCodeElementVersionsByCodeElementUri(codelement,model.levelsWithCodeElements);
                });
            });
        };
        this.getLatestCodeElementVersionsByCodeElementUri = function(codeElementUri, list) {
            LatestCodeElementVersionsByCodeElementUri.get({codeElementUri: codeElementUri}, function (result) {
                var ce = {};
                ce.uri = codeElementUri;
                ce.name = model.languageSpecificValue(result.metadata, 'nimi', 'FI');
                ce.description = model.languageSpecificValue(result.metadata, 'kuvaus', 'FI');
                CodesByUri.get({codesUri: result.koodisto.koodistoUri}, function (result) {
                    ce.codesname = model.languageSpecificValue(result.latestKoodistoVersio.metadata,'nimi','FI');
                });
                list.push(ce);
            });
        };

        this.languageSpecificValue = function(fieldArray,fieldName,language) {
            return getLanguageSpecificValue(fieldArray,fieldName,language);
        };

        this.removeCodeElement = function() {
            model.deleteCodeElementModalInstance = $modal.open({
                templateUrl: 'confirmDeleteCodeElementModalContent.html',
                controller: ViewCodeElementController,
                resolve: {
                }
            });
        };
        this.removeFromWithinCodeElements = function(codeelement) {
            model.withinRelationToRemove = codeelement;

            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: ViewCodeElementController,
                resolve: {
                }
            });

        };

        this.removeFromIncludesCodeElements = function(codeelement) {
            model.includesRelationToRemove = codeelement;
            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: ViewCodeElementController,
                resolve: {
                }
            });
        };

        this.removeFromLevelsWithCodeElements = function(codeelement) {
            model.levelsRelationToRemove = codeelement;
            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: ViewCodeElementController,
                resolve: {
                }
            });
        };
    };

    return model;
});

function ViewCodeElementController($scope, $location, $routeParams, ViewCodeElementModel, DeleteCodeElement, RemoveRelationCodeElement) {
    $scope.model = ViewCodeElementModel;
    $scope.codeElementUri = $routeParams.codeElementUri;
    $scope.codeElementVersion = $routeParams.codeElementVersion;
    ViewCodeElementModel.init($scope, $scope.codeElementUri, $scope.codeElementVersion);
    $scope.sortBy = 'name';

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/koodisto/"+$scope.model.codeElement.koodisto.koodistoUri+"/"+$scope.model.codeElement.koodisto.koodistoVersios[$scope.model.codeElement.koodisto.koodistoVersios.length-1]);
    };

    $scope.okconfirmdeletecodeelement = function() {
        DeleteCodeElement.put({codeElementUri: $scope.codeElementUri,
            codeElementVersion: $scope.codeElementVersion},function(success) {
            $location.path("/koodisto/"+$scope.model.codeElement.koodisto.koodistoUri+"/"+$scope.model.codeElement.koodisto.koodistoVersios[$scope.model.codeElement.koodisto.koodistoVersios.length-1]);
        }, function(error) {
            var alert = { type: 'danger', msg: 'Koodin poisto ep\u00E4onnistui.' }
            $scope.model.alerts.push(alert);
        });

        $scope.model.deleteCodeElementModalInstance.close();
    };

    $scope.cancelconfirmdeletecodeelement = function() {
        $scope.model.deleteCodeElementModalInstance.dismiss('cancel');
    };

    $scope.okconfirm = function() {
        if ($scope.model.withinRelationToRemove && $scope.model.withinRelationToRemove.uri !== "") {
            $scope.model.withinCodeElements.splice($scope.model.withinCodeElements.indexOf($scope.model.withinRelationToRemove.uri), 1);

            RemoveRelationCodeElement.put({codeElementUri: $scope.model.withinRelationToRemove.uri,
                codeElementUriToRemove: $scope.model.codeElement.koodiUri,relationType: "SISALTYY"},function(result) {
            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodien v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.includesRelationToRemove && $scope.model.includesRelationToRemove.uri !== "") {

            $scope.model.includesCodeElements.splice($scope.model.includesCodeElements.indexOf($scope.model.includesRelationToRemove.uri), 1);

            RemoveRelationCodeElement.put({codeElementUri: $scope.model.codeElement.koodiUri,
                codeElementUriToRemove: $scope.model.includesRelationToRemove.uri,relationType: "SISALTYY"},function(result) {

            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodien v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.levelsRelationToRemove && $scope.model.levelsRelationToRemove.uri !== "") {
            $scope.model.levelsWithCodeElements.splice($scope.model.levelsWithCodeElements.indexOf($scope.model.levelsRelationToRemove.uri), 1);

            RemoveRelationCodeElement.put({codeElementUri: $scope.model.levelsRelationToRemove.uri,
                codeElementUriToRemove: $scope.model.codeElement.koodiUri,relationType: "RINNASTEINEN"},function(result) {
            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodien v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
                $scope.model.alerts.push(alert);
            });
        }
        $scope.model.levelsRelationToRemove = null;
        $scope.model.includesRelationToRemove = null;
        $scope.model.withinRelationToRemove = null;
        $scope.model.modalInstance.close();
    };

    $scope.cancelconfirm = function() {
        $scope.model.levelsRelationToRemove = null;
        $scope.model.includesRelationToRemove = null;
        $scope.model.withinRelationToRemove = null;
        $scope.model.modalInstance.dismiss('cancel');
    };
}