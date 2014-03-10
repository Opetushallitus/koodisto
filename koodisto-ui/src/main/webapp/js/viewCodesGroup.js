
app.factory('ViewCodesGroupModel', function($location) {
    var model;
    model = new function() {
        this.alerts = [];
        this.init = function() {
            this.alerts = [];
        };

    };


    return model;
});

function ViewCodesGroupController($scope, $location, ViewCodesGroupModel) {
    $scope.model = ViewCodesGroupModel;
    ViewCodesGroupModel.init();

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/");
    };

}
