
app.factory('ViewCodesModel', function($location, $modal, CodesByUriAndVersion, CodeElementsByCodesUriAndVersion,
                                       CodeElementVersionsByCodeElementUri, OrganizationByOid) {
    var model;
    model = new function() {
        codeElements = [];
        this.init = function(codesUri, codesVersion) {
            model.codesUri = codesUri;
            model.codesVersion = codesVersion;
            model.showversion = null;

            model.format = "JHS_XML";
            model.encoding = "UTF8";

            model.getCodes(codesUri,codesVersion);
        };

        this.getCodes = function(codesUri, codesVersion) {
            CodesByUriAndVersion.get({codesUri: codesUri, codesVersion: codesVersion}, function (result) {
                model.codes = result;
                OrganizationByOid.get({oid: model.codes.organisaatioOid}, function (result) {
                    if (result.nimi['fi']) {
                        model.codes.organizationName = result.nimi['fi'];
                    } else {
                        model.codes.organizationName = result.nimi['sv'];
                    }
                });
                model.getCodeElements(codesUri,codesVersion);
            });
        };
        this.getCodeElements = function(codesUri,codesVersion) {
            CodeElementsByCodesUriAndVersion.get({codesUri: codesUri, codesVersion: codesVersion}, function (result) {
                model.codeElements = result;
                for(var i=0; i < model.codeElements.length; i++) {
                    model.codeElements[i].name = model.languageSpecificValue(model.codeElements[i].metadata, 'lyhytNimi', 'FI');
                }
            });

        };
        this.getCodeElementVersions = function() {
            if (!model.showversion) {
                var elements = model.codeElements;
                model.codeElements = [];
                if (elements) {
                    for(var i=0; i < elements.length; i++) {
                        model.getCodeElementVersionsByCodeElementUri(elements[i].koodiUri);
                    }
                }
            } else {
                model.getCodes(model.codesUri,model.codesVersion);
            }
        };
        this.getCodeElementVersionsByCodeElementUri = function(codeElementUri) {
            CodeElementVersionsByCodeElementUri.get({codeElementUri: codeElementUri}, function (result) {
                for(var i=0; i < result.length; i++) {
                    result[i].name = model.languageSpecificValue(result[i].metadata, 'lyhytNimi', 'FI');
                    model.codeElements.push(result[i]);
                }
            });
        };
        this.languageSpecificValue = function(fieldArray,fieldName,language) {
            return getLanguageSpecificValue(fieldArray,fieldName,language);
        };


        this.download = function() {
            model.downloadModalInstance = $modal.open({
                templateUrl: 'downloadModalContent.html',
                controller: ViewCodesController,
                resolve: {
                }
            });

        };

    };


    return model;
});

function ViewCodesController($scope, $location, $routeParams, ViewCodesModel, DownloadCodes, UploadCodes) {
    $scope.model = ViewCodesModel;
    $scope.codesUri = $routeParams.codesUri;
    $scope.codesVersion = $routeParams.codesVersion;
    $scope.identity = angular.identity;
    ViewCodesModel.init($scope.codesUri,$scope.codesVersion);

    $scope.cancel = function() {
        $location.path("/");
    };

    $scope.search = function (item){
        if (!$scope.query || item.name.toLowerCase().indexOf($scope.query.toLowerCase())!==-1 || item.koodiArvo.toLowerCase().indexOf($scope.query.toLowerCase())!==-1) {
            return true;
        }
        return false;
    };

    $scope.okdownload = function() {
        var fileFormat = {
            format: $scope.model.format,
            encoding: $scope.model.encoding
        }
        DownloadCodes.put({codesUri: $scope.codesUri}, fileFormat,function(result) {
        });
        $scope.model.downloadModalInstance.close();
    };

    $scope.canceldownload = function() {
        $scope.model.downloadModalInstance.dismiss('cancel');
    };
}