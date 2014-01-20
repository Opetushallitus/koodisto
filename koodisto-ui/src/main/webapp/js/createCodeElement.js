
app.factory('CodeElementCreatorModel', function($location) {
    var model;
    model = new function() {
        this.alerts = [];

        this.init = function() {
            this.alerts = [];
        };


        this.languageSpecificValue = function(fieldArray,fieldName,language) {
            return getLanguageSpecificValue(fieldArray,fieldName,language);
        };


    };


    return model;
});

function CodeElementCreatorController($scope, $location, $routeParams, CodeElementCreatorModel, NewCodeElement, ValidateService) {
    $scope.model = CodeElementCreatorModel;
    $scope.codesUri = $routeParams.codesUri;
    $scope.codesVersion = $routeParams.codesVersion;
    CodeElementCreatorModel.init();

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
        var codeelement = {
            voimassaAlkuPvm: $scope.dActiveStart,
            voimassaLoppuPvm: $scope.dActiveEnd,
            koodiArvo: $scope.codeValue,
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
        NewCodeElement.put({codesUri: $scope.codesUri}, codeelement, function(result) {
            $location.path("/koodi/"+result.koodiUri+"/"+result.versio);
        }, function(error) {
            ValidateService.validateCodeElement($scope,error,false);
        });
    };

    $scope.setSameValue = function(name) {
        if (name === 'name' && !$scope.samename) {
            $scope.namesv = $scope.form.namefi.$viewValue;
            $scope.nameen = $scope.form.namefi.$viewValue;
        } else if (name === 'description' && !$scope.samedescription) {
            $scope.descriptionsv = $scope.form.descriptionfi.$viewValue;
            $scope.descriptionen = $scope.form.descriptionfi.$viewValue;
        } else if (name === 'shortname' && !$scope.sameshortname) {
            $scope.shortnamesv = $scope.form.shortnamefi.$viewValue;
            $scope.shortnameen = $scope.form.shortnamefi.$viewValue;
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

}
