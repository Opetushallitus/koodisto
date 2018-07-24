import angular from 'angular';

const app = angular.module('koodisto');

app.factory('CodesGroupCreatorModel', function($location) {
    var model;
    model = new function() {
        this.alerts = [];
        this.init = function() {
            this.alerts = [];
            this.namefi = "";
            this.namesv = "";
            this.nameen = "";
            this.samename = false;
        };

    };
    return model;
});

app.controller('CodesGroupCreatorController', function ($scope, $location, $filter, CodesGroupCreatorModel, NewCodesGroup, Treemodel) {
    $scope.model = CodesGroupCreatorModel;
    $scope.errorMessage = $filter('i18n')('field.required');
    CodesGroupCreatorModel.init();

    $scope.closeAlert = function(index) {
        $scope.model.alerts.splice(index, 1);
    };

    $scope.setSameValue = function(name) {
        if (name === 'name' && !$scope.model.samename) {
            $scope.model.namesv = $scope.model.namefi;
            $scope.model.nameen = $scope.model.namefi;
        }
    };

    $scope.cancel = function() {
        $location.path("/");
    };

    $scope.submit = function() {
        $scope.persistCodesGroup();
    };

    $scope.persistCodesGroup = function() {
        var codesgroup = {
            koodistoRyhmaMetadatas : []
        };
        if ($scope.model.namefi) {
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'FI',
                nimi : $scope.model.namefi
            });
        }
        if ($scope.model.namesv) {
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'SV',
                nimi : $scope.model.namesv
            });
        }
        if ($scope.model.nameen) {
            codesgroup.koodistoRyhmaMetadatas.push({
                kieli : 'EN',
                nimi : $scope.model.nameen
            });
        }
        NewCodesGroup.post({}, codesgroup, function(result) {
            Treemodel.refresh();
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
});
