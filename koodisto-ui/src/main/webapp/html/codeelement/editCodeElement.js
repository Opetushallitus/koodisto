import {koodistoConfig} from "../app.utils";
import alertIcon from '../../img/alert-icon28x29.png';
import jQuery from 'jquery';
import moment from "moment";
import {getLanguageSpecificValue, getLanguageSpecificValueOrValidValue} from "../app.utils";

export class CodeElementEditorModel {
    constructor($modal, $location, RootCodes, CodeElementByUriAndVersion, AllCodes, CodeElementsByCodesUriAndVersion, LatestCodeElementVersionsByCodeElementUri, authService) {
        "ngInject";
        this.$modal = $modal;
        this.$location = $location;
        this.RootCodes = RootCodes;
        this.CodeElementByUriAndVersion = CodeElementByUriAndVersion;
        this.AllCodes = AllCodes;
        this.CodeElementsByCodesUriAndVersion = CodeElementsByCodesUriAndVersion;
        this.LatestCodeElementVersionsByCodeElementUri = LatestCodeElementVersionsByCodeElementUri;
        this.authService = authService;

        this.states = [{
            key: 'PASSIIVINEN',
            value: 'PASSIIVINEN'
        }, {
            key: 'LUONNOS',
            value: 'LUONNOS'
        }];

        this.allCodes = [];
        this.withinCodeElements = [];
        this.includesCodeElements = [];
        this.levelsWithCodeElements = [];
        this.alerts = [];
        this.allWithinCodeElements = [];
        this.allIncludesCodeElements = [];
        this.allLevelsWithCodeElements = [];
        this.showCode = '';
        this.withinListLengthLimit = 10;
        this.includesListLengthLimit = 10;
        this.levelsWithListLengthLimit = 10;
        this.isAddingRelationsComplete = false;
        this.isRemovingRelationsComplete = false;
    }


    init(scope, codeElementUri, codeElementVersion) {
        this.allCodes = [];
        this.withinCodeElements = [];
        this.includesCodeElements = [];
        this.levelsWithCodeElements = [];
        this.alerts = [];
        this.allWithinCodeElements = [];
        this.allIncludesCodeElements = [];
        this.allLevelsWithCodeElements = [];
        this.shownCodeElements = [];
        this.loadingCodeElements = false;

        this.withinListLengthLimit = 10;
        this.includesListLengthLimit = 10;
        this.levelsWithListLengthLimit = 10;

        this.isAddingRelationsComplete = false;
        this.isRemovingRelationsComplete = false;

        // Pagination
        this.currentPage = 0;
        this.pageSize = 10;
        this.pageSizeOptions = [10, 50, 100, 200, 500];
        this.sortOrder = "value";
        this.sortOrderSelection = 1;
        this.sortOrderReversed = false;

        this.getAllCodes();
        this.getCodeElement(scope, codeElementUri, codeElementVersion);

    }

    getCodeElement(scope, codeElementUri, codeElementVersion) {
        this.CodeElementByUriAndVersion.get({
            codeElementUri: codeElementUri,
            codeElementVersion: codeElementVersion
        }, (result) => {
            this.codeElement = result;
            if (this.codeElement.voimassaAlkuPvm) {
                this.codeElement.voimassaAlkuPvm = new Date(this.codeElement.voimassaAlkuPvm);
            }
            if (this.codeElement.voimassaLoppuPvm) {
                this.codeElement.voimassaLoppuPvm = new Date(this.codeElement.voimassaLoppuPvm);
            }
            this.codeValue = result.koodiArvo;

            this.namefi = getLanguageSpecificValue(result.metadata, 'nimi', 'FI');
            this.namesv = getLanguageSpecificValue(result.metadata, 'nimi', 'SV');
            this.nameen = getLanguageSpecificValue(result.metadata, 'nimi', 'EN');

            this.shortnamefi = getLanguageSpecificValue(result.metadata, 'lyhytNimi', 'FI');
            this.shortnamesv = getLanguageSpecificValue(result.metadata, 'lyhytNimi', 'SV');
            this.shortnameen = getLanguageSpecificValue(result.metadata, 'lyhytNimi', 'EN');

            this.descriptionfi = getLanguageSpecificValue(result.metadata, 'kuvaus', 'FI');
            this.descriptionsv = getLanguageSpecificValue(result.metadata, 'kuvaus', 'SV');
            this.descriptionen = getLanguageSpecificValue(result.metadata, 'kuvaus', 'EN');

            this.instructionsfi = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'FI');
            this.instructionssv = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'SV');
            this.instructionsen = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'EN');

            this.conceptfi = getLanguageSpecificValue(result.metadata, 'kasite', 'FI');
            this.conceptsv = getLanguageSpecificValue(result.metadata, 'kasite', 'SV');
            this.concepten = getLanguageSpecificValue(result.metadata, 'kasite', 'EN');

            this.totakenoticeoffi = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodi', 'FI');
            this.totakenoticeofsv = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodi', 'SV');
            this.totakenoticeofen = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodi', 'EN');

            this.containssignificancefi = getLanguageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'FI');
            this.containssignificancesv = getLanguageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'SV');
            this.containssignificanceen = getLanguageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'EN');

            this.doesnotcontainsignificancefi = getLanguageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'FI');
            this.doesnotcontainsignificancesv = getLanguageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'SV');
            this.doesnotcontainsignificanceen = getLanguageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'EN');

            this.containscodesfi = getLanguageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'FI');
            this.containscodessv = getLanguageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'SV');
            this.containscodesen = getLanguageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'EN');

            this.samename = false;
            this.samedescription = false;
            this.sameshortname = false;
            this.sameinstructions = false;
            this.sameconcept = false;
            this.sametotakenoticeof = false;
            this.samecontainssignificance = false;
            this.samedoesnotcontainsignificance = false;
            this.samecontainscodes = false;

            this.codeElement.withinCodeElements.forEach((codeElement) => {
                this.extractAndPushCodeElementInformation(codeElement, this.withinCodeElements);
            });
            this.codeElement.includesCodeElements.forEach((codeElement) => {
                this.extractAndPushCodeElementInformation(codeElement, this.includesCodeElements);
            });
            this.codeElement.levelsWithCodeElements.forEach((codeElement) => {
                this.extractAndPushCodeElementInformation(codeElement, this.levelsWithCodeElements);
            });

            scope.loadingReady = true;
        });
    }

    extractAndPushCodeElementInformation(codeElement, list) {
        var ce = {};
        ce.uri = codeElement.codeElementUri;
        ce.name = getLanguageSpecificValueOrValidValue(codeElement.relationMetadata, 'nimi', 'FI');
        ce.value = codeElement.codeElementValue;
        ce.versio = codeElement.codeElementVersion;
        ce.codesname = getLanguageSpecificValueOrValidValue(codeElement.parentMetadata, 'nimi', 'FI');
        ce.passive = codeElement.passive;
        list.push(ce);
    }

    filterCodes() {
        for (var i = 0; i < this.allCodes.length; i++) {
            var koodistos = this.allCodes[i].koodistos;
            var temp = [];
            if (koodistos) {
                for (let j = 0; j < koodistos.length; j++) {
                    var koodisto = koodistos[j];
                    // Vain ne koodit näytetään, jotka ovat samssa organisaatiossa tämän kanssa
                    if (koodisto.organisaatioOid === this.codeElement.koodisto.organisaatioOid) {
                        temp.push(koodisto);
                    }
                }
                this.allCodes[i].koodistos = temp;
            }
        }
    }

    getAllCodes() {
        this.RootCodes.get({}, (result) => {
            this.allCodes = result;
            this.authService.updateOph(koodistoConfig.SERVICE_NAME)
                .then(
                    () => { },
                    this.filterCodes
                );
        });
    }

    openChildren(data) {
        data.open = !data.open;
        if (data.open) {
            const iter = (children) => {
                if (children) {
                    children.forEach((child) => {
                        child.open = true;
                    });
                }
            };

            if (data.latestKoodistoVersio) {
                this.CodeElementsByCodesUriAndVersion.get({
                    codesUri: data.koodistoUri,
                    codesVersion: data.latestKoodistoVersio.versio
                }, (result) => {
                    data.children = result;
                });
            }
            iter(data.children);
        }
    }

    incrementListLimit(listName) {
        if (listName === "within") {
            this.withinListLengthLimit = this.withinCodeElements.length;
        }
        if (listName === "includes") {
            this.includesListLengthLimit = this.includesCodeElements.length;
        }
        if (listName === "levelsWith") {
            this.levelsWithListLengthLimit = this.levelsWithCodeElements.length;
        }
    }
}

export class CodeElementEditorController {
    constructor($scope, $location, $routeParams, $filter, codeElementEditorModel,
                CodesByUriAndVersion, SaveCodeElement, CodeElementsByCodesUriAndVersion, $modal,
                isModalController, loadingService) {
        "ngInject";
        this.$location = $location;
        this.$routeParams = $routeParams;
        this.$filter = $filter;
        this.codeElementEditorModel = codeElementEditorModel;
        this.CodesByUriAndVersion = CodesByUriAndVersion;
        this.SaveCodeElement = SaveCodeElement;
        this.CodeElementsByCodesUriAndVersion = CodeElementsByCodesUriAndVersion;
        this.$modal = $modal;
        this.isModalController = isModalController;
        this.loadingService = loadingService;

        this.alertIcon = alertIcon;

        this.model = codeElementEditorModel;
        this.codeElementUri = $routeParams.codeElementUri;
        this.codeElementVersion = $routeParams.codeElementVersion;
        this.errorMessage = $filter('i18n')('field.required');
        this.errorMessageAtLeastOneName = $filter('i18n')('field.required.at.least.one.name');
        this.errorMessageIfOtherInfoIsGiven = $filter('i18n')('field.required.if.other.info.is.given');
        this.sortBy = 'name';

        if (!isModalController) {
            codeElementEditorModel.init(this, this.codeElementUri, this.codeElementVersion);
        }

        this.selectallcodelements = false;
        this.isCodeElementLoading = loadingService.isLoading;

        // Pagination
        this.cachedPageCount = 0;
        this.cachedElementCount = 0;
        // Refresh the page count when the model changes
        // After angular 1.5 this could be achieved with lifecycle hooks or using components
        $scope.$watch(() => this.model.shownCodeElements, () => {
            if (this.model.shownCodeElements.length !== this.cachedElementCount) {
                this.cachedElementCount = this.model.shownCodeElements.length;
                this.updatePaginationPage(true);
            }
        });
        this.oldValueForPageSize = 10;
        this.paginationPage = [];
    }

    onMasterChange(master) {
        for (let i = 0; i < this.model.shownCodeElements.length; i++) {
            if (this.search(this.model.shownCodeElements[i])) {
                this.model.shownCodeElements[i].checked = master;
            }
        }
    }

    closeAlert(index) {
        this.model.alerts.splice(index, 1);
    }

    redirectCancel() {
        this.$location.path("/koodi/" + this.codeElementUri + "/" + this.codeElementVersion);
    }

    cancel() {
        this.closeCancelConfirmModal();
        this.redirectCancel();
    }

    showCancelConfirmModal(formHasChanged) {
        if (formHasChanged) {
            this.model.cancelConfirmModal = this.$modal.open({
                // Included in editcodeelement.html
                templateUrl: 'confirmcancel.html',
                controller: 'codeElementEditorController as codeElementEditorModal',
                resolve: {
                    isModalController: () => true
                }
            });
        } else {
            this.redirectCancel();
        }
    }

    closeCancelConfirmModal() {
        this.model.cancelConfirmModal.close();
    }

    submit() {
        this.persistCodes();
    }

    search(item) {
        return !this.model.query
            || item.name.toLowerCase().indexOf(this.model.query.toLowerCase()) !== -1
            || item.value.toLowerCase().indexOf(this.model.query.toLowerCase()) !== -1;

    }

    persistCodes() {
        const format = 'YYYY-MM-DD';
        const codeelement = {
            koodiUri: this.codeElementUri,
            versio: this.codeElementVersion,
            voimassaAlkuPvm: moment(this.model.codeElement.voimassaAlkuPvm).format(format),
            voimassaLoppuPvm: this.model.codeElement.voimassaLoppuPvm && moment(this.model.codeElement.voimassaLoppuPvm).format(format),
            koodiArvo: this.model.codeValue,
            tila: this.model.codeElement.tila,
            version: this.model.codeElement.version,

            withinCodeElements: this.changeToRelationCodeElements(this.model.withinCodeElements),
            includesCodeElements: this.changeToRelationCodeElements(this.model.includesCodeElements),
            levelsWithCodeElements: this.changeToRelationCodeElements(this.model.levelsWithCodeElements),

            metadata: []
        };

        CodeElementEditorController.addMetadataByLanguage(this.model, codeelement, 'FI');
        CodeElementEditorController.addMetadataByLanguage(this.model, codeelement, 'SV');
        CodeElementEditorController.addMetadataByLanguage(this.model, codeelement, 'EN');

        const codeElementVersionResponse = this.SaveCodeElement.put({}, codeelement);
        codeElementVersionResponse.$promise.then(() => {
            this.$location.path("/koodi/" + this.codeElementUri + "/" + codeElementVersionResponse.content).search({
                edited: true
            });
        }, (error) => {
            const type = 'danger';
            let message = jQuery.i18n && jQuery.i18n.prop(error.data);
            if (error.status === 504) {
                message = jQuery.i18n && jQuery.i18n.prop('error.save.timeout');
            }
            const alert = {
                type: type,
                msg: message
            };
            this.model.alerts.push(alert);
        });
    }

    static addMetadataByLanguage(model, codeelement, lang) {
        const langLower = lang.toLowerCase();
        const langUpper = lang.toUpperCase();
        if (model['name' + langLower]) {
            codeelement.metadata.push({
                kieli: langUpper,
                nimi: model['name' + langLower],
                kuvaus: model['description' + langLower],
                lyhytNimi: model['shortname' + langLower],
                kayttoohje: model['instructions' + langLower],
                kasite: model['concept' + langLower],
                huomioitavaKoodi: model['totakenoticeof' + langLower],
                sisaltaaMerkityksen: model['containssignificance' + langLower],
                eiSisallaMerkitysta: model['doesnotcontainsignificance' + langLower],
                sisaltaaKoodiston: model['containscodes' + langLower],
            });
        }
    }

    changeToRelationCodeElements(listToBeChanged) {
        const result = [];
        listToBeChanged.forEach((ce) => {
            const dt = {};
            dt.codeElementUri = ce.uri;
            dt.codeElementVersion = 1; // This does nothing. Latest version is used.
            dt.passive = ce.passive ? ce.passive : false;
            result.push(dt);
        });
        return result;
    }

    setSameValue(name) {
        if (name === 'name' && this.model.samename) {
            this.model.namesv = this.model.namefi;
            this.model.nameen = this.model.namefi;
        } else if (name === 'description' && this.model.samedescription) {
            this.model.descriptionsv = this.model.descriptionfi;
            this.model.descriptionen = this.model.descriptionfi;
        } else if (name === 'shortname' && this.model.sameshortname) {
            this.model.shortnamesv = this.model.shortnamefi;
            this.model.shortnameen = this.model.shortnamefi;
        } else if (name === 'instructions' && this.model.sameinstructions) {
            this.model.instructionssv = this.model.instructionsfi;
            this.model.instructionsen = this.model.instructionsfi;
        } else if (name === 'concept' && this.model.sameconcept) {
            this.model.conceptsv = this.model.conceptfi;
            this.model.concepten = this.model.conceptfi;
        } else if (name === 'totakenoticeof' && this.model.sametotakenoticeof) {
            this.model.totakenoticeofsv = this.model.totakenoticeoffi;
            this.model.totakenoticeofen = this.model.totakenoticeoffi;
        } else if (name === 'containssignificance' && this.model.samecontainssignificance) {
            this.model.containssignificancesv = this.model.containssignificancefi;
            this.model.containssignificanceen = this.model.containssignificancefi;
        } else if (name === 'doesnotcontainsignificance' && this.model.samedoesnotcontainsignificance) {
            this.model.doesnotcontainsignificancesv = this.model.doesnotcontainsignificancefi;
            this.model.doesnotcontainsignificanceen = this.model.doesnotcontainsignificancefi;
        } else if (name === 'containscodes' && this.model.samecontainscodes) {
            this.model.containscodessv = this.model.containscodesfi;
            this.model.containscodesen = this.model.containscodesfi;
        }
    }

    createCodes(data) {
        const ce = {};
        ce.uri = data.uri;
        ce.name = data.name;
        ce.value = data.value;
        return ce;
    }

    addRelationCodeElement(codeElementToAdd, collectionToAddTo, relationTypeString, modelCodeElementIsHost) {
        var found = false;
        collectionToAddTo.forEach((codeElement, index) => {
            if (codeElement.uri.indexOf(codeElementToAdd.uri) !== -1) {
                found = true;
            }
        });
        if (found === false) {
            collectionToAddTo.push(this.createCodes(codeElementToAdd));
        }
    }

    addRelationsCodeElement(selectedItems, collectionToAddTo) {
        const elementUrisToAdd = [];
        const addedElements = [];

        selectedItems.forEach((codeElement) => {
            let found = false;
            collectionToAddTo.forEach((innerCodeElement) => {
                if (codeElement.uri === innerCodeElement.uri && !innerCodeElement.passive) {
                    found = true;
                }
            });
            if (!found) {
                elementUrisToAdd.push(codeElement.uri);
                addedElements.push(codeElement);
            }
        });

        if (elementUrisToAdd.length < 1) {
            if (this.model.isRemovingRelationsComplete) {
                this.model.codeelementmodalInstance.close();
            }
            this.model.isAddingRelationsComplete = true;
            return;
        }

        addedElements.forEach((item) => {
            // Passive elements are not saved.
            item.passive = false;
            collectionToAddTo.push(item);
        });
        this.model.codeelementmodalInstance.close();
    }

    removeRelationsCodeElement(unselectedItems, collectionToRemoveFrom) {
        const elementUrisToRemove = [];
        unselectedItems.forEach((codeElement) => {
            collectionToRemoveFrom.forEach((innerCodeElement) => {
                if (codeElement.uri === innerCodeElement.uri) {
                    elementUrisToRemove.push(innerCodeElement.uri);
                }
            });
        });

        if (elementUrisToRemove.length < 1) {
            if (this.model.isAddingRelationsComplete) {
                this.model.codeelementmodalInstance.close();
            }
            this.model.isRemovingRelationsComplete = true;
            return;
        }

        const remainingElements = collectionToRemoveFrom.filter((element) => elementUrisToRemove.indexOf(element.uri) === -1);
        collectionToRemoveFrom.length = 0;
        Array.prototype.push.apply(collectionToRemoveFrom, remainingElements);

        this.model.codeelementmodalInstance.close();
    }

    cancelcodeelement() {
        this.model.codeelementmodalInstance.dismiss('cancel');
    }

    okcodeelement() {
        const selectedItems = this.model.shownCodeElements.filter(shownCodeElement => shownCodeElement.checked === true);
        const unselectedItems = this.model.shownCodeElements.filter(shownCodeElement => shownCodeElement.checked === false);
        if (this.model.addToListName === 'withincodes') {
            this.addRelationsCodeElement(selectedItems, this.model.withinCodeElements);
            this.removeRelationsCodeElement(unselectedItems, this.model.withinCodeElements);

        } else if (this.model.addToListName === 'includescodes') {
            this.addRelationsCodeElement(selectedItems, this.model.includesCodeElements);
            this.removeRelationsCodeElement(unselectedItems, this.model.includesCodeElements);

        } else if (this.model.addToListName === 'levelswithcodes') {
            this.addRelationsCodeElement(selectedItems, this.model.levelsWithCodeElements);
            this.removeRelationsCodeElement(unselectedItems, this.model.levelsWithCodeElements);
        }
    };

    showCodeElementsInCodeSet(toBeShown, existingSelections) {
        const existingActiveSelections = existingSelections.filter((existingSelection) => !existingSelection.passive);
        toBeShown = [];
        this.CodeElementsByCodesUriAndVersion.get({
            codesUri: this.model.showCode,
            codesVersion: 0
        }, (result) => {
            this.selectallcodelements = true;
            result.forEach((codeElement) => {
                if (codeElement.koodiUri !== this.codeElementUri) {
                    const ce = {};
                    ce.uri = codeElement.koodiUri;
                    ce.checked = existingActiveSelections
                        .filter((element) => codeElement.koodiUri === element.uri && codeElement.versio === element.versio && !element.passive)
                        .length > 0;
                    ce.value = codeElement.koodiArvo;
                    ce.name = getLanguageSpecificValueOrValidValue(codeElement.metadata, 'nimi', 'FI');
                    if (this.selectallcodelements && !ce.checked) {
                        this.selectallcodelements = false;
                    }
                    ce.passive = false;
                    toBeShown.push(ce);
                    this.updatePaginationPage(true);
                }
            });

            this.model.shownCodeElements = toBeShown;
            this.model.loadingCodeElements = false;
        });
    }

    removeFromWithinCodeElements(codeelement) {
        this.model.withinRelationToRemove = codeelement;
        this.okconfirm();
    }

    removeFromIncludesCodeElements(codeelement) {
        this.model.includesRelationToRemove = codeelement;
        this.okconfirm();
    }

    removeFromLevelsWithCodeElements(codeelement) {
        this.model.levelsRelationToRemove = codeelement;
        this.okconfirm();
    }

    getCodeElements() {
        this.model.loadingCodeElements = true;
        this.model.currentPage = 0;
        const name = this.model.addToListName;
        this.CodesByUriAndVersion.get({
            codesUri: this.model.codeElement.koodisto.koodistoUri,
            codesVersion: 0
        }, (result) => {

            const getCodesUris = (relationArray) => {
                const codesUris = [];
                relationArray.forEach((value) => {
                    codesUris.push(value.codesUri);
                });
                return codesUris;
            };

            if (name === 'withincodes') {
                if (this.model.showCode && this.model.showCode.length > 0) {
                    this.showCodeElementsInCodeSet(this.model.allWithinCodeElements, this.model.withinCodeElements);
                }
                this.model.shownCodes = getCodesUris(result.withinCodes);
                this.model.shownCodes.unshift(this.model.codeElement.koodisto.koodistoUri);
                this.model.shownCodeElements = this.model.allWithinCodeElements;

            } else if (name === 'includescodes') {
                if (this.model.showCode && this.model.showCode.length > 0) {
                    this.showCodeElementsInCodeSet(this.model.allIncludesCodeElements, this.model.includesCodeElements);
                }
                this.model.shownCodes = getCodesUris(result.includesCodes);
                this.model.shownCodes.unshift(this.model.codeElement.koodisto.koodistoUri);
                this.model.shownCodeElements = this.model.allIncludesCodeElements;

            } else if (name === 'levelswithcodes') {
                if (this.model.showCode && this.model.showCode.length > 0) {
                    this.showCodeElementsInCodeSet(this.model.allLevelsWithCodeElements, this.model.levelsWithCodeElements);
                }
                this.model.shownCodes = getCodesUris(result.levelsWithCodes);
                this.model.shownCodeElements = this.model.allLevelsWithCodeElements;
            }
            if (!this.model.showCode || this.model.showCode.length === 0) {
                this.model.loadingCodeElements = false;
            }
        });
    }

    show(name) {
        this.model.showCode = '';
        this.model.addToListName = name;
        if (this.model.allWithinCodeElements.length === 0 || this.model.allIncludesCodeElements.length === 0
            || this.model.allLevelsWithCodeElements.length === 0) {

            this.getCodeElements();

            this.model.codeelementmodalInstance = this.$modal.open({
                templateUrl: 'codeElementModalContent.html',
                controller: 'codeElementEditorController as codeElementEditorModal',
                resolve: {
                    isModalController: () => true
                }
            });
        }
    }

    okconfirm() {
        if (this.model.withinRelationToRemove && this.model.withinRelationToRemove.uri !== "") {
            this.model.withinCodeElements.forEach((codeElement, index) => {
                if (codeElement.uri === this.model.withinRelationToRemove.uri && codeElement.versio === this.model.withinRelationToRemove.versio) {
                    this.model.withinCodeElements.splice(index, 1);
                }
            });

        } else if (this.model.includesRelationToRemove && this.model.includesRelationToRemove.uri !== "") {
            this.model.includesCodeElements.forEach((codeElement, index) => {
                if (codeElement.uri === this.model.includesRelationToRemove.uri && codeElement.versio === this.model.includesRelationToRemove.versio) {
                    this.model.includesCodeElements.splice(index, 1);
                }
            });
        } else if (this.model.levelsRelationToRemove && this.model.levelsRelationToRemove.uri !== "") {
            this.model.levelsWithCodeElements.forEach((codeElement, index) => {
                if (codeElement.uri === this.model.levelsRelationToRemove.uri && codeElement.versio === this.model.levelsRelationToRemove.versio) {
                    this.model.levelsWithCodeElements.splice(index, 1);
                }
            });
        }
        this.model.levelsRelationToRemove = null;
        this.model.includesRelationToRemove = null;
        this.model.withinRelationToRemove = null;
        this.model.modalInstance.close();
    }

    cancelconfirm() {
        this.model.levelsRelationToRemove = null;
        this.model.includesRelationToRemove = null;
        this.model.withinRelationToRemove = null;
        this.model.modalInstance.dismiss('cancel');
    }

    // Pagination

    // Get the filtered page count
    getNumberOfPages() {
        return this.cachedPageCount;
    }

    // Change the currentPage when the pageSize is changed.
    pageSizeChanged() {
        const topmostCodeElement = this.model.currentPage * this.oldValueForPageSize;
        this.model.currentPage = Math.floor(topmostCodeElement / this.model.pageSize);
        this.oldValueForPageSize = this.model.pageSize;
        this.updatePaginationPage();
    }

    sortOrderChanged(value) {
        if (value) {
            this.model.sortOrderSelection = value;
        }
        const selection = parseInt(this.model.sortOrderSelection);
        switch (selection) {
            case 1:
                this.model.sortOrderReversed = false;
                this.model.sortOrder = "value";
                break;
            case 2:
                this.model.sortOrderReversed = true;
                this.model.sortOrder = "value";
                break;
            case 3:
                this.model.sortOrderReversed = false;
                this.model.sortOrder = "name";
                break;
            case 4:
                this.model.sortOrderReversed = true;
                this.model.sortOrder = "name";
                break;

            default:
                break;
        }
        this.updatePaginationPage(true);
    }

    // When user changes the search string the page count changes and the current page must be adjusted
    filterChangedPageCount() {
        if (this.model.currentPage >= this.getNumberOfPages()) {
            this.model.currentPage = this.getNumberOfPages() - 1;
        }
        if (this.getNumberOfPages() !== 0 && this.model.currentPage < 0) {
            this.model.currentPage = 0;
        }
        this.updatePaginationPage();
    };

    changePage(i) {
        this.model.currentPage = i;
        this.updatePaginationPage();
    }

    incrementPage(i) {
        const newPageNumber = this.model.currentPage + i;
        if (newPageNumber > -1 && newPageNumber < this.getNumberOfPages()) {
            this.model.currentPage = newPageNumber;
            this.updatePaginationPage();
        }
    }

    updatePaginationPage(refreshPage) {
        if (refreshPage) {
            // Only do sorting when the model has changed, heavy operation
            this.refreshPage = false;
            this.model.shownCodeElements = this.$filter("naturalSort")(this.model.shownCodeElements, this.model.sortOrder, this.model.sortOrderReversed);
        }
        let results = this.model.shownCodeElements;
        results = results.filter((item) => this.search(item));
        this.cachedPageCount = Math.ceil(results.length / this.model.pageSize);
        results = results.splice(this.model.currentPage * this.model.pageSize, this.model.pageSize);
        this.cachedShownCodeElements = this.model.shownCodeElements;
        this.paginationPage = results;
    }

    // Pagination ends
}
