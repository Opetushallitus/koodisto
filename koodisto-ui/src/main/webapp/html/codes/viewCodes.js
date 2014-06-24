app.factory('ViewCodesModel', function($location, $modal, CodesByUriAndVersion, CodeElementsByCodesUriAndVersion, CodeElementVersionsByCodeElementUri,
        OrganizationByOid, CodesByUri) {
    var model;
    model = new function() {
        this.codeElements = [];
        this.alerts = [];
        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.deleteState = "disabled";

        this.init = function(codesUri, codesVersion) {
            // Samaa koodistoa on turha päivittää
            if (model.forceRefresh || !(model.codes && model.codes.koodistoUri == codesUri && model.codes.versio == codesVersion)) {
                model.forceRefresh = false;
                model.codes = null;
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
                this.editState = "";
                this.codeElements = [];

                this.currentPage = 0;
                this.pageSize = 10;
                this.pageSizeOptions = [ 10, 50, 100, 200, 500 ];
                this.sortOrder = "koodiArvo";
                this.sortOrderSelection = 1;
                this.sortOrderReversed = false;

                model.getCodes(codesUri, codesVersion);
            }
        };

        this.getCodes = function(codesUri, codesVersion) {
            CodesByUriAndVersion.get({
                codesUri : codesUri,
                codesVersion : codesVersion
            }, function(result) {
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

                model.codes.withinCodes.forEach(function(codes) {
                    model.getLatestCodesVersionsByCodesUri(codes, model.withinCodes);
                });
                model.codes.includesCodes.forEach(function(codes) {
                    model.getLatestCodesVersionsByCodesUri(codes, model.includesCodes);
                });
                model.codes.levelsWithCodes.forEach(function(codes) {
                    model.getLatestCodesVersionsByCodesUri(codes, model.levelsWithCodes);
                });
                if (model.codes.tila === "PASSIIVINEN") {
                    model.deleteState = "";
                }

                model.codes.codesVersions.forEach(function(version) {
                    if (version > codesVersion)
                        model.editState = "disabled";
                });

                OrganizationByOid.get({
                    oid : model.codes.organisaatioOid
                }, function(result2) {
                    model.codes.organizationName = result2.nimi['fi'] || result2.nimi['sv'] || result2.nimi['en'];
                });
                model.getCodeElements(codesUri, codesVersion);
            });
        };

        this.getLatestCodesVersionsByCodesUri = function(codes, list) {
            CodesByUri.get({
                codesUri : codes.codesUri
            }, function(result) {
                var ce = {};
                ce.uri = codes.codesUri;
                ce.name = getLanguageSpecificValue(result.latestKoodistoVersio.metadata, 'nimi', 'FI');
                ce.versio = codes.codesVersion;
                list.push(ce);
            });
        };

        this.getCodeElements = function(codesUri, codesVersion) {
            CodeElementsByCodesUriAndVersion.get({
                codesUri : codesUri,
                codesVersion : codesVersion
            }, function(result) {
                model.codeElements = result;
                for (var i = 0; i < model.codeElements.length; i++) {
                    model.codeElements[i].name = getLanguageSpecificValue(model.codeElements[i].metadata, 'nimi', 'FI');
                    model.codeElements[i].namesv = getLanguageSpecificValue(model.codeElements[i].metadata, 'nimi', 'SV');
                    model.codeElements[i].nameen = getLanguageSpecificValue(model.codeElements[i].metadata, 'nimi', 'EN');
                }
            });

        };
        this.getCodeElementVersions = function() {
            if (!model.showversion) {
                var elements = model.codeElements;
                model.codeElements = [];
                if (elements) {
                    for (var i = 0; i < elements.length; i++) {
                        model.getCodeElementVersionsByCodeElementUri(elements[i].koodiUri);
                    }
                }
            } else {
                model.getCodes(model.codesUri, model.codesVersion);
            }
        };
        this.getCodeElementVersionsByCodeElementUri = function(codeElementUri) {
            CodeElementVersionsByCodeElementUri.get({
                codeElementUri : codeElementUri
            }, function(result) {
                for (var i = 0; i < result.length; i++) {
                    result[i].name = getLanguageSpecificValue(result[i].metadata, 'nimi', 'FI');
                    model.codeElements.push(result[i]);
                }
            });
        };

        this.download = function() {
            model.downloadModalInstance = $modal.open({
                templateUrl : 'downloadModalContent.html',
                controller : ViewCodesController,
                resolve : {}
            });

        };

        this.upload = function() {
            model.uploadModalInstance = $modal.open({
                templateUrl : 'uploadModalContent.html',
                controller : ViewCodesController,
                resolve : {}
            });
        };

        this.removeCodes = function() {
            model.deleteCodesModalInstance = $modal.open({
                templateUrl : 'confirmDeleteCodesModalContent.html',
                controller : ViewCodesController,
                resolve : {}
            });
        };
    };

    return model;
});

function ViewCodesController($scope, $location, $filter, $routeParams, $window, ViewCodesModel, DownloadCodes, RemoveRelationCodes, DeleteCodes) {
    $scope.model = ViewCodesModel;
    $scope.codesUri = $routeParams.codesUri;
    $scope.codesVersion = $routeParams.codesVersion;
    $scope.model.forceRefresh=$routeParams.forceRefresh;
    $scope.identity = angular.identity;
    ViewCodesModel.init($scope.codesUri, $scope.codesVersion);
    $scope.sortBy1 = 'name';
    $scope.sortBy2 = 'name';
    $scope.sortBy3 = 'name';


    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/");
    };

    $scope.addCodeElement = function() {
        $location.path("/lisaaKoodi/" + $scope.codesUri + "/" + $scope.codesVersion);
    };

    $scope.editCodes = function() {
        $location.path("/muokkaaKoodisto/" + $scope.codesUri + "/" + $scope.codesVersion);
    };

    $scope.okconfirmdeletecodes = function() {
        DeleteCodes.put({
            codesUri : $scope.codesUri,
            codesVersion : $scope.codesVersion
        }, function(success) {
            $location.path("/etusivu").search({forceRefresh: true});
        }, function(error) {
            var alert = {
                type : 'danger',
                msg : 'Koodiston poisto ep\u00E4onnistui.'
            };
            $scope.model.alerts.push(alert);
        });

        $scope.model.deleteCodesModalInstance.close();
    };

    $scope.cancelconfirmdeletecodes = function() {
        $scope.model.deleteCodesModalInstance.dismiss('cancel');
    };

    $scope.search = function(item) {
        function matchesName(name) {
            return name && name.toLowerCase().indexOf($scope.query.toLowerCase()) > -1;
        }
        if (!$scope.query || matchesName(item.name) || matchesName(item.namesv) || matchesName(item.nameen)
                || item.koodiArvo.toLowerCase().indexOf($scope.query.toLowerCase()) !== -1) {
            return true;
        }
        return false;
    };

    $scope.okdownload = function() {
        var url = DownloadCodes($scope.codesUri, $scope.codesVersion, $scope.model.format, $scope.model.encoding);
        $window.open(url);
        if ($scope.model.downloadModalInstance) {
            $scope.model.downloadModalInstance.close();
        }
    };

    $scope.downloadBlank = function() {
        $scope.model.format = "XLS";
        $scope.codesUri = "blankKoodistoDocument";
        $scope.codesVersion = "-1";

        $scope.okdownload();
    };

    $scope.formatEquals = function(s) {
        return ($scope.model.format === s);
    };

    $scope.canceldownload = function() {
        $scope.model.downloadModalInstance.dismiss('cancel');
    };

    $scope.okupload = function() {
        var fd = new FormData();
        for ( var i in $scope.files) {
            fd.append("uploadedFile", $scope.files[i]);
        }
        fd.append("fileFormat", $scope.model.format);
        fd.append("fileEncoding", $scope.model.encoding);
        var xhr = new XMLHttpRequest();
        xhr.addEventListener("load", uploadComplete, false);
        xhr.addEventListener("error", uploadFailed, false);
        xhr.addEventListener("abort", uploadCanceled, false);
        xhr.open("POST", SERVICE_URL_BASE + "codes" + "/upload/" + $scope.codesUri);
        xhr.send(fd);

        $scope.model.forceRefresh = true;
        
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
        ViewCodesModel.init($scope.codesUri, $scope.codesVersion);
        var alert;
        if (evt.originalTarget.status == "202") {
            alert = {
                type : 'success',
                msg : 'Koodisto ' + $scope.codesUri + ' on tuotu koodistoryhm\u00E4\u00E4n ' + $scope.model.codes.codesGroupUri
            };
        } else {
            alert = {
                type : 'danger',
                msg : 'Koodiston ' + $scope.codesUri + ' vienti ep\u00E4onnistui: ' + evt.originalTarget.status + ': ' + evt.originalTarget.statusText
            };
        }
        $scope.model.alerts.push(alert);
    }

    function uploadFailed(evt) {
        var alert = {
            type : 'danger',
            msg : 'Koodiston vienti ep\u00E4onnistui.'
        };
        $scope.model.alerts.push(alert);
    }

    function uploadCanceled(evt) {
        var alert = {
            type : 'danger',
            msg : 'Koodiston vienti ep\u00E4onnistui.'
        };
        $scope.model.alerts.push(alert);

    }

    $scope.getLanguageSpecificValue = function(fieldArray, fieldName, language) {
        return getLanguageSpecificValue(fieldArray, fieldName, language);
    };

    // Pagination

    // Get the filtered page count
    var cachedPageCount = 0;
    $scope.getNumberOfPages = function() {
        if (cachedPageCount == 0) {
            $scope.refreshNumberOfPages();
        }
        return cachedPageCount;
    };

    // Refresh the page count when the model changes
    var cachedElementCount = 0;
    $scope.$watch('model.codeElements', function() {
        if ($scope.model.codeElements.length != cachedElementCount) {
            $scope.refreshNumberOfPages();
            cachedElementCount = $scope.model.codeElements.length;
        }
    });

    // Refresh the page count (less redundant filtering)
    $scope.refreshNumberOfPages = function() {
        cachedPageCount = Math.ceil(($filter("filter")($scope.model.codeElements, $scope.search)).length / $scope.model.pageSize);
        return cachedPageCount;
    };

    // Change the currentPage when the pageSize is changed.
    var oldValueForPageSize = 10;
    $scope.pageSizeChanged = function() {
        var topmostCodeElement = $scope.model.currentPage * oldValueForPageSize;
        $scope.model.currentPage = Math.floor(topmostCodeElement / $scope.model.pageSize);
        oldValueForPageSize = $scope.model.pageSize;
        $scope.refreshNumberOfPages();
    };

    $scope.sortOrderChanged = function(value) {
        if(value){
            $scope.model.sortOrderSelection = value;
        }
        var selection = parseInt($scope.model.sortOrderSelection);
        switch (selection) {
        case 1:
            $scope.model.sortOrderReversed = false;
            $scope.model.sortOrder = "koodiArvo";
            break;
        case 2:
            $scope.model.sortOrderReversed = true;
            $scope.model.sortOrder = "koodiArvo";
            break;
        case 3:
            $scope.model.sortOrderReversed = false;
            $scope.model.sortOrder = "name";
            break;
        case 4:
            $scope.model.sortOrderReversed = true;
            $scope.model.sortOrder = "name";
            break;
        case 5:
            $scope.model.sortOrderReversed = false;
            $scope.model.sortOrder = "versio";
            break;
        case 6:
            $scope.model.sortOrderReversed = true;
            $scope.model.sortOrder = "versio";
            break;

        default:
            break;
        }
    };

    // When user changes the search string the page count changes and the current page must be adjusted
    $scope.filterChangedPageCount = function() {
        currentNumberOfPages = $scope.refreshNumberOfPages();
        if ($scope.model.currentPage >= currentNumberOfPages) {
            $scope.model.currentPage = currentNumberOfPages - 1;
        }
        if (currentNumberOfPages != 0 && $scope.model.currentPage < 0) {
            $scope.model.currentPage = 0;
        }
    };

    $scope.changePage = function(i) {
        $scope.model.currentPage = i;
    };

    $scope.incrementPage = function(i) {
        var newPageNumber = $scope.model.currentPage + i;
        if (newPageNumber > -1 && newPageNumber < $scope.getNumberOfPages()) {
            $scope.model.currentPage = newPageNumber;
        }
    };

    // Pagination ends
}