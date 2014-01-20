app.factory('ValidateService', function() {
    return {
        validateCodes: function(scope,error,isupdate) {
            if (isupdate) {
                var alert = { type: 'danger', msg: 'Koodiston muokkaus ep\u00E4onnistui.' }
            } else {
                var alert = { type: 'danger', msg: 'Koodiston luonti ep\u00E4onnistui.' }
            }
            if (scope.form.namefi.$viewValue && !scope.form.descriptionfi.$viewValue ||
                scope.form.descriptionfi.$viewValue && !scope.form.namefi.$viewValue ||
                scope.form.namesv.$viewValue && !scope.form.descriptionsv.$viewValue ||
                scope.form.descriptionsv.$viewValue && !scope.form.namesv.$viewValue ||
                scope.form.nameen.$viewValue && !scope.form.descriptionen.$viewValue ||
                scope.form.descriptionen.$viewValue && !scope.form.nameen.$viewValue) {
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
            if (scope.form.namefi.$viewValue && !scope.form.descriptionfi.$viewValue ||
                scope.form.descriptionfi.$viewValue && !scope.form.namefi.$viewValue ||
                scope.form.namefi.$viewValue && !scope.form.shortnamefi.$viewValue ||
                scope.form.descriptionfi.$viewValue && !scope.form.shortnamefi.$viewValue ||
                scope.form.shortnamefi.$viewValue && !scope.form.namefi.$viewValue ||
                scope.form.shortnamefi.$viewValue && !scope.form.descriptionfi.$viewValue ||
                scope.form.namesv.$viewValue && !scope.form.descriptionsv.$viewValue ||
                scope.form.descriptionsv.$viewValue && !scope.form.namesv.$viewValue ||
                scope.form.namesv.$viewValue && !scope.form.shortnamesv.$viewValue ||
                scope.form.descriptionsv.$viewValue && !scope.form.shortnamesv.$viewValue ||
                scope.form.shortnamesv.$viewValue && !scope.form.namesv.$viewValue ||
                scope.form.shortnamesv.$viewValue && !scope.form.descriptionsv.$viewValue ||
                scope.form.nameen.$viewValue && !scope.form.descriptionen.$viewValue ||
                scope.form.descriptionen.$viewValue && !scope.form.nameen.$viewValue ||
                scope.form.nameen.$viewValue && !scope.form.shortnameen.$viewValue ||
                scope.form.descriptionen.$viewValue && !scope.form.shortnameen.$viewValue ||
                scope.form.shortnameen.$viewValue && !scope.form.nameen.$viewValue ||
                scope.form.shortnameen.$viewValue && !scope.form.descriptionen.$viewValue) {
                alert = { type: 'danger', msg: 'Nimi, lyhyt nimi ja kuvaus tulee olla annettu samalla kielell\u00E4' };
            }

            scope.model.alerts.push(alert);

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