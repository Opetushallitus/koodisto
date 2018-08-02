import {getLanguageSpecificValueOrValidValue} from "../app";
import alertIcon from '../../img/alert-icon28x29.png';

export class CodesCreatorModel {
    constructor($location, RootCodes, $modal) {
        "ngInject";
        this.$location = $location;
        this.RootCodes = RootCodes;
        this.$modal = $modal;

        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.allCodes = [];
        this.onlyCodes = [];
        this.alerts = [];

    }

    init() {
        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.allCodes = [];
        this.onlyCodes = [];
        this.alerts = [];
        this.getCodes();
    }

    getCodes() {
        this.RootCodes.get({}, (result) => {
            this.allCodes = result;
            for (var i = 0; i < this.allCodes.length; i++) {
                if(!this.allCodes[i].shownName){
                    this.allCodes[i].shownName = getLanguageSpecificValueOrValidValue(this.allCodes[i].metadata, 'nimi', 'FI');
                }
                if (this.allCodes[i].koodistos) {
                    for (var j = 0; j < this.allCodes[i].koodistos.length; j++) {
                        if (!this.inCodesList(this.onlyCodes, this.allCodes[i].koodistos[j])) {
                            this.onlyCodes.push(this.allCodes[i].koodistos[j]);
                        }
                    }
                }
            }

        });
    }

    inCodesList(codesList, codesToFind) {
        for (var i = 0; i < codesList.length; i++) {
            if (codesList[i].koodistoUri === codesToFind.koodistoUri) {
                return true;
            }
        }
        return false;
    }

}

export class CodesCreatorController {
    constructor($scope, $location, $modal, $log, $filter, codesCreatorModel, NewCodes, treemodel, isModalController) {
        "ngInject";
        this.$scope = $scope;
        this.$location = $location;
        this.$modal = $modal;
        this.$log = $log;
        this.$filter = $filter;
        this.codesCreatorModel = codesCreatorModel;
        this.NewCodes = NewCodes;
        this.treemodel = treemodel;
        this.isModalController = isModalController;

        this.alertIcon = alertIcon;

        this.model = codesCreatorModel;
        this.errorMessage = $filter('i18n')('field.required');
        this.errorMessageAtLeastOneName = $filter('i18n')('field.required.at.least.one.name');
        this.errorMessageIfOtherInfoIsGiven = $filter('i18n')('field.required.if.other.info.is.given');

        if (!isModalController) {
            codesCreatorModel.init();
        }

    }

    closeAlert(index) {
        this.model.alerts.splice(index, 1);
    }

    redirectCancel() {
        this.$location.path("/");
    }

    cancel() {
        this.closeCancelConfirmModal();
        this.redirectCancel();
    }

    showCancelConfirmModal(formHasChanged) {
        if (formHasChanged) {
            this.model.cancelConfirmModal = this.$modal.open({
                // Included in createcodes.html
                templateUrl: 'confirmcancel.html',
                controller: 'codesCreatorController as codesCreatorModal',
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

    persistCodes() {
        const codes = {
            codesGroupUri: this.selectedCGoup,
            voimassaAlkuPvm: this.dActiveStart,
            voimassaLoppuPvm: this.dActiveEnd,
            omistaja: this.ownerName,
            organisaatioOid: this.organizationOid,
            metadata: []
        };
        if (this.namefi) {
            codes.metadata.push({
                kieli: 'FI',
                nimi: this.namefi,
                kuvaus: this.descriptionfi,
                kayttoohje: this.instructionsfi,
                kohdealue: this.targetareafi,
                kohdealueenOsaAlue: this.targetareapartfi,
                kasite: this.conceptfi,
                toimintaymparisto: this.operationalenvironmentfi,
                koodistonLahde: this.codessourcefi,
                tarkentaaKoodistoa: this.specifiescodesfi,
                huomioitavaKoodisto: this.totakenoticeoffi,
                sitovuustaso: this.validitylevelfi
            });
        }
        if (this.namesv) {
            codes.metadata.push({
                kieli: 'SV',
                nimi: this.namesv,
                kuvaus: this.descriptionsv,
                kayttoohje: this.instructionssv,
                kohdealue: this.targetareasv,
                kohdealueenOsaAlue: this.targetareapartsv,
                kasite: this.conceptsv,
                toimintaymparisto: this.operationalenvironmentsv,
                koodistonLahde: this.codessourcesv,
                tarkentaaKoodistoa: this.specifiescodessv,
                huomioitavaKoodisto: this.totakenoticeofsv,
                sitovuustaso: this.validitylevelsv
            });
        }
        if (this.nameen) {
            codes.metadata.push({
                kieli: 'EN',
                nimi: this.nameen,
                kuvaus: this.descriptionen,
                kayttoohje: this.instructionsen,
                kohdealue: this.targetareaen,
                kohdealueenOsaAlue: this.targetareaparten,
                kasite: this.concepten,
                toimintaymparisto: this.operationalenvironmenten,
                koodistonLahde: this.codessourceen,
                tarkentaaKoodistoa: this.specifiescodesen,
                huomioitavaKoodisto: this.totakenoticeofen,
                sitovuustaso: this.validitylevelen
            });
        }
        this.NewCodes.post({}, codes, (result) => {
            this.treemodel.refresh();
            this.$location.path("/koodisto/" + result.koodistoUri + "/" + result.versio).search({
                forceRefresh: true
            });
        }, (error) => {
            const alert = {
                type: 'danger',
                msg: jQuery.i18n.prop(error.data)
            };
            this.model.alerts.push(alert);
        });
    }

    setSameValue(name) {
        if (name === 'name' && !this.samename) {
            this.namesv = this.namefi;
            this.nameen = this.namefi;
        } else if (name === 'description' && !this.samedescription) {
            this.descriptionsv = this.descriptionfi;
            this.descriptionen = this.descriptionfi;
        } else if (name === 'instructions' && !this.sameinstructions) {
            this.instructionssv = this.instructionsfi;
            this.instructionsen = this.instructionsfi;
        } else if (name === 'targetarea' && !this.sametargetarea) {
            this.targetareasv = this.targetareafi;
            this.targetareaen = this.targetareafi;
        } else if (name === 'targetareapart' && !this.sametargetareapart) {
            this.targetareapartsv = this.targetareapartfi;
            this.targetareaparten = this.targetareapartfi;
        } else if (name === 'concept' && !this.sameconcept) {
            this.conceptsv = this.conceptfi;
            this.concepten = this.conceptfi;
        } else if (name === 'operationalenvironment' && !this.sameoperationalenvironment) {
            this.operationalenvironmentsv = this.operationalenvironmentfi;
            this.operationalenvironmenten = this.operationalenvironmentfi;
        } else if (name === 'codessource' && !this.samecodessource) {
            this.codessourcesv = this.codessourcefi;
            this.codessourceen = this.codessourcefi;
        } else if (name === 'specifiescodes' && !this.samespecifiescodes) {
            this.specifiescodessv = this.specifiescodesfi;
            this.specifiescodesen = this.specifiescodesfi;
        } else if (name === 'totakenoticeof' && !this.sametotakenoticeof) {
            this.totakenoticeofsv = this.totakenoticeoffi;
            this.totakenoticeofen = this.totakenoticeoffi;
        } else if (name === 'validitylevel' && !this.samevaliditylevel) {
            this.validitylevelsv = this.validitylevelfi;
            this.validitylevelen = this.validitylevelfi;
        }
    }

    open() {
        const modalInstance = this.$modal.open({
            // Included in organisaatioSelector.html
            templateUrl: 'organizationModalContent.html',
            controller: 'modalInstanceCtrl as modalInstance',
            resolve: {}
        });

        modalInstance.result.then((selectedItem) => {
            this.organizationOid = selectedItem.oid;
            this.organizationName = selectedItem.nimi['fi'] || selectedItem.nimi['sv'] || selectedItem.nimi['en'];
        }, () => {
            this.$log.info('Modal dismissed at: ' + new Date());
        });
    }


}
