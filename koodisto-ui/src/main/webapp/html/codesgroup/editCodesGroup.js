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
        }, function(result) {
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
        $scope.model = CodesGroupEditorModel;
        $scope.errorMessage = $filter('i18n')('field.required');
        CodesGroupEditorModel.init($scope, $routeParams.id);

        $scope.closeAlert = function(index) {
            $scope.model.alerts.splice(index, 1);
        };

        $scope.cancel = function() {
            $location.path("/koodistoryhma/" + $routeParams.id);
        };

        $scope.submit = function() {
            $scope.persistCodesGroup();
        };

        $scope.persistCodesGroup = function() {
            var codesgroup = {
                id : $routeParams.id,
                koodistoRyhmaUri : $scope.model.codesgroup.koodistoRyhmaUri,
                koodistoRyhmaMetadatas : []
            };
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'FI',
                nimi : $scope.model.namefi
            });
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'SV',
                nimi : $scope.model.namesv
            });
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'EN',
                nimi : $scope.model.nameen
            });
            UpdateCodesGroup.put({}, codesgroup, function(result) {
                treemodel.refresh();
                $location.path("/koodistoryhma/" + result.id);
            }, function(error) {
                var alert = {
                    type : 'danger',
                    msg : jQuery.i18n.prop(error.data)
                };
                $scope.model.alerts.push(alert);
            });
        };

        $scope.setSameName = function() {
            if ($scope.model.samename) {
                $scope.model.namesv = $scope.model.namefi;
                $scope.model.nameen = $scope.model.namefi;
            }
        };
    }
}
