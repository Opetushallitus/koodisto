
app.factory('CodesEditorModel', function($location, RootCodes, Organizations, CodesByUri, OrganizationByOid) {
    var model;
    model = new function() {
        this.withinCodes = [];
        this.includesCodes = [];
        this.levelsWithCodes = [];
        this.allCodes = [];
        this.onlyCodes = [];
        this.organizations = [];
        this.states = [{key:'PASSIIVINEN', value:'PASSIIVINEN'},{key:'LUONNOS', value:'LUONNOS'},{key:'HYVAKSYTTY',value:'HYVÄKSYTTY'}];

        this.init = function(scope, codesUri) {
            this.withinCodes = [];
            this.includesCodes = [];
            this.levelsWithCodes = [];
            this.allCodes = [];
            this.onlyCodes = [];
            this.organizations = [];

            model.getCodes(scope, codesUri);
            model.getAllCodes();
            model.getOrganizations();
        };

        this.getCodes = function(scope, codesUri) {
            CodesByUri.get({codesUri: codesUri}, function (result) {
                model.codes = result;
                scope.namefi = model.languageSpecificValue(result.latestKoodistoVersio.metadata, 'nimi', 'FI');
                scope.namesv = model.languageSpecificValue(result.latestKoodistoVersio.metadata, 'nimi', 'SV');
                scope.nameen = model.languageSpecificValue(result.latestKoodistoVersio.metadata, 'nimi', 'EN');

                scope.descriptionfi = model.languageSpecificValue(result.latestKoodistoVersio.metadata, 'kuvaus', 'FI');
                scope.descriptionsv = model.languageSpecificValue(result.latestKoodistoVersio.metadata, 'kuvaus', 'SV');
                scope.descriptionen = model.languageSpecificValue(result.latestKoodistoVersio.metadata, 'kuvaus', 'EN');

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

function CodesEditorController($scope, $location, $modal, $log, $routeParams, CodesEditorModel, UpdateCodes, Treemodel, OrganizationByOid) {
    $scope.model = CodesEditorModel;
    $scope.codesUri = $routeParams.codesUri;
    CodesEditorModel.init($scope,$scope.codesUri);

    $scope.cancel = function() {
        $location.path("/koodisto/"+$scope.codesUri+"/"+$scope.model.codes.latestKoodistoVersio.versio);
    };

    $scope.submit = function() {
        $scope.persistCodes();
    };

    $scope.persistCodes = function() {
        var codes = {
            koodistoUri: $scope.model.codes.koodistoUri,
            voimassaAlkuPvm: $scope.model.codes.latestKoodistoVersio.voimassaAlkuPvm,
            voimassaLoppuPvm: $scope.model.codes.latestKoodistoVersio.voimassaLoppuPvm,
            omistaja: $scope.model.codes.omistaja,
            organisaatioOid: $scope.model.codes.organisaatioOid,
            versio: $scope.model.codes.latestKoodistoVersio.versio,
            tila: $scope.model.codes.latestKoodistoVersio.tila,
            version: $scope.model.codes.latestKoodistoVersio.version,
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

        UpdateCodes.put({}, codes, function(result) {
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

}

