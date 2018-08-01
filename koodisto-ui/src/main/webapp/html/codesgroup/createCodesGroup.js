
export class CodesGroupCreatorModel {
    constructor($location) {
        "ngInject";
        this.$location = $location;

        this.alerts = [];
        this.init = () => {
            this.alerts = [];
            this.namefi = "";
            this.namesv = "";
            this.nameen = "";
            this.samename = false;
        };
    }
}

export class CodesGroupCreatorController {
    constructor($scope, $location, $filter, codesGroupCreatorModel, NewCodesGroup, treemodel) {
        "ngInject";
        this.$scope = $scope;
        this.$location = $location;
        this.$filter = $filter;
        this.codesGroupCreatorModel = codesGroupCreatorModel;
        this.NewCodesGroup = NewCodesGroup;
        this.treemodel = treemodel;

        this.model = codesGroupCreatorModel;
        this.errorMessage = $filter('i18n')('field.required');
        this.codesGroupCreatorModel.init();
    }

    closeAlert(index) {
        this.model.alerts.splice(index, 1);
    }

    setSameValue(name) {
        if (name === 'name' && !this.model.samename) {
            this.model.namesv = this.model.namefi;
            this.model.nameen = this.model.namefi;
        }
    }

    cancel() {
        this.$location.path("/");
    }

    submit() {
        this.persistCodesGroup();
    }

    persistCodesGroup() {
        const codesgroup = {
            koodistoRyhmaMetadatas: []
        };
        if (this.model.namefi) {
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'FI',
                nimi : this.model.namefi
            });
        }
        if (this.model.namesv) {
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'SV',
                nimi : this.model.namesv
            });
        }
        if (this.model.nameen) {
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'EN',
                nimi : this.model.nameen
            });
        }
        this.NewCodesGroup.post({}, codesgroup, (result) => {
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
