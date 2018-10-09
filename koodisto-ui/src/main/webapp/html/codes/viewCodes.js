import {koodistoConfig} from "../app.utils";
import {getLanguageSpecificValue, getLanguageSpecificValueOrValidValue} from "../app.utils";

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

    init(codesUri, codesVersion) {
        if (this.forceRefresh) {
            this.forceRefreshCodeElements = "?forceRefresh";
        } else {
            this.forceRefreshCodeElements = "";
        }
        // Samaa koodistoa on turha ladata uudelleen modelliin
        if (this.forceRefresh || !(this.codes && this.codes.koodistoUri === codesUri && this.codes.versio === codesVersion)) {
            this.showPassive = false;
            this.forceRefresh = false;
            this.codes = null;
            this.withinCodes = [];
            this.includesCodes = [];
            this.levelsWithCodes = [];
            this.codesUri = codesUri;
            this.codesVersion = codesVersion;
            this.showversion = false;
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

            this.getCodes(codesUri, codesVersion);
        }
    }

    getCodes(codesUri, codesVersion) {
        this.CodesByUriAndVersion.get({
            codesUri: codesUri,
            codesVersion: codesVersion
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

            this.codes.codesVersions.forEach((version) => {
                if (version > codesVersion)
                    this.editState = "disabled";
            });

            this.OrganizationByOid.get({
                oid : this.codes.organisaatioOid
            }, (result2) => {
                this.codes.organizationName = result2.nimi['fi'] || result2.nimi['sv'] || result2.nimi['en'];
            });
            this.getCodeElements(codesUri, codesVersion);
            this.loadingReady = true;
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
        if (this.showversion) {
            const elements = this.codeElements;
            this.codeElements = [];
            const newElementsList = [];
            if (elements) {
                this.updatedCodeElementsCount = 0;
                this.searchResultsLength = 0;
                for (let i = 0; i < elements.length; i++) {
                    this.getCodeElementVersionsByCodeElementUri(elements[i].koodiUri, elements.length, newElementsList);
                }
            }
        }
        else {
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
            // Included in viewcodes.html
            templateUrl: 'downloadModalContent.html',
            controller: 'viewCodesController as viewCodesDownloadModal',
            resolve: {
                isModalController : () => {
                    return true;
                }
            }
        });
    }

    upload() {
        this.uploadModalInstance = this.$modal.open({
            // Included in viewcodes.html
            templateUrl: 'uploadModalContent.html',
            controller: 'viewCodesController as viewCodesUploadModal',
            resolve: {
                isModalController : () => {
                    return true;
                }
            }
        });
    }

    removeCodes() {
        this.deleteCodesModalInstance = this.$modal.open({
            // Included in viewcodes.html
            templateUrl: 'confirmDeleteCodesModalContent.html',
            controller: 'viewCodesController as viewCodesConfirmDeleteModal',
            resolve: {
                isModalController: () => {
                    return true;
                }
            }
        });
    }
}

export class ViewCodesController {
    constructor($scope, $location, $filter, $routeParams, $window, viewCodesModel, DownloadCodes, RemoveRelationCodes, DeleteCodes, loadingService, isModalController) {
        "ngInject";
        this.$scope = $scope;
        this.$location = $location;
        this.$filter = $filter;
        this.$routeParams = $routeParams;
        this.$window = $window;
        this.viewCodesModel = viewCodesModel;
        this.DownloadCodes = DownloadCodes;
        this.RemoveRelationCodes = RemoveRelationCodes;
        this.DeleteCodes = DeleteCodes;
        this.loadingService = loadingService;
        this.isModalController = isModalController;

        this.model = viewCodesModel;
        this.codesUri = $routeParams.codesUri;
        this.uploadUrl = koodistoConfig.SERVICE_URL_BASE + "codes" + "/upload/" + this.codesUri;
        this.codesVersion = $routeParams.codesVersion;
        this.model.forceRefresh = $routeParams.forceRefresh === true;
        this.identity = (value) => value;
        if (!isModalController) {
            viewCodesModel.init(this.codesUri, this.codesVersion);
        }

        // Alert is passed when reloading after versioning import.
        if ($routeParams.alert && $routeParams.alert.type) {
            this.model.alerts.push($routeParams.alert);
        }

        this.sortBy1 = 'name';
        this.sortBy2 = 'name';
        this.sortBy3 = 'name';

        this.refreshPage = true;
        this.cachedPageCount = 0;
        this.cachedElementCount = 0;

        $scope.$watch( () => this.model.codeElements, () => {
            if (this.model.codeElements.length !== this.cachedElementCount) {
                this.refreshNumberOfPages();
                this.refreshPage = true;
                this.cachedElementCount = this.model.codeElements.length;
            }
        });

        this.oldValueForPageSize = 10;

        this.showRelation = (codes) => {
            return codes.active || this.model.showPassive
        }

    }

    closeAlert(index) {
        this.model.alerts.splice(index, 1);
    }

    cancel() {
        this.$location.path("/");
    }

    addCodeElement() {
        this.$location.path("/lisaaKoodi/" + this.codesUri + "/" + this.codesVersion);
    }

    editCodes() {
        this.$location.path("/muokkaaKoodisto/" + this.codesUri + "/" + this.codesVersion);
    }

    okconfirmdeletecodes() {
        this.DeleteCodes.put({
            codesUri: this.codesUri,
            codesVersion: this.codesVersion
        }, (success) => {
            this.$location.path("/etusivu").search({
                forceRefresh: true
            });
        }, (error) => {
            var alert = {
                type: 'danger',
                msg: 'Koodiston poisto ep\u00E4onnistui.'
            };
            this.model.alerts.push(alert);
        });

        this.model.deleteCodesModalInstance.close();
    }

    cancelconfirmdeletecodes() {
        this.model.deleteCodesModalInstance.dismiss('cancel');
    }

    search(item) {
        const matchesName = (name) => {
            return name && name.toLowerCase().indexOf(this.query.toLowerCase()) > -1;
        };

        if (!this.query || matchesName(item.name) || matchesName(item.namesv) || matchesName(item.nameen)
            || item.koodiArvo.toLowerCase().indexOf(this.query.toLowerCase()) !== -1) {
            return true;
        }
        return false;
    }

    okdownload() {
        const url = this.DownloadCodes(this.codesUri, this.codesVersion, this.model.format, this.model.encoding);
        this.$window.open(url);
        if (this.model.downloadModalInstance) {
            this.model.downloadModalInstance.close();
        }
    }

    downloadBlank() {
        this.model.format = "XLS";
        this.codesUri = "blankKoodistoDocument";
        this.codesVersion = "-1";

        this.okdownload();
    }

    formatEquals(s) {
        return (this.model.format === s);
    }

    canceldownload() {
        this.model.downloadModalInstance.dismiss('cancel');
    }

    loadStartFunction(evt) {
        this.loadingService.requestCount++;
    }
    transferCompleteFunction(evt) {
        this.loadingService.requestCount--;
    }

    cancelupload() {
        this.model.uploadModalInstance.dismiss('cancel');
    }

    uploadComplete(evt) {
        this.transferCompleteFunction();
        let alert;
        this.model.forceRefresh = true;
        if (evt.indexOf && evt.indexOf("error") > -1) {
            alert = {
                type: 'danger',
                msg: 'Koodiston ' + this.codesUri + ' tuonti ep\u00E4onnistui. Virhe tiedoston lukemisessa: ' + (this.$filter("i18n")(evt))
            };
        } else {
            alert = {
                type: 'success',
                msg: 'Koodisto ' + this.codesUri + ' on tuotu onnistuneesti'
            };
            if (evt > this.codesVersion) { // Redirect to new version
                this.$location.path("/koodisto/" + this.codesUri + "/" + evt).search({
                    forceRefresh: true,
                    alert: alert
                });
                this.model.uploadModalInstance.close();
                return;
            }
        }
        this.viewCodesModel.init(this.$scope, this.codesUri, this.codesVersion);
        this.model.alerts.push(alert);
        this.model.uploadModalInstance.close();
    }

    // Pagination

    // Get the filtered page count
    getNumberOfPages() {
        if (this.cachedPageCount === 0 && this.model.codeElements.length > 0) {
            this.refreshNumberOfPages();
        }
        return this.cachedPageCount;
    };

    // Refresh the page count when the model changes
    // Refresh the page count (less redundant filtering)
    refreshNumberOfPages() {
        this.model.searchResultsLength = (this.$filter("filter")(this.model.codeElements, () => this.search)).length;
        this.cachedPageCount = Math.ceil(this.model.searchResultsLength / this.model.pageSize);
        return this.cachedPageCount;
    }

    // Change the currentPage when the pageSize is changed.
    pageSizeChanged() {
        const topmostCodeElement = this.model.currentPage * this.oldValueForPageSize;
        this.model.currentPage = Math.floor(topmostCodeElement / this.model.pageSize);
        this.oldValueForPageSize = this.model.pageSize;
        this.refreshNumberOfPages();
    }

    sortOrderChanged(value) {
        if (value) {
            this.model.sortOrderSelection = value;
        }
        const selection = parseInt(this.model.sortOrderSelection);
        switch (selection) {
            case 1:
                this.model.sortOrderReversed = false;
                this.model.sortOrder = "koodiArvo";
                break;
            case 2:
                this.model.sortOrderReversed = true;
                this.model.sortOrder = "koodiArvo";
                break;
            case 3:
                this.model.sortOrderReversed = false;
                this.model.sortOrder = "name";
                break;
            case 4:
                this.model.sortOrderReversed = true;
                this.model.sortOrder = "name";
                break;
            case 5:
                this.model.sortOrderReversed = false;
                this.model.sortOrder = "versio";
                break;
            case 6:
                this.model.sortOrderReversed = true;
                this.model.sortOrder = "versio";
                break;

            default:
                break;
        }
        this.refreshPage = true;
    }

    // When user changes the search string the page count changes and the current page must be adjusted
    filterChangedPageCount() {
        const currentNumberOfPages = this.refreshNumberOfPages();
        if (this.model.currentPage >= currentNumberOfPages) {
            this.model.currentPage = currentNumberOfPages - 1;
        }
        if (currentNumberOfPages !== 0 && this.model.currentPage < 0) {
            this.model.currentPage = 0;
        }
    }

    changePage(i) {
        this.model.currentPage = i;
    }

    incrementPage(i) {
        const newPageNumber = this.model.currentPage + i;
        if (newPageNumber > -1 && newPageNumber < this.getNumberOfPages()) {
            this.model.currentPage = newPageNumber;
        }
    }

    getPaginationPage() {
        if (this.refreshPage) {
            // Only do sorting when the model has changed, heavy operation
            this.refreshPage = false;
            this.model.codeElements = this.$filter("naturalSort")(this.model.codeElements, this.model.sortOrder, this.model.sortOrderReversed);
        }
        let results = this.model.codeElements;
        results = results.filter((item) => this.search(item));
        results = results.splice(this.model.currentPage * this.model.pageSize, this.model.pageSize);
        return results;
    };
    // Pagination ends

}
