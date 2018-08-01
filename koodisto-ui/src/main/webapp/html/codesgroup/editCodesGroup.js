import {getLanguageSpecificValue} from "../app";

export class CodesGroupEditorModel {
    constructor($location, CodesGroupByUri) {
        "ngInject";
        this.$location = $location;
        this.CodesGroupByUri = CodesGroupByUri;

        this.alerts = [];
        this.codesgroup = {};
    }

    init(scope, id) {
        this.alerts = [];
        this.CodesGroupByUri.get({
            id : id
        }, (result) => {
            this.codesgroup = result;
            scope.model.namefi = getLanguageSpecificValue(result.koodistoRyhmaMetadatas, 'nimi', 'FI');
            scope.model.namesv = getLanguageSpecificValue(result.koodistoRyhmaMetadatas, 'nimi', 'SV');
            scope.model.nameen = getLanguageSpecificValue(result.koodistoRyhmaMetadatas, 'nimi', 'EN');
        });
        scope.loadingReady = true;
    }
}

export class CodesGroupEditorController {
    constructor($scope, $location, $routeParams, $filter, CodesGroupEditorModel, UpdateCodesGroup, treemodel) {
        "ngInject";
        this.$scope = $scope;
        this.$location = $location;
        this.$routeParams = $routeParams;
        this.$filter = $filter;
        this.CodesGroupEditorModel = CodesGroupEditorModel;
        this.UpdateCodesGroup = UpdateCodesGroup;
        this.treemodel = treemodel;

        this.model = CodesGroupEditorModel;
        this.errorMessage = $filter('i18n')('field.required');
        CodesGroupEditorModel.init(this, $routeParams.id);
    }

    closeAlert(index) {
        this.model.alerts.splice(index, 1);
    }

    cancel() {
        this.$location.path("/koodistoryhma/" + this.$routeParams.id);
    }

    submit() {
        this.persistCodesGroup();
    }

    persistCodesGroup() {
        const codesgroup = {
            id: this.$routeParams.id,
            koodistoRyhmaUri: this.model.codesgroup.koodistoRyhmaUri,
            koodistoRyhmaMetadatas: []
        };
        codesgroup.koodistoRyhmaMetadatas.push({
            kieli : 'FI',
            nimi : this.model.namefi
        });
        codesgroup.koodistoRyhmaMetadatas.push({
            kieli : 'SV',
            nimi : this.model.namesv
        });
        codesgroup.koodistoRyhmaMetadatas.push({
            kieli : 'EN',
            nimi : this.model.nameen
        });
        this.UpdateCodesGroup.put({}, codesgroup, (result) => {
            this.treemodel.refresh();
            this.$location.path("/koodistoryhma/" + result.id);
        }, (error) => {
            const alert = {
                type: 'danger',
                msg: jQuery.i18n.prop(error.data)
            };
            this.model.alerts.push(alert);
        });
    }

    setSameName() {
        if (this.model.samename) {
            this.model.namesv = this.model.namefi;
            this.model.nameen = this.model.namefi;
        }
    }
}
