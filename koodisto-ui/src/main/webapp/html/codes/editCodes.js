
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


                scope.namefi = model.languageSpecificValue(result.metadata, 'nimi', 'FI');
                scope.namesv = model.languageSpecificValue(result.metadata, 'nimi', 'SV');
                scope.nameen = model.languageSpecificValue(result.metadata, 'nimi', 'EN');

                scope.descriptionfi = model.languageSpecificValue(result.metadata, 'kuvaus', 'FI');
                scope.descriptionsv = model.languageSpecificValue(result.metadata, 'kuvaus', 'SV');
                scope.descriptionen = model.languageSpecificValue(result.metadata, 'kuvaus', 'EN');

                scope.instructionsfi = model.languageSpecificValue(result.metadata, 'kayttoohje', 'FI');
                scope.instructionssv = model.languageSpecificValue(result.metadata, 'kayttoohje', 'SV');
                scope.instructionsen = model.languageSpecificValue(result.metadata, 'kayttoohje', 'EN');

                scope.targetareafi = model.languageSpecificValue(result.metadata, 'kohdealue', 'FI');
                scope.targetareasv = model.languageSpecificValue(result.metadata, 'kohdealue', 'SV');
                scope.targetareaen = model.languageSpecificValue(result.metadata, 'kohdealue', 'EN');

                scope.targetareapartfi = model.languageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'FI');
                scope.targetareapartsv = model.languageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'SV');
                scope.targetareaparten = model.languageSpecificValue(result.metadata, 'kohdealueenOsaAlue', 'EN');

                scope.conceptfi = model.languageSpecificValue(result.metadata, 'kasite', 'FI');
                scope.conceptsv = model.languageSpecificValue(result.metadata, 'kasite', 'SV');
                scope.concepten = model.languageSpecificValue(result.metadata, 'kasite', 'EN');

                scope.operationalenvironmentfi = model.languageSpecificValue(result.metadata, 'toimintaymparisto', 'FI');
                scope.operationalenvironmentsv = model.languageSpecificValue(result.metadata, 'toimintaymparisto', 'SV');
                scope.operationalenvironmenten = model.languageSpecificValue(result.metadata, 'toimintaymparisto', 'EN');

                scope.codessourcefi = model.languageSpecificValue(result.metadata, 'koodistonLahde', 'FI');
                scope.codessourcesv = model.languageSpecificValue(result.metadata, 'koodistonLahde', 'SV');
                scope.codessourceen = model.languageSpecificValue(result.metadata, 'koodistonLahde', 'EN');

                scope.specifiescodesfi = model.languageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'FI');
                scope.specifiescodessv = model.languageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'SV');
                scope.specifiescodesen = model.languageSpecificValue(result.metadata, 'tarkentaaKoodistoa', 'EN');

                scope.totakenoticeoffi = model.languageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'FI');
                scope.totakenoticeofsv = model.languageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'SV');
                scope.totakenoticeofen = model.languageSpecificValue(result.metadata, 'huomioitavaKoodisto', 'EN');

                scope.validitylevelfi = model.languageSpecificValue(result.metadata, 'sitovuustaso', 'FI');
                scope.validitylevelsv = model.languageSpecificValue(result.metadata, 'sitovuustaso', 'SV');
                scope.validitylevelen = model.languageSpecificValue(result.metadata, 'sitovuustaso', 'EN');

                model.codes.withinCodes.forEach(function(codes){
                    model.getLatestCodesVersionsByCodesUri(codes,model.withinCodes);
                });
                model.codes.includesCodes.forEach(function(codes){
                    model.getLatestCodesVersionsByCodesUri(codes,model.includesCodes);
                });
                model.codes.levelsWithCodes.forEach(function(codes){
                    model.getLatestCodesVersionsByCodesUri(codes,model.levelsWithCodes);
                });

                OrganizationByOid.get({oid: model.codes.organisaatioOid}, function (result) {
                    model.codes.organizationName = result.nimi['fi'] || result.nimi['sv'] || result.nimi['en'];
                });
            });
        };

        this.getLatestCodesVersionsByCodesUri = function(codesUri, list) {
            CodesByUri.get({codesUri: codesUri}, function (result) {
                var ce = {};
                ce.uri = codesUri;
                ce.name = model.languageSpecificValue(result.latestKoodistoVersio.metadata, 'nimi', 'FI');
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

        this.languageSpecificValue = function(fieldArray,fieldName,language) {
            return getLanguageSpecificValue(fieldArray,fieldName,language);
        };


        this.removeFromWithinCodes = function(codes) {
            model.withinRelationToRemove = codes;

            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: CodesEditorController,
                resolve: {
                }
            });

        };

        this.removeFromIncludesCodes = function(codes) {
            model.includesRelationToRemove = codes;
            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: CodesEditorController,
                resolve: {
                }
            });
        };

        this.removeFromLevelsWithCodes = function(codes) {
            model.levelsRelationToRemove = codes;
            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: CodesEditorController,
                resolve: {
                }
            });
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
                }
                iter(data.children);
            }
        };

    };


    return model;
});

function CodesEditorController($scope, $location, $modal, $log, $routeParams, CodesEditorModel, UpdateCodes, Treemodel,
                               ValidateService, AddRelationCodes, RemoveRelationCodes) {
    $scope.model = CodesEditorModel;
    $scope.codesUri = $routeParams.codesUri;
    $scope.codesVersion = $routeParams.codesVersion;
    CodesEditorModel.init($scope,$routeParams.codesUri, $scope.codesVersion);

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/koodisto/"+$scope.codesUri+"/"+$scope.codesVersion);
    };

    $scope.submit = function() {
        $scope.persistCodes();
    };

    $scope.search = function (item){
        if (!$scope.model.query || item.koodistoUri.toLowerCase().indexOf($scope.model.query.toLowerCase())!==-1) {
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
            metadata : [{
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
            }]
        };
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
        UpdateCodes.put({}, codes, function(result) {
            Treemodel.refresh();
            $location.path("/koodisto/"+$scope.codesUri+"/"+result[0]);
        }, function(error) {
            ValidateService.validateCodes($scope,error,true);
        });
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
        ce.name = $scope.model.languageSpecificValue(data.latestKoodistoVersio.metadata, 'nimi', 'FI');
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
            AddRelationCodes.put({codesUri: data.koodistoUri,
                codesUriToAdd: $scope.model.codes.koodistoUri,relationType: "SISALTYY"},function(result) {
                    $scope.model.withinCodes.push(ce);
                }, function(error) {
                    var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen lis\u00E4\u00E4minen ep\u00E4onnistui' }
                    $scope.model.alerts.push(alert);
                });
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
            AddRelationCodes.put({codesUri: $scope.model.codes.koodistoUri,
                codesUriToAdd: data.koodistoUri,relationType: "SISALTYY"},function(result) {
                    $scope.model.includesCodes.push(ce);
                }, function(error) {
                    var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen lis\u00E4\u00E4minen ep\u00E4onnistui' }
                    $scope.model.alerts.push(alert);
                });
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
            AddRelationCodes.put({codesUri: data.koodistoUri,
                codesUriToAdd: $scope.model.codes.koodistoUri,relationType: "RINNASTEINEN"},function(result) {
                    $scope.model.levelsWithCodes.push(ce);
                }, function(error) {
                    var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen lis\u00E4\u00E4minen ep\u00E4onnistui' }
                    $scope.model.alerts.push(alert);
                });
        }
    };

    $scope.openChildren = function(data) {
        CodesEditorModel.openChildren(data);
    }

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
    }

    $scope.show = function(name){
        $scope.addToListName = name;
        $scope.codesSelector = true;
    }

    $scope.open = function () {

        var modalInstance = $modal.open({
            templateUrl: 'organizationModalContent.html',
            controller: ModalInstanceCtrl,
            resolve: {
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

            RemoveRelationCodes.put({codesUri: $scope.model.withinRelationToRemove.uri,
                codesUriToRemove: $scope.model.codes.koodistoUri,relationType: "SISALTYY"},function(result) {

            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.includesRelationToRemove && $scope.model.includesRelationToRemove.uri !== "") {
            $scope.model.includesCodes.forEach(function(codes, index){
                if (codes.uri.indexOf($scope.model.includesRelationToRemove.uri) !== -1) {
                    $scope.model.includesCodes.splice(index,1);
                }
            });
            RemoveRelationCodes.put({codesUri: $scope.model.codes.koodistoUri,
                codesUriToRemove: $scope.model.includesRelationToRemove.uri,relationType: "SISALTYY"},function(result) {

            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.levelsRelationToRemove && $scope.model.levelsRelationToRemove.uri !== "") {
            $scope.model.levelsWithCodes.forEach(function(codes, index){
                if (codes.uri.indexOf($scope.model.levelsRelationToRemove.uri) !== -1) {
                    $scope.model.levelsWithCodes.splice(index,1);
                }
            });
            RemoveRelationCodes.put({codesUri: $scope.model.levelsRelationToRemove.uri,
                codesUriToRemove: $scope.model.codes.koodistoUri,relationType: "RINNASTEINEN"},function(result) {
            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
                $scope.model.alerts.push(alert);
            });
        }
        $scope.model.levelsRelationToRemove = null;
        $scope.model.includesRelationToRemove = null;
        $scope.model.withinRelationToRemove = null;
        $scope.model.modalInstance.close();
    };

    $scope.cancelconfirm = function() {
        $scope.model.levelsRelationToRemove = null;
        $scope.model.includesRelationToRemove = null;
        $scope.model.withinRelationToRemove = null;
        $scope.model.modalInstance.dismiss('cancel');
    };
}

