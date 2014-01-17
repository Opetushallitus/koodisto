
app.factory('ViewCodesModel', function($location, $modal, CodesByUriAndVersion, CodeElementsByCodesUriAndVersion,
                                       CodeElementVersionsByCodeElementUri, OrganizationByOid) {
    var model;
    model = new function() {
        codeElements = [];
        this.alerts = [];

        this.init = function(codesUri, codesVersion) {
            model.codesUri = codesUri;
            model.codesVersion = codesVersion;
            model.showversion = null;
            this.alerts = [];
            model.format = "JHS_XML";
            model.encoding = "UTF-8";

            model.getCodes(codesUri,codesVersion);
        };

        this.getCodes = function(codesUri, codesVersion) {
            CodesByUriAndVersion.get({codesUri: codesUri, codesVersion: codesVersion}, function (result) {
                model.codes = result;
                model.namefi = model.languageSpecificValue(result.metadata, 'nimi', 'FI');
                model.namesv = model.languageSpecificValue(result.metadata, 'nimi', 'SV');
                model.nameen = model.languageSpecificValue(result.metadata, 'nimi', 'EN');

                model.descriptionfi = model.languageSpecificValue(result.metadata, 'kuvaus', 'FI');
                model.descriptionsv = model.languageSpecificValue(result.metadata, 'kuvaus', 'SV');
                model.descriptionen = model.languageSpecificValue(result.metadata, 'kuvaus', 'EN');

                model.instructionsfi = model.languageSpecificValue(result.metadata, 'kayttoohje', 'FI');
                model.instructionssv = model.languageSpecificValue(result.metadata, 'kayttoohje', 'SV');
                model.instructionsen = model.languageSpecificValue(result.metadata, 'kayttoohje', 'EN');

                model.targetareafi = model.languageSpecificValue(result.metadata, 'kohdealue', 'FI');
                model.targetareasv = model.languageSpecificValue(result.metadata, 'kohdealue', 'SV');
                model.targetareaen = model.languageSpecificValue(result.metadata, 'kohdealue', 'EN');

                model.targetareapartfi = model.languageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'FI');
                model.targetareapartsv = model.languageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'SV');
                model.targetareaparten = model.languageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'EN');

                model.conceptfi = model.languageSpecificValue(result.metadata, 'kasite', 'FI');
                model.conceptsv = model.languageSpecificValue(result.metadata, 'kasite', 'SV');
                model.concepten = model.languageSpecificValue(result.metadata, 'kasite', 'EN');

                model.operationalenvironmentfi = model.languageSpecificValue(result.metadata, 'toimintaymparisto', 'FI');
                model.operationalenvironmentsv = model.languageSpecificValue(result.metadata, 'toimintaymparisto', 'SV');
                model.operationalenvironmenten = model.languageSpecificValue(result.metadata, 'toimintaymparisto', 'EN');

                model.codessourcefi = model.languageSpecificValue(result.metadata, 'koodistonLahde', 'FI');
                model.codessourcesv = model.languageSpecificValue(result.metadata, 'koodistonLahde', 'SV');
                model.codessourceen = model.languageSpecificValue(result.metadata, 'koodistonLahde', 'EN');

                model.specifiescodesfi = model.languageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'FI');
                model.specifiescodessv = model.languageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'SV');
                model.specifiescodesen = model.languageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'EN');

                model.totakenoticeoffi = model.languageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'FI');
                model.totakenoticeofsv = model.languageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'SV');
                model.totakenoticeofen = model.languageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'EN');

                model.validitylevelfi = model.languageSpecificValue(result.metadata, 'sitovuustaso', 'FI');
                model.validitylevelsv = model.languageSpecificValue(result.metadata, 'sitovuustaso', 'SV');
                model.validitylevelen = model.languageSpecificValue(result.metadata, 'sitovuustaso', 'EN');

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

        this.upload = function() {
            model.uploadModalInstance = $modal.open({
                templateUrl: 'uploadModalContent.html',
                controller: ViewCodesController,
                resolve: {
                }
            });
        };

    };


    return model;
});

function ViewCodesController($scope, $location, $routeParams, ViewCodesModel, DownloadCodes) {
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
        DownloadCodes.put({codesUri: $scope.codesUri,codesVersion: $scope.codesVersion}, fileFormat, function(result) {

            if (result.data) {
                var type = 'text/xml';

                if (fileFormat.format === "CSV") {
                    type = 'text/csv';
                }

                var blob = new Blob([ result.data ], { type : type });

                var url  = window.URL || window.webkitURL;
                var link = document.createElementNS("http://www.w3.org/1999/xhtml", "a");
                link.href = url.createObjectURL(blob);
                if (fileFormat.format === "CSV") {
                    link.download = $scope.codesUri+".csv";
                } else {
                    link.download = $scope.codesUri;
                }

                var event = document.createEvent("MouseEvents");
                event.initEvent("click", true, false);
                link.dispatchEvent(event);
            }
        }, function(error) {
            var alert = { type: 'danger', msg: 'Koodiston tuonti ep\u00E4onnistui.' }
            $scope.model.alerts.push(alert);
        });
        $scope.model.downloadModalInstance.close();
    };

    $scope.canceldownload = function() {
        $scope.model.downloadModalInstance.dismiss('cancel');
    };

    $scope.okupload = function() {
        var fileFormat = {
            format: $scope.model.format,
            encoding: $scope.model.encoding
        }
        var fd = new FormData()
        for (var i in $scope.files) {
            fd.append("uploadedFile", $scope.files[i])
        }
        fd.append("fileFormat", $scope.model.format)
        fd.append("fileEncoding", $scope.model.encoding)
        var xhr = new XMLHttpRequest()
        xhr.addEventListener("load", uploadComplete, false)
        xhr.addEventListener("error", uploadFailed, false)
        xhr.addEventListener("abort", uploadCanceled, false)
        xhr.open("POST", SERVICE_URL_BASE + "codes" + "/upload/"+$scope.codesUri)
        xhr.send(fd)

        $scope.model.uploadModalInstance.close();
    };

    $scope.cancelupload = function() {
        $scope.model.uploadModalInstance.dismiss('cancel');
    };

    $scope.setFiles = function(element) {
        $scope.$apply(function(scope) {
            console.log('files:', element.files);
            // Turn the FileList object into an Array
            $scope.files = []
            for (var i = 0; i < element.files.length; i++) {
                $scope.files.push(element.files[i])
            }
        });
    };

    function uploadComplete(evt) {
    }

    function uploadFailed(evt) {
        var alert = { type: 'danger', msg: 'Koodiston vienti ep\u00E4onnistui.' }
        $scope.model.alerts.push(alert);
    }

    function uploadCanceled(evt) {
        var alert = { type: 'danger', msg: 'Koodiston vienti ep\u00E4onnistui.' }
        $scope.model.alerts.push(alert);

    }
}