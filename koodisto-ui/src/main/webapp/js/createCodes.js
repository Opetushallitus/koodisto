
app.factory('CodesCreatorModel', function($location, RootCodes, Organizations) {
    var model;
    model = new function() {
        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.allCodes = [];
        this.onlyCodes = [];

        this.init = function() {
            this.withinCodes = [];
            this.includesCodes = [];
            this.levelsWithCodes = [];
            this.allCodes = [];
            this.onlyCodes = [];
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

function CodesCreatorController($scope, $location, $modal, $log, CodesCreatorModel, NewCodes, Treemodel) {
    $scope.createmodel = CodesCreatorModel;
    CodesCreatorModel.init();

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
                kuvaus: $scope.form.descriptionfi.$viewValue
            }]
        };
        if ($scope.form.namesv && $scope.form.namesv.$viewValue) {
            codes.metadata.push({
            kieli: 'SV',
                nimi: $scope.form.namesv.$viewValue,
                kuvaus: $scope.form.descriptionsv.$viewValue
            });
        }
        if ($scope.form.nameen && $scope.form.nameen.$viewValue) {
            codes.metadata.push({
            kieli: 'EN',
                nimi: $scope.form.nameen.$viewValue,
                kuvaus: $scope.form.descriptionen.$viewValue
            });
        }
        NewCodes.put({}, codes, function(result) {
            Treemodel.refresh();
            $location.path("/koodisto/"+result.koodistoUri+"/"+result.versio);
        });
    };

    $scope.setSameValue = function(name) {
        if (name === 'name' && !$scope.samename) {
            $scope.namesv = $scope.form.namefi.$viewValue;
            $scope.nameen = $scope.form.namefi.$viewValue;
        } else if (name === 'description' && !$scope.samedescription) {
            $scope.descriptionsv = $scope.form.descriptionfi.$viewValue;
            $scope.descriptionen = $scope.form.descriptionfi.$viewValue;
        }
    };

    $scope.addToWithinCodes = function() {
        if ($scope.createmodel.withinCodes.indexOf($scope.withinCodesItem) === -1) {
            $scope.createmodel.withinCodes.push($scope.withinCodesItem);
        }
    };

    $scope.addToIncludesCodes = function() {
        if (!$scope.createmodel.inCodesList($scope.createmodel.includesCodes,$scope.includesCodesItem)) {
            $scope.createmodel.includesCodes.push($scope.includesCodesItem);
        }
    };
    $scope.addToLevelsWithCodes = function() {
        if (!$scope.createmodel.inCodesList($scope.createmodel.levelsWithCodes,$scope.levelsWithCodesItem)) {
            $scope.createmodel.levelsWithCodes.push($scope.levelsWithCodesItem);
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
