app.factory('ValidateService', function() {
    return {
        validateCodes: function(scope,error,isupdate) {
            if (isupdate) {
                var alert = { type: 'danger', msg: 'Koodiston muokkaus ep\u00E4onnistui.' }
            } else {
                var alert = { type: 'danger', msg: 'Koodiston luonti ep\u00E4onnistui.' }
            }
            if (scope.namefi && !scope.descriptionfi ||
                scope.descriptionfi && !scope.namefi ||
                scope.namesv && !scope.descriptionsv ||
                scope.descriptionsv && !scope.namesv ||
                scope.nameen && !scope.descriptionen ||
                scope.descriptionen && !scope.nameen) {
                alert = { type: 'danger', msg: 'Nimi ja kuvaus tulee olla annettu samalla kielell\u00E4' };
            }

            scope.model.alerts.push(alert);

        },
        validateCodeElement: function(scope,error,isupdate) {
            if (isupdate) {
                var alert = { type: 'danger', msg: 'Koodin muokkaus ep\u00E4onnistui.' }
            } else {
                var alert = { type: 'danger', msg: 'Koodin luonti ep\u00E4onnistui.' }
            }
            if (scope.namefi && !scope.descriptionfi ||
                scope.descriptionfi && !scope.namefi ||
                scope.namefi && !scope.shortnamefi ||
                scope.descriptionfi && !scope.shortnamefi ||
                scope.shortnamefi && !scope.namefi ||
                scope.shortnamefi && !scope.descriptionfi ||
                scope.namesv && !scope.descriptionsv ||
                scope.descriptionsv && !scope.namesv ||
                scope.namesv && !scope.shortnamesv ||
                scope.descriptionsv && !scope.shortnamesv ||
                scope.shortnamesv && !scope.namesv ||
                scope.shortnamesv && !scope.descriptionsv ||
                scope.nameen && !scope.descriptionen ||
                scope.descriptionen && !scope.nameen ||
                scope.nameen && !scope.shortnameen ||
                scope.descriptionen && !scope.shortnameen ||
                scope.shortnameen && !scope.nameen ||
                scope.shortnameen && !scope.descriptionen) {
                alert = { type: 'danger', msg: 'Nimi, lyhyt nimi ja kuvaus tulee olla annettu samalla kielell\u00E4' };
            }

            scope.model.alerts.push(alert);

        }
    };
});

app.directive('requiredField', function() {
    return {
        require: 'ngModel',

        link: function(scope, elm, attrs, ctrl) {
            elm.bind('blur', function () {
                if (!attrs.$$element[0].value || attrs.$$element[0].value.length === 0) {
                    ctrl.$setValidity('requiredfield', false);
                    scope.$apply();
                } else {
                    ctrl.$setValidity('requiredfield', true);
                    scope.$apply();
                }
            });
            ctrl.$parsers.unshift(function(viewValue) {
                if (!viewValue || viewValue.length === 0) {
                    ctrl.$setValidity('requiredfield', false);
                    return viewValue;
                } else {
                    ctrl.$setValidity('requiredfield', true);
                    return viewValue;
                }

            });
        }
    };
});

app.directive('codesCombinedField', function() {
    return {
        require: 'ngModel',

        link: function(scope, elm, attrs, ctrl) {
            ctrl.$parsers.unshift(function(viewValue) {
                var returnUndefined = false;

                if (returnUndefined === true) {
                    return undefined;
                } else {
                    return viewValue;
                }

            });
        }
    };
});

app.directive('codeelementCombinedField', function() {
    return {
        require: 'ngModel',

        link: function(scope, elm, attrs, ctrl) {
            ctrl.$parsers.unshift(function(viewValue) {
                var returnUndefined = false;

                if (returnUndefined === true) {
                    return undefined;
                } else {
                    return viewValue;
                }

            });
        }
    };
});