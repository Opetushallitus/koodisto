app.factory('CodesGroupEditorModel', function($location, CodesGroupByUri) {
    var model;
    model = new function() {
        this.alerts = [];
        this.codesgroup = {};
        this.init = function(scope, id) {
            this.alerts = [];
            CodesGroupByUri.get({
                id : id
            }, function(result) {
                model.codesgroup = result;
                scope.model.namefi = getLanguageSpecificValue(result.koodistoRyhmaMetadatas, 'nimi', 'FI');
                scope.model.namesv = getLanguageSpecificValue(result.koodistoRyhmaMetadatas, 'nimi', 'SV');
                scope.model.nameen = getLanguageSpecificValue(result.koodistoRyhmaMetadatas, 'nimi', 'EN');
            });
            scope.loadingReady = true;
        };

    };

    return model;
});

function CodesGroupEditorController($scope, $location, $routeParams, $filter, CodesGroupEditorModel, UpdateCodesGroup, Treemodel) {
    $scope.model = CodesGroupEditorModel;
    $scope.errorMessage = $filter('i18n')('field.required');
    CodesGroupEditorModel.init($scope, $routeParams.id);

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/koodistoryhma/" + $routeParams.id);
    };

    $scope.submit = function() {
        $scope.persistCodesGroup();
    };

    $scope.persistCodesGroup = function() {
        var codesgroup = {
            id : $routeParams.id,
            koodistoRyhmaUri : $scope.model.codesgroup.koodistoRyhmaUri,
            koodistoRyhmaMetadatas : []
        };
        codesgroup.koodistoRyhmaMetadatas.push({
            kieli : 'FI',
            nimi : $scope.model.namefi
        });
        codesgroup.koodistoRyhmaMetadatas.push({
            kieli : 'SV',
            nimi : $scope.model.namesv
        });
        codesgroup.koodistoRyhmaMetadatas.push({
            kieli : 'EN',
            nimi : $scope.model.nameen
        });
        UpdateCodesGroup.put({}, codesgroup, function(result) {
            Treemodel.refresh();
            $location.path("/koodistoryhma/" + result.id);
        }, function(error) {
            var alert = {
                type : 'danger',
                msg : jQuery.i18n.prop(error.data)
            };
            $scope.model.alerts.push(alert);
        });
    };
}
