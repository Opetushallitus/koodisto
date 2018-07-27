import angular from 'angular';
import {getLanguageSpecificValue, getLanguageSpecificValueOrValidValue, SERVICE_URL_BASE} from "../app";

export class ViewCodesModel {
    constructor($location, $modal, CodesByUriAndVersion, CodeElementsByCodesUriAndVersion, CodeElementVersionsByCodeElementUri,
                OrganizationByOid, CodesByUri) {
        "ngInject";
        this.$location = $location;
        this.$modal = $modal;
        this.CodesByUriAndVersion = CodesByUriAndVersion;
        this.CodeElementsByCodesUriAndVersion = CodeElementsByCodesUriAndVersion;
        this.CodeElementVersionsByCodeElementUri = CodeElementVersionsByCodeElementUri;
        this.OrganizationByOid = OrganizationByOid;
        this.CodesByUri = CodesByUri;

        this.codeElements = [];
        this.alerts = [];
        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.deleteState = "disabled";
    }

    init(scope, codesUri, codesVersion) {
        if (this.forceRefresh) {
            this.forceRefreshCodeElements = "?forceRefresh";
        } else {
            this.forceRefreshCodeElements = "";
        }
        // Samaa koodistoa on turha ladata uudelleen modelliin
        if (this.forceRefresh || !(this.codes && this.codes.koodistoUri === codesUri && this.codes.versio === codesVersion)) {
            scope.showPassive = false;
            this.forceRefresh = false;
            this.codes = null;
            this.withinCodes = [];
            this.includesCodes = [];
            this.levelsWithCodes = [];
            this.codesUri = codesUri;
            this.codesVersion = codesVersion;
            this.showversion = null;
            this.alerts = [];
            this.format = "JHS_XML";
            this.encoding = "UTF-8";
            this.deleteState = "disabled";
            this.editState = "";
            this.codeElements = [];

            this.currentPage = 0;
            this.pageSize = 10;
            this.pageSizeOptions = [ 10, 50, 100, 200, 500 ];
            this.sortOrder = "koodiArvo";
            this.sortOrderSelection = 1;
            this.sortOrderReversed = false;
            this.searchResultsLength = 0;

            this.updatedCodeElementsCount = 0;

            this.getCodes(scope, codesUri, codesVersion);
        }
    }

    getCodes(scope, codesUri, codesVersion) {
        this.CodesByUriAndVersion.get({
            codesUri : codesUri,
            codesVersion : codesVersion
        }, (result) => {
            this.codes = result;
            this.namefi = getLanguageSpecificValue(result.metadata, 'nimi', 'FI');
            this.namesv = getLanguageSpecificValue(result.metadata, 'nimi', 'SV');
            this.nameen = getLanguageSpecificValue(result.metadata, 'nimi', 'EN');
            this.name = getLanguageSpecificValueOrValidValue(result.metadata, 'nimi', 'FI');

            this.descriptionfi = getLanguageSpecificValue(result.metadata, 'kuvaus', 'FI');
            this.descriptionsv = getLanguageSpecificValue(result.metadata, 'kuvaus', 'SV');
            this.descriptionen = getLanguageSpecificValue(result.metadata, 'kuvaus', 'EN');
            this.description = getLanguageSpecificValueOrValidValue(result.metadata, 'kuvaus', 'FI');

            this.instructionsfi = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'FI');
            this.instructionssv = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'SV');
            this.instructionsen = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'EN');

            this.targetareafi = getLanguageSpecificValue(result.metadata, 'kohdealue', 'FI');
            this.targetareasv = getLanguageSpecificValue(result.metadata, 'kohdealue', 'SV');
            this.targetareaen = getLanguageSpecificValue(result.metadata, 'kohdealue', 'EN');

            this.targetareapartfi = getLanguageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'FI');
            this.targetareapartsv = getLanguageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'SV');
            this.targetareaparten = getLanguageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'EN');

            this.conceptfi = getLanguageSpecificValue(result.metadata, 'kasite', 'FI');
            this.conceptsv = getLanguageSpecificValue(result.metadata, 'kasite', 'SV');
            this.concepten = getLanguageSpecificValue(result.metadata, 'kasite', 'EN');

            this.operationalenvironmentfi = getLanguageSpecificValue(result.metadata, 'toimintaymparisto', 'FI');
            this.operationalenvironmentsv = getLanguageSpecificValue(result.metadata, 'toimintaymparisto', 'SV');
            this.operationalenvironmenten = getLanguageSpecificValue(result.metadata, 'toimintaymparisto', 'EN');

            this.codessourcefi = getLanguageSpecificValue(result.metadata, 'koodistonLahde', 'FI');
            this.codessourcesv = getLanguageSpecificValue(result.metadata, 'koodistonLahde', 'SV');
            this.codessourceen = getLanguageSpecificValue(result.metadata, 'koodistonLahde', 'EN');

            this.specifiescodesfi = getLanguageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'FI');
            this.specifiescodessv = getLanguageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'SV');
            this.specifiescodesen = getLanguageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'EN');

            this.totakenoticeoffi = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'FI');
            this.totakenoticeofsv = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'SV');
            this.totakenoticeofen = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'EN');

            this.validitylevelfi = getLanguageSpecificValue(result.metadata, 'sitovuustaso', 'FI');
            this.validitylevelsv = getLanguageSpecificValue(result.metadata, 'sitovuustaso', 'SV');
            this.validitylevelen = getLanguageSpecificValue(result.metadata, 'sitovuustaso', 'EN');

            this.codes.withinCodes.forEach((codes) => {
                this.extractAndPushRelatedCode(codes, this.withinCodes);
            });
            this.codes.includesCodes.forEach((codes) => {
                this.extractAndPushRelatedCode(codes, this.includesCodes);
            });
            this.codes.levelsWithCodes.forEach((codes) => {
                this.extractAndPushRelatedCode(codes, this.levelsWithCodes);
            });
            if (this.codes.tila === "PASSIIVINEN") {
                this.deleteState = "";
            }

            this.codes.codesVersions.forEach(function(version) {
                if (version > codesVersion)
                    this.editState = "disabled";
            });

            this.OrganizationByOid.get({
                oid : this.codes.organisaatioOid
            }, (result2) => {
                this.codes.organizationName = result2.nimi['fi'] || result2.nimi['sv'] || result2.nimi['en'];
            });
            this.getCodeElements(codesUri, codesVersion);
            scope.loadingReady = true;
        });
    }

    extractAndPushRelatedCode(codes, list) {
        const languages = Object.keys(codes.nimi)
            .map((languageCode) => ({kieli: languageCode, nimi: codes.nimi[languageCode]}));
        var ce = {};
        ce.uri = codes.codesUri;
        ce.name = getLanguageSpecificValueOrValidValue(languages, 'nimi', 'FI');
        ce.versio = codes.codesVersion;
        ce.active = !codes.passive;
        list.push(ce);
    }

    getCodeElements(codesUri, codesVersion) {
        this.CodeElementsByCodesUriAndVersion.get({
            codesUri : codesUri,
            codesVersion : codesVersion
        }, (result) => {
            this.codeElements = result;
            this.searchResultsLength = this.codeElements.length;
            for (var i = 0; i < this.codeElements.length; i++) {
                this.codeElements[i].name = getLanguageSpecificValueOrValidValue(this.codeElements[i].metadata, 'nimi', 'FI');
            }
        });
    }

    getCodeElementVersions() {
        if (!this.showversion) {
            var elements = this.codeElements;
            this.codeElements = [];
            var newElementsList = [];
            if (elements) {
                this.updatedCodeElementsCount = 0;
                this.searchResultsLength = 0;
                for (var i = 0; i < elements.length; i++) {
                    this.getCodeElementVersionsByCodeElementUri(elements[i].koodiUri, elements.length, newElementsList);
                }
            }
        } else {
            this.getCodes(this.codesUri, this.codesVersion);
        }
    }

    getCodeElementVersionsByCodeElementUri(codeElementUri, elementCount, list) {
        this.CodeElementVersionsByCodeElementUri.get({
            codeElementUri : codeElementUri
        }, (result) => {
            for (var i = 0; i < result.length; i++) {
                result[i].name = getLanguageSpecificValueOrValidValue(result[i].metadata, 'nimi', 'FI');
                list.push(result[i]);
                this.searchResultsLength++;
            }
            this.updatedCodeElementsCount++;
            if (this.updatedCodeElementsCount === elementCount) {
                this.codeElements = list;
            }
        });
    }

    download() {
        this.downloadModalInstance = this.$modal.open({
            templateUrl : 'downloadModalContent.html',
            controller : 'viewCodesController',
            resolve : {
                isModalController : function() {
                    return true;
                }
            }
        });
    }

    upload() {
        this.uploadModalInstance = this.$modal.open({
            templateUrl : 'uploadModalContent.html',
            controller : 'viewCodesController',
            resolve : {
                isModalController : function() {
                    return true;
                }
            }
        });
    }

    removeCodes() {
        this.deleteCodesModalInstance = this.$modal.open({
            templateUrl : 'confirmDeleteCodesModalContent.html',
            controller : 'viewCodesController',
            resolve : {
                isModalController : function() {
                    return true;
                }
            }
        });
    }
}

export class ViewCodesController {
    constructor($scope, $location, $filter, $routeParams, $window, viewCodesModel, DownloadCodes, RemoveRelationCodes, DeleteCodes, loadingService, isModalController) {
        "ngInject";
        $scope.model = viewCodesModel;
        $scope.codesUri = $routeParams.codesUri;
        $scope.uploadUrl = SERVICE_URL_BASE + "codes" + "/upload/" + $scope.codesUri;
        $scope.codesVersion = $routeParams.codesVersion;
        $scope.model.forceRefresh = $routeParams.forceRefresh === true;
        $scope.identity = angular.identity;
        if (!isModalController) {
            viewCodesModel.init($scope, $scope.codesUri, $scope.codesVersion);
        }

        // Alert is passed when reloading after versioning import.
        if ($routeParams.alert && $routeParams.alert.type) {
            $scope.model.alerts.push($routeParams.alert);
        }

        $scope.sortBy1 = 'name';
        $scope.sortBy2 = 'name';
        $scope.sortBy3 = 'name';

        $scope.closeAlert = function (index) {
            $scope.model.alerts.splice(index, 1);
        };

        $scope.cancel = function () {
            $location.path("/");
        };

        $scope.addCodeElement = function () {
            $location.path("/lisaaKoodi/" + $scope.codesUri + "/" + $scope.codesVersion);
        };

        $scope.editCodes = function () {
            $location.path("/muokkaaKoodisto/" + $scope.codesUri + "/" + $scope.codesVersion);
        };

        $scope.okconfirmdeletecodes = function () {
            DeleteCodes.put({
                codesUri: $scope.codesUri,
                codesVersion: $scope.codesVersion
            }, (success) => {
                $location.path("/etusivu").search({
                    forceRefresh: true
                });
            }, (error) => {
                var alert = {
                    type: 'danger',
                    msg: 'Koodiston poisto ep\u00E4onnistui.'
                };
                $scope.model.alerts.push(alert);
            });

            $scope.model.deleteCodesModalInstance.close();
        };

        $scope.cancelconfirmdeletecodes = function () {
            $scope.model.deleteCodesModalInstance.dismiss('cancel');
        };

        $scope.search = function (item) {
            function matchesName(name) {
                return name && name.toLowerCase().indexOf($scope.query.toLowerCase()) > -1;
            }

            if (!$scope.query || matchesName(item.name) || matchesName(item.namesv) || matchesName(item.nameen)
                || item.koodiArvo.toLowerCase().indexOf($scope.query.toLowerCase()) !== -1) {
                return true;
            }
            return false;
        };

        $scope.okdownload = function () {
            var url = DownloadCodes($scope.codesUri, $scope.codesVersion, $scope.model.format, $scope.model.encoding);
            $window.open(url);
            if ($scope.model.downloadModalInstance) {
                $scope.model.downloadModalInstance.close();
            }
        };

        $scope.downloadBlank = function () {
            $scope.model.format = "XLS";
            $scope.codesUri = "blankKoodistoDocument";
            $scope.codesVersion = "-1";

            $scope.okdownload();
        };

        $scope.formatEquals = function (s) {
            return ($scope.model.format === s);
        };

        $scope.canceldownload = function () {
            $scope.model.downloadModalInstance.dismiss('cancel');
        };

        $scope.loadStartFunction = function (evt) {
            loadingService.requestCount++;
        };
        $scope.transferCompleteFunction = function (evt) {
            loadingService.requestCount--;
        };

        $scope.cancelupload = function () {
            $scope.model.uploadModalInstance.dismiss('cancel');
        };

        $scope.uploadComplete = function (evt) {
            $scope.transferCompleteFunction();
            var alert;
            $scope.model.forceRefresh = true;
            if (evt.indexOf && evt.indexOf("error") > -1) {
                alert = {
                    type: 'danger',
                    msg: 'Koodiston ' + $scope.codesUri + ' tuonti ep\u00E4onnistui. Virhe tiedoston lukemisessa: ' + ($filter("i18n")(evt))
                };
            } else {
                alert = {
                    type: 'success',
                    msg: 'Koodisto ' + $scope.codesUri + ' on tuotu onnistuneesti'
                };
                if (evt > $scope.codesVersion) { // Redirect to new version
                    $location.path("/koodisto/" + $scope.codesUri + "/" + evt).search({
                        forceRefresh: true,
                        alert: alert
                    });
                    $scope.model.uploadModalInstance.close();
                    return;
                }
            }
            viewCodesModel.init($scope, $scope.codesUri, $scope.codesVersion);
            $scope.model.alerts.push(alert);
            $scope.model.uploadModalInstance.close();
        };

        // Pagination

        // Get the filtered page count
        $scope.cachedPageCount = 0;
        $scope.getNumberOfPages = function () {
            if ($scope.cachedPageCount === 0 && $scope.model.codeElements.length > 0) {
                $scope.refreshNumberOfPages();
            }
            return $scope.cachedPageCount;
        };

        // Refresh the page count when the model changes
        $scope.cachedElementCount = 0;
        $scope.$watch('model.codeElements', function () {
            if ($scope.model.codeElements.length !== $scope.cachedElementCount) {
                $scope.refreshNumberOfPages();
                refreshPage = true;
                $scope.cachedElementCount = $scope.model.codeElements.length;
            }
        });

        // Refresh the page count (less redundant filtering)
        $scope.refreshNumberOfPages = function () {
            $scope.model.searchResultsLength = ($filter("filter")($scope.model.codeElements, $scope.search)).length;
            $scope.cachedPageCount = Math.ceil($scope.model.searchResultsLength / $scope.model.pageSize);
            return $scope.cachedPageCount;
        };

        // Change the currentPage when the pageSize is changed.
        var oldValueForPageSize = 10;
        $scope.pageSizeChanged = function () {
            var topmostCodeElement = $scope.model.currentPage * oldValueForPageSize;
            $scope.model.currentPage = Math.floor(topmostCodeElement / $scope.model.pageSize);
            oldValueForPageSize = $scope.model.pageSize;
            $scope.refreshNumberOfPages();
        };

        $scope.sortOrderChanged = function (value) {
            if (value) {
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
            refreshPage = true;
        };

        // When user changes the search string the page count changes and the current page must be adjusted
        $scope.filterChangedPageCount = function () {
            const currentNumberOfPages = $scope.refreshNumberOfPages();
            if ($scope.model.currentPage >= currentNumberOfPages) {
                $scope.model.currentPage = currentNumberOfPages - 1;
            }
            if (currentNumberOfPages !== 0 && $scope.model.currentPage < 0) {
                $scope.model.currentPage = 0;
            }
        };

        $scope.changePage = function (i) {
            $scope.model.currentPage = i;
        };

        $scope.incrementPage = function (i) {
            var newPageNumber = $scope.model.currentPage + i;
            if (newPageNumber > -1 && newPageNumber < $scope.getNumberOfPages()) {
                $scope.model.currentPage = newPageNumber;
            }
        };

        var refreshPage = true;
        $scope.getPaginationPage = function () {
            if (refreshPage) {
                // Only do sorting when the model has changed, heavy operation
                refreshPage = false;
                $scope.model.codeElements = $filter("naturalSort")($scope.model.codeElements, $scope.model.sortOrder, $scope.model.sortOrderReversed);
            }
            var results = $scope.model.codeElements;
            results = $filter("filter")(results, $scope.search);
            results = results.splice($scope.model.currentPage * $scope.model.pageSize, $scope.model.pageSize);
            return results;
        };
        // Pagination ends

        $scope.showRelation = function (codes) {
            return codes.active || $scope.showPassive
        }
    }
}
