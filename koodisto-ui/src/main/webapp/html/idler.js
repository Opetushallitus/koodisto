import angular from 'angular';
import {MAX_SESSION_IDLE_TIME_IN_SECONDS} from "./app.utils";
import {SERVICE_URL_BASE, SESSION_KEEPALIVE_INTERVAL_IN_SECONDS} from "./app.utils";

export class Idler {
    constructor() {
        this.restrict = 'A';
        this.controller = IdlerController;
    }

    link(scope, elem, attrs, ctrl) {
        let timeout;
        let timestamp = localStorage.lastEventTime;

        // Watch for the events set in ng-idle's options
        // If any of them fire (considering 500ms debounce), update localStorage.lastEventTime with a current timestamp
        elem.on(ctrl.Idle._options().interrupt, () => {
            if (ctrl.Idle.running()) {
                if (timeout) {
                    ctrl.$timeout.cancel(timeout);
                }
                timeout = ctrl.$timeout(() => {
                    localStorage.setItem('lastEventTime', new Date().getTime());
                }, 3000, false);
            }
        });

        // Every 5s, poll localStorage.lastEventTime to see if its value is greater than the timestamp set for the last known event
        // If it is, reset the ng-idle timer and update the last known event timestamp to the value found in localStorage
        window.setInterval(() => {
            if (localStorage.lastEventTime > timestamp) {
                // Requires jquery
                const element = angular.element('#sessionWarning .btn');
                if (element.length > 0) {
                    ctrl.$timeout(() => {
                        element.click();
                    }, 500, false);
                }
                ctrl.Idle.watch();
                timestamp = localStorage.lastEventTime;
            }
        }, 5000, false);
    }

    static directiveFactory() {
        return new Idler;
    }
}

class IdlerController {
    constructor(Idle, $timeout, $interval) {
        "ngInject";
        this.Idle = Idle;
        this.$timeout = $timeout;
        this.$interval = $interval;
    }
}

export class SessionExpiresCtrl {
    constructor( Idle, $scope, $modalInstance, $window) {
        "ngInject";
        this.Idle = Idle;
        this.$scope = $scope;
        this.$modalInstance = $modalInstance;
        this.$window = $window;

    }
    timeoutMessage() {
        const duration = Math.floor(MAX_SESSION_IDLE_TIME_IN_SECONDS / 60);
        return jQuery.i18n.prop('session.expired.text1.part1') + " " + duration +  " " + jQuery.i18n.prop('session.expired.text1.part2');
    }

    okConfirm() {
        this.Idle.watch();
        this.$modalInstance.close();
    }

    redirectToLogin() {
        this.$window.location.reload();
    }
}

export class EventsCtrl {
    constructor($scope, Idle, $modal) {
        "ngInject";
        this.$scope = $scope;
        this.Idle = Idle;
        this.$modal = $modal;

        $scope.$on('IdleWarn', () => {
            // Requires jquery
            if (!this.sessionWarning || angular.element('#sessionWarning').length < 1) {
                // No need to import. Already included in sessionTimeout.html.
                this.sessionWarning = this.openModal('sessionWarning.html');
            }
        });

        $scope.$on('IdleTimeout', () => {
            this.sessionWarning.close();
            // No need to import. Already included in sessionTimeout.html.
            this.sessionWarning = this.openModal('sessionExpired.html');
            this.Idle.unwatch();
        });

    }

    openModal(template) {
        return this.$modal.open({
            templateUrl: template,
            controller: 'sessionExpiresCtrl as sessionExpires',
            keyboard: false,
            backdrop: 'static',
            windowClass: 'modal-warning'
        });
    };
}

export const idleConfig = (IdleProvider, KeepaliveProvider) => {
    "ngInject";
    const warningDuration = 300;
    IdleProvider.idle(MAX_SESSION_IDLE_TIME_IN_SECONDS - warningDuration);
    IdleProvider.timeout(warningDuration);
    KeepaliveProvider.interval(SESSION_KEEPALIVE_INTERVAL_IN_SECONDS);
    KeepaliveProvider.http(SERVICE_URL_BASE + "session/maxinactiveinterval");
};

export const idleRun = (Idle) => {
    "ngInject";
    Idle.watch();
};
