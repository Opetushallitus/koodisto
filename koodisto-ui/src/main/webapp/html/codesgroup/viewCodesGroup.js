
app.factory('ViewCodesGroupModel', function($location, $modal, CodesGroupByUri) {
    var model;
    model = new function() {
        this.alerts = [];
        this.codesgroup = {};
        this.deleteState = "disabled";

        this.init = function(id) {
            this.alerts = [];
            this.deleteState = "disabled";
            CodesGroupByUri.get({id: id}, function (result) {
                model.codesgroup = result;
                if (result.koodistos.length === 0) {
                    model.deleteState = "";
                }
                model.name = getLanguageSpecificValueOrValidValue( model.codesgroup.koodistoRyhmaMetadatas , 'nimi', 'FI');
            });
        };

        this.removeCodesGroup = function() {
            model.deleteCodesGroupModalInstance = $modal.open({
                templateUrl: 'confirmDeleteCodesGroupModalContent.html',
                controller: ViewCodesGroupController,
                resolve: {
                }
            });
        };

    };


    return model;
});

function ViewCodesGroupController($scope, $location, $routeParams, ViewCodesGroupModel, DeleteCodesGroup, Treemodel) {
    $scope.model = ViewCodesGroupModel;
    ViewCodesGroupModel.init($routeParams.id);

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.cancel = function() {
        $location.path("/");
    };


    $scope.okconfirmdeletecodesgroup = function() {
        DeleteCodesGroup.post({id: $routeParams.id},function(success) {
            Treemodel.refresh();
            $location.path("/");
        }, function(error) {
            var alert = { type: 'danger', msg: 'Koodiryhm\u00E4n poisto ep\u00E4onnistui.' };
            $scope.model.alerts.push(alert);
        });

        $scope.model.deleteCodesGroupModalInstance.close();
    };

    $scope.cancelconfirmdeletecodesgroup = function() {
        $scope.model.deleteCodesGroupModalInstance.dismiss('cancel');
    };
}
