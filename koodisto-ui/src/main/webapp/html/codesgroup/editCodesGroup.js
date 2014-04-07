
app.factory('CodesGroupEditorModel', function($location, CodesGroupByUri) {
    var model;
    model = new function() {
        this.alerts = [];
        this.codesgroup = {};
        this.init = function(scope, id) {
            this.alerts = [];
            CodesGroupByUri.get({id: id}, function (result) {
                model.codesgroup = result;
                scope.namefi = getLanguageSpecificValue(result.koodistoRyhmaMetadatas, 'nimi', 'FI');
                scope.namesv = getLanguageSpecificValue(result.koodistoRyhmaMetadatas, 'nimi', 'SV');
                scope.nameen = getLanguageSpecificValue(result.koodistoRyhmaMetadatas, 'nimi', 'EN');
            });
        };

    };


    return model;
});

function CodesGroupEditorController($scope, $location, $routeParams, CodesGroupEditorModel, UpdateCodesGroup, Treemodel) {
    $scope.model = CodesGroupEditorModel;
    CodesGroupEditorModel.init($scope, $routeParams.id);

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/koodistoryhma/"+$routeParams.id);
    };

    $scope.submit = function() {
        $scope.persistCodesGroup();
    };

    $scope.persistCodesGroup = function() {
        var codesgroup = {
            id: $routeParams.id,
            koodistoRyhmaUri: $scope.model.codesgroup.koodistoRyhmaUri,
            koodistoRyhmaMetadatas : [{
                kieli: 'FI',
                nimi: $scope.namefi
            }]
        };
        codesgroup.koodistoRyhmaMetadatas.push({
            kieli: 'SV',
            nimi: $scope.namesv
        });
        codesgroup.koodistoRyhmaMetadatas.push({
            kieli: 'EN',
            nimi: $scope.nameen
        });
        UpdateCodesGroup.put({}, codesgroup, function(result) {
            Treemodel.refresh();
            $location.path("/koodistoryhma/"+result.id);
        }, function(error) {
            var alert = { type: 'danger', msg: jQuery.i18n.prop(error.data) };
            $scope.model.alerts.push(alert);
        });
    };
}

