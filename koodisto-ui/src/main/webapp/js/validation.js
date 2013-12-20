
app.directive('codesCombinedField', function() {
    return {
        require: 'ngModel',

        link: function(scope, elm, attrs, ctrl) {
            ctrl.$parsers.unshift(function(viewValue) {
                var returnUndefined = false;
                scope.form.namefi.$setValidity('codescombinedrequired', true);
                scope.form.namefi.$setValidity('required', true);
                scope.form.descriptionfi.$setValidity('codescombinedrequired', true);
                scope.form.descriptionfi.$setValidity('required', true);
                scope.form.namesv.$setValidity('codescombinedrequired', true);
                scope.form.descriptionsv.$setValidity('codescombinedrequired', true);
                scope.form.nameen.$setValidity('codescombinedrequired', true);
                scope.form.descriptionen.$setValidity('codescombinedrequired', true);

                if (scope.form.namefi.$viewValue && !scope.form.descriptionfi.$viewValue) {
                    scope.form.descriptionfi.$setValidity('codescombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.descriptionfi.$viewValue && !scope.form.namefi.$viewValue) {
                    scope.form.namefi.$setValidity('codescombinedrequired', false);
                    returnUndefined = true;
                }

                if (scope.form.namesv.$viewValue && !scope.form.descriptionsv.$viewValue) {
                    scope.form.descriptionsv.$setValidity('codescombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.descriptionsv.$viewValue && !scope.form.namesv.$viewValue) {
                    scope.form.namesv.$setValidity('codescombinedrequired', false);
                    returnUndefined = true;
                }

                if (scope.form.nameen.$viewValue && !scope.form.descriptionen.$viewValue) {
                    scope.form.descriptionen.$setValidity('codescombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.descriptionen.$viewValue && !scope.form.nameen.$viewValue) {
                    scope.form.nameen.$setValidity('codescombinedrequired', false);
                    returnUndefined = true;
                }
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
                scope.form.namefi.$setValidity('codeelementcombinedrequired', true);
                scope.form.namefi.$setValidity('required', true);
                scope.form.descriptionfi.$setValidity('codeelementcombinedrequired', true);
                scope.form.descriptionfi.$setValidity('required', true);
                scope.form.shortnamefi.$setValidity('codeelementcombinedrequired', true);
                scope.form.shortnamefi.$setValidity('required', true);

                scope.form.namesv.$setValidity('codeelementcombinedrequired', true);
                scope.form.descriptionsv.$setValidity('codeelementcombinedrequired', true);
                scope.form.shortnamesv.$setValidity('codeelementcombinedrequired', true);
                scope.form.nameen.$setValidity('codeelementcombinedrequired', true);
                scope.form.descriptionen.$setValidity('codeelementcombinedrequired', true);
                scope.form.shortnameen.$setValidity('codeelementcombinedrequired', true);

                if (scope.form.namefi.$viewValue && !scope.form.descriptionfi.$viewValue) {
                    scope.form.descriptionfi.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.namefi.$viewValue && !scope.form.shortnamefi.$viewValue) {
                    scope.form.shortnamefi.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.descriptionfi.$viewValue && !scope.form.namefi.$viewValue) {
                    scope.form.namefi.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.descriptionfi.$viewValue && !scope.form.shortnamefi.$viewValue) {
                    scope.form.shortnamefi.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }

                if (scope.form.shortnamefi.$viewValue && !scope.form.namefi.$viewValue) {
                    scope.form.namefi.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.shortnamefi.$viewValue && !scope.form.descriptionfi.$viewValue) {
                    scope.form.descriptionfi.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }


                if (scope.form.namesv.$viewValue && !scope.form.descriptionsv.$viewValue) {
                    scope.form.descriptionsv.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.namesv.$viewValue && !scope.form.shortnamesv.$viewValue) {
                    scope.form.shortnamesv.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.descriptionsv.$viewValue && !scope.form.namesv.$viewValue) {
                    scope.form.namesv.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.descriptionsv.$viewValue && !scope.form.shortnamesv.$viewValue) {
                    scope.form.shortnamesv.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }

                if (scope.form.shortnamesv.$viewValue && !scope.form.namesv.$viewValue) {
                    scope.form.namesv.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.shortnamesv.$viewValue && !scope.form.descriptionsv.$viewValue) {
                    scope.form.descriptionsv.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }



                if (scope.form.nameen.$viewValue && !scope.form.descriptionen.$viewValue) {
                    scope.form.descriptionen.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.nameen.$viewValue && !scope.form.shortnameen.$viewValue) {
                    scope.form.shortnameen.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.descriptionen.$viewValue && !scope.form.nameen.$viewValue) {
                    scope.form.nameen.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.descriptionen.$viewValue && !scope.form.shortnameen.$viewValue) {
                    scope.form.shortnameen.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }

                if (scope.form.shortnameen.$viewValue && !scope.form.nameen.$viewValue) {
                    scope.form.nameen.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }
                if (scope.form.shortnameen.$viewValue && !scope.form.descriptionen.$viewValue) {
                    scope.form.descriptionen.$setValidity('codeelementcombinedrequired', false);
                    returnUndefined = true;
                }

                if (returnUndefined === true) {
                    return undefined;
                } else {
                    return viewValue;
                }

            });
        }
    };
});