import {getLanguageSpecificValueOrValidValue} from "../app";

export class ViewCodesGroupModel {
    constructor($location, $modal, CodesGroupByUri) {
        "ngInject";
        this.$location = $location;
        this.$modal = $modal;
        this.CodesGroupByUri = CodesGroupByUri;

        this.alerts = [];
        this.codesgroup = {};
        this.deleteState = "disabled";
    }

    init(scope, id) {
        this.alerts = [];
        this.deleteState = "disabled";
        this.CodesGroupByUri.get({id: id}, (result) => {
            this.codesgroup = result;
            if (result.koodistos.length === 0) {
                this.deleteState = "";
            }
            this.name = getLanguageSpecificValueOrValidValue( this.codesgroup.koodistoRyhmaMetadatas , 'nimi', 'FI');
        });
        scope.loadingReady = true;
    }

    removeCodesGroup() {
        this.deleteCodesGroupModalInstance = this.$modal.open({
            templateUrl: 'confirmDeleteCodesGroupModalContent.html',
            controller: 'ViewCodesGroupController',
            resolve: {
            }
        });
    }
}

export class ViewCodesGroupController {
    constructor($scope, $location, $routeParams, viewCodesGroupModel, DeleteCodesGroup, treemodel) {
        "ngInject";
        $scope.model = viewCodesGroupModel;
        viewCodesGroupModel.init($scope, $routeParams.id);

        $scope.closeAlert = function(index) {
            $scope.model.alerts.splice(index, 1);
        };

        $scope.cancel = function() {
            $location.path("/");
        };


        $scope.okconfirmdeletecodesgroup = function() {
            DeleteCodesGroup.post({id: $routeParams.id},function(success) {
                treemodel.refresh();
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
}
