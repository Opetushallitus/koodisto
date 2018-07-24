import angular from 'angular';
import {getLanguageSpecificValue, getLanguageSpecificValueOrValidValue} from "../app";

const app = angular.module('koodisto');

app.factory('CodesEditorModel', function($location, RootCodes, Organizations, CodesByUriAndVersion, OrganizationByOid, CodesByUri, AuthService, $modal) {
    var model;
    model = new function() {
        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.allCodes = [];
        this.onlyCodes = [];
        this.organizations = [];
        this.states = [{key:'PASSIIVINEN', value:'PASSIIVINEN'},{key:'LUONNOS', value:'LUONNOS'},{key:'HYVAKSYTTY',value:'HYVÄKSYTTY'}];
        this.alerts = [];

        this.init = function(scope, codesUri, codesVersion) {
            this.withinCodes = [];
            this.includesCodes = [];
            this.levelsWithCodes = [];
            this.allCodes = [];
            this.onlyCodes = [];
            this.organizations = [];
            this.alerts = [];
            model.getCodes(scope, codesUri, codesVersion);
            model.getAllCodes();
        };

        this.getCodes = function(scope, codesUri, codesVersion) {
            CodesByUriAndVersion.get({codesUri: codesUri, codesVersion: codesVersion}, function (result) {
                model.codes = result;
                if (model.codes.tila && model.codes.tila==='HYVAKSYTTY') {
                    model.states = [{key:'PASSIIVINEN', value:'PASSIIVINEN'},{key:'HYVAKSYTTY',value:'HYVÄKSYTTY'}];
                } else {
                    model.states = [{key:'PASSIIVINEN', value:'PASSIIVINEN'},{key:'LUONNOS', value:'LUONNOS'},{key:'HYVAKSYTTY',value:'HYVÄKSYTTY'}];
                }

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

                model.samename = false;
                model.samedescription = false;
                model.sameinstructions = false;
                model.sametargetarea = false;
                model.sametargetareapart = false;
                model.sameconcept = false;
                model.sameoperationalenvironment = false;
                model.samecodessource = false;
                model.samespecifiescodes = false;
                model.sametotakenoticeof = false;
                model.samevaliditylevel = false;

                model.codes.withinCodes.forEach(function(codes) {
                    model.extractAndPushRelatedCode(codes, model.withinCodes);
                });
                model.codes.includesCodes.forEach(function(codes) {
                    model.extractAndPushRelatedCode(codes,model.includesCodes);
                });
                model.codes.levelsWithCodes.forEach(function(codes){
                    model.extractAndPushRelatedCode(codes,model.levelsWithCodes);
                });

                OrganizationByOid.get({oid: model.codes.organisaatioOid}, function (result2) {
                    model.codes.organizationName = result2.nimi['fi'] || result2.nimi['sv'] || result2.nimi['en'];
                });
                
                scope.loadingReady = true;
            });
        };

        this.extractAndPushRelatedCode = function(codes, list) {
            var languages = Object.keys(codes.nimi).map(function (languageCode) {
                return {kieli: languageCode, nimi: codes.nimi[languageCode]};
            });
            var ce = {};
            ce.uri = codes.codesUri;
            ce.name = getLanguageSpecificValueOrValidValue(languages, 'nimi', 'FI');
            ce.versio = codes.codesVersion;
            list.push(ce);
        };

        this.filterCodes = function() {
            for (var i = 0; i < model.allCodes.length; i++) {
                var koodistos = model.allCodes[i].koodistos;
                var temp = [];
                if (koodistos) {
                    for (var j = 0; j < koodistos.length; j++) {
                        var koodisto = koodistos[j];
                        // Vain ne koodistot näytetään, jotka ovat samassa organisaatiossa tämän kanssa
                        if (koodisto.organisaatioOid === model.codes.organisaatioOid) {
                            temp.push(koodisto);
                            if (!model.inCodesList(model.onlyCodes, koodisto)) {
                                model.onlyCodes.push(koodisto);
                            }
                        }
                    }
                    model.allCodes[i].koodistos = temp;
                }
            }
        };

        this.getPreferredNames = function() {
            for (var i = 0; i < model.allCodes.length; i++) {
                if (!model.allCodes[i].shownName) {
                    model.allCodes[i].shownName = getLanguageSpecificValueOrValidValue(model.allCodes[i].metadata, 'nimi', 'FI');
                }
                if (model.allCodes[i].koodistos) {
                    for (var j = 0; j < model.allCodes[i].koodistos.length; j++) {
                        if (!model.allCodes[i].koodistos[j].shownName) {
                            model.allCodes[i].koodistos[j].shownName = getLanguageSpecificValueOrValidValue(
                                    model.allCodes[i].koodistos[j].latestKoodistoVersio.metadata, 'nimi', 'FI');
                        }
                    }
                }
            }
        };

        this.getAllCodes = function() {
            RootCodes.get({}, function(result) {
                model.allCodes = result;
                model.getPreferredNames();
                // OVT-7496 skip codes filtering for OPH user
                AuthService.updateOph(SERVICE_NAME).then(function() {}, model.filterCodes);
            });
        };


        this.inCodesList = function(codesList,codesToFind) {
            for(var i=0; i < codesList.length; i++) {
                if (codesList[i].koodistoUri === codesToFind.koodistoUri) {
                    return true;
                }
            }
            return false;
        };

        this.openChildren = function(data) {
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
        };

    };


    return model;
});

app.controller('CodesEditorController', function ($scope, $location, $modal, $log, $routeParams, $filter, CodesEditorModel, Treemodel,
                               CodesMatcher, SaveCodes, isModalController, loadingService) {
    $scope.model = CodesEditorModel;
    $scope.codesUri = $routeParams.codesUri;
    $scope.codesVersion = $routeParams.codesVersion;
    $scope.errorMessage = $filter('i18n')('field.required');
    $scope.errorMessageAtLeastOneName = $filter('i18n')('field.required.at.least.one.name');
    $scope.errorMessageIfOtherInfoIsGiven = $filter('i18n')('field.required.if.other.info.is.given');
    
    if (!isModalController) {
        CodesEditorModel.init($scope,$routeParams.codesUri, $scope.codesVersion);
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
                controller : 'CodesEditorController',
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

    $scope.search = function (item){
    
        if (!$scope.model.query || CodesMatcher.nameOrTunnusMatchesSearch(item, $scope.model.query)) {
            item.open = true;
            return true;
        }
        return false;
    };


    $scope.persistCodes = function() {
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
            metadata : [],
            withinCodes : $scope.changeToRelationCodes($scope.model.withinCodes),
            includesCodes : $scope.changeToRelationCodes($scope.model.includesCodes),
            levelsWithCodes : $scope.changeToRelationCodes($scope.model.levelsWithCodes)
        };
        if ($scope.model.namefi) {
            codes.metadata.push({
                kieli : 'FI',
                nimi : $scope.model.namefi,
                kuvaus : $scope.model.descriptionfi,
                kayttoohje : $scope.model.instructionsfi,
                kohdealue : $scope.model.targetareafi,
                kohdealueenOsaAlue : $scope.model.targetareapartfi,
                kasite : $scope.model.conceptfi,
                toimintaymparisto : $scope.model.operationalenvironmentfi,
                koodistonLahde : $scope.model.codessourcefi,
                tarkentaaKoodistoa : $scope.model.specifiescodesfi,
                huomioitavaKoodisto : $scope.model.totakenoticeoffi,
                sitovuustaso : $scope.model.validitylevelfi
            });
        }
        if ($scope.model.namesv) {
            codes.metadata.push({
                kieli : 'SV',
                nimi : $scope.model.namesv,
                kuvaus : $scope.model.descriptionsv,
                kayttoohje : $scope.model.instructionssv,
                kohdealue : $scope.model.targetareasv,
                kohdealueenOsaAlue : $scope.model.targetareapartsv,
                kasite : $scope.model.conceptsv,
                toimintaymparisto : $scope.model.operationalenvironmentsv,
                koodistonLahde : $scope.model.codessourcesv,
                tarkentaaKoodistoa : $scope.model.specifiescodessv,
                huomioitavaKoodisto : $scope.model.totakenoticeofsv,
                sitovuustaso : $scope.model.validitylevelsv
            });
        }
        if ($scope.model.nameen) {
            codes.metadata.push({
                kieli : 'EN',
                nimi : $scope.model.nameen,
                kuvaus : $scope.model.descriptionen,
                kayttoohje : $scope.model.instructionsen,
                kohdealue : $scope.model.targetareaen,
                kohdealueenOsaAlue : $scope.model.targetareaparten,
                kasite : $scope.model.concepten,
                toimintaymparisto : $scope.model.operationalenvironmenten,
                koodistonLahde : $scope.model.codessourceen,
                tarkentaaKoodistoa : $scope.model.specifiescodesen,
                huomioitavaKoodisto : $scope.model.totakenoticeofen,
                sitovuustaso : $scope.model.validitylevelen
            });
        }
        var codeVersionResponse = SaveCodes.put({}, codes);
        codeVersionResponse.$promise.then(function() {
            Treemodel.refresh();
            $location.path("/koodisto/" + $scope.codesUri + "/" + codeVersionResponse.content).search({
                forceRefresh : true
            });
        }, function(error) {
            var type = 'danger';
            if (error.data === "error.codes.has.no.codeelements") {
                type = 'info';
            }
            var message = jQuery.i18n.prop(error.data);
            if (error.status === 504) {
                message = jQuery.i18n.prop('error.save.timeout');
            }
            var alert = {
                type : type,
                msg : message
            };
            $scope.model.alerts.push(alert);
        });
    };

    $scope.changeToRelationCodes = function(listToBeChanged) {
        result = [];
        listToBeChanged.forEach(function(ce) {
            dt = {};
            dt.codesUri = ce.uri;
            dt.codesVersion = 1;
            dt.passive = ce.passive ? ce.passive : false;
            result.push(dt);
        });
        return result;
    };

    $scope.setSameValue = function(name) {
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

    $scope.createCodes = function(data) {
        var ce = {};
        ce.uri = data.koodistoUri;
        ce.name = getLanguageSpecificValueOrValidValue(data.latestKoodistoVersio.metadata, 'nimi', 'FI');
        return ce;
    };

    $scope.addToWithinCodes = function(data) {
        var ce = {};
        ce = $scope.createCodes(data);
        var found = false;
        $scope.model.withinCodes.forEach(function(codes, index) {
            if (codes.uri.indexOf(data.koodistoUri) !== -1) {
                found = true;
            }
        });

        if (found === false) {
            $scope.model.withinCodes.push(ce);
        }
    };

    $scope.addToIncludesCodes = function(data) {
        var ce = {};
        ce = $scope.createCodes(data);
        var found = false;
        $scope.model.includesCodes.forEach(function(codes, index) {
            if (codes.uri===data.koodistoUri) {
                found = true;
            }
        });

        if (found === false) {
            $scope.model.includesCodes.push(ce);
        }
    };
    $scope.addToLevelsWithCodes = function(data) {
        var ce = {};
        ce = $scope.createCodes(data);
        var found = false;
        $scope.model.levelsWithCodes.forEach(function(codes, index) {
            if (codes.uri.indexOf(data.koodistoUri) !== -1) {
                found = true;
            }
        });

        if (found === false) {
            $scope.model.levelsWithCodes.push(ce);
        }
    };

    $scope.openChildren = function(data) {
        CodesEditorModel.openChildren(data);
    };

    $scope.close = function(selectedCodes) {
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

    $scope.show = function(name) {
        $scope.addToListName = name;
        $scope.codesSelector = true;
    };

    $scope.open = function() {

        var modalInstance = $modal.open({
            templateUrl : 'organizationModalContent.html',
            controller : 'ModalInstanceCtrl',
            resolve : {
                isModalController : function() {
                    return true;
                }
            }
        });

        modalInstance.result.then(function(selectedItem) {
            $scope.model.codes.organisaatioOid = selectedItem.oid;
            $scope.model.codes.organizationName = selectedItem.nimi['fi'] || selectedItem.nimi['sv'] || selectedItem.nimi['en'];
        }, function() {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

    $scope.okconfirm = function() {
        if ($scope.model.withinRelationToRemove && $scope.model.withinRelationToRemove.uri !== "") {

            $scope.model.withinCodes.forEach(function(codes, index) {
                if (codes.uri.indexOf($scope.model.withinRelationToRemove.uri) !== -1) {
                    $scope.model.withinCodes.splice(index, 1);
                }
            });

        } else if ($scope.model.includesRelationToRemove && $scope.model.includesRelationToRemove.uri !== "") {
            $scope.model.includesCodes.forEach(function(codes, index) {
                if (codes.uri.indexOf($scope.model.includesRelationToRemove.uri) !== -1) {
                    $scope.model.includesCodes.splice(index, 1);
                }
            });

        } else if ($scope.model.levelsRelationToRemove && $scope.model.levelsRelationToRemove.uri !== "") {
            $scope.model.levelsWithCodes.forEach(function(codes, index) {
                if (codes.uri.indexOf($scope.model.levelsRelationToRemove.uri) !== -1) {
                    $scope.model.levelsWithCodes.splice(index, 1);
                }
            });

        }
        $scope.model.levelsRelationToRemove = null;
        $scope.model.includesRelationToRemove = null;
        $scope.model.withinRelationToRemove = null;
    };

    $scope.removeFromWithinCodes = function(codes) {
        $scope.model.withinRelationToRemove = codes;
        $scope.okconfirm();
    };

    $scope.removeFromIncludesCodes = function(codes) {
        $scope.model.includesRelationToRemove = codes;
        $scope.okconfirm();
    };

    $scope.removeFromLevelsWithCodes = function(codes) {
        $scope.model.levelsRelationToRemove = codes;
        $scope.okconfirm();
    };

    $scope.cancelconfirm = function() {
        $scope.model.levelsRelationToRemove = null;
        $scope.model.includesRelationToRemove = null;
        $scope.model.withinRelationToRemove = null;
        $scope.model.modalInstance.dismiss('cancel');
    };

    $scope.isCodeLoading = loadingService.isLoading;

});

