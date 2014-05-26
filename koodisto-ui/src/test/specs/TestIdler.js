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
	$controller("EventsCtrl", {$scope: scope, $modal: modal});	
	angular.mock.inject(function ($injector) {
	    mockBackend = $injector.get('$httpBackend');
	})
	fakeModal = {
	    controller: 'SessionExpiresCtrl',
	    result: {
		then: jasmine.createSpy('modalInstance.result.then')
	    },
	    close: jasmine.createSpy('modalInstance.close')
	};
	expiresController = $controller("SessionExpiresCtrl", {$scope: scope, $modalInstance: fakeModal});
	spyOn(modal, 'open').andReturn(fakeModal);
    }));
        
    it("refreshes session on $keepalive", function() {
	mockBackend.expectGET(SERVICE_URL_BASE + "session/maxinactiveinterval").respond(6);
	scope.$emit('$keepalive');	    
	mockBackend.flush();
    })
    
    it("configures $idle with default values", inject(function($idle, $keepalive) {
	expect($idle._options().warningDuration).toEqual(300);
	expect($keepalive._options().interval).toEqual(SESSION_KEEPALIVE_INTERVAL_IN_SECODS);
    }))
    
    
    describe("Warning message", function() {
			
	it("shows warning message when user is idle", function() {
	    scope.$broadcast('$idleWarn');
	    expect(modal.open).toHaveBeenCalled();
	    expect(fakeModal.close).not.toHaveBeenCalled();
	})
		
	it("calling okConfirm should close the modal instance", function() {
	    scope.$broadcast('$idleWarn');
	    scope.okConfirm();
	    expect(fakeModal.close).toHaveBeenCalled();
	})
    })
    
    describe("Timeout message", function() {
	
	var $window;
	
	beforeEach(function() {   
	 
	})
	
	it("shows timeout message when user has been idle too long", function() {
	    scope.$broadcast('$idleWarn');
	    scope.$broadcast('$idleTimeout');
	    expect(fakeModal.close).toHaveBeenCalled();
	    expect(modal.open.callCount).toEqual(2);
	})
	
	it("calling redirectToLogin refreshes page", function() {
	    scope.$broadcast('$idleWarn');
	    scope.$broadcast('$idleTimeout');
	    scope.redirectToLogin();
	    expect(windo.location.reload).toHaveBeenCalled();
	})
    })
})