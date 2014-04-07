describe("Application Test", function() {
    
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
});