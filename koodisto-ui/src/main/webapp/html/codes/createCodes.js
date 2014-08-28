app.factory('CodesCreatorModel', function($location, RootCodes, $modal) {
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
            RootCodes.get({}, function(result) {
                model.allCodes = result;
                for (var i = 0; i < model.allCodes.length; i++) {
                    if (model.allCodes[i].koodistos) {
                        for (var j = 0; j < model.allCodes[i].koodistos.length; j++) {
                            if (!model.inCodesList(model.onlyCodes, model.allCodes[i].koodistos[j])) {
                                model.onlyCodes.push(model.allCodes[i].koodistos[j]);
                            }
                        }
                    }
                }

            });
        };

        this.inCodesList = function(codesList, codesToFind) {
            for (var i = 0; i < codesList.length; i++) {
                if (codesList[i].koodistoUri === codesToFind.koodistoUri) {
                    return true;
                }
            }
            return false;
        };

    };

    return model;
});

function CodesCreatorController($scope, $location, $modal, $log, $filter, CodesCreatorModel, NewCodes, Treemodel) {
    $scope.model = CodesCreatorModel;
    $scope.errorMessage = $filter('i18n')('field.required');
    $scope.errorMessageAtLeastOneName = $filter('i18n')('field.required.at.least.one.name');
    $scope.errorMessageIfOtherInfoIsGiven = $filter('i18n')('field.required.if.other.info.is.given');

    CodesCreatorModel.init();

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/").search({
            forceRefresh : false
        });
    };

    $scope.submit = function() {
        $scope.persistCodes();
    };

    $scope.persistCodes = function() {
        var codes = {
            codesGroupUri : $scope.selectedCGoup,
            voimassaAlkuPvm : $scope.dActiveStart,
            voimassaLoppuPvm : $scope.dActiveEnd,
            omistaja : $scope.ownerName,
            organisaatioOid : $scope.organizationOid,
            metadata : []
        };
        if ($scope.namefi) {
            codes.metadata.push({
                kieli : 'FI',
                nimi : $scope.namefi,
                kuvaus : $scope.descriptionfi,
                kayttoohje : $scope.instructionsfi,
                kohdealue : $scope.targetareafi,
                kohdealueenOsaAlue : $scope.targetareapartfi,
                kasite : $scope.conceptfi,
                toimintaymparisto : $scope.operationalenvironmentfi,
                koodistonLahde : $scope.codessourcefi,
                tarkentaaKoodistoa : $scope.specifiescodesfi,
                huomioitavaKoodisto : $scope.totakenoticeoffi,
                sitovuustaso : $scope.validitylevelfi
            });
        }
        if ($scope.namesv) {
            codes.metadata.push({
                kieli : 'SV',
                nimi : $scope.namesv,
                kuvaus : $scope.descriptionsv,
                kayttoohje : $scope.instructionssv,
                kohdealue : $scope.targetareasv,
                kohdealueenOsaAlue : $scope.targetareapartsv,
                kasite : $scope.conceptsv,
                toimintaymparisto : $scope.operationalenvironmentsv,
                koodistonLahde : $scope.codessourcesv,
                tarkentaaKoodistoa : $scope.specifiescodessv,
                huomioitavaKoodisto : $scope.totakenoticeofsv,
                sitovuustaso : $scope.validitylevelsv
            });
        }
        if ($scope.nameen) {
            codes.metadata.push({
                kieli : 'EN',
                nimi : $scope.nameen,
                kuvaus : $scope.descriptionen,
                kayttoohje : $scope.instructionsen,
                kohdealue : $scope.targetareaen,
                kohdealueenOsaAlue : $scope.targetareaparten,
                kasite : $scope.concepten,
                toimintaymparisto : $scope.operationalenvironmenten,
                koodistonLahde : $scope.codessourceen,
                tarkentaaKoodistoa : $scope.specifiescodesen,
                huomioitavaKoodisto : $scope.totakenoticeofen,
                sitovuustaso : $scope.validitylevelen
            });
        }
        NewCodes.post({}, codes, function(result) {
            Treemodel.refresh();
            $location.path("/koodisto/" + result.koodistoUri + "/" + result.versio).search({
                forceRefresh : true
            });
        }, function(error) {
            var alert = {
                type : 'danger',
                msg : jQuery.i18n.prop(error.data)
            };
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

    $scope.open = function() {

        var modalInstance = $modal.open({
            templateUrl : 'organizationModalContent.html',
            controller : ModalInstanceCtrl,
            resolve : {}
        });

        modalInstance.result.then(function(selectedItem) {
            $scope.organizationOid = selectedItem.oid;
            $scope.organizationName = selectedItem.nimi['fi'] || selectedItem.nimi['sv'] || selectedItem.nimi['en'];
        }, function() {
            $log.info('Modal dismissed at: ' + new Date());
        });
    };

}
