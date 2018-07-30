export class CodeElementCreatorModel {
    constructor() {
        "ngInject";
        this.alerts = [];
    }

    init() {
        this.alerts = [];
    }
}

export class CodeElementCreatorController {
    constructor($scope, $location, $routeParams, $filter, $modal,  codeElementCreatorModel, NewCodeElement, isModalController) {
        "ngInject";
        this.$scope = $scope;
        this.$location = $location;
        this.$routeParams = $routeParams;
        this.$filter = $filter;
        this.$modal = $modal;
        this.codeElementCreatorModel = codeElementCreatorModel;
        this.NewCodeElement = NewCodeElement;
        this.isModalController = isModalController;

        this.model = codeElementCreatorModel;
        this.codesUri = $routeParams.codesUri;
        this.codesVersion = $routeParams.codesVersion;
        this.errorMessage = $filter('i18n')('field.required');
        this.errorMessageAtLeastOneName = $filter('i18n')('field.required.at.least.one.name');
        this.errorMessageIfOtherInfoIsGiven = $filter('i18n')('field.required.if.other.info.is.given');

        if (!isModalController) {
            codeElementCreatorModel.init();
        }
    }

    closeAlert(index) {
        this.model.alerts.splice(index, 1);
    }

    redirectCancel() {
        this.$location.path("/koodisto/"+this.codesUri+"/"+this.codesVersion);
    }

    cancel() {
        this.closeCancelConfirmModal();
        this.redirectCancel();
    }


    showCancelConfirmModal(formHasChanged) {
        if (formHasChanged) {
            this.model.cancelConfirmModal = this.$modal.open({
                // Included in createcodeelement.html
                templateUrl: 'confirmcancel.html',
                controller: 'codeElementCreatorController as codeElementCreatorModal',
                resolve: {
                    isModalController: function() {
                        return true;
                    }
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
        const codeelement = {
            voimassaAlkuPvm: this.dActiveStart,
            voimassaLoppuPvm: this.dActiveEnd,
            koodiArvo: this.codeValue,
            metadata: []
        };
        if (this.namefi){
            codeelement.metadata.push({
                kieli: 'FI',
                nimi: this.namefi,
                kuvaus: this.descriptionfi,
                lyhytNimi: this.shortnamefi,
                kayttoohje: this.instructionsfi,
                kasite: this.conceptfi,
                huomioitavaKoodi: this.totakenoticeoffi,
                sisaltaaMerkityksen: this.containssignificancefi,
                eiSisallaMerkitysta: this.doesnotcontainsignificancefi,
                sisaltaaKoodiston: this.containscodesfi
            });
        }
        if (this.namesv) {
            codeelement.metadata.push({
                kieli: 'SV',
                nimi: this.namesv,
                kuvaus: this.descriptionsv,
                lyhytNimi: this.shortnamesv,
                kayttoohje: this.instructionssv,
                kasite: this.conceptsv,
                huomioitavaKoodi: this.totakenoticeofsv,
                sisaltaaMerkityksen: this.containssignificancesv,
                eiSisallaMerkitysta: this.doesnotcontainsignificancesv,
                sisaltaaKoodiston: this.containscodessv
            });
        }
        if (this.nameen) {
            codeelement.metadata.push({
                kieli: 'EN',
                nimi: this.nameen,
                kuvaus: this.descriptionen,
                lyhytNimi: this.shortnameen,
                kayttoohje: this.instructionsen,
                kasite: this.concepten,
                huomioitavaKoodi: this.totakenoticeofen,
                sisaltaaMerkityksen: this.containssignificanceen,
                eiSisallaMerkitysta: this.doesnotcontainsignificanceen,
                sisaltaaKoodiston: this.containscodesen
            });
        }
        this.NewCodeElement.post({codesUri: this.codesUri}, codeelement, (result) => {
            this.$location.path("/koodi/"+result.koodiUri+"/"+result.versio).search({edited: true});
        }, function(error) {
            var alert = { type: 'danger', msg: jQuery.i18n.prop(error.data) };
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
        } else if (name === 'shortname' && !this.sameshortname) {
            this.shortnamesv = this.shortnamefi;
            this.shortnameen = this.shortnamefi;
        } else if (name === 'instructions' && !this.sameinstructions) {
            this.instructionssv = this.instructionsfi;
            this.instructionsen = this.instructionsfi;
        } else if (name === 'concept' && !this.sameconcept) {
            this.conceptsv = this.conceptfi;
            this.concepten = this.conceptfi;
        } else if (name === 'totakenoticeof' && !this.sametotakenoticeof) {
            this.totakenoticeofsv = this.totakenoticeoffi;
            this.totakenoticeofen = this.totakenoticeoffi;
        } else if (name === 'containssignificance' && !this.samecontainssignificance) {
            this.containssignificancesv = this.containssignificancefi;
            this.containssignificanceen = this.containssignificancefi;
        } else if (name === 'doesnotcontainsignificance' && !this.samedoesnotcontainsignificance) {
            this.doesnotcontainsignificancesv = this.doesnotcontainsignificancefi;
            this.doesnotcontainsignificanceen = this.doesnotcontainsignificancefi;
        } else if (name === 'containscodes' && !this.samecontainscodes) {
            this.containscodessv = this.containscodesfi;
            this.containscodesen = this.containscodesfi;
        }
    }

}
