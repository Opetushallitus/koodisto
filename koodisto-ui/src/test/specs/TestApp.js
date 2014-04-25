describe("Application Test", function() {
   
    var RootCodes, NewCodes, DeleteCodes, NewCodesGroup, DeleteCodesGroup, UpdateCodesGroup, UpdateCodes, CodesByUri, CodesByUriAndVersion;
    var	CodesGroupByUri, AllCodes, DownloadCodes, MyRoles, CodeElementsByCodesUriAndVersion, CodeElementByUriAndVersion, CodeElementByCodeElementUri;
    var CodeElementVersionsByCodeElementUri, LatestCodeElementVersionsByCodeElementUri, NewCodeElement, DeleteCodeElement, RemoveRelationCodeElement;
    var AddRelationCodeElement, AddRelationCodes, RemoveRelationCodes, UpdateCodeElement;
    var Organizations, OrganizationChildrenByOid, OrganizationByOid;
    
    beforeEach(module("koodisto"));
    beforeEach(function () {
        inject(function ($injector) {
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
});