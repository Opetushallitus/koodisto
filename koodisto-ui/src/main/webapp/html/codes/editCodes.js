import {getLanguageSpecificValue, getLanguageSpecificValueOrValidValue, SERVICE_NAME} from "../app";

export class CodesEditorModel {

    constructor($location, RootCodes, Organizations, CodesByUriAndVersion, OrganizationByOid, CodesByUri, authService, $modal) {
        "ngInject";
        this.$location = $location;
        this.RootCodes = RootCodes;
        this.Organizations = Organizations;
        this.CodesByUriAndVersion = CodesByUriAndVersion;
        this.OrganizationByOid = OrganizationByOid;
        this.CodesByUri = CodesByUri;
        this.authService = authService;
        this.$modal = $modal;

        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.allCodes = [];
        this.onlyCodes = [];
        this.organizations = [];
        this.states = [{key:'PASSIIVINEN', value:'PASSIIVINEN'},{key:'LUONNOS', value:'LUONNOS'},{key:'HYVAKSYTTY',value:'HYVÄKSYTTY'}];
        this.alerts = [];

    }

    init(codesUri, codesVersion) {
        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.allCodes = [];
        this.onlyCodes = [];
        this.organizations = [];
        this.alerts = [];
        this.getCodes(codesUri, codesVersion);
        this.getAllCodes();
    }

    getCodes(codesUri, codesVersion) {
        this.CodesByUriAndVersion.get({codesUri: codesUri, codesVersion: codesVersion}, (result) => {
            this.codes = result;
            if (this.codes.tila && this.codes.tila==='HYVAKSYTTY') {
                this.states = [{key:'PASSIIVINEN', value:'PASSIIVINEN'},{key:'HYVAKSYTTY',value:'HYVÄKSYTTY'}];
            } else {
                this.states = [{key:'PASSIIVINEN', value:'PASSIIVINEN'},{key:'LUONNOS', value:'LUONNOS'},{key:'HYVAKSYTTY',value:'HYVÄKSYTTY'}];
            }

            this.namefi = getLanguageSpecificValue(result.metadata, 'nimi', 'FI');
            this.namesv = getLanguageSpecificValue(result.metadata, 'nimi', 'SV');
            this.nameen = getLanguageSpecificValue(result.metadata, 'nimi', 'EN');

            this.descriptionfi = getLanguageSpecificValue(result.metadata, 'kuvaus', 'FI');
            this.descriptionsv = getLanguageSpecificValue(result.metadata, 'kuvaus', 'SV');
            this.descriptionen = getLanguageSpecificValue(result.metadata, 'kuvaus', 'EN');

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

            this.samename = false;
            this.samedescription = false;
            this.sameinstructions = false;
            this.sametargetarea = false;
            this.sametargetareapart = false;
            this.sameconcept = false;
            this.sameoperationalenvironment = false;
            this.samecodessource = false;
            this.samespecifiescodes = false;
            this.sametotakenoticeof = false;
            this.samevaliditylevel = false;

            this.codes.withinCodes.forEach((codes) => {
                this.extractAndPushRelatedCode(codes, this.withinCodes);
            });
            this.codes.includesCodes.forEach((codes) => {
                this.extractAndPushRelatedCode(codes,this.includesCodes);
            });
            this.codes.levelsWithCodes.forEach((codes) => {
                this.extractAndPushRelatedCode(codes,this.levelsWithCodes);
            });

            this.OrganizationByOid.get({oid: this.codes.organisaatioOid}, (result2) => {
                this.codes.organizationName = result2.nimi['fi'] || result2.nimi['sv'] || result2.nimi['en'];
            });

            this.loadingReady = true;
        });
    }

    extractAndPushRelatedCode(codes, list) {
        const languages = Object.keys(codes.nimi).map(function (languageCode) {
            return {kieli: languageCode, nimi: codes.nimi[languageCode]};
        });
        const ce = {};
        ce.uri = codes.codesUri;
        ce.name = getLanguageSpecificValueOrValidValue(languages, 'nimi', 'FI');
        ce.versio = codes.codesVersion;
        list.push(ce);
    }

    filterCodes() {
        for (let i = 0; i < this.allCodes.length; i++) {
            let koodistos = this.allCodes[i].koodistos;
            const temp = [];
            if (koodistos) {
                for (let j = 0; j < koodistos.length; j++) {
                    const koodisto = koodistos[j];
                    // Vain ne koodistot näytetään, jotka ovat samassa organisaatiossa tämän kanssa
                    if (koodisto.organisaatioOid === this.codes.organisaatioOid) {
                        temp.push(koodisto);
                        if (!this.inCodesList(this.onlyCodes, koodisto)) {
                            this.onlyCodes.push(koodisto);
                        }
                    }
                }
                this.allCodes[i].koodistos = temp;
            }
        }
    }

    getPreferredNames() {
        for (var i = 0; i < this.allCodes.length; i++) {
            if (!this.allCodes[i].shownName) {
                this.allCodes[i].shownName = getLanguageSpecificValueOrValidValue(this.allCodes[i].metadata, 'nimi', 'FI');
            }
            if (this.allCodes[i].koodistos) {
                for (var j = 0; j < this.allCodes[i].koodistos.length; j++) {
                    if (!this.allCodes[i].koodistos[j].shownName) {
                        this.allCodes[i].koodistos[j].shownName = getLanguageSpecificValueOrValidValue(
                            this.allCodes[i].koodistos[j].latestKoodistoVersio.metadata, 'nimi', 'FI');
                    }
                }
            }
        }
    }

    getAllCodes() {
        this.RootCodes.get({}, (result) => {
            this.allCodes = result;
            this.getPreferredNames();
            // OVT-7496 skip codes filtering for OPH user
            this.authService.updateOph(SERVICE_NAME).then(function() {}, this.filterCodes);
        });
    }


    inCodesList(codesList,codesToFind) {
        for(var i=0; i < codesList.length; i++) {
            if (codesList[i].koodistoUri === codesToFind.koodistoUri) {
                return true;
            }
        }
        return false;
    }

    openChildren(data) {
        data.open = !data.open;
        if (data.open) {

            var iter = function(children){
                if(children) {
                    children.forEach(function(child){
                        child.open = true;

                    });
                }
            };
            iter(data.children);
        }
    }

}

export class CodesEditorController {
    constructor($scope, $location, $modal, $log, $routeParams, $filter, codesEditorModel, treemodel,
                codesMatcher, SaveCodes, isModalController, loadingService) {
        "ngInject";
        this.$scope = $scope;
        this.$location = $location;
        this.$modal = $modal;
        this.$log = $log;
        this.$routeParams = $routeParams;
        this.$filter = $filter;
        this.codesEditorModel = codesEditorModel;
        this.treemodel = treemodel;
        this.codesMatcher = codesMatcher;
        this.SaveCodes = SaveCodes;
        this.isModalController = isModalController;
        this.loadingService = loadingService;

        this.model = codesEditorModel;
        this.codesUri = $routeParams.codesUri;
        this.codesVersion = $routeParams.codesVersion;
        this.errorMessage = $filter('i18n')('field.required');
        this.errorMessageAtLeastOneName = $filter('i18n')('field.required.at.least.one.name');
        this.errorMessageIfOtherInfoIsGiven = $filter('i18n')('field.required.if.other.info.is.given');

        if (!isModalController) {
            codesEditorModel.init($routeParams.codesUri, this.codesVersion);
        }

        this.isCodeLoading = loadingService.isLoading;

    }
    closeAlert(index) {
        this.model.alerts.splice(index, 1);
    }

    redirectCancel() {
        this.$location.path("/koodisto/" + this.codesUri + "/" + this.codesVersion);
    };

    cancel() {
        this.closeCancelConfirmModal();
        this.redirectCancel();
    };

    showCancelConfirmModal(formHasChanged) {
        if (formHasChanged) {
            this.model.cancelConfirmModal = this.$modal.open({
                // Included in editcodes.html
                templateUrl: 'confirmcancel.html',
                controller: 'codesEditorController as codesEditorModal',
                resolve: {
                    isModalController: function () {
                        return true;
                    }
                }
            });
        } else {
            this.redirectCancel();
        }
    };

    closeCancelConfirmModal() {
        this.model.cancelConfirmModal.close();
    };

    submit() {
        this.persistCodes();
    };

    search(item) {

        if (!this.model.query || this.codesMatcher.nameOrTunnusMatchesSearch(item, this.model.query)) {
            item.open = true;
            return true;
        }
        return false;
    };


    persistCodes() {
        var codes = {
            koodistoUri: this.codesUri,
            voimassaAlkuPvm: this.model.codes.voimassaAlkuPvm,
            voimassaLoppuPvm: this.model.codes.voimassaLoppuPvm,
            omistaja: this.model.codes.omistaja,
            organisaatioOid: this.model.codes.organisaatioOid,
            versio: this.model.codes.versio,
            tila: this.model.codes.tila,
            version: this.model.codes.version,
            codesGroupUri: this.model.codes.codesGroupUri,
            metadata: [],
            withinCodes: this.changeToRelationCodes(this.model.withinCodes),
            includesCodes: this.changeToRelationCodes(this.model.includesCodes),
            levelsWithCodes: this.changeToRelationCodes(this.model.levelsWithCodes)
        };
        if (this.model.namefi) {
            codes.metadata.push({
                kieli: 'FI',
                nimi: this.model.namefi,
                kuvaus: this.model.descriptionfi,
                kayttoohje: this.model.instructionsfi,
                kohdealue: this.model.targetareafi,
                kohdealueenOsaAlue: this.model.targetareapartfi,
                kasite: this.model.conceptfi,
                toimintaymparisto: this.model.operationalenvironmentfi,
                koodistonLahde: this.model.codessourcefi,
                tarkentaaKoodistoa: this.model.specifiescodesfi,
                huomioitavaKoodisto: this.model.totakenoticeoffi,
                sitovuustaso: this.model.validitylevelfi
            });
        }
        if (this.model.namesv) {
            codes.metadata.push({
                kieli: 'SV',
                nimi: this.model.namesv,
                kuvaus: this.model.descriptionsv,
                kayttoohje: this.model.instructionssv,
                kohdealue: this.model.targetareasv,
                kohdealueenOsaAlue: this.model.targetareapartsv,
                kasite: this.model.conceptsv,
                toimintaymparisto: this.model.operationalenvironmentsv,
                koodistonLahde: this.model.codessourcesv,
                tarkentaaKoodistoa: this.model.specifiescodessv,
                huomioitavaKoodisto: this.model.totakenoticeofsv,
                sitovuustaso: this.model.validitylevelsv
            });
        }
        if (this.model.nameen) {
            codes.metadata.push({
                kieli: 'EN',
                nimi: this.model.nameen,
                kuvaus: this.model.descriptionen,
                kayttoohje: this.model.instructionsen,
                kohdealue: this.model.targetareaen,
                kohdealueenOsaAlue: this.model.targetareaparten,
                kasite: this.model.concepten,
                toimintaymparisto: this.model.operationalenvironmenten,
                koodistonLahde: this.model.codessourceen,
                tarkentaaKoodistoa: this.model.specifiescodesen,
                huomioitavaKoodisto: this.model.totakenoticeofen,
                sitovuustaso: this.model.validitylevelen
            });
        }
        var codeVersionResponse = this.SaveCodes.put({}, codes);
        codeVersionResponse.$promise.then(function () {
            this.treemodel.refresh();
            this.$location.path("/koodisto/" + this.codesUri + "/" + codeVersionResponse.content).search({
                forceRefresh: true
            });
        }, function (error) {
            var type = 'danger';
            if (error.data === "error.codes.has.no.codeelements") {
                type = 'info';
            }
            var message = jQuery.i18n.prop(error.data);
            if (error.status === 504) {
                message = jQuery.i18n.prop('error.save.timeout');
            }
            var alert = {
                type: type,
                msg: message
            };
            this.model.alerts.push(alert);
        });
    };

    changeToRelationCodes(listToBeChanged) {
        result = [];
        listToBeChanged.forEach(function (ce) {
            dt = {};
            dt.codesUri = ce.uri;
            dt.codesVersion = 1;
            dt.passive = ce.passive ? ce.passive : false;
            result.push(dt);
        });
        return result;
    };

    setSameValue(name) {
        if (name === 'name' && this.model.samename) {
            this.model.namesv = this.model.namefi;
            this.model.nameen = this.model.namefi;
        } else if (name === 'description' && this.model.samedescription) {
            this.model.descriptionsv = this.model.descriptionfi;
            this.model.descriptionen = this.model.descriptionfi;
        } else if (name === 'instructions' && this.model.sameinstructions) {
            this.model.instructionssv = this.model.instructionsfi;
            this.model.instructionsen = this.model.instructionsfi;
        } else if (name === 'targetarea' && this.model.sametargetarea) {
            this.model.targetareasv = this.model.targetareafi;
            this.model.targetareaen = this.model.targetareafi;
        } else if (name === 'targetareapart' && this.model.sametargetareapart) {
            this.model.targetareapartsv = this.model.targetareapartfi;
            this.model.targetareaparten = this.model.targetareapartfi;
        } else if (name === 'concept' && this.model.sameconcept) {
            this.model.conceptsv = this.model.conceptfi;
            this.model.concepten = this.model.conceptfi;
        } else if (name === 'operationalenvironment' && this.model.sameoperationalenvironment) {
            this.model.operationalenvironmentsv = this.model.operationalenvironmentfi;
            this.model.operationalenvironmenten = this.model.operationalenvironmentfi;
        } else if (name === 'codessource' && this.model.samecodessource) {
            this.model.codessourcesv = this.model.codessourcefi;
            this.model.codessourceen = this.model.codessourcefi;
        } else if (name === 'specifiescodes' && this.model.samespecifiescodes) {
            this.model.specifiescodessv = this.model.specifiescodesfi;
            this.model.specifiescodesen = this.model.specifiescodesfi;
        } else if (name === 'totakenoticeof' && this.model.sametotakenoticeof) {
            this.model.totakenoticeofsv = this.model.totakenoticeoffi;
            this.model.totakenoticeofen = this.model.totakenoticeoffi;
        } else if (name === 'validitylevel' && this.model.samevaliditylevel) {
            this.model.validitylevelsv = this.model.validitylevelfi;
            this.model.validitylevelen = this.model.validitylevelfi;
        }
    };

    createCodes(data) {
        var ce = {};
        ce.uri = data.koodistoUri;
        ce.name = getLanguageSpecificValueOrValidValue(data.latestKoodistoVersio.metadata, 'nimi', 'FI');
        return ce;
    };

    addToWithinCodes(data) {
        var ce = {};
        ce = this.createCodes(data);
        var found = false;
        this.model.withinCodes.forEach(function (codes, index) {
            if (codes.uri.indexOf(data.koodistoUri) !== -1) {
                found = true;
            }
        });

        if (found === false) {
            this.model.withinCodes.push(ce);
        }
    };

    addToIncludesCodes(data) {
        var ce = {};
        ce = this.createCodes(data);
        var found = false;
        this.model.includesCodes.forEach(function (codes, index) {
            if (codes.uri === data.koodistoUri) {
                found = true;
            }
        });

        if (found === false) {
            this.model.includesCodes.push(ce);
        }
    };
    addToLevelsWithCodes(data) {
        var ce = {};
        ce = this.createCodes(data);
        var found = false;
        this.model.levelsWithCodes.forEach(function (codes, index) {
            if (codes.uri.indexOf(data.koodistoUri) !== -1) {
                found = true;
            }
        });

        if (found === false) {
            this.model.levelsWithCodes.push(ce);
        }
    };

    openChildren(data) {
        this.codesEditorModel.openChildren(data);
    };

    close(selectedCodes) {
        this.codesSelector = false;
        if (selectedCodes) {
            if (this.addToListName === 'withincodes') {
                this.addToWithinCodes(selectedCodes);
            } else if (this.addToListName === 'includescodes') {
                this.addToIncludesCodes(selectedCodes);
            } else if (this.addToListName === 'levelswithcodes') {
                this.addToLevelsWithCodes(selectedCodes);
            }
        }
    };

    show(name) {
        this.addToListName = name;
        this.codesSelector = true;
    };

    open() {
        const modalInstance = this.$modal.open({
            // Included in organisaatioSelector.html
            templateUrl: 'organizationModalContent.html',
            controller: 'modalInstanceCtrl as modalInstance',
            resolve: {
                isModalController: function () {
                    return true;
                }
            }
        });

        modalInstance.result.then((selectedItem) => {
            this.model.codes.organisaatioOid = selectedItem.oid;
            this.model.codes.organizationName = selectedItem.nimi['fi'] || selectedItem.nimi['sv'] || selectedItem.nimi['en'];
        }, () => {
            this.$log.info('Modal dismissed at: ' + new Date());
        });
    };

    okconfirm() {
        if (this.model.withinRelationToRemove && this.model.withinRelationToRemove.uri !== "") {

            this.model.withinCodes.forEach(function (codes, index) {
                if (codes.uri.indexOf(this.model.withinRelationToRemove.uri) !== -1) {
                    this.model.withinCodes.splice(index, 1);
                }
            });

        } else if (this.model.includesRelationToRemove && this.model.includesRelationToRemove.uri !== "") {
            this.model.includesCodes.forEach(function (codes, index) {
                if (codes.uri.indexOf(this.model.includesRelationToRemove.uri) !== -1) {
                    this.model.includesCodes.splice(index, 1);
                }
            });

        } else if (this.model.levelsRelationToRemove && this.model.levelsRelationToRemove.uri !== "") {
            this.model.levelsWithCodes.forEach(function (codes, index) {
                if (codes.uri.indexOf(this.model.levelsRelationToRemove.uri) !== -1) {
                    this.model.levelsWithCodes.splice(index, 1);
                }
            });

        }
        this.model.levelsRelationToRemove = null;
        this.model.includesRelationToRemove = null;
        this.model.withinRelationToRemove = null;
    };

    removeFromWithinCodes(codes) {
        this.model.withinRelationToRemove = codes;
        this.okconfirm();
    };

    removeFromIncludesCodes(codes) {
        this.model.includesRelationToRemove = codes;
        this.okconfirm();
    };

    removeFromLevelsWithCodes(codes) {
        this.model.levelsRelationToRemove = codes;
        this.okconfirm();
    };

    cancelconfirm() {
        this.model.levelsRelationToRemove = null;
        this.model.includesRelationToRemove = null;
        this.model.withinRelationToRemove = null;
        this.model.modalInstance.dismiss('cancel');
    };
}

