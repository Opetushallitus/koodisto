import angular from 'angular';
import {MAX_SESSION_IDLE_TIME_IN_SECONDS, SERVICE_URL_BASE, SESSION_KEEPALIVE_INTERVAL_IN_SECONDS} from "./app";

export class Idler {
    constructor(Idle, $timeout, $interval) {
        "ngInject";
        this.Idle = Idle;
        this.$timeout = $timeout;
        this.$interval = $interval;
        this.restrict = 'A';
    }

    link(scope, elem, attrs) {
        var timeout;
        var timestamp = localStorage.lastEventTime;

        // Watch for the events set in ng-idle's options
        // If any of them fire (considering 500ms debounce), update localStorage.lastEventTime with a current timestamp
        elem.on(this.Idle._options().events, function(){
            if (this.Idle.running()) {
                if (timeout) { this.$timeout.cancel(timeout); }
                timeout = this.$timeout(function(){
                    localStorage.setItem('lastEventTime', new Date().getTime());
                }, 3000, false);
            }
        });

        // Every 5s, poll localStorage.lastEventTime to see if its value is greater than the timestamp set for the last known event
        // If it is, reset the ng-idle timer and update the last known event timestamp to the value found in localStorage
        window.setInterval(function() {
            if (localStorage.lastEventTime > timestamp) {
                var element = angular.element('#sessionWarning .btn');
                if (element.length > 0) {
                    this.$timeout(function() {
                        element.click();
                    }, 500, false);
                }
                this.Idle.watch();
                timestamp = localStorage.lastEventTime;
            }
        }, 5000, false);
    }
}

export class SessionExpiresCtrl {
    constructor( Idle, $scope, $modalInstance, $window) {
        "ngInject";
        this.Idle = Idle;
        this.$scope = $scope;
        this.$modalInstace = $modalInstance;
        this.$window = $window;

        this.$scope.timeoutMessage = function() {
            var duration = Math.floor(MAX_SESSION_IDLE_TIME_IN_SECONDS / 60);
            return jQuery.i18n.prop('session.expired.text1.part1') + " " + duration +  " " + jQuery.i18n.prop('session.expired.text1.part2');
        };

        this.$scope.okConfirm = function() {
            this.Idle.watch();
            this.$modalInstance.close();
        };
        this.$scope.redirectToLogin = function() {
            this.$window.location.reload();
        };
    }
}

export class EventsCtrl {
    constructor($scope, Idle, $modal) {
        "ngInject";
        this.$scope = $scope;
        this.Idle = Idle;
        this.$modal = $modal;

        this.$scope.$on('IdleWarn', () => {
            if (!this.$scope.sessionWarning || angular.element('#sessionWarning').length < 1) {
                this.$scope.sessionWarning = this.openModal('sessionWarning.html');
            }
        });

        this.$scope.$on('IdleTimeout', () => {
            this.$scope.sessionWarning.close();
            this.$scope.sessionWarning = this.openModal('sessionExpired.html');
            this.Idle.unwatch();
        });

    }

    openModal(template) {
        return this.$modal.open({
            templateUrl: template,
            controller: 'sessionExpiresCtrl',
            keyboard: false,
            backdrop: 'static',
            windowClass: 'modal-warning'
        });
    };
}

export const idleConfig = function(IdleProvider, KeepaliveProvider) {
    "ngInject";
    var warningDuration = 300;
    IdleProvider.idle(MAX_SESSION_IDLE_TIME_IN_SECONDS - warningDuration);
    IdleProvider.timeout(warningDuration);
    KeepaliveProvider.interval(SESSION_KEEPALIVE_INTERVAL_IN_SECONDS);
    KeepaliveProvider.http(SERVICE_URL_BASE + "session/maxinactiveinterval");
};

export const idleRun = function(Idle){
    "ngInject";
    Idle.watch();
};
