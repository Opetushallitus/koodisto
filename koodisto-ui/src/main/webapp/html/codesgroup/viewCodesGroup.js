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
            // Included in viewcodesgroup.html
            templateUrl: 'confirmDeleteCodesGroupModalContent.html',
            controller: 'viewCodesGroupController as viewCodesGroupModal',
            resolve: {
            }
        });
    }
}

export class ViewCodesGroupController {
    constructor($scope, $location, $routeParams, viewCodesGroupModel, DeleteCodesGroup, treemodel) {
        "ngInject";
        this.$scope = $scope;
        this.$location = $location;
        this.$routeParams = $routeParams;
        this.viewCodesGroupModel = viewCodesGroupModel;
        this.DeleteCodesGroup = DeleteCodesGroup;
        this.treemodel = treemodel;

        this.model = viewCodesGroupModel;
        viewCodesGroupModel.init(this, $routeParams.id);
    }

    closeAlert(index) {
        this.model.alerts.splice(index, 1);
    }

    cancel() {
        this.$location.path("/");
    }


    okconfirmdeletecodesgroup() {
        this.DeleteCodesGroup.post({id: this.$routeParams.id}, (success) => {
            this.treemodel.refresh();
            this.$location.path("/");
        }, (error) => {
            const alert = {type: 'danger', msg: 'Koodiryhm\u00E4n poisto ep\u00E4onnistui.'};
            this.model.alerts.push(alert);
        });

        this.model.deleteCodesGroupModalInstance.close();
    }

    cancelconfirmdeletecodesgroup() {
        this.model.deleteCodesGroupModalInstance.dismiss('cancel');
    }
}
