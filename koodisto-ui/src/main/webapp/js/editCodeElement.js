
app.factory('CodeElementEditorModel', function($modal, $location, RootCodes, CodeElementByUriAndVersion, AllCodes,
                                               CodeElementsByCodesUriAndVersion, RemoveRelationCodeElement,
                                               LatestCodeElementVersionsByCodeElementUri) {
    var model;
    model = new function() {
        this.states = [{key:'PASSIIVINEN', value:'PASSIIVINEN'},{key:'LUONNOS', value:'LUONNOS'}];

        this.allCodes = [];
        this.withinCodeElements = [];
        this.includesCodeElements = [];
        this.levelsWithCodeElements = [];
        this.alerts = [];
        this.allWithinCodeElements = [];
        this.allIncludesCodeElements = [];
        this.allLevelsWithCodeElements = [];

        this.init = function(scope, codeElementUri, codeElementVersion) {
            this.allCodes = [];
            this.withinCodeElements = [];
            this.includesCodeElements = [];
            this.levelsWithCodeElements = [];
            this.alerts = [];
            this.allWithinCodeElements = [];
            this.allIncludesCodeElements = [];
            this.allLevelsWithCodeElements = [];
            model.getAllCodes();
            model.getCodeElement(scope, codeElementUri, codeElementVersion);
        };

        this.getCodeElement = function(scope, codeElementUri, codeElementVersion) {
            CodeElementByUriAndVersion.get({codeElementUri: codeElementUri, codeElementVersion: codeElementVersion}, function (result) {
                model.codeElement = result;
                scope.codeValue = result.koodiArvo;

                scope.namefi = model.languageSpecificValue(result.metadata, 'nimi', 'FI');
                scope.namesv = model.languageSpecificValue(result.metadata, 'nimi', 'SV');
                scope.nameen = model.languageSpecificValue(result.metadata, 'nimi', 'EN');

                scope.shortnamefi = model.languageSpecificValue(result.metadata, 'lyhytNimi', 'FI');
                scope.shortnamesv = model.languageSpecificValue(result.metadata, 'lyhytNimi', 'SV');
                scope.shortnameen = model.languageSpecificValue(result.metadata, 'lyhytNimi', 'EN');

                scope.descriptionfi = model.languageSpecificValue(result.metadata, 'kuvaus', 'FI');
                scope.descriptionsv = model.languageSpecificValue(result.metadata, 'kuvaus', 'SV');
                scope.descriptionen = model.languageSpecificValue(result.metadata, 'kuvaus', 'EN');


                scope.instructionsfi = model.languageSpecificValue(result.metadata, 'kayttoohje', 'FI');
                scope.instructionssv = model.languageSpecificValue(result.metadata, 'kayttoohje', 'SV');
                scope.instructionsen = model.languageSpecificValue(result.metadata, 'kayttoohje', 'EN');

                scope.conceptfi = model.languageSpecificValue(result.metadata, 'kasite', 'FI');
                scope.conceptsv = model.languageSpecificValue(result.metadata, 'kasite', 'SV');
                scope.concepten = model.languageSpecificValue(result.metadata, 'kasite', 'EN');

                scope.totakenoticeoffi = model.languageSpecificValue(result.metadata, 'huomioitavaKoodi', 'FI');
                scope.totakenoticeofsv = model.languageSpecificValue(result.metadata, 'huomioitavaKoodi', 'SV');
                scope.totakenoticeofen = model.languageSpecificValue(result.metadata, 'huomioitavaKoodi', 'EN');

                scope.containssignificancefi = model.languageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'FI');
                scope.containssignificancesv = model.languageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'SV');
                scope.containssignificanceen = model.languageSpecificValue(result.metadata, 'sisaltaaMerkityksen', 'EN');

                scope.doesnotcontainsignificancefi = model.languageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'FI');
                scope.doesnotcontainsignificancesv = model.languageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'SV');
                scope.doesnotcontainsignificanceen = model.languageSpecificValue(result.metadata, 'eiSisallaMerkitysta', 'EN');

                scope.containscodesfi = model.languageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'FI');
                scope.containscodessv = model.languageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'SV');
                scope.containscodesen = model.languageSpecificValue(result.metadata, 'sisaltaaKoodiston', 'EN');


                model.codeElement.withinCodeElements.forEach(function(codelement){
                    model.getLatestCodeElementVersionsByCodeElementUri(codelement,model.withinCodeElements);
                });
                model.codeElement.includesCodeElements.forEach(function(codelement){
                    model.getLatestCodeElementVersionsByCodeElementUri(codelement,model.includesCodeElements);
                });
                model.codeElement.levelsWithCodeElements.forEach(function(codelement){
                    model.getLatestCodeElementVersionsByCodeElementUri(codelement,model.levelsWithCodeElements);
                });
            });
        };

        this.getLatestCodeElementVersionsByCodeElementUri = function(codeElementUri, list) {
            LatestCodeElementVersionsByCodeElementUri.get({codeElementUri: codeElementUri}, function (result) {
                var ce = {};
                ce.uri = codeElementUri;
                ce.value = result.koodiArvo;
                ce.name = model.languageSpecificValue(result.metadata, 'lyhytNimi', 'FI');
                list.push(ce);
            });
        };

        this.languageSpecificValue = function(fieldArray,fieldName,language) {
            return getLanguageSpecificValue(fieldArray,fieldName,language);
        };
        this.removeFromWithinCodeElements = function(codeelement) {
            model.withinRelationToRemove = codeelement;

            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: CodeElementEditorController,
                resolve: {
                }
            });

        };

        this.removeFromIncludesCodeElements = function(codeelement) {
            model.includesRelationToRemove = codeelement;
            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: CodeElementEditorController,
                resolve: {
                }
            });
        };

        this.removeFromLevelsWithCodeElements = function(codeelement) {
            model.levelsRelationToRemove = codeelement;
            model.modalInstance = $modal.open({
                templateUrl: 'confirmModalContent.html',
                controller: CodeElementEditorController,
                resolve: {
                }
            });
        };

        this.getAllCodes = function() {
            RootCodes.get({}, function (result) {
                model.allCodes = result;
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
                if (data.latestKoodistoVersio) {
                    CodeElementsByCodesUriAndVersion.get({codesUri: data.koodistoUri, codesVersion: data.latestKoodistoVersio.versio}, function (result) {
                        data.children = result;
                    });
                }
                iter(data.children);
            }
        };
    };


    return model;
});

function CodeElementEditorController($scope, $location, $routeParams, CodeElementEditorModel, UpdateCodeElement,
                                     AddRelationCodeElement, RemoveRelationCodeElement, ValidateService,
                                     CodesByUriAndVersion,CodeElementsByCodesUriAndVersion,$modal, $filter) {

    $scope.model = CodeElementEditorModel;
    $scope.codeElementUri = $routeParams.codeElementUri;
    $scope.codeElementVersion = $routeParams.codeElementVersion;
    CodeElementEditorModel.init($scope, $scope.codeElementUri, $scope.codeElementVersion);

    $scope.selectallcodelements = false;

    $scope.onMasterChange = function(master){

        for(var i = 0; i < $scope.model.shownCodeElements.length; i++) {
            if ($scope.search($scope.model.shownCodeElements[i])) {
                $scope.model.shownCodeElements[i].checked = master;
            }
        }
    };

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/koodi/"+$scope.codeElementUri+"/"+$scope.codeElementVersion);
    };

    $scope.submit = function() {
        $scope.persistCodes();
    };

    $scope.search = function (item){
        if (!$scope.model.query || item.name.toLowerCase().indexOf($scope.model.query.toLowerCase())!==-1 || item.value.toLowerCase().indexOf($scope.model.query.toLowerCase())!==-1) {
            return true;
        }
        return false;
    };

    $scope.persistCodes = function() {
        var codeelement = {
            koodiUri: $scope.codeElementUri,
            versio: $scope.codeElementVersion,
            voimassaAlkuPvm: $scope.model.codeElement.voimassaAlkuPvm,
            voimassaLoppuPvm: $scope.model.codeElement.voimassaLoppuPvm,
            koodiArvo: $scope.codeValue,
            tila: $scope.model.codeElement.tila,
            version: $scope.model.codeElement.version,

            metadata : [{
                kieli: 'FI',
                nimi: $scope.form.namefi.$viewValue,
                kuvaus: $scope.form.descriptionfi.$viewValue,
                lyhytNimi: $scope.form.shortnamefi.$viewValue,
                kayttoohje: $scope.form.instructionsfi.$viewValue,
                kasite: $scope.form.conceptfi.$viewValue,
                huomioitavaKoodi: $scope.form.totakenoticeoffi.$viewValue,
                sisaltaaMerkityksen: $scope.form.containssignificancefi.$viewValue,
                eiSisallaMerkitysta: $scope.form.doesnotcontainsignificancefi.$viewValue,
                sisaltaaKoodiston: $scope.form.containscodesfi.$viewValue
            }]
        };
        if ($scope.form.namesv && $scope.form.namesv.$viewValue) {
            codeelement.metadata.push({
                kieli: 'SV',
                nimi: $scope.form.namesv.$viewValue,
                kuvaus: $scope.form.descriptionsv.$viewValue,
                lyhytNimi: $scope.form.shortnamesv.$viewValue,
                kayttoohje: $scope.form.instructionssv.$viewValue,
                kasite: $scope.form.conceptsv.$viewValue,
                huomioitavaKoodi: $scope.form.totakenoticeofsv.$viewValue,
                sisaltaaMerkityksen: $scope.form.containssignificancesv.$viewValue,
                eiSisallaMerkitysta: $scope.form.doesnotcontainsignificancesv.$viewValue,
                sisaltaaKoodiston: $scope.form.containscodessv.$viewValue
            });
        }
        if ($scope.form.nameen && $scope.form.nameen.$viewValue) {
            codeelement.metadata.push({
                kieli: 'EN',
                nimi: $scope.form.nameen.$viewValue,
                kuvaus: $scope.form.descriptionen.$viewValue,
                lyhytNimi: $scope.form.shortnameen.$viewValue,
                kayttoohje: $scope.form.instructionsen.$viewValue,
                kasite: $scope.form.concepten.$viewValue,
                huomioitavaKoodi: $scope.form.totakenoticeofen.$viewValue,
                sisaltaaMerkityksen: $scope.form.containssignificanceen.$viewValue,
                eiSisallaMerkitysta: $scope.form.doesnotcontainsignificanceen.$viewValue,
                sisaltaaKoodiston: $scope.form.containscodesen.$viewValue
            });
        }
        UpdateCodeElement.put({}, codeelement, function(result) {
            $location.path("/koodi/"+result.koodiUri+"/"+result.versio);
        }, function(error) {
            ValidateService.validateCodeElement($scope,error,true);
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
            $scope.instructionssv = $scope.form.instructionsfi.$viewValue;
            $scope.instructionsen = $scope.form.instructionsfi.$viewValue;
        } else if (name === 'concept' && !$scope.sameconcept) {
            $scope.conceptsv = $scope.form.conceptfi.$viewValue;
            $scope.concepten = $scope.form.conceptfi.$viewValue;
        } else if (name === 'totakenoticeof' && !$scope.sametotakenoticeof) {
            $scope.totakenoticeofsv = $scope.form.totakenoticeoffi.$viewValue;
            $scope.totakenoticeofen = $scope.form.totakenoticeoffi.$viewValue;
        } else if (name === 'containssignificance' && !$scope.samecontainssignificance) {
            $scope.containssignificancesv = $scope.form.containssignificancefi.$viewValue;
            $scope.containssignificanceen = $scope.form.containssignificancefi.$viewValue;
        } else if (name === 'doesnotcontainsignificance' && !$scope.samedoesnotcontainsignificance) {
            $scope.doesnotcontainsignificancesv = $scope.form.doesnotcontainsignificancefi.$viewValue;
            $scope.doesnotcontainsignificanceen = $scope.form.doesnotcontainsignificancefi.$viewValue;
        } else if (name === 'containscodes' && !$scope.samecontainscodes) {
            $scope.containscodessv = $scope.form.containscodesfi.$viewValue;
            $scope.containscodesen = $scope.form.containscodesfi.$viewValue;
        }
    };


    $scope.createCodes = function(data) {
        var ce = {};
        ce.uri = data.uri;
        ce.name = data.name
        ce.value = data.value;
        return ce;
    };

    $scope.addToWithinCodeElement = function(data) {
        var ce = {};
        ce = $scope.createCodes(data);

        var found = false;
        $scope.model.withinCodeElements.forEach(function(codeElement, index){
            if (codeElement.uri.indexOf(data.uri) !== -1) {
                found = true;
            }
        });

        if (found === false) {
            $scope.model.withinCodeElements.push(ce);
            AddRelationCodeElement.put({codeElementUri: data.uri,
                codeElementUriToAdd: $scope.model.codeElement.koodiUri,relationType: "SISALTYY"},function(result) {
            });
        }
    };

    $scope.addToIncludesCodeElement = function(data) {
        var ce = {};
        ce = $scope.createCodes(data);
        var found = false;
        $scope.model.includesCodeElements.forEach(function(codeElement, index){
            if (codeElement.uri.indexOf(data.uri) !== -1) {
                found = true;
            }
        });

        if (found === false) {
            $scope.model.includesCodeElements.push(ce);
            AddRelationCodeElement.put({codeElementUri: $scope.model.codeElement.koodiUri,
                codeElementUriToAdd: data.uri,relationType: "SISALTYY"},function(result) {

            });
        }
    };
    $scope.addToLevelsWithCodeElement = function(data) {
        var ce = {};
        ce = $scope.createCodes(data);
        var found = false;
        $scope.model.levelsWithCodeElements.forEach(function(codeElement, index){
            if (codeElement.uri.indexOf(data.uri) !== -1) {
                found = true;
            }
        });
        if (found === false) {
            $scope.model.levelsWithCodeElements.push(ce);
            AddRelationCodeElement.put({codeElementUri: data.uri,
                codeElementUriToAdd: $scope.model.codeElement.koodiUri,relationType: "RINNASTEINEN"},function(result) {

            });
        }
    };


    $scope.cancelcodeelement = function () {
        $scope.model.codeelementmodalInstance.dismiss('cancel');
    };

    $scope.okcodeelement = function() {
        var selectedItems = $filter('filter')($scope.model.shownCodeElements, {checked: true});
        if ($scope.model.addToListName === 'withincodes') {
            selectedItems.forEach(function(codeElement){
                $scope.addToWithinCodeElement(codeElement);
            });

        } else if ($scope.model.addToListName === 'includescodes') {
            selectedItems.forEach(function(codeElement){
                $scope.addToIncludesCodeElement(codeElement);
            });

        } else if ($scope.model.addToListName === 'levelswithcodes') {
            selectedItems.forEach(function(codeElement){
                $scope.addToLevelsWithCodeElement(codeElement);
            });
        }
        $scope.model.codeelementmodalInstance.close();
    }


    $scope.show = function(name){
        $scope.model.addToListName = name;
        if ($scope.model.allWithinCodeElements.length === 0 || $scope.model.allIncludesCodeElements.length === 0 ||
            $scope.model.allLevelsWithCodeElements.length === 0) {
            CodesByUriAndVersion.get({codesUri: $scope.model.codeElement.koodisto.koodistoUri, codesVersion: 0}, function (result) {
                if (name === 'withincodes') {
                    if ($scope.model.allWithinCodeElements.length === 0) {
                        result.withinCodes.forEach(function(codes){
                            CodeElementsByCodesUriAndVersion.get({codesUri: codes, codesVersion: 0}, function (result2) {
                                result2.forEach(function(codeElement){
                                     var ce = {};
                                     ce.uri = codeElement.koodiUri;
                                     ce.value = codeElement.koodiArvo;
                                     ce.name = $scope.model.languageSpecificValue(codeElement.metadata, 'lyhytNimi', 'FI');
                                     $scope.model.allWithinCodeElements.push(ce);
                                });

                                $scope.model.shownCodeElements = $scope.model.allWithinCodeElements;

                             });

                        });
                    }
                    $scope.model.shownCodeElements = $scope.model.allWithinCodeElements;

                } else if (name === 'includescodes') {
                    if ($scope.model.allIncludesCodeElements.length === 0) {
                        result.includesCodes.forEach(function(codes){
                            CodeElementsByCodesUriAndVersion.get({codesUri: codes, codesVersion: 0}, function (result2) {
                                result2.forEach(function(codeElement){
                                    var ce = {};
                                    ce.uri = codeElement.koodiUri;
                                    ce.value = codeElement.koodiArvo;
                                    ce.name = $scope.model.languageSpecificValue(codeElement.metadata, 'lyhytNimi', 'FI');
                                    $scope.model.allIncludesCodeElements.push(ce);
                                });

                                $scope.model.shownCodeElements = $scope.model.allIncludesCodeElements;

                            });

                        });
                    }
                    $scope.model.shownCodeElements = $scope.model.allIncludesCodeElements;


                } else if (name === 'levelswithcodes') {
                    if ($scope.model.allLevelsWithCodeElements.length === 0) {
                        result.levelsWithCodes.forEach(function(codes){
                            CodeElementsByCodesUriAndVersion.get({codesUri: codes, codesVersion: 0}, function (result2) {
                                result2.forEach(function(codeElement){
                                    var ce = {};
                                    ce.uri = codeElement.koodiUri;
                                    ce.value = codeElement.koodiArvo;
                                    ce.name = $scope.model.languageSpecificValue(codeElement.metadata, 'lyhytNimi', 'FI');
                                    $scope.model.allLevelsWithCodeElements.push(ce);
                                });

                                $scope.model.shownCodeElements = $scope.model.allLevelsWithCodeElements;

                            });

                        });
                    }
                    $scope.model.shownCodeElements = $scope.model.allLevelsWithCodeElements;

                }

                $scope.model.codeelementmodalInstance = $modal.open({
                    templateUrl: 'codeElementModalContent.html',
                    controller: CodeElementEditorController,
                    resolve: {
                    }
                });

            });
        }
    }

    $scope.okconfirm = function() {
        if ($scope.model.withinRelationToRemove && $scope.model.withinRelationToRemove.uri !== "") {
            $scope.model.withinCodeElements.forEach(function(codeElement, index){
                if (codeElement.uri.indexOf($scope.model.withinRelationToRemove.uri) !== -1) {
                    $scope.model.withinCodeElements.splice(index,1);
                }
            });


            RemoveRelationCodeElement.put({codeElementUri: $scope.model.withinRelationToRemove.uri,
                codeElementUriToRemove: $scope.model.codeElement.koodiUri,relationType: "SISALTYY"},function(result) {

            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodien v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.includesRelationToRemove && $scope.model.includesRelationToRemove.uri !== "") {
            $scope.model.includesCodeElements.forEach(function(codeElement, index){
                if (codeElement.uri.indexOf($scope.model.includesRelationToRemove.uri) !== -1) {
                    $scope.model.includesCodeElements.splice(index,1);
                }
            });

            RemoveRelationCodeElement.put({codeElementUri: $scope.model.codeElement.koodiUri,
                codeElementUriToRemove: $scope.model.includesRelationToRemove.uri,relationType: "SISALTYY"},function(result) {

            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodien v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.levelsRelationToRemove && $scope.model.levelsRelationToRemove.uri !== "") {
            $scope.model.levelsWithCodeElements.forEach(function(codeElement, index){
                if (codeElement.uri.indexOf($scope.model.levelsRelationToRemove.uri) !== -1) {
                    $scope.model.levelsWithCodeElements.splice(index,1);
                }
            });

            RemoveRelationCodeElement.put({codeElementUri: $scope.model.levelsRelationToRemove.uri,
                codeElementUriToRemove: $scope.model.codeElement.koodiUri,relationType: "RINNASTEINEN"},function(result) {
            }, function(error) {
                var alert = { type: 'danger', msg: 'Koodien v\u00E4lisen suhteen poistaminen ep\u00E4onnistui' }
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
