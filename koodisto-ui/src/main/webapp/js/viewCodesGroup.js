
app.factory('ViewCodesGroupModel', function($location, CodesGroupByUri) {
    var model;
    model = new function() {
        this.alerts = [];
        this.codesgroup = {};
        this.init = function(id) {
            this.alerts = [];
            CodesGroupByUri.get({id: id}, function (result) {
                model.codesgroup = result;
            });
        };


    };


    return model;
});

function ViewCodesGroupController($scope, $location, $routeParams, ViewCodesGroupModel) {
    $scope.model = ViewCodesGroupModel;
    ViewCodesGroupModel.init($routeParams.id);

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/");
    };

    $scope.getLanguageSpecificValue = function(fieldArray,fieldName,language) {
        return getLanguageSpecificValue(fieldArray,fieldName,language);
    };
}
