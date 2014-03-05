
app.factory('CodesGroupCreatorModel', function($location, RootCodes) {
    var model;
    model = new function() {
        this.alerts = [];
        this.init = function() {
            this.alerts = [];
        };

    };


    return model;
});

function CodesGroupCreatorController($scope, $location, $modal, $log, CodesGroupCreatorModel) {
    $scope.model = CodesGroupCreatorModel;
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
        $scope.persistCodes();
    };

    $scope.persistCodes = function() {

    };
}
