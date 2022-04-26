import { API_BASE_PATH, BASE_PATH } from '../../../src/context/constants';
import path from 'path';
const koodistoUri = 'arvosanat';
beforeEach(() => {
    cy.task('deleteFolder', Cypress.config('downloadsFolder'));
});
describe('CSV functionality tests', () => {
    it('shows download icon on landing page', () => {
        cy.intercept(`${API_BASE_PATH}/codes`, { fixture: 'codes.json' });
        cy.visit(`${BASE_PATH}`);
        cy.get(`svg[name="${koodistoUri}-uploadicon"]`).scrollIntoView().should('be.visible');
    });
    it('can download arvosanat', () => {
        cy.intercept(`${API_BASE_PATH}/json/arvosanat/koodi`, { fixture: 'arvosanat.json' });
        const downloadsFolder = Cypress.config('downloadsFolder');
        const downloadedFilename = path.join(downloadsFolder, `${koodistoUri}.csv`);
        cy.get(`svg[name="${koodistoUri}-uploadicon"]`).click();
        cy.readFile(downloadedFilename, 'utf-16le', { timeout: 15000 }).should((file) => {
            expect(file.length).to.be.gt(100);
            expect(file).to.contain('Godkänt');
        });
    });
});
