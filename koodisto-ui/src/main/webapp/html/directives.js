
app.directive('auth', function($animate, $timeout, AuthService) {
    return {
        link : function($scope, element, attrs) {

            element.addClass('ng-hide');

            var success = function() {
                if(additionalCheck()) {
                    element.removeClass('ng-hide');
                }
            };
            var additionalCheck = function() {
                if(attrs.authAdditionalCheck) {
                    var temp = $scope.$eval(attrs.authAdditionalCheck);
                    return temp;
                }
                return true;
            };
            $timeout(function() {
                switch(attrs.auth) {

                    case "crudOph":
                        AuthService.crudOph(attrs.authService).then(success);
                        break;

                    case "updateOph":
                        AuthService.updateOph(attrs.authService).then(success);
                        break;

                    case "readOph":
                        AuthService.readOph(attrs.authService).then(success);
                        break;

                    case "crudAny":
                        AuthService.crudAny(attrs.authService).then(success);
                        break;

                    case "updateAny":
                        AuthService.updateAny(attrs.authService).then(success);
                        break;
                }
            },0);

            attrs.$observe('authOrg', function() {
                if(attrs.authOrg) {
                    switch(attrs.auth) {
                        case "crud":
                            AuthService.crudOrg(attrs.authService, attrs.authOrg).then(success);
                            break;

                        case "update":
                            AuthService.updateOrg(attrs.authService, attrs.authOrg).then(success);
                            break;

                        case "read":
                            AuthService.readOrg(attrs.authService, attrs.authOrg).then(success);
                            break;
                    }
                }
            });

        }
    };
});
