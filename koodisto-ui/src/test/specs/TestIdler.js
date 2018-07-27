import {MAX_SESSION_IDLE_TIME_IN_SECONDS, SESSION_KEEPALIVE_INTERVAL_IN_SECONDS} from "../../main/webapp/html/app";

describe("Idler test", function() {

    var mockBackend, scope, compile, modal, fakeModal, expiresController, windo;

    beforeEach(module("koodisto", function ($provide) {
        windo = {location: { reload: jasmine.createSpy('$window.location.reload')} };
        $provide.value('NoCacheInterceptor', {});
        $provide.value('$window', windo);
    }));

    beforeEach(inject(function ($controller, $injector, $rootScope, $routeParams, $compile, $modal, $window) {
        scope = $rootScope.$new();
        compile = $compile;
        modal = $modal;
        wind = $window;
        $controller("eventsCtrl", {$scope: scope, $modal: modal});
        angular.mock.inject(function ($injector) {
            mockBackend = $injector.get('$httpBackend');
        });
        fakeModal = {
            controller: 'sessionExpiresCtrl',
            result: {
                then: jasmine.createSpy('modalInstance.result.then')
            },
            close: jasmine.createSpy('modalInstance.close')
        };
        expiresController = $controller("sessionExpiresCtrl", {$scope: scope, $modalInstance: fakeModal});
        spyOn(modal, 'open').andReturn(fakeModal);
    }));

    it("configures Idle with default values", inject(function(Idle, Keepalive) {
        expect(Idle._options().timeout).toEqual(300);
        expect(Idle._options().idle).toEqual(MAX_SESSION_IDLE_TIME_IN_SECONDS - 300);
        expect(Keepalive._options().interval).toEqual(SESSION_KEEPALIVE_INTERVAL_IN_SECONDS);
    }))


    describe("Warning message", function() {

        it("shows warning message when user is idle", function() {
            scope.$broadcast('IdleWarn');
            expect(modal.open).toHaveBeenCalled();
            expect(fakeModal.close).not.toHaveBeenCalled();
        })

        it("calling okConfirm should close the modal instance", function() {
            scope.$broadcast('IdleWarn');
            scope.okConfirm();
            expect(fakeModal.close).toHaveBeenCalled();
        })
    })

    describe("Timeout message", function() {

        var $window;

        beforeEach(function() {

        })

        it("shows timeout message when user has been idle too long", function() {
            scope.$broadcast('IdleWarn');
            scope.$broadcast('IdleTimeout');
            expect(fakeModal.close).toHaveBeenCalled();
            expect(modal.open.callCount).toEqual(2);
        })

        it("calling redirectToLogin refreshes page", function() {
            scope.$broadcast('IdleWarn');
            scope.$broadcast('IdleTimeout');
            scope.redirectToLogin();
            expect(windo.location.reload).toHaveBeenCalled();
        })
    })
})