
app.factory('ViewCodeElementModel', function($location, $modal, CodeElementByUriAndVersion,
                                             CodesByUri, LatestCodeElementVersionsByCodeElementUri) {
    var model;
    model = new function() {

        this.withinCodeElements = [];
        this.includesCodeElements = [];
        this.levelsWithCodeElements = [];
        this.deleteState = "disabled";
        this.alerts = [];

        this.init = function(codeElementUri, codeElementVersion) {

            this.withinCodeElements = [];
            this.includesCodeElements = [];
            this.levelsWithCodeElements = [];
            this.deleteState = "disabled";

            model.getCodeElement(codeElementUri, codeElementVersion);
        };
        this.getCodeElement = function(codeElementUri, codeElementVersion) {
            CodeElementByUriAndVersion.get({codeElementUri: codeElementUri, codeElementVersion: codeElementVersion}, function (result) {
                model.codeElement = result;

                if (model.codeElement.tila === "PASSIIVINEN")
                    model.deleteState = "";


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
                ce.name = model.languageSpecificValue(result.metadata, 'lyhytNimi', 'FI');
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
    ViewCodeElementModel.init($scope.codeElementUri, $scope.codeElementVersion);


    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/koodisto/"+$scope.model.codeElement.koodisto.koodistoUri+"/"+$scope.model.codeElement.koodisto.koodistoVersios[0]);
    };

    $scope.okconfirmdeletecodeelement = function() {
        DeleteCodeElement.put({codeElementUri: $scope.codeElementUri,
            codeElementVersion: $scope.codeElementVersion},function(success) {
            $location.path("/koodisto/"+$scope.model.codeElement.koodisto.koodistoUri+"/"+$scope.model.codeElement.koodisto.koodistoVersios[0]);
        }, function(error) {
            var alert = { type: 'danger', msg: 'Koodin poisto ep\u00E4onnistui. Koodin rinnastuu/sis\u00E4lt\u00E4\u00E4/sis\u00E4ltyy-suhteita ei ole poistettu.' }
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

            });
        } else if ($scope.model.includesRelationToRemove && $scope.model.includesRelationToRemove.uri !== "") {

            $scope.model.includesCodeElements.splice($scope.model.includesCodeElements.indexOf($scope.model.includesRelationToRemove.uri), 1);

            RemoveRelationCodeElement.put({codeElementUri: $scope.model.codeElement.koodiUri,
                codeElementUriToRemove: $scope.model.includesRelationToRemove.uri,relationType: "SISALTYY"},function(result) {

            });
        } else if ($scope.model.levelsRelationToRemove && $scope.model.levelsRelationToRemove.uri !== "") {
            $scope.model.levelsWithCodeElements.splice($scope.model.levelsWithCodeElements.indexOf($scope.model.levelsRelationToRemove.uri), 1);

            RemoveRelationCodeElement.put({codeElementUri: $scope.model.levelsRelationToRemove.uri,
                codeElementUriToRemove: $scope.model.codeElement.koodiUri,relationType: "RINNASTEINEN"},function(result) {
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