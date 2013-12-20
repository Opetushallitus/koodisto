
app.factory('CodeElementCreatorModel', function($location) {
    var model;
    model = new function() {
        this.init = function() {

        };


        this.languageSpecificValue = function(fieldArray,fieldName,language) {
            return getLanguageSpecificValue(fieldArray,fieldName,language);
        };


    };


    return model;
});

function CodeElementCreatorController($scope, $location, $routeParams, CodeElementCreatorModel, NewCodeElement) {
    $scope.model = CodeElementCreatorModel;
    $scope.codesUri = $routeParams.codesUri;
    $scope.codesVersion = $routeParams.codesVersion;
    CodeElementCreatorModel.init();

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
                lyhytNimi: $scope.form.shortnamefi.$viewValue
            }]
        };
        if ($scope.form.namesv && $scope.form.namesv.$viewValue) {
            codeelement.metadata.push({
                kieli: 'SV',
                nimi: $scope.form.namesv.$viewValue,
                kuvaus: $scope.form.descriptionsv.$viewValue,
                lyhytNimi: $scope.form.shortnamesv.$viewValue
            });
        }
        if ($scope.form.nameen && $scope.form.nameen.$viewValue) {
            codeelement.metadata.push({
                kieli: 'EN',
                nimi: $scope.form.nameen.$viewValue,
                kuvaus: $scope.form.descriptionen.$viewValue,
                lyhytNimi: $scope.form.shortnameen.$viewValue
            });
        }
        NewCodeElement.put({codesUri: $scope.codesUri}, codeelement, function(result) {
            $location.path("/koodi/"+result.koodiUri+"/"+result.versio);
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
        }
    };

}
