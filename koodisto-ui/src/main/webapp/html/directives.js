
export class Auth {
    constructor($animate, $timeout, authService) {
        "ngInject";
        this.$animate = $animate;
        this.$timeout = $timeout;
        this.authService = authService;
    }

    link($scope, element, attrs) {

        element.addClass('ng-hide');

        var success = function() {
            if (additionalCheck()) {
                element.removeClass('ng-hide');
            }
        };
        var additionalCheck = function() {
            if (attrs.authAdditionalCheck) {
                var temp = $scope.$eval(attrs.authAdditionalCheck);
                return temp;
            }
            return true;
        };
        this.$timeout(() => {
            switch (attrs.auth) {

                case "crudOph":
                    this.authService.crudOph(attrs.authService).then(success);
                    break;

                case "updateOph":
                    this.authService.updateOph(attrs.authService).then(success);
                    break;

                case "readOph":
                    this.authService.readOph(attrs.authService).then(success);
                    break;

                case "crudAny":
                    this.authService.crudAny(attrs.authService).then(success);
                    break;

                case "updateAny":
                    this.authService.updateAny(attrs.authService).then(success);
                    break;
            }
        }, 0);

        attrs.$observe('authOrg', function() {
            if (attrs.authOrg) {
                switch (attrs.auth) {
                    case "crud":
                        this.authService.crudOrg(attrs.authService, attrs.authOrg).then(success);
                        break;

                    case "update":
                        this.authService.updateOrg(attrs.authService, attrs.authOrg).then(success);
                        break;

                    case "read":
                        this.authService.readOrg(attrs.authService, attrs.authOrg).then(success);
                        break;
                }
            }
        });

    }

}

/**
 * Source: http://kkurni.blogspot.com.au/2013/10/angularjs-ng-option-with-ie8.html
 * General-purpose Fix IE 8 issue with parent and detail controller.
 *
 * @example <select sk-ie-select="parentModel">
 *
 * @param sk-ie-select
 *            require a value which depend on the parent model, to trigger rendering in IE8
 */
export class IeSelectFix {
    constructor() {
        "ngInject";
        this.restrict = 'A';
        this.require = 'ngModel';
    }

    link(scope, element, attributes, ngModelCtrl) {
        let isIE = document.attachEvent;
        if (!isIE)
            return;
        const control = element[0];
        // to fix IE8 issue with parent and detail controller, we need to depend on the parent controller
        scope.$watch(attributes.ieSelectFix, () => {
            // this will add and remove the options to trigger the rendering in IE8
            const option = document.createElement("option");
            control.add(option, null);
            control.remove(control.options.length - 1);
        });
    }
}
