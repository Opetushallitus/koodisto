
app.factory('CodesEditorModel', function($location, RootCodes, Organizations, CodesByUriAndVersion, OrganizationByOid,
                                         CodesByUri, AuthService, $modal) {
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


                scope.namefi = getLanguageSpecificValue(result.metadata, 'nimi', 'FI');
                scope.namesv = getLanguageSpecificValue(result.metadata, 'nimi', 'SV');
                scope.nameen = getLanguageSpecificValue(result.metadata, 'nimi', 'EN');

                scope.descriptionfi = getLanguageSpecificValue(result.metadata, 'kuvaus', 'FI');
                scope.descriptionsv = getLanguageSpecificValue(result.metadata, 'kuvaus', 'SV');
                scope.descriptionen = getLanguageSpecificValue(result.metadata, 'kuvaus', 'EN');

                scope.instructionsfi = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'FI');
                scope.instructionssv = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'SV');
                scope.instructionsen = getLanguageSpecificValue(result.metadata, 'kayttoohje', 'EN');

                scope.targetareafi = getLanguageSpecificValue(result.metadata, 'kohdealue', 'FI');
                scope.targetareasv = getLanguageSpecificValue(result.metadata, 'kohdealue', 'SV');
                scope.targetareaen = getLanguageSpecificValue(result.metadata, 'kohdealue', 'EN');

                scope.targetareapartfi = getLanguageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'FI');
                scope.targetareapartsv = getLanguageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'SV');
                scope.targetareaparten = getLanguageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'EN');

                scope.conceptfi = getLanguageSpecificValue(result.metadata, 'kasite', 'FI');
                scope.conceptsv = getLanguageSpecificValue(result.metadata, 'kasite', 'SV');
                scope.concepten = getLanguageSpecificValue(result.metadata, 'kasite', 'EN');

                scope.operationalenvironmentfi = getLanguageSpecificValue(result.metadata, 'toimintaymparisto', 'FI');
                scope.operationalenvironmentsv = getLanguageSpecificValue(result.metadata, 'toimintaymparisto', 'SV');
                scope.operationalenvironmenten = getLanguageSpecificValue(result.metadata, 'toimintaymparisto', 'EN');

                scope.codessourcefi = getLanguageSpecificValue(result.metadata, 'koodistonLahde', 'FI');
                scope.codessourcesv = getLanguageSpecificValue(result.metadata, 'koodistonLahde', 'SV');
                scope.codessourceen = getLanguageSpecificValue(result.metadata, 'koodistonLahde', 'EN');

                scope.specifiescodesfi = getLanguageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'FI');
                scope.specifiescodessv = getLanguageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'SV');
                scope.specifiescodesen = getLanguageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'EN');

                scope.totakenoticeoffi = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'FI');
                scope.totakenoticeofsv = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'SV');
                scope.totakenoticeofen = getLanguageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'EN');

                scope.validitylevelfi = getLanguageSpecificValue(result.metadata, 'sitovuustaso', 'FI');
                scope.validitylevelsv = getLanguageSpecificValue(result.metadata, 'sitovuustaso', 'SV');
                scope.validitylevelen = getLanguageSpecificValue(result.metadata, 'sitovuustaso', 'EN');

                model.codes.withinCodes.forEach(function(codes){
                    model.getLatestCodesVersionsByCodesUri(codes,model.withinCodes);
                });
                model.codes.includesCodes.forEach(function(codes){
                    model.getLatestCodesVersionsByCodesUri(codes,model.includesCodes);
                });
                model.codes.levelsWithCodes.forEach(function(codes){
                    model.getLatestCodesVersionsByCodesUri(codes,model.levelsWithCodes);
                });

                OrganizationByOid.get({oid: model.codes.organisaatioOid}, function (result2) {
                    model.codes.organizationName = result2.nimi['fi'] || result2.nimi['sv'] || result2.nimi['en'];
                });
                
                scope.loadingReady = true;
            });
        };

        this.getLatestCodesVersionsByCodesUri = function(codes, list) {
            CodesByUri.get({codesUri: codes.codesUri}, function (result) {
                var ce = {};
                ce.uri = codes.codesUri;
                ce.name = getLanguageSpecificValueOrValidValue(result.latestKoodistoVersio.metadata, 'nimi', 'FI');
                ce.versio = codes.codesVersion;
                list.push(ce);
            });
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

        
        this.getAllCodes = function() {
            RootCodes.get({}, function(result) {
                model.allCodes = result;
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
            if(data.open) {

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

function CodesEditorController($scope, $location, $modal, $log, $routeParams, $filter, CodesEditorModel, Treemodel,
                               CodesMatcher, SaveCodes, isModalController) {
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
                controller : CodesEditorController,
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
        if ($scope.namefi) {
            codes.metadata.push({
                kieli: 'FI',
                nimi: $scope.namefi,
                kuvaus: $scope.descriptionfi,
                kayttoohje: $scope.instructionsfi,
                kohdealue: $scope.targetareafi,
                kohdealueenOsaAlue: $scope.targetareapartfi,
                kasite: $scope.conceptfi,
                toimintaymparisto: $scope.operationalenvironmentfi,
                koodistonLahde: $scope.codessourcefi,
                tarkentaaKoodistoa: $scope.specifiescodesfi,
                huomioitavaKoodisto: $scope.totakenoticeoffi,
                sitovuustaso: $scope.validitylevelfi
            });
        }
        if ($scope.namesv) {
                codes.metadata.push({
                kieli: 'SV',
                nimi: $scope.namesv,
                kuvaus: $scope.descriptionsv,
                kayttoohje: $scope.instructionssv,
                kohdealue: $scope.targetareasv,
                kohdealueenOsaAlue: $scope.targetareapartsv,
                kasite: $scope.conceptsv,
                toimintaymparisto: $scope.operationalenvironmentsv,
                koodistonLahde: $scope.codessourcesv,
                tarkentaaKoodistoa: $scope.specifiescodessv,
                huomioitavaKoodisto: $scope.totakenoticeofsv,
                sitovuustaso: $scope.validitylevelsv
            });
        }
        if ($scope.nameen) {
            codes.metadata.push({
                kieli: 'EN',
                nimi: $scope.nameen,
                kuvaus: $scope.descriptionen,
                kayttoohje: $scope.instructionsen,
                kohdealue: $scope.targetareaen,
                kohdealueenOsaAlue: $scope.targetareaparten,
                kasite: $scope.concepten,
                toimintaymparisto: $scope.operationalenvironmenten,
                koodistonLahde: $scope.codessourceen,
                tarkentaaKoodistoa: $scope.specifiescodesen,
                huomioitavaKoodisto: $scope.totakenoticeofen,
                sitovuustaso: $scope.validitylevelen
            });
        }
        SaveCodes.put({}, codes, function(result) {
            Treemodel.refresh();
            $location.path("/koodisto/"+$scope.codesUri+"/"+result[0]).search({forceRefresh: true});
        }, function(error) {
            if (error.data == "error.codes.has.no.codeelements") {
                var alert = {
                    type : 'info',
                    msg : jQuery.i18n.prop(error.data)
                };
                $scope.model.alerts.push(alert);
            } else {
                var alert = {
                    type : 'danger',
                    msg : jQuery.i18n.prop(error.data)
                };
                $scope.model.alerts.push(alert);
            }
        });
    };

    $scope.changeToRelationCodes = function(listToBeChanged){
        result = [];
        listToBeChanged.forEach(function(ce){
            dt = {};
            dt.codesUri = ce.uri;
            dt.codesVersion = 1;
            result.push(dt);
        });
        return result;
    };
    
    $scope.setSameValue = function(name) {
        if (name === 'name' && !$scope.samename) {
            $scope.namesv = $scope.namefi;
            $scope.nameen = $scope.namefi;
        } else if (name === 'description' && !$scope.samedescription) {
            $scope.descriptionsv = $scope.descriptionfi;
            $scope.descriptionen = $scope.descriptionfi;
        } else if (name === 'instructions' && !$scope.sameinstructions) {
            $scope.instructionssv = $scope.instructionsfi;
            $scope.instructionsen = $scope.instructionsfi;
        } else if (name === 'targetarea' && !$scope.sametargetarea) {
            $scope.targetareasv = $scope.targetareafi;
            $scope.targetareaen = $scope.targetareafi;
        } else if (name === 'targetareapart' && !$scope.sametargetareapart) {
            $scope.targetareapartsv = $scope.targetareapartfi;
            $scope.targetareaparten = $scope.targetareapartfi;
        } else if (name === 'concept' && !$scope.sameconcept) {
            $scope.conceptsv = $scope.conceptfi;
            $scope.concepten = $scope.conceptfi;
        } else if (name === 'operationalenvironment' && !$scope.sameoperationalenvironment) {
            $scope.operationalenvironmentsv = $scope.operationalenvironmentfi;
            $scope.operationalenvironmenten = $scope.operationalenvironmentfi;
        } else if (name === 'codessource' && !$scope.samecodessource) {
            $scope.codessourcesv = $scope.codessourcefi;
            $scope.codessourceen = $scope.codessourcefi;
        } else if (name === 'specifiescodes' && !$scope.samespecifiescodes) {
            $scope.specifiescodessv = $scope.specifiescodesfi;
            $scope.specifiescodesen = $scope.specifiescodesfi;
        } else if (name === 'totakenoticeof' && !$scope.sametotakenoticeof) {
            $scope.totakenoticeofsv = $scope.totakenoticeoffi;
            $scope.totakenoticeofen = $scope.totakenoticeoffi;
        } else if (name === 'validitylevel' && !$scope.samevaliditylevel) {
            $scope.validitylevelsv = $scope.validitylevelfi;
            $scope.validitylevelen = $scope.validitylevelfi;
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
        $scope.model.withinCodes.forEach(function(codes, index){
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
        $scope.model.includesCodes.forEach(function(codes, index){
            if (codes.uri.indexOf(data.koodistoUri) !== -1) {
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
        $scope.model.levelsWithCodes.forEach(function(codes, index){
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

    $scope.close = function(selectedCodes){
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

    $scope.show = function(name){
        $scope.addToListName = name;
        $scope.codesSelector = true;
    };

    $scope.open = function () {

        var modalInstance = $modal.open({
            templateUrl: 'organizationModalContent.html',
            controller: ModalInstanceCtrl,
            resolve : {
                isModalController : function() {
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

    $scope.okconfirm = function() {
        if ($scope.model.withinRelationToRemove && $scope.model.withinRelationToRemove.uri !== "") {

            $scope.model.withinCodes.forEach(function(codes, index){
                if (codes.uri.indexOf($scope.model.withinRelationToRemove.uri) !== -1) {
                    $scope.model.withinCodes.splice(index,1);
                }
            });

        } else if ($scope.model.includesRelationToRemove && $scope.model.includesRelationToRemove.uri !== "") {
            $scope.model.includesCodes.forEach(function(codes, index){
                if (codes.uri.indexOf($scope.model.includesRelationToRemove.uri) !== -1) {
                    $scope.model.includesCodes.splice(index,1);
                }
            });

        } else if ($scope.model.levelsRelationToRemove && $scope.model.levelsRelationToRemove.uri !== "") {
            $scope.model.levelsWithCodes.forEach(function(codes, index){
                if (codes.uri.indexOf($scope.model.levelsRelationToRemove.uri) !== -1) {
                    $scope.model.levelsWithCodes.splice(index,1);
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
}

