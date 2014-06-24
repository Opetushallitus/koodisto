app.factory('CodeElementEditorModel', function($modal, $location, RootCodes, CodeElementByUriAndVersion, AllCodes, CodeElementsByCodesUriAndVersion,
        RemoveRelationCodeElement, LatestCodeElementVersionsByCodeElementUri, AuthService) {
    var model;
    model = new function() {
        this.states = [ {
            key : 'PASSIIVINEN',
            value : 'PASSIIVINEN'
        }, {
            key : 'LUONNOS',
            value : 'LUONNOS'
        } ];

        this.allCodes = [];
        this.withinCodeElements = [];
        this.includesCodeElements = [];
        this.levelsWithCodeElements = [];
        this.alerts = [];
        this.allWithinCodeElements = [];
        this.allIncludesCodeElements = [];
        this.allLevelsWithCodeElements = [];
        this.showCode = '';

        this.init = function(scope, codeElementUri, codeElementVersion) {
            this.allCodes = [];
            this.withinCodeElements = [];
            this.includesCodeElements = [];
            this.levelsWithCodeElements = [];
            this.alerts = [];
            this.allWithinCodeElements = [];
            this.allIncludesCodeElements = [];
            this.allLevelsWithCodeElements = [];
            this.shownCodeElements = [];
            this.loadingCodeElements = false;

            // Pagination
            this.currentPage = 0;
            this.pageSize = 10;
            this.pageSizeOptions = [ 10, 50, 100, 200, 500 ];
            this.sortOrder = "value";
            this.sortOrderSelection = 1;
            this.sortOrderReversed = false;

            model.getAllCodes();
            model.getCodeElement(scope, codeElementUri, codeElementVersion);

        };

        this.getCodeElement = function(scope, codeElementUri, codeElementVersion) {
            CodeElementByUriAndVersion.get({
                codeElementUri : codeElementUri,
                codeElementVersion : codeElementVersion
            }, function(result) {
        	
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

                model.codeElement.withinCodeElements.forEach(function(codeElement) {
                    model.extractAndPushCodeElementInformation(codeElement, model.withinCodeElements);
                });
                model.codeElement.includesCodeElements.forEach(function(codeElement) {
                    model.extractAndPushCodeElementInformation(codeElement, model.includesCodeElements);
                });
                model.codeElement.levelsWithCodeElements.forEach(function(codeElement) {
                    model.extractAndPushCodeElementInformation(codeElement, model.levelsWithCodeElements);
                });

            });
        };

        this.extractAndPushCodeElementInformation = function(codeElement, list) {
            var ce = {};
            ce.uri = codeElement.codeElementUri;
            ce.name = model.languageSpecificValue(codeElement.relationMetadata, 'nimi', 'FI');
            ce.value = codeElement.codeElementValue;
            ce.versio = codeElement.codeElementVersion;
            ce.codesname = model.languageSpecificValue(codeElement.parentMetadata, 'nimi', 'FI');
            list.push(ce);
        };

        this.languageSpecificValue = function(fieldArray, fieldName, language) {
            return getLanguageSpecificValue(fieldArray, fieldName, language);
        };
        this.removeFromWithinCodeElements = function(codeelement) {
            model.withinRelationToRemove = codeelement;

            model.modalInstance = $modal.open({
                templateUrl : 'confirmModalContent.html',
                controller : CodeElementEditorController,
                resolve : {
                    isModalController : function() {
                        return true;
                    }
                }
            });

        };

        this.removeFromIncludesCodeElements = function(codeelement) {
            model.includesRelationToRemove = codeelement;
            model.modalInstance = $modal.open({
                templateUrl : 'confirmModalContent.html',
                controller : CodeElementEditorController,
                resolve : {
                    isModalController : function() {
                        return true;
                    }
                }
            });
        };

        this.removeFromLevelsWithCodeElements = function(codeelement) {
            model.levelsRelationToRemove = codeelement;
            model.modalInstance = $modal.open({
                templateUrl : 'confirmModalContent.html',
                controller : CodeElementEditorController,
                resolve : {
                    isModalController : function() {
                        return true;
                    }
                }
            });
        };

        this.filterCodes = function() {
            for (var i = 0; i < model.allCodes.length; i++) {
                var koodistos = model.allCodes[i].koodistos;
                var temp = [];
                if (koodistos) {
                    for (var j = 0; j < koodistos.length; j++) {
                        var koodisto = koodistos[j];
                        // Vain ne koodit näytetään, jotka ovat samssa organisaatiossa tämän kanssa
                        if (koodisto.organisaatioOid === model.codeElement.koodisto.organisaatioOid) {
                            temp.push(koodisto);
                        }
                    }
                    model.allCodes[i].koodistos = temp;
                }
            }
        };

        this.getAllCodes = function() {
            RootCodes.get({}, function(result) {
                model.allCodes = result;
                AuthService.updateOph(SERVICE_NAME).then(function() {
                }, model.filterCodes);
            });
        };

        this.openChildren = function(data) {
            data.open = !data.open;
            if (data.open) {

                var iter = function(children) {
                    if (children) {
                        children.forEach(function(child) {
                            child.open = true;

                        });
                    }
                };
                if (data.latestKoodistoVersio) {
                    CodeElementsByCodesUriAndVersion.get({
                        codesUri : data.koodistoUri,
                        codesVersion : data.latestKoodistoVersio.versio
                    }, function(result) {
                        data.children = result;
                    });
                }
                iter(data.children);
            }
        };
    };

    return model;
});

function CodeElementEditorController($scope, $location, $routeParams, $filter, CodeElementEditorModel, UpdateCodeElement, AddRelationCodeElement,
        RemoveRelationCodeElement, MassRemoveRelationCodeElements, ValidateService, CodesByUriAndVersion, CodeElementsByCodesUriAndVersion, $modal, isModalController) {

    $scope.model = CodeElementEditorModel;
    $scope.codeElementUri = $routeParams.codeElementUri;
    $scope.codeElementVersion = $routeParams.codeElementVersion;
    $scope.sortBy = 'name';

    if (!isModalController) {
        CodeElementEditorModel.init($scope, $scope.codeElementUri, $scope.codeElementVersion);
    }

    $scope.selectallcodelements = false;

    $scope.onMasterChange = function(master) {

        for (var i = 0; i < $scope.model.shownCodeElements.length; i++) {
            if ($scope.search($scope.model.shownCodeElements[i])) {
                $scope.model.shownCodeElements[i].checked = master;
            }
        }
    };

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/koodi/" + $scope.codeElementUri + "/" + $scope.codeElementVersion).search({forceRefresh: true});
    };

    $scope.submit = function() {
        $scope.persistCodes();
    };

    $scope.search = function(item) {
        if (!$scope.model.query || item.name.toLowerCase().indexOf($scope.model.query.toLowerCase()) !== -1
                || item.value.toLowerCase().indexOf($scope.model.query.toLowerCase()) !== -1) {
            return true;
        }
        return false;
    };

    $scope.persistCodes = function() {
        var codeelement = {
            koodiUri : $scope.codeElementUri,
            versio : $scope.codeElementVersion,
            voimassaAlkuPvm : $scope.model.codeElement.voimassaAlkuPvm,
            voimassaLoppuPvm : $scope.model.codeElement.voimassaLoppuPvm,
            koodiArvo : $scope.codeValue,
            tila : $scope.model.codeElement.tila,
            version : $scope.model.codeElement.version,

            metadata : [ {
                kieli : 'FI',
                nimi : $scope.namefi,
                kuvaus : $scope.descriptionfi,
                lyhytNimi : $scope.shortnamefi,
                kayttoohje : $scope.instructionsfi,
                kasite : $scope.conceptfi,
                huomioitavaKoodi : $scope.totakenoticeoffi,
                sisaltaaMerkityksen : $scope.containssignificancefi,
                eiSisallaMerkitysta : $scope.doesnotcontainsignificancefi,
                sisaltaaKoodiston : $scope.containscodesfi
            } ]
        };
        if ($scope.namesv) {
            codeelement.metadata.push({
                kieli : 'SV',
                nimi : $scope.namesv,
                kuvaus : $scope.descriptionsv,
                lyhytNimi : $scope.shortnamesv,
                kayttoohje : $scope.instructionssv,
                kasite : $scope.conceptsv,
                huomioitavaKoodi : $scope.totakenoticeofsv,
                sisaltaaMerkityksen : $scope.containssignificancesv,
                eiSisallaMerkitysta : $scope.doesnotcontainsignificancesv,
                sisaltaaKoodiston : $scope.containscodessv
            });
        }
        if ($scope.nameen) {
            codeelement.metadata.push({
                kieli : 'EN',
                nimi : $scope.nameen,
                kuvaus : $scope.descriptionen,
                lyhytNimi : $scope.shortnameen,
                kayttoohje : $scope.instructionsen,
                kasite : $scope.concepten,
                huomioitavaKoodi : $scope.totakenoticeofen,
                sisaltaaMerkityksen : $scope.containssignificanceen,
                eiSisallaMerkitysta : $scope.doesnotcontainsignificanceen,
                sisaltaaKoodiston : $scope.containscodesen
            });
        }
        UpdateCodeElement.put({}, codeelement, function(result) {
            $location.path("/koodi/" + result.koodiUri + "/" + result.versio).search({forceRefresh: true});
        }, function(error) {
            ValidateService.validateCodeElement($scope, error, true);
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
            $scope.instructionssv = $scope.instructionsfi;
            $scope.instructionsen = $scope.instructionsfi;
        } else if (name === 'concept' && !$scope.sameconcept) {
            $scope.conceptsv = $scope.conceptfi;
            $scope.concepten = $scope.conceptfi;
        } else if (name === 'totakenoticeof' && !$scope.sametotakenoticeof) {
            $scope.totakenoticeofsv = $scope.totakenoticeoffi;
            $scope.totakenoticeofen = $scope.totakenoticeoffi;
        } else if (name === 'containssignificance' && !$scope.samecontainssignificance) {
            $scope.containssignificancesv = $scope.containssignificancefi;
            $scope.containssignificanceen = $scope.containssignificancefi;
        } else if (name === 'doesnotcontainsignificance' && !$scope.samedoesnotcontainsignificance) {
            $scope.doesnotcontainsignificancesv = $scope.doesnotcontainsignificancefi;
            $scope.doesnotcontainsignificanceen = $scope.doesnotcontainsignificancefi;
        } else if (name === 'containscodes' && !$scope.samecontainscodes) {
            $scope.containscodessv = $scope.containscodesfi;
            $scope.containscodesen = $scope.containscodesfi;
        }
    };

    $scope.createCodes = function(data) {
        var ce = {};
        ce.uri = data.uri;
        ce.name = data.name;
        ce.value = data.value;
        return ce;
    };

    $scope.addRelationCodeElement = function(codeElementToAdd, collectionToAddTo, relationTypeString, modelCodeElementIsHost) {
        var found = false;
        collectionToAddTo.forEach(function(codeElement, index) {
            if (codeElement.uri.indexOf(codeElementToAdd.uri) !== -1) {
                found = true;
            }
        });
        if (found === false) {
            var uriToBeAddedTo = modelCodeElementIsHost ? $scope.model.codeElement.koodiUri : codeElementToAdd.uri;
            var uriToBeAdded = modelCodeElementIsHost ? codeElementToAdd.uri : $scope.model.codeElement.koodiUri;
            AddRelationCodeElement.put({
                codeElementUri : uriToBeAddedTo,
                codeElementUriToAdd : uriToBeAdded,
                relationType : relationTypeString
            }, function(result) {
                collectionToAddTo.push($scope.createCodes(codeElementToAdd));
            }, function(result) {
                var alert = {
                    type : 'danger',
                    msg : 'Koodien v\u00E4lisen suhteen luominen ep\u00E4onnistui'
                };
                $scope.model.alerts.push(alert);
            });
        }
    };

    $scope.removeRelationsCodeElement = function(unselectedItems, collectionToRemoveFrom, relationTypeString, modelCodeElementIsHost) {
        var itemsToRemove = [];
        unselectedItems.forEach(function(codeElement) {
            collectionToRemoveFrom.forEach(function(innerCodeElement) {
                if (codeElement.uri == innerCodeElement.uri) {
                    itemsToRemove.push(innerCodeElement.uri);
                }
            });
        });
        
        if (itemsToRemove.length < 1) {
            return;
        }

        MassRemoveRelationCodeElements.remove({
            codeElementUri : $scope.model.codeElement.koodiUri,
            relationType : relationTypeString,
            isChild : !modelCodeElementIsHost,
            relationsToRemove : itemsToRemove
        }, function(result) {
            itemsToRemove.forEach(function(item) {
        	collectionToRemoveFrom.splice(jQuery.grep(collectionToRemoveFrom, function(from) {
        	    return from.uri = item;
        	}), 1);
            });
        }, function(error) {
            var alert = {
        	    type : 'danger',
        	    msg : 'Koodien v\u00E4lisen suhteen poistaminen ep\u00E4onnistui'
            };
            $scope.model.alerts.push(alert);
        });
        
    };

    $scope.cancelcodeelement = function() {
        $scope.model.codeelementmodalInstance.dismiss('cancel');
    };

    $scope.okcodeelement = function() {
        var selectedItems = $filter('filter')($scope.model.shownCodeElements, {
            checked : true
        });
        var unselectedItems = $filter('filter')($scope.model.shownCodeElements, {
            checked : false
        });
        if ($scope.model.addToListName === 'withincodes') {
            selectedItems.forEach(function(codeElement) {
                $scope.addRelationCodeElement(codeElement, $scope.model.withinCodeElements, "SISALTYY");
            });
            $scope.removeRelationsCodeElement(unselectedItems, $scope.model.withinCodeElements, "SISALTYY");

        } else if ($scope.model.addToListName === 'includescodes') {
            selectedItems.forEach(function(codeElement) {
                $scope.addRelationCodeElement(codeElement, $scope.model.includesCodeElements, "SISALTYY", true);
            });
            $scope.removeRelationsCodeElement(unselectedItems, $scope.model.includesCodeElements, "SISALTYY", true);

        } else if ($scope.model.addToListName === 'levelswithcodes') {
            selectedItems.forEach(function(codeElement) {
                $scope.addRelationCodeElement(codeElement, $scope.model.levelsWithCodeElements, "RINNASTEINEN");
            });
            $scope.removeRelationsCodeElement(unselectedItems, $scope.model.levelsWithCodeElements, "RINNASTEINEN");
        }
        $scope.model.codeelementmodalInstance.close();
    };

    showCodeElementsInCodeSet = function(toBeShown, existingSelections) {
        toBeShown = [];
        CodeElementsByCodesUriAndVersion.get({
            codesUri : $scope.model.showCode,
            codesVersion : 0
        }, function(result2) {
            result2.forEach(function(codeElement) {
                var ce = {};
                ce.uri = codeElement.koodiUri;
                ce.checked = jQuery.grep(existingSelections, function(element) {
                    return codeElement.koodiUri == element.uri;
                }).length > 0;
                ce.value = codeElement.koodiArvo;
                ce.name = $scope.model.languageSpecificValue(codeElement.metadata, 'nimi', 'FI');
                toBeShown.push(ce);
            });

            $scope.model.shownCodeElements = toBeShown;
            $scope.updatePaginationPage(true);
            $scope.model.loadingCodeElements = false;
        });
    };

    $scope.getCodeElements = function() {
        $scope.model.loadingCodeElements = true;
        var name = $scope.model.addToListName;
        CodesByUriAndVersion.get({
            codesUri : $scope.model.codeElement.koodisto.koodistoUri,
            codesVersion : 0
        }, function(result) {

            function getCodesUris(relationArray) {
                var codesUris = [];
                relationArray.forEach(function(value) {
                    codesUris.push(value.codesUri);
                });
                return codesUris;
            }

            if (name === 'withincodes') {
                if ($scope.model.showCode && $scope.model.showCode.length > 0) {
                    showCodeElementsInCodeSet($scope.model.allWithinCodeElements, $scope.model.withinCodeElements);
                }
                $scope.model.shownCodes = getCodesUris(result.withinCodes);
                $scope.model.shownCodeElements = $scope.model.allWithinCodeElements;

            } else if (name === 'includescodes') {
                if ($scope.model.showCode && $scope.model.showCode.length > 0) {
                    showCodeElementsInCodeSet($scope.model.allIncludesCodeElements, $scope.model.includesCodeElements);
                }
                $scope.model.shownCodes = getCodesUris(result.includesCodes);
                $scope.model.shownCodeElements = $scope.model.allIncludesCodeElements;

            } else if (name === 'levelswithcodes') {
                if ($scope.model.showCode && $scope.model.showCode.length > 0) {
                    showCodeElementsInCodeSet($scope.model.allLevelsWithCodeElements, $scope.model.levelsWithCodeElements);
                }
                $scope.model.shownCodes = getCodesUris(result.levelsWithCodes);
                $scope.model.shownCodeElements = $scope.model.allLevelsWithCodeElements;
            }
            if(!$scope.model.showCode || $scope.model.showCode.length === 0){
                $scope.model.loadingCodeElements = false;
            }
        });
    };

    $scope.show = function(name) {
        $scope.model.showCode = '';
        $scope.model.addToListName = name;
        if ($scope.model.allWithinCodeElements.length === 0 || $scope.model.allIncludesCodeElements.length === 0
                || $scope.model.allLevelsWithCodeElements.length === 0) {
            
            $scope.getCodeElements();

            $scope.model.codeelementmodalInstance = $modal.open({
                templateUrl : 'codeElementModalContent.html',
                controller : CodeElementEditorController,
                resolve : {
                    isModalController : function() {
                        return true;
                    }
                }
            });
        }
    };

    $scope.okconfirm = function() {
        if ($scope.model.withinRelationToRemove && $scope.model.withinRelationToRemove.uri !== "") {
            $scope.model.withinCodeElements.forEach(function(codeElement, index) {
                if (codeElement.uri.indexOf($scope.model.withinRelationToRemove.uri) !== -1) {
                    $scope.model.withinCodeElements.splice(index, 1);
                }
            });

            RemoveRelationCodeElement.put({
                codeElementUri : $scope.model.withinRelationToRemove.uri,
                codeElementUriToRemove : $scope.model.codeElement.koodiUri,
                relationType : "SISALTYY"
            }, function(result) {

            }, function(error) {
                var alert = {
                    type : 'danger',
                    msg : 'Koodien v\u00E4lisen suhteen poistaminen ep\u00E4onnistui'
                };
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.includesRelationToRemove && $scope.model.includesRelationToRemove.uri !== "") {
            $scope.model.includesCodeElements.forEach(function(codeElement, index) {
                if (codeElement.uri.indexOf($scope.model.includesRelationToRemove.uri) !== -1) {
                    $scope.model.includesCodeElements.splice(index, 1);
                }
            });

            RemoveRelationCodeElement.put({
                codeElementUri : $scope.model.codeElement.koodiUri,
                codeElementUriToRemove : $scope.model.includesRelationToRemove.uri,
                relationType : "SISALTYY"
            }, function(result) {

            }, function(error) {
                var alert = {
                    type : 'danger',
                    msg : 'Koodien v\u00E4lisen suhteen poistaminen ep\u00E4onnistui'
                };
                $scope.model.alerts.push(alert);
            });
        } else if ($scope.model.levelsRelationToRemove && $scope.model.levelsRelationToRemove.uri !== "") {
            $scope.model.levelsWithCodeElements.forEach(function(codeElement, index) {
                if (codeElement.uri.indexOf($scope.model.levelsRelationToRemove.uri) !== -1) {
                    $scope.model.levelsWithCodeElements.splice(index, 1);
                }
            });

            RemoveRelationCodeElement.put({
                codeElementUri : $scope.model.levelsRelationToRemove.uri,
                codeElementUriToRemove : $scope.model.codeElement.koodiUri,
                relationType : "RINNASTEINEN"
            }, function(result) {
            }, function(error) {
                var alert = {
                    type : 'danger',
                    msg : 'Koodien v\u00E4lisen suhteen poistaminen ep\u00E4onnistui'
                };
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

    // Pagination

    // Get the filtered page count
    var cachedPageCount = 0;
    $scope.getNumberOfPages = function() {
        return cachedPageCount;
    };
    
    // Refresh the page count when the model changes
    var cachedElementCount = 0;
    $scope.$watch('model.shownCodeElements', function() {
        if ($scope.model.shownCodeElements.length != cachedElementCount) {
            cachedElementCount = $scope.model.shownCodeElements.length;
            $scope.updatePaginationPage(true);
        }
    });

    // Change the currentPage when the pageSize is changed.
    var oldValueForPageSize = 10;
    $scope.pageSizeChanged = function() {
        var topmostCodeElement = $scope.model.currentPage * oldValueForPageSize;
        $scope.model.currentPage = Math.floor(topmostCodeElement / $scope.model.pageSize);
        oldValueForPageSize = $scope.model.pageSize;
        $scope.updatePaginationPage();
    };

    $scope.sortOrderChanged = function(value) {
        if (value) {
            $scope.model.sortOrderSelection = value;
        }
        var selection = parseInt($scope.model.sortOrderSelection);
        switch (selection) {
        case 1:
            $scope.model.sortOrderReversed = false;
            $scope.model.sortOrder = "value";
            break;
        case 2:
            $scope.model.sortOrderReversed = true;
            $scope.model.sortOrder = "value";
            break;
        case 3:
            $scope.model.sortOrderReversed = false;
            $scope.model.sortOrder = "name";
            break;
        case 4:
            $scope.model.sortOrderReversed = true;
            $scope.model.sortOrder = "name";
            break;

        default:
            break;
        }
        $scope.updatePaginationPage(true);
    };

    // When user changes the search string the page count changes and the current page must be adjusted
    $scope.filterChangedPageCount = function() {
        if ($scope.model.currentPage >= $scope.getNumberOfPages()) {
            $scope.model.currentPage = $scope.getNumberOfPages() - 1;
        }
        if ($scope.getNumberOfPages() != 0 && $scope.model.currentPage < 0) {
            $scope.model.currentPage = 0;
        }
        $scope.updatePaginationPage();
    };

    $scope.changePage = function(i) {
        $scope.model.currentPage = i;
        $scope.updatePaginationPage();
    };

    $scope.incrementPage = function(i) {
        var newPageNumber = $scope.model.currentPage + i;
        if (newPageNumber > -1 && newPageNumber < $scope.getNumberOfPages()) {
            $scope.model.currentPage = newPageNumber;
            $scope.updatePaginationPage();
        }
    };
    

    


        $scope.paginationPage = [];
        $scope.updatePaginationPage = function(refreshPage) {
        if (refreshPage) {
            // Only do sorting when the model has changed, heavy operation
            refreshPage = false;
            $scope.model.shownCodeElements = $filter("naturalSort")($scope.model.shownCodeElements, $scope.model.sortOrder, $scope.model.sortOrderReversed);
        }
        var results = $scope.model.shownCodeElements;
        results = $filter("filter")(results, $scope.search);
        cachedPageCount = Math.ceil(results.length / $scope.model.pageSize);
        results = results.splice($scope.model.currentPage * $scope.model.pageSize, $scope.model.pageSize);
        cachedShownCodeElements = $scope.model.shownCodeElements;
        $scope.paginationPage = results;
    };

    // Pagination ends
}
