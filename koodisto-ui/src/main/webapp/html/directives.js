
export class Auth {
    constructor() {
        this.controller = AuthController;
    }

    link($scope, element, attrs, ctrl) {
        const additionalCheck = () => {
            if (attrs.authAdditionalCheck) {
                return $scope.$eval(attrs.authAdditionalCheck);
            }
            return true;
        };
        element.addClass('ng-hide');

        const success = function () {
            if (additionalCheck()) {
                element.removeClass('ng-hide');
            }
        };
        ctrl.$timeout(() => {
            switch (attrs.auth) {
                case "crudOph":
                    ctrl.authService.crudOph(attrs.authService).then(success);
                    break;

                case "updateOph":
                    ctrl.authService.updateOph(attrs.authService).then(success);
                    break;

                case "readOph":
                    ctrl.authService.readOph(attrs.authService).then(success);
                    break;

                case "crudAny":
                    ctrl.authService.crudAny(attrs.authService).then(success);
                    break;

                case "updateAny":
                    ctrl.authService.updateAny(attrs.authService).then(success);
                    break;
            }
        }, 0);

        attrs.$observe('authOrg', () => {
            if (attrs.authOrg) {
                switch (attrs.auth) {
                    case "crud":
                        ctrl.authService.crudOrg(attrs.authService, attrs.authOrg).then(success);
                        break;

                    case "update":
                        ctrl.authService.updateOrg(attrs.authService, attrs.authOrg).then(success);
                        break;

                    case "read":
                        ctrl.authService.readOrg(attrs.authService, attrs.authOrg).then(success);
                        break;
                }
            }
        });

    }

    static directiveFactory() {
        return new Auth;
    }

}

class AuthController {
    constructor($animate, $timeout, authService) {
        "ngInject";
        this.$animate = $animate;
        this.$timeout = $timeout;
        this.authService = authService;
    }
}
