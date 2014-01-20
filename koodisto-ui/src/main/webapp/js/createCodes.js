
app.factory('CodesCreatorModel', function($location, RootCodes, Organizations) {
    var model;
    model = new function() {
        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.allCodes = [];
        this.onlyCodes = [];
        this.alerts = [];

        this.init = function() {
            this.withinCodes = [];
            this.includesCodes = [];
            this.levelsWithCodes = [];
            this.allCodes = [];
            this.onlyCodes = [];
            this.alerts = [];
            model.getCodes();
        };

        this.getCodes = function() {
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


        this.languageSpecificValue = function(fieldArray,fieldName,language) {
            return getLanguageSpecificValue(fieldArray,fieldName,language);
        };

        this.removeFromWithinCodes = function(code) {
            model.withinCodes.splice(model.withinCodes.indexOf(code), 1);
        };

        this.removeFromIncludesCodes = function(code) {
            model.includesCodes.splice(model.includesCodes.indexOf(code), 1);
        };

        this.removeFromLevelsWithCodes = function(code) {
            model.levelsWithCodes.splice(model.levelsWithCodes.indexOf(code), 1);
        };


    };


    return model;
});

function CodesCreatorController($scope, $location, $modal, $log, CodesCreatorModel, NewCodes, Treemodel, ValidateService) {
    $scope.model = CodesCreatorModel;
    CodesCreatorModel.init();

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/");
    };

    $scope.submit = function() {
        $scope.persistCodes();
    };

    $scope.persistCodes = function() {
        var codes = {
            codesGroupUri: $scope.selectedCGoup,
            voimassaAlkuPvm: $scope.dActiveStart,
            voimassaLoppuPvm: $scope.dActiveEnd,
            omistaja: $scope.ownerName,
            organisaatioOid: $scope.organizationOid,
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
        NewCodes.put({}, codes, function(result) {
            Treemodel.refresh();
            $location.path("/koodisto/"+result.koodistoUri+"/"+result.versio);
        }, function(error) {
            ValidateService.validateCodes($scope,error,false);
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

    $scope.addToWithinCodes = function() {
        if ($scope.model.withinCodes.indexOf($scope.withinCodesItem) === -1) {
            $scope.model.withinCodes.push($scope.withinCodesItem);
        }
    };

    $scope.addToIncludesCodes = function() {
        if (!$scope.model.inCodesList($scope.model.includesCodes,$scope.includesCodesItem)) {
            $scope.model.includesCodes.push($scope.includesCodesItem);
        }
    };
    $scope.addToLevelsWithCodes = function() {
        if (!$scope.model.inCodesList($scope.model.levelsWithCodes,$scope.levelsWithCodesItem)) {
            $scope.model.levelsWithCodes.push($scope.levelsWithCodesItem);
        }
    };


    $scope.open = function () {

        var modalInstance = $modal.open({
            templateUrl: 'organizationModalContent.html',
            controller: ModalInstanceCtrl,
            resolve: {
            }
        });

        modalInstance.result.then(function (selectedItem) {
            $scope.organizationOid = selectedItem.oid;
            if (selectedItem.nimi['fi']) {
                $scope.organizationName = selectedItem.nimi['fi'];
            } else {
                $scope.organizationName = selectedItem.nimi['sv'];
            }

        }, function () {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };
}
