
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

function CodesGroupCreatorController($scope, $location, CodesGroupCreatorModel, NewCodesGroup) {
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
        $scope.persistCodesGroup();
    };

    $scope.persistCodesGroup = function() {
        var codes = {
            codesGroupUri: $scope.selectedCGoup,
            voimassaAlkuPvm: $scope.dActiveStart,
            voimassaLoppuPvm: $scope.dActiveEnd,
            omistaja: $scope.ownerName
        };
        NewCodesGroup.post({}, codes, function(result) {
            Treemodel.refresh();
            $location.path("/koodistoryhma/"+result.koodistoUri+"/"+result.versio);
        }, function(error) {
            ValidateService.validateCodes($scope,error,false);
        });
    };
}
