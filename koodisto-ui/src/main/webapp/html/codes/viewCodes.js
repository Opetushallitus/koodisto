
app.factory('ViewCodesModel', function($location, $modal, CodesByUriAndVersion, CodeElementsByCodesUriAndVersion,
                                       CodeElementVersionsByCodeElementUri, OrganizationByOid, CodesByUri) {
    var model;
    model = new function() {
        this.codeElements = [];
        this.alerts = [];
        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.deleteState = "disabled";

        this.init = function(codesUri, codesVersion) {
            this.withinCodes = [];
            this.includesCodes = [];
            this.levelsWithCodes = [];
            model.codesUri = codesUri;
            model.codesVersion = codesVersion;
            model.showversion = null;
            this.alerts = [];
            model.format = "JHS_XML";
            model.encoding = "UTF-8";
            this.deleteState = "disabled";

            model.getCodes(codesUri,codesVersion);
        };

        this.getCodes = function(codesUri, codesVersion) {
            CodesByUriAndVersion.get({codesUri: codesUri, codesVersion: codesVersion}, function (result) {
                model.codes = result;
                model.namefi = getLanguageSpecificValue(result.metadata, 'nimi', 'FI');
                model.namesv = getLanguageSpecificValue(result.metadata, 'nimi', 'SV');
                model.nameen = getLanguageSpecificValue(result.metadata, 'nimi', 'EN');

                model.descriptionfi = getLanguageSpecificValue(result.metadata, 'kuvaus', 'FI');
                model.descriptionsv = getLanguageSpecificValue(result.metadata, 'kuvaus', 'SV');
                model.descriptionen = getLanguageSpecificValue(result.metadata, 'kuvaus', 'EN');

                model.instructionsfi = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'FI');
                model.instructionssv = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'SV');
                model.instructionsen = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'EN');

                model.targetareafi = getLanguageSpecificValue(result.metadata, 'kohdealue', 'FI');
                model.targetareasv = getLanguageSpecificValue(result.metadata, 'kohdealue', 'SV');
                model.targetareaen = getLanguageSpecificValue(result.metadata, 'kohdealue', 'EN');

                model.targetareapartfi = getLanguageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'FI');
                model.targetareapartsv = getLanguageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'SV');
                model.targetareaparten = getLanguageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'EN');

                model.conceptfi = getLanguageSpecificValue(result.metadata, 'kasite', 'FI');
                model.conceptsv = getLanguageSpecificValue(result.metadata, 'kasite', 'SV');
                model.concepten = getLanguageSpecificValue(result.metadata, 'kasite', 'EN');

                model.operationalenvironmentfi = getLanguageSpecificValue(result.metadata, 'toimintaymparisto', 'FI');
                model.operationalenvironmentsv = getLanguageSpecificValue(result.metadata, 'toimintaymparisto', 'SV');
                model.operationalenvironmenten = getLanguageSpecificValue(result.metadata, 'toimintaymparisto', 'EN');

                model.codessourcefi = getLanguageSpecificValue(result.metadata, 'koodistonLahde', 'FI');
                model.codessourcesv = getLanguageSpecificValue(result.metadata, 'koodistonLahde', 'SV');
                model.codessourceen = getLanguageSpecificValue(result.metadata, 'koodistonLahde', 'EN');

                model.specifiescodesfi = getLanguageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'FI');
                model.specifiescodessv = getLanguageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'SV');
                model.specifiescodesen = getLanguageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'EN');

                model.totakenoticeoffi = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'FI');
                model.totakenoticeofsv = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'SV');
                model.totakenoticeofen = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'EN');

                model.validitylevelfi = getLanguageSpecificValue(result.metadata, 'sitovuustaso', 'FI');
                model.validitylevelsv = getLanguageSpecificValue(result.metadata, 'sitovuustaso', 'SV');
                model.validitylevelen = getLanguageSpecificValue(result.metadata, 'sitovuustaso', 'EN');

                model.codes.withinCodes.forEach(function(codes){
                    model.getLatestCodesVersionsByCodesUri(codes,model.withinCodes);
                });
                model.codes.includesCodes.forEach(function(codes){
                    model.getLatestCodesVersionsByCodesUri(codes,model.includesCodes);
                });
                model.codes.levelsWithCodes.forEach(function(codes){
                    model.getLatestCodesVersionsByCodesUri(codes,model.levelsWithCodes);
                });
                if (model.codes.tila === "PASSIIVINEN") {
                    model.deleteState = "";
                }
                OrganizationByOid.get({oid: model.codes.organisaatioOid}, function (result) {
                    model.codes.organizationName = result.nimi['fi'] || result.nimi['sv'] || result.nimi['en'];
                });
                model.getCodeElements(codesUri,codesVersion);
            });
        };

        this.getLatestCodesVersionsByCodesUri = function(codesUri, list) {
            CodesByUri.get({codesUri: codesUri}, function (result) {
                var ce = {};
                ce.uri = codesUri;
                ce.name = getLanguageSpecificValue(result.latestKoodistoVersio.metadata, 'nimi', 'FI');
                list.push(ce);
            });
        };

        this.getCodeElements = function(codesUri,codesVersion) {
            CodeElementsByCodesUriAndVersion.get({codesUri: codesUri, codesVersion: codesVersion}, function (result) {
                model.codeElements = result;
                for(var i=0; i < model.codeElements.length; i++) {
                    model.codeElements[i].name = getLanguageSpecificValue(model.codeElements[i].metadata, 'nimi', 'FI');
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
                    result[i].name = getLanguageSpecificValue(result[i].metadata, 'lyhytNimi', 'FI');
                    model.codeElements.push(result[i]);
                }
            });
        };

        this.download = function() {
            model.downloadModalInstance = $modal.open({
                templateUrl: 'downloadModalContent.html',
                controller: ViewCodesController,
                resolve: {
                }
            });

        };

        this.upload = function() {
            model.uploadModalInstance = $modal.open({
                templateUrl: 'uploadModalContent.html',
                controller: ViewCodesController,
                resolve: {
                }
            });
        };

        this.removeFromWithinCodes = function(codes) {
            model.withinRelationToRemove = codes;

            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: ViewCodesController,
                resolve: {
                }
            });

        };

        this.removeFromIncludesCodes = function(codes) {
            model.includesRelationToRemove = codes;
            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: ViewCodesController,
                resolve: {
                }
            });
        };

        this.removeFromLevelsWithCodes = function(codes) {
            model.levelsRelationToRemove = codes;
            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: ViewCodesController,
                resolve: {
                }
            });
        };

        this.removeCodes = function() {
            model.deleteCodesModalInstance = $modal.open({
                templateUrl: 'confirmDeleteCodesModalContent.html',
                controller: ViewCodesController,
                resolve: {
                }
            });
        };
    };


    return model;
});

function ViewCodesController($scope, $location, $routeParams, ViewCodesModel, DownloadCodes, RemoveRelationCodes,
                             DeleteCodes) {
    $scope.model = ViewCodesModel;
    $scope.codesUri = $routeParams.codesUri;
    $scope.codesVersion = $routeParams.codesVersion;
    $scope.identity = angular.identity;
    ViewCodesModel.init($scope.codesUri,$scope.codesVersion);

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/");
    };

    $scope.okconfirmdeletecodes = function() {
        DeleteCodes.put({codesUri: $scope.codesUri,
            codesVersion: $scope.codesVersion},function(success) {
            $location.path("/");
        }, function(error) {
            var alert = { type: 'danger', msg: 'Koodiston poisto ep\u00E4onnistui.' };
            $scope.model.alerts.push(alert);
        });

        $scope.model.deleteCodesModalInstance.close();
    };

    $scope.cancelconfirmdeletecodes = function() {
        $scope.model.deleteCodesModalInstance.dismiss('cancel');
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
        };
        DownloadCodes.put({codesUri: $scope.codesUri,codesVersion: $scope.codesVersion}, fileFormat, function(result) {

            if (result.data) {
                var type = '';
                var data = '';

                if(fileFormat.format === "JHS_XML"){
                    type='text/xml';
                    data = result.data;
                } else if (fileFormat.format === "CSV") {
                    type = 'text/csv';
                    data = result.data;
                } else if (fileFormat.format === "XLS") {
                    type = 'application/vnd.ms-excel';

                    // Decode base64 binary data
                    raw = atob( result.data );
                    data = new Uint8Array(new ArrayBuffer(raw.length));
                    for (var i = 0; i < raw.length; i++) {
                        data[i] = raw.charCodeAt(i);
                    }
                }

                var blob = new Blob([ data ], { type : type });

                var url  = window.URL || window.webkitURL;
                var link = document.createElementNS("http://www.w3.org/1999/xhtml", "a");
                link.href = url.createObjectURL(blob);
                if (fileFormat.format === "CSV") {
                    link.download = $scope.codesUri+".csv";
                } else if (fileFormat.format === "XLS") {
                        link.download = $scope.codesUri+".xls";
                } else {
                    link.download = $scope.codesUri;
                }

                var event = document.createEvent("MouseEvents");
                event.initEvent("click", true, false);
                link.dispatchEvent(event);
            }
            var alert = { type: 'success', msg: 'Koodiston vienti onnistui.' };
            $scope.model.alerts.push(alert);
        }, function(error) {
            var alert = { type: 'danger', msg: 'Koodiston vienti ep\u00E4onnistui.' };
            $scope.model.alerts.push(alert);
        });
        $scope.model.downloadModalInstance.close();
    };

    $scope.formatEquals = function(s){
        return ($scope.model.format === s);
    };
    
    $scope.canceldownload = function() {
        $scope.model.downloadModalInstance.dismiss('cancel');
    };

    $scope.okupload = function() {
        var fileFormat = {
            format: $scope.model.format,
            encoding: $scope.model.encoding
        };
        var fd = new FormData();
        for (var i in $scope.files) {
            fd.append("uploadedFile", $scope.files[i]);
        }
        fd.append("fileFormat", $scope.model.format);
        fd.append("fileEncoding", $scope.model.encoding);
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", uploadComplete, false);
        xhr.addEventListener("error", uploadFailed, false);
        xhr.addEventListener("abort", uploadCanceled, false);
        xhr.open("POST", SERVICE_URL_BASE + "codes" + "/upload/"+$scope.codesUri);
        xhr.send(fd);

        $scope.model.uploadModalInstance.close();
    };

    $scope.cancelupload = function() {
        $scope.model.uploadModalInstance.dismiss('cancel');
    };

    $scope.setFiles = function(element) {
        $scope.$apply(function(scope) {
            // Turn the FileList object into an Array
            $scope.files = [];
            for (var i = 0; i < element.files.length; i++) {
                $scope.files.push(element.files[i]);
            }
        });
    };

    function uploadComplete(evt) {
        ViewCodesModel.init($scope.codesUri,$scope.codesVersion);
        var alert = { type: 'success', msg: 'Koodisto ' + $scope.codesUri + ' on tuotu koodistoryhm\u00E4\u00E4n ' + $scope.model.codes.codesGroupUri };
        $scope.model.alerts.push(alert);
    }

    function uploadFailed(evt) {
        var alert = { type: 'danger', msg: 'Koodiston vienti ep\u00E4onnistui.' };
        $scope.model.alerts.push(alert);
    }

    function uploadCanceled(evt) {
        var alert = { type: 'danger', msg: 'Koodiston vienti ep\u00E4onnistui.' };
        $scope.model.alerts.push(alert);

    }

    $scope.okconfirm = function() {
        if ($scope.model.withinRelationToRemove && $scope.model.withinRelationToRemove.uri !== "") {
            $scope.model.withinCodes.splice($scope.model.withinCodes.indexOf($scope.model.withinRelationToRemove.uri), 1);

            RemoveRelationCodes.put({codesUri: $scope.model.withinRelationToRemove.uri,
                codesUriToRemove: $scope.model.codes.koodistoUri,relationType: "SISALTYY"},function(result) {

            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' };
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.includesRelationToRemove && $scope.model.includesRelationToRemove.uri !== "") {

            $scope.model.includesCodes.splice($scope.model.includesCodes.indexOf($scope.model.includesRelationToRemove.uri), 1);

            RemoveRelationCodes.put({codesUri: $scope.model.codes.koodistoUri,
                codesUriToRemove: $scope.model.includesRelationToRemove.uri,relationType: "SISALTYY"},function(result) {

            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' };
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.levelsRelationToRemove && $scope.model.levelsRelationToRemove.uri !== "") {
            $scope.model.levelsWithCodes.splice($scope.model.levelsWithCodes.indexOf($scope.model.levelsRelationToRemove.uri), 1);

            RemoveRelationCodes.put({codesUri: $scope.model.levelsRelationToRemove.uri,
                codesUriToRemove: $scope.model.codes.koodistoUri,relationType: "RINNASTEINEN"},function(result) {
            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' };
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


    $scope.getLanguageSpecificValue = function(fieldArray,fieldName,language) {
        return getLanguageSpecificValue(fieldArray,fieldName,language);
    };

}