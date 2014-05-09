describe("Application Test", function() {
   
    var RootCodes, NewCodes, DeleteCodes, NewCodesGroup, DeleteCodesGroup, UpdateCodesGroup, UpdateCodes, CodesByUri, CodesByUriAndVersion;
    var	CodesGroupByUri, AllCodes, DownloadCodes, MyRoles, CodeElementsByCodesUriAndVersion, CodeElementByUriAndVersion, CodeElementByCodeElementUri;
    var CodeElementVersionsByCodeElementUri, LatestCodeElementVersionsByCodeElementUri, NewCodeElement, DeleteCodeElement, RemoveRelationCodeElement;
    var AddRelationCodeElement, AddRelationCodes, RemoveRelationCodes, UpdateCodeElement;
    var Organizations, OrganizationChildrenByOid, OrganizationByOid;
    var scope;
    
    beforeEach(module("koodisto"));
    beforeEach(function () {
        inject(function ($injector, $rootScope) {
            scope = $rootScope;
            RootCodes = $injector.get('RootCodes');
            NewCodes = $injector.get('NewCodes');
            DeleteCodes = $injector.get('DeleteCodes');
            NewCodesGroup = $injector.get("NewCodesGroup");
            DeleteCodesGroup = $injector.get("DeleteCodesGroup");
            UpdateCodesGroup = $injector.get("UpdateCodesGroup");
            UpdateCodes = $injector.get("UpdateCodes");
            CodesByUri = $injector.get("CodesByUri");
            CodesByUriAndVersion = $injector.get("CodesByUriAndVersion");
            CodesGroupByUri = $injector.get("CodesGroupByUri");
            AllCodes = $injector.get("AllCodes");
            DownloadCodes = $injector.get("DownloadCodes");
            MyRoles = $injector.get("MyRoles");
            CodeElementsByCodesUriAndVersion = $injector.get("CodeElementsByCodesUriAndVersion");
            CodeElementByUriAndVersion = $injector.get("CodeElementByUriAndVersion");
            CodeElementByCodeElementUri = $injector.get("CodeElementByCodeElementUri");
            CodeElementVersionsByCodeElementUri = $injector.get("CodeElementVersionsByCodeElementUri");
            LatestCodeElementVersionsByCodeElementUri = $injector.get("LatestCodeElementVersionsByCodeElementUri");
            NewCodeElement = $injector.get("NewCodeElement");
            DeleteCodeElement = $injector.get("DeleteCodeElement");
            RemoveRelationCodeElement = $injector.get("RemoveRelationCodeElement");
            AddRelationCodeElement = $injector.get("AddRelationCodeElement");
            AddRelationCodes = $injector.get("AddRelationCodes");
            RemoveRelationCodes = $injector.get("RemoveRelationCodes");
            UpdateCodeElement = $injector.get("UpdateCodeElement");
            Organizations = $injector.get("Organizations");
            OrganizationChildrenByOid = $injector.get("OrganizationChildrenByOid");
            OrganizationByOid = $injector.get("OrganizationByOid");
        });
    });
    
    it("controllers are defined", function() {
       expect(KoodistoTreeController).toBeDefined();
       expect(CodesCreatorController).toBeDefined();
       expect(CodesEditorController).toBeDefined();
       expect(ViewCodesController).toBeDefined();
       expect(ViewCodeElementController).toBeDefined();
       expect(CodeElementCreatorController).toBeDefined();
       expect(CodeElementEditorController).toBeDefined();
       expect(CodesGroupCreatorController).toBeDefined();
       expect(ViewCodesGroupController).toBeDefined();
       expect(CodesGroupEditorController).toBeDefined();
    });
    
    it("resources are defined", function() {
	expect(RootCodes).toBeDefined();
	expect(NewCodes).toBeDefined();
	expect(DeleteCodes).toBeDefined();	
	expect(NewCodesGroup).toBeDefined();
	expect(DeleteCodesGroup).toBeDefined();
	expect(UpdateCodesGroup).toBeDefined();
	expect(UpdateCodes).toBeDefined();
	expect(CodesByUri).toBeDefined();
	expect(CodesByUriAndVersion).toBeDefined();
	expect(CodesGroupByUri).toBeDefined();
	expect(AllCodes).toBeDefined();
	expect(DownloadCodes).toBeDefined();
	expect(MyRoles).toBeDefined();
	expect(CodeElementsByCodesUriAndVersion).toBeDefined();
	expect(CodeElementByUriAndVersion).toBeDefined();
	expect(CodeElementByCodeElementUri).toBeDefined();
	expect(CodeElementVersionsByCodeElementUri).toBeDefined();
	expect(LatestCodeElementVersionsByCodeElementUri).toBeDefined();
	expect(NewCodeElement).toBeDefined();
	expect(DeleteCodeElement).toBeDefined();
	expect(RemoveRelationCodeElement).toBeDefined();
	expect(AddRelationCodeElement).toBeDefined();
	expect(AddRelationCodes).toBeDefined();
	expect(RemoveRelationCodes).toBeDefined();
	expect(UpdateCodeElement).toBeDefined();
	expect(Organizations).toBeDefined();
	expect(OrganizationChildrenByOid).toBeDefined();
	expect(OrganizationByOid).toBeDefined();
    });
    
    describe("Test natural ordering", function() {
	var compile;
	
	beforeEach(inject(function($compile) {
	    compile = $compile;
	}))
	
	createElementAndCompile = function(elementString) {
	    var element = angular.element(elementString);
	    compile(element)(scope);
	    scope.$digest();
	    return element
	}
	
	assertOutputOrder = function(element, expected) {
	    var childValues = "";
	    angular.forEach(element.children(), function(child) {
		childValues = childValues + child.textContent;
	    })
	    expect(childValues).toBe(expected);
	}
	
	it("Sorts string array naturally", function() {
	    scope.stringArray = ["a10", "cba", "abc", "a2", "3", "10", "11"];
	    var element = createElementAndCompile("<ul><li ng-repeat='value in stringArray | naturalSort'>{{value}}</li></ul>");
	    assertOutputOrder(element, "31011a2a10abccba");
	})
	
	it("Sorts object array by fieldname", function() {
	    scope.objectArray = [{content: "a1"}, {content: "a10"}, {content: "a2"}];
	    var naturalSort = 'naturalSort:"content"';
	    var element = createElementAndCompile("<ul><li ng-repeat='value in objectArray | " + naturalSort + " '>{{value.content}}</li></ul>");
	    assertOutputOrder(element, "a1a2a10");
	})
	
	it("Sorts strings in reverse", function() {
	    scope.stringArray = ["abc", "bca", "f10"];
	    var element = createElementAndCompile("<ul><li ng-repeat='value in stringArray | naturalSort:value:true'>{{value}}</li></ul>");
	    assertOutputOrder(element, "f10bcaabc");
	})
	
	it("Handles numbers properly", function() {
	    scope.numberArray = [106,21,19,17,20,100,105,3,1000,2001,5213,6213,4,5,6,107,99];
	    var element = createElementAndCompile("<ul><li ng-repeat='value in numberArray | naturalSort:value'>{{value}}</li></ul>");
	    assertOutputOrder(element, "345617192021991001051061071000200152136213");
	})
	
	it("Handles mass of numbers properly", function() {
	    scope.numberArray = [1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,97,98,99,100,101,102,103,104,105,106,107,108,109,110,111,112,113,114,115,116,117,118,119,120,121,122,123,124,125,126];
	    var element = createElementAndCompile("<ul><li ng-repeat='value in numberArray | naturalSort:value'>{{value}}</li></ul>");
	    assertOutputOrder(element, "123456789101112131415161718192021222324252627282930313233343536373839404142434445464748495051525354555657585960616263646566676869707172737475767778798081828384858687888990919293949596979899100101102103104105106107108109110111112113114115116117118119120121122123124125126");
	})
    })
    
});