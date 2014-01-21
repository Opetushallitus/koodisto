
app.factory('CodesEditorModel', function($location, RootCodes, Organizations, CodesByUriAndVersion, OrganizationByOid, $modal) {
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
            model.getOrganizations();
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

                this.withinCodes = model.codes.withinCodes;
                this.includesCodes = model.codes.includesCodes;
                this.levelsWithCodes = model.codes.levelsWithCodes;

                OrganizationByOid.get({oid: model.codes.organisaatioOid}, function (result) {
                    if (result.nimi['fi']) {
                        model.codes.organizationName = result.nimi['fi'];
                    } else {
                        model.codes.organizationName = result.nimi['sv'];
                    }
                });
            });
        };

        this.getAllCodes = function() {
            RootCodes.get({}, function (result) {
                model.allCodes = result;
                for(var i=0; i < model.allCodes.length; i++) {
                    if (model.allCodes[i].koodistos) {
                        for(var j=0; j < model.allCodes[i].koodistos.length; j++) {
                            if (!model.inCodesList(model.onlyCodes,model.allCodes[i].koodistos[j])) {
                                model.onlyCodes.push(model.allCodes[i].koodistos[j]);
                            }
                        }
                    }
                }
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
        this.getOrganizations = function() {
            Organizations.get({}, function (result) {
                model.organizations = result;
                for(var i=0; i < model.organizations.organisaatiot.length; i++) {
                    if (model.organizations.organisaatiot[i].nimi['fi']) {
                        model.organizations.organisaatiot[i].name =  model.organizations.organisaatiot[i].nimi['fi'];
                    } else {
                        model.organizations.organisaatiot[i].name =  model.organizations.organisaatiot[i].nimi['sv'];
                    }
                }
            });
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
                nimi: $scope.form.namefi.$viewValue,
                kuvaus: $scope.form.descriptionfi.$viewValue,
                kayttoohje: $scope.form.instructionsfi.$viewValue,
                kohdealue: $scope.form.targetareafi.$viewValue,
                kohdealueenOsaAlue: $scope.form.targetareapartfi.$viewValue,
                kasite: $scope.form.conceptfi.$viewValue,
                toimintaymparisto: $scope.form.operationalenvironmentfi.$viewValue,
                koodistonLahde: $scope.form.codessourcefi.$viewValue,
                tarkentaaKoodistoa: $scope.form.specifiescodesfi.$viewValue,
                huomioitavaKoodisto: $scope.form.totakenoticeoffi.$viewValue,
                sitovuustaso: $scope.form.validitylevelfi.$viewValue
            }]
        };
        if ($scope.form.namesv && $scope.form.namesv.$viewValue) {
            codes.metadata.push({
                kieli: 'SV',
                nimi: $scope.form.namesv.$viewValue,
                kuvaus: $scope.form.descriptionsv.$viewValue,
                kayttoohje: $scope.form.instructionssv.$viewValue,
                kohdealue: $scope.form.targetareasv.$viewValue,
                kohdealueenOsaAlue: $scope.form.targetareapartsv.$viewValue,
                kasite: $scope.form.conceptsv.$viewValue,
                toimintaymparisto: $scope.form.operationalenvironmentsv.$viewValue,
                koodistonLahde: $scope.form.codessourcesv.$viewValue,
                tarkentaaKoodistoa: $scope.form.specifiescodessv.$viewValue,
                huomioitavaKoodisto: $scope.form.totakenoticeofsv.$viewValue,
                sitovuustaso: $scope.form.validitylevelsv.$viewValue
            });
        }
        if ($scope.form.nameen && $scope.form.nameen.$viewValue) {
            codes.metadata.push({
                kieli: 'EN',
                nimi: $scope.form.nameen.$viewValue,
                kuvaus: $scope.form.descriptionen.$viewValue,
                kayttoohje: $scope.form.instructionsen.$viewValue,
                kohdealue: $scope.form.targetareaen.$viewValue,
                kohdealueenOsaAlue: $scope.form.targetareaparten.$viewValue,
                kasite: $scope.form.concepten.$viewValue,
                toimintaymparisto: $scope.form.operationalenvironmenten.$viewValue,
                koodistonLahde: $scope.form.codessourceen.$viewValue,
                tarkentaaKoodistoa: $scope.form.specifiescodesen.$viewValue,
                huomioitavaKoodisto: $scope.form.totakenoticeofen.$viewValue,
                sitovuustaso: $scope.form.validitylevelen.$viewValue
            });
        }
        UpdateCodes.put({}, codes, function(result) {
            Treemodel.refresh();
            $location.path("/koodisto/"+result.koodistoUri+"/"+result.versio);
        }, function(error) {
            ValidateService.validateCodes($scope,error,true);
        });
    };

    $scope.setSameValue = function(name) {
        if (name === 'name' && !$scope.samename) {
            $scope.namesv = $scope.form.namefi.$viewValue;
            $scope.nameen = $scope.form.namefi.$viewValue;
        } else if (name === 'description' && !$scope.samedescription) {
            $scope.descriptionsv = $scope.form.descriptionfi.$viewValue;
            $scope.descriptionen = $scope.form.descriptionfi.$viewValue;
        } else if (name === 'instructions' && !$scope.sameinstructions) {
            $scope.instructionssv = $scope.form.instructionsfi.$viewValue;
            $scope.instructionsen = $scope.form.instructionsfi.$viewValue;
        } else if (name === 'targetarea' && !$scope.sametargetarea) {
            $scope.targetareasv = $scope.form.targetareafi.$viewValue;
            $scope.targetareaen = $scope.form.targetareafi.$viewValue;
        } else if (name === 'targetareapart' && !$scope.sametargetareapart) {
            $scope.targetareapartsv = $scope.form.targetareapartfi.$viewValue;
            $scope.targetareaparten = $scope.form.targetareapartfi.$viewValue;
        } else if (name === 'concept' && !$scope.sameconcept) {
            $scope.conceptsv = $scope.form.conceptfi.$viewValue;
            $scope.concepten = $scope.form.conceptfi.$viewValue;
        } else if (name === 'operationalenvironment' && !$scope.sameoperationalenvironment) {
            $scope.operationalenvironmentsv = $scope.form.operationalenvironmentfi.$viewValue;
            $scope.operationalenvironmenten = $scope.form.operationalenvironmentfi.$viewValue;
        } else if (name === 'codessource' && !$scope.samecodessource) {
            $scope.codessourcesv = $scope.form.codessourcefi.$viewValue;
            $scope.codessourceen = $scope.form.codessourcefi.$viewValue;
        } else if (name === 'specifiescodes' && !$scope.samespecifiescodes) {
            $scope.specifiescodessv = $scope.form.specifiescodesfi.$viewValue;
            $scope.specifiescodesen = $scope.form.specifiescodesfi.$viewValue;
        } else if (name === 'totakenoticeof' && !$scope.sametotakenoticeof) {
            $scope.totakenoticeofsv = $scope.form.totakenoticeoffi.$viewValue;
            $scope.totakenoticeofen = $scope.form.totakenoticeoffi.$viewValue;
        } else if (name === 'validitylevel' && !$scope.samevaliditylevel) {
            $scope.validitylevelsv = $scope.form.validitylevelfi.$viewValue;
            $scope.validitylevelen = $scope.form.validitylevelfi.$viewValue;
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
        if ($scope.model.withinCodes.indexOf(ce) === -1) {
            $scope.model.withinCodes.push(ce);
            AddRelationCodes.put({codesUri: data.koodistoUri,
                codesUriToAdd: $scope.model.codes.koodistoUri,relationType: "SISALTYY"},function(result) {
            });
        }
    };

    $scope.addToIncludesCodes = function(data) {
        var ce = {};
        ce = $scope.createCodes(data);
        if ($scope.model.includesCodes.indexOf(ce) === -1) {
            $scope.model.includesCodes.push(ce);
            AddRelationCodes.put({codesUri: $scope.model.codes.koodistoUri,
                codesUriToAdd: data.koodistoUri,relationType: "SISALTYY"},function(result) {
            });
        }
    };
    $scope.addToLevelsWithCodes = function(data) {
        var ce = {};
        ce = $scope.createCodes(data);
        if ($scope.model.levelsWithCodes.indexOf(ce) === -1) {
            $scope.model.levelsWithCodes.push(ce);
            AddRelationCodes.put({codesUri: data.koodistoUri,
                codesUriToAdd: $scope.model.codes.koodistoUri,relationType: "RINNASTEINEN"},function(result) {
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
            if (selectedItem.nimi['fi']) {
                $scope.model.codes.organizationName = selectedItem.nimi['fi'];
            } else {
                $scope.model.codes.organizationName = selectedItem.nimi['sv'];
            }

        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

    $scope.okconfirm = function() {
        if ($scope.model.withinRelationToRemove && $scope.model.withinRelationToRemove.uri !== "") {
            $scope.model.withinCodes.splice($scope.model.withinCodes.indexOf($scope.model.withinRelationToRemove.uri), 1);

            RemoveRelationCodes.put({codesUri: $scope.model.withinRelationToRemove.uri,
                codesUriToRemove: $scope.model.codes.koodistoUri,relationType: "SISALTYY"},function(result) {

            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.includesRelationToRemove && $scope.model.includesRelationToRemove.uri !== "") {

            $scope.model.includesCodes.splice($scope.model.includesCodes.indexOf($scope.model.includesRelationToRemove.uri), 1);

            RemoveRelationCodes.put({codesUri: $scope.model.codes.koodistoUri,
                codesUriToRemove: $scope.model.includesRelationToRemove.uri,relationType: "SISALTYY"},function(result) {

            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodistojen v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.levelsRelationToRemove && $scope.model.levelsRelationToRemove.uri !== "") {
            $scope.model.levelsWithCodes.splice($scope.model.levelsWithCodes.indexOf($scope.model.levelsRelationToRemove.uri), 1);

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

