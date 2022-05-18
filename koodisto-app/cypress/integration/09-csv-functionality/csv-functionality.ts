import { API_BASE_PATH, API_INTERNAL_PATH, BASE_PATH } from '../../../src/context/constants';
import path from 'path';

const koodistoUri = 'arvosanat';
before(() => {
    cy.task('deleteFolder', Cypress.config('downloadsFolder'));
});
describe('CSV functionality tests', () => {
    it('shows download button on koodisto page', () => {
        cy.intercept(`${API_BASE_PATH}/codes/${koodistoUri}/1`, { fixture: `${koodistoUri}Koodisto.json` });
        cy.visit(`${BASE_PATH}/koodisto/${koodistoUri}/1`);
        cy.get(`[name="${koodistoUri}-csv"]`).scrollIntoView().should('be.visible');
    });
    it('can download arvosanat', () => {
        cy.intercept(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, { fixture: `${koodistoUri}.json` });
        const downloadsFolder = Cypress.config('downloadsFolder');
        const downloadedFilename = path.join(downloadsFolder, `${koodistoUri}.csv`);
        cy.get(`[name="${koodistoUri}-csv"]`).click();
        cy.get(`[name="${koodistoUri}-download"]`).click();
        cy.readFile(downloadedFilename, 'utf-16le', { timeout: 15000 }).should((file) => {
            expect(file.length).to.be.gt(100);
            expect(file).to.contain('Godkänt');
        });
    });

    it('upload csv file', () => {
        cy.get('input[type="file"]').attachFile({
            filePath: `${koodistoUri}_upload.csv`,
            encoding: 'utf-16le',
        });
        cy.contains('Cypress_Muokattu').should('be.visible');
        cy.intercept('POST', `${API_INTERNAL_PATH}/koodi/${koodistoUri}`, (req) => {
            req.reply({ fixture: 'arvosanatKoodisto.json' });
            expect(req.body[0].metadata[0]).to.include({ nimi: 'Cypress_Muokattu', kieli: 'FI' });
        });
        cy.get('button').contains('Tallenna').click();
    });
});
