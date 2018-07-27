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
        $scope.model = codeElementCreatorModel;
        $scope.codesUri = $routeParams.codesUri;
        $scope.codesVersion = $routeParams.codesVersion;
        $scope.errorMessage = $filter('i18n')('field.required');
        $scope.errorMessageAtLeastOneName = $filter('i18n')('field.required.at.least.one.name');
        $scope.errorMessageIfOtherInfoIsGiven = $filter('i18n')('field.required.if.other.info.is.given');

        if(!isModalController){
            codeElementCreatorModel.init();
        }

        $scope.closeAlert = function(index) {
            $scope.model.alerts.splice(index, 1);
        };

        $scope.redirectCancel = function() {
            $location.path("/koodisto/"+$scope.codesUri+"/"+$scope.codesVersion);
        };

        $scope.cancel = function() {
            $scope.closeCancelConfirmModal();
            $scope.redirectCancel();
        };


        $scope.showCancelConfirmModal = function(formHasChanged) {
            if (formHasChanged) {
                $scope.model.cancelConfirmModal = $modal.open({
                    templateUrl : 'confirmcancel.html',
                    controller : 'codeElementCreatorController',
                    resolve : {
                        isModalController : function() {
                            return true;
                        }
                    }
                });
            } else {
                $scope.redirectCancel();
            }
        };

        $scope.closeCancelConfirmModal = function() {
            $scope.model.cancelConfirmModal.close();
        };

        $scope.submit = function() {
            $scope.persistCodes();
        };

        $scope.persistCodes = function() {
            var codeelement = {
                voimassaAlkuPvm: $scope.dActiveStart,
                voimassaLoppuPvm: $scope.dActiveEnd,
                koodiArvo: $scope.codeValue,
                metadata : []
            };
            if ($scope.namefi){
                codeelement.metadata.push({
                    kieli: 'FI',
                    nimi: $scope.namefi,
                    kuvaus: $scope.descriptionfi,
                    lyhytNimi: $scope.shortnamefi,
                    kayttoohje: $scope.instructionsfi,
                    kasite: $scope.conceptfi,
                    huomioitavaKoodi: $scope.totakenoticeoffi,
                    sisaltaaMerkityksen: $scope.containssignificancefi,
                    eiSisallaMerkitysta: $scope.doesnotcontainsignificancefi,
                    sisaltaaKoodiston: $scope.containscodesfi
                });
            }
            if ($scope.namesv) {
                codeelement.metadata.push({
                    kieli: 'SV',
                    nimi: $scope.namesv,
                    kuvaus: $scope.descriptionsv,
                    lyhytNimi: $scope.shortnamesv,
                    kayttoohje: $scope.instructionssv,
                    kasite: $scope.conceptsv,
                    huomioitavaKoodi: $scope.totakenoticeofsv,
                    sisaltaaMerkityksen: $scope.containssignificancesv,
                    eiSisallaMerkitysta: $scope.doesnotcontainsignificancesv,
                    sisaltaaKoodiston: $scope.containscodessv
                });
            }
            if ($scope.nameen) {
                codeelement.metadata.push({
                    kieli: 'EN',
                    nimi: $scope.nameen,
                    kuvaus: $scope.descriptionen,
                    lyhytNimi: $scope.shortnameen,
                    kayttoohje: $scope.instructionsen,
                    kasite: $scope.concepten,
                    huomioitavaKoodi: $scope.totakenoticeofen,
                    sisaltaaMerkityksen: $scope.containssignificanceen,
                    eiSisallaMerkitysta: $scope.doesnotcontainsignificanceen,
                    sisaltaaKoodiston: $scope.containscodesen
                });
            }
            NewCodeElement.post({codesUri: $scope.codesUri}, codeelement, function(result) {
                $location.path("/koodi/"+result.koodiUri+"/"+result.versio).search({edited: true});
            }, function(error) {
                var alert = { type: 'danger', msg: jQuery.i18n.prop(error.data) };
                $scope.model.alerts.push(alert);
            });
        };

        $scope.setSameValue = function(name) {
            if (name === 'name' && !$scope.samename) {
                $scope.namesv = $scope.namefi;
                $scope.nameen = $scope.namefi;
            } else if (name === 'description' && !$scope.samedescription) {
                $scope.descriptionsv = $scope.descriptionfi;
                $scope.descriptionen = $scope.descriptionfi;
            } else if (name === 'shortname' && !$scope.sameshortname) {
                $scope.shortnamesv = $scope.shortnamefi;
                $scope.shortnameen = $scope.shortnamefi;
            } else if (name === 'instructions' && !$scope.sameinstructions) {
                $scope.instructionssv = $scope.instructionsfi;
                $scope.instructionsen = $scope.instructionsfi;
            } else if (name === 'concept' && !$scope.sameconcept) {
                $scope.conceptsv = $scope.conceptfi;
                $scope.concepten = $scope.conceptfi;
            } else if (name === 'totakenoticeof' && !$scope.sametotakenoticeof) {
                $scope.totakenoticeofsv = $scope.totakenoticeoffi;
                $scope.totakenoticeofen = $scope.totakenoticeoffi;
            } else if (name === 'containssignificance' && !$scope.samecontainssignificance) {
                $scope.containssignificancesv = $scope.containssignificancefi;
                $scope.containssignificanceen = $scope.containssignificancefi;
            } else if (name === 'doesnotcontainsignificance' && !$scope.samedoesnotcontainsignificance) {
                $scope.doesnotcontainsignificancesv = $scope.doesnotcontainsignificancefi;
                $scope.doesnotcontainsignificanceen = $scope.doesnotcontainsignificancefi;
            } else if (name === 'containscodes' && !$scope.samecontainscodes) {
                $scope.containscodessv = $scope.containscodesfi;
                $scope.containscodesen = $scope.containscodesfi;
            }
        };

    }
}
