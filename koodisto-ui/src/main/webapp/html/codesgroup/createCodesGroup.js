app.factory('CodesGroupCreatorModel', function($location) {
    var model;
    model = new function() {
        this.alerts = [];
        this.init = function() {
            this.alerts = [];
        };

    };

    return model;
});

function CodesGroupCreatorController($scope, $location, $filter, CodesGroupCreatorModel, NewCodesGroup, Treemodel) {
    $scope.model = CodesGroupCreatorModel;
    $scope.errorMessage = $filter('i18n')('field.required');
    CodesGroupCreatorModel.init();

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.setSameValue = function(name) {
        if (name === 'name' && !$scope.samename) {
            $scope.namesv = $scope.namefi;
            $scope.nameen = $scope.namefi;
        }
    };

    $scope.cancel = function() {
        $location.path("/");
    };

    $scope.submit = function() {
        $scope.persistCodesGroup();
    };

    $scope.persistCodesGroup = function() {
        var codesgroup = {
            koodistoRyhmaMetadatas : []
        };
        if ($scope.namefi) {
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'FI',
                nimi : $scope.namefi
            });
        }
        if ($scope.namesv) {
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'SV',
                nimi : $scope.namesv
            });
        }
        if ($scope.nameen) {
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'EN',
                nimi : $scope.nameen
            });
        }
        NewCodesGroup.post({}, codesgroup, function(result) {
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
