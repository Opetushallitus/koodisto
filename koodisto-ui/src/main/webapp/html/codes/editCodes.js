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

    init(scope, codesUri, codesVersion) {
        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.allCodes = [];
        this.onlyCodes = [];
        this.organizations = [];
        this.alerts = [];
        this.getCodes(scope, codesUri, codesVersion);
        this.getAllCodes();
    }

    getCodes(scope, codesUri, codesVersion) {
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

            scope.loadingReady = true;
        });
    }

    extractAndPushRelatedCode(codes, list) {
        var languages = Object.keys(codes.nimi).map(function (languageCode) {
            return {kieli: languageCode, nimi: codes.nimi[languageCode]};
        });
        var ce = {};
        ce.uri = codes.codesUri;
        ce.name = getLanguageSpecificValueOrValidValue(languages, 'nimi', 'FI');
        ce.versio = codes.codesVersion;
        list.push(ce);
    }

    filterCodes() {
        for (var i = 0; i < this.allCodes.length; i++) {
            var koodistos = this.allCodes[i].koodistos;
            var temp = [];
            if (koodistos) {
                for (var j = 0; j < koodistos.length; j++) {
                    var koodisto = koodistos[j];
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
        $scope.model = codesEditorModel;
        $scope.codesUri = $routeParams.codesUri;
        $scope.codesVersion = $routeParams.codesVersion;
        $scope.errorMessage = $filter('i18n')('field.required');
        $scope.errorMessageAtLeastOneName = $filter('i18n')('field.required.at.least.one.name');
        $scope.errorMessageIfOtherInfoIsGiven = $filter('i18n')('field.required.if.other.info.is.given');

        if (!isModalController) {
            codesEditorModel.init($scope, $routeParams.codesUri, $scope.codesVersion);
        }

        $scope.closeAlert = function (index) {
            $scope.model.alerts.splice(index, 1);
        };

        $scope.redirectCancel = function () {
            $location.path("/koodisto/" + $scope.codesUri + "/" + $scope.codesVersion);
        };

        $scope.cancel = function () {
            $scope.closeCancelConfirmModal();
            $scope.redirectCancel();
        };

        $scope.showCancelConfirmModal = function (formHasChanged) {
            if (formHasChanged) {
                $scope.model.cancelConfirmModal = $modal.open({
                    templateUrl: 'confirmcancel.html',
                    controller: 'codesEditorController',
                    resolve: {
                        isModalController: function () {
                            return true;
                        }
                    }
                });
            } else {
                $scope.redirectCancel();
            }
        };

        $scope.closeCancelConfirmModal = function () {
            $scope.model.cancelConfirmModal.close();
        };

        $scope.submit = function () {
            $scope.persistCodes();
        };

        $scope.search = function (item) {

            if (!$scope.model.query || codesMatcher.nameOrTunnusMatchesSearch(item, $scope.model.query)) {
                item.open = true;
                return true;
            }
            return false;
        };


        $scope.persistCodes = function () {
            var codes = {
                koodistoUri: $scope.codesUri,
                voimassaAlkuPvm: $scope.model.codes.voimassaAlkuPvm,
                voimassaLoppuPvm: $scope.model.codes.voimassaLoppuPvm,
                omistaja: $scope.model.codes.omistaja,
                organisaatioOid: $scope.model.codes.organisaatioOid,
                versio: $scope.model.codes.versio,
                tila: $scope.model.codes.tila,
                version: $scope.model.codes.version,
                codesGroupUri: $scope.model.codes.codesGroupUri,
                metadata: [],
                withinCodes: $scope.changeToRelationCodes($scope.model.withinCodes),
                includesCodes: $scope.changeToRelationCodes($scope.model.includesCodes),
                levelsWithCodes: $scope.changeToRelationCodes($scope.model.levelsWithCodes)
            };
            if ($scope.model.namefi) {
                codes.metadata.push({
                    kieli: 'FI',
                    nimi: $scope.model.namefi,
                    kuvaus: $scope.model.descriptionfi,
                    kayttoohje: $scope.model.instructionsfi,
                    kohdealue: $scope.model.targetareafi,
                    kohdealueenOsaAlue: $scope.model.targetareapartfi,
                    kasite: $scope.model.conceptfi,
                    toimintaymparisto: $scope.model.operationalenvironmentfi,
                    koodistonLahde: $scope.model.codessourcefi,
                    tarkentaaKoodistoa: $scope.model.specifiescodesfi,
                    huomioitavaKoodisto: $scope.model.totakenoticeoffi,
                    sitovuustaso: $scope.model.validitylevelfi
                });
            }
            if ($scope.model.namesv) {
                codes.metadata.push({
                    kieli: 'SV',
                    nimi: $scope.model.namesv,
                    kuvaus: $scope.model.descriptionsv,
                    kayttoohje: $scope.model.instructionssv,
                    kohdealue: $scope.model.targetareasv,
                    kohdealueenOsaAlue: $scope.model.targetareapartsv,
                    kasite: $scope.model.conceptsv,
                    toimintaymparisto: $scope.model.operationalenvironmentsv,
                    koodistonLahde: $scope.model.codessourcesv,
                    tarkentaaKoodistoa: $scope.model.specifiescodessv,
                    huomioitavaKoodisto: $scope.model.totakenoticeofsv,
                    sitovuustaso: $scope.model.validitylevelsv
                });
            }
            if ($scope.model.nameen) {
                codes.metadata.push({
                    kieli: 'EN',
                    nimi: $scope.model.nameen,
                    kuvaus: $scope.model.descriptionen,
                    kayttoohje: $scope.model.instructionsen,
                    kohdealue: $scope.model.targetareaen,
                    kohdealueenOsaAlue: $scope.model.targetareaparten,
                    kasite: $scope.model.concepten,
                    toimintaymparisto: $scope.model.operationalenvironmenten,
                    koodistonLahde: $scope.model.codessourceen,
                    tarkentaaKoodistoa: $scope.model.specifiescodesen,
                    huomioitavaKoodisto: $scope.model.totakenoticeofen,
                    sitovuustaso: $scope.model.validitylevelen
                });
            }
            var codeVersionResponse = SaveCodes.put({}, codes);
            codeVersionResponse.$promise.then(function () {
                treemodel.refresh();
                $location.path("/koodisto/" + $scope.codesUri + "/" + codeVersionResponse.content).search({
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
                $scope.model.alerts.push(alert);
            });
        };

        $scope.changeToRelationCodes = function (listToBeChanged) {
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

        $scope.setSameValue = function (name) {
            if (name === 'name' && $scope.model.samename) {
                $scope.model.namesv = $scope.model.namefi;
                $scope.model.nameen = $scope.model.namefi;
            } else if (name === 'description' && $scope.model.samedescription) {
                $scope.model.descriptionsv = $scope.model.descriptionfi;
                $scope.model.descriptionen = $scope.model.descriptionfi;
            } else if (name === 'instructions' && $scope.model.sameinstructions) {
                $scope.model.instructionssv = $scope.model.instructionsfi;
                $scope.model.instructionsen = $scope.model.instructionsfi;
            } else if (name === 'targetarea' && $scope.model.sametargetarea) {
                $scope.model.targetareasv = $scope.model.targetareafi;
                $scope.model.targetareaen = $scope.model.targetareafi;
            } else if (name === 'targetareapart' && $scope.model.sametargetareapart) {
                $scope.model.targetareapartsv = $scope.model.targetareapartfi;
                $scope.model.targetareaparten = $scope.model.targetareapartfi;
            } else if (name === 'concept' && $scope.model.sameconcept) {
                $scope.model.conceptsv = $scope.model.conceptfi;
                $scope.model.concepten = $scope.model.conceptfi;
            } else if (name === 'operationalenvironment' && $scope.model.sameoperationalenvironment) {
                $scope.model.operationalenvironmentsv = $scope.model.operationalenvironmentfi;
                $scope.model.operationalenvironmenten = $scope.model.operationalenvironmentfi;
            } else if (name === 'codessource' && $scope.model.samecodessource) {
                $scope.model.codessourcesv = $scope.model.codessourcefi;
                $scope.model.codessourceen = $scope.model.codessourcefi;
            } else if (name === 'specifiescodes' && $scope.model.samespecifiescodes) {
                $scope.model.specifiescodessv = $scope.model.specifiescodesfi;
                $scope.model.specifiescodesen = $scope.model.specifiescodesfi;
            } else if (name === 'totakenoticeof' && $scope.model.sametotakenoticeof) {
                $scope.model.totakenoticeofsv = $scope.model.totakenoticeoffi;
                $scope.model.totakenoticeofen = $scope.model.totakenoticeoffi;
            } else if (name === 'validitylevel' && $scope.model.samevaliditylevel) {
                $scope.model.validitylevelsv = $scope.model.validitylevelfi;
                $scope.model.validitylevelen = $scope.model.validitylevelfi;
            }
        };

        $scope.createCodes = function (data) {
            var ce = {};
            ce.uri = data.koodistoUri;
            ce.name = getLanguageSpecificValueOrValidValue(data.latestKoodistoVersio.metadata, 'nimi', 'FI');
            return ce;
        };

        $scope.addToWithinCodes = function (data) {
            var ce = {};
            ce = $scope.createCodes(data);
            var found = false;
            $scope.model.withinCodes.forEach(function (codes, index) {
                if (codes.uri.indexOf(data.koodistoUri) !== -1) {
                    found = true;
                }
            });

            if (found === false) {
                $scope.model.withinCodes.push(ce);
            }
        };

        $scope.addToIncludesCodes = function (data) {
            var ce = {};
            ce = $scope.createCodes(data);
            var found = false;
            $scope.model.includesCodes.forEach(function (codes, index) {
                if (codes.uri === data.koodistoUri) {
                    found = true;
                }
            });

            if (found === false) {
                $scope.model.includesCodes.push(ce);
            }
        };
        $scope.addToLevelsWithCodes = function (data) {
            var ce = {};
            ce = $scope.createCodes(data);
            var found = false;
            $scope.model.levelsWithCodes.forEach(function (codes, index) {
                if (codes.uri.indexOf(data.koodistoUri) !== -1) {
                    found = true;
                }
            });

            if (found === false) {
                $scope.model.levelsWithCodes.push(ce);
            }
        };

        $scope.openChildren = function (data) {
            codesEditorModel.openChildren(data);
        };

        $scope.close = function (selectedCodes) {
            $scope.codesSelector = false;
            if (selectedCodes) {
                if ($scope.addToListName === 'withincodes') {
                    $scope.addToWithinCodes(selectedCodes);
                } else if ($scope.addToListName === 'includescodes') {
                    $scope.addToIncludesCodes(selectedCodes);
                } else if ($scope.addToListName === 'levelswithcodes') {
                    $scope.addToLevelsWithCodes(selectedCodes);
                }
            }
        };

        $scope.show = function (name) {
            $scope.addToListName = name;
            $scope.codesSelector = true;
        };

        $scope.open = function () {

            var modalInstance = $modal.open({
                templateUrl: 'organizationModalContent.html',
                controller: 'modalInstanceCtrl',
                resolve: {
                    isModalController: function () {
                        return true;
                    }
                }
            });

            modalInstance.result.then(function (selectedItem) {
                $scope.model.codes.organisaatioOid = selectedItem.oid;
                $scope.model.codes.organizationName = selectedItem.nimi['fi'] || selectedItem.nimi['sv'] || selectedItem.nimi['en'];
            }, function () {
                $log.info('Modal dismissed at: ' + new Date());
            });
        };

        $scope.okconfirm = function () {
            if ($scope.model.withinRelationToRemove && $scope.model.withinRelationToRemove.uri !== "") {

                $scope.model.withinCodes.forEach(function (codes, index) {
                    if (codes.uri.indexOf($scope.model.withinRelationToRemove.uri) !== -1) {
                        $scope.model.withinCodes.splice(index, 1);
                    }
                });

            } else if ($scope.model.includesRelationToRemove && $scope.model.includesRelationToRemove.uri !== "") {
                $scope.model.includesCodes.forEach(function (codes, index) {
                    if (codes.uri.indexOf($scope.model.includesRelationToRemove.uri) !== -1) {
                        $scope.model.includesCodes.splice(index, 1);
                    }
                });

            } else if ($scope.model.levelsRelationToRemove && $scope.model.levelsRelationToRemove.uri !== "") {
                $scope.model.levelsWithCodes.forEach(function (codes, index) {
                    if (codes.uri.indexOf($scope.model.levelsRelationToRemove.uri) !== -1) {
                        $scope.model.levelsWithCodes.splice(index, 1);
                    }
                });

            }
            $scope.model.levelsRelationToRemove = null;
            $scope.model.includesRelationToRemove = null;
            $scope.model.withinRelationToRemove = null;
        };

        $scope.removeFromWithinCodes = function (codes) {
            $scope.model.withinRelationToRemove = codes;
            $scope.okconfirm();
        };

        $scope.removeFromIncludesCodes = function (codes) {
            $scope.model.includesRelationToRemove = codes;
            $scope.okconfirm();
        };

        $scope.removeFromLevelsWithCodes = function (codes) {
            $scope.model.levelsRelationToRemove = codes;
            $scope.okconfirm();
        };

        $scope.cancelconfirm = function () {
            $scope.model.levelsRelationToRemove = null;
            $scope.model.includesRelationToRemove = null;
            $scope.model.withinRelationToRemove = null;
            $scope.model.modalInstance.dismiss('cancel');
        };

        $scope.isCodeLoading = loadingService.isLoading;
    }
}

