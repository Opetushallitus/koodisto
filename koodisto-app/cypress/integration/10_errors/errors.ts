import { API_BASE_PATH, API_INTERNAL_PATH, BASE_PATH } from '../../../src/context/constants';

const koodistoUri = 'kunta';
Cypress.on('uncaught:exception', (err, runnable) => {
    return false;
});
beforeEach(() => {
    cy.task('deleteFolder', Cypress.config('downloadsFolder'));
});
describe('Errors', () => {
    it('shows error boudary if codes fails', () => {
        cy.intercept('GET', `${API_INTERNAL_PATH}/koodisto`, (req) => {
            req.reply({
                statusCode: 404,
                body: '404 Not Found!',
                delay: 10,
            });
        });
        cy.visit(`${BASE_PATH}`);
        cy.contains('Service Unavailable').should('be.visible');
    });
    it('error if 500', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/${koodistoUri}/2`, { fixture: 'kuntaKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodisto/${koodistoUri}/2`);
        cy.intercept(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, (req) => {
            req.reply({
                statusCode: 500,
                delay: 10,
            });
        });
        cy.get(`[name="${koodistoUri}-csv"]`).click();
        cy.contains('Server Error').should('be.visible');
    });
    it('error if 404 with key', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/${koodistoUri}/2`, { fixture: 'kuntaKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodisto/${koodistoUri}/2`);
        cy.intercept(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, (req) => {
            req.reply({
                statusCode: 404,
                delay: 10,
                body: { errorMessage: 'custom message' },
            });
        });
        cy.get(`[name="${koodistoUri}-csv"]`).click();
        cy.contains('Not Found').should('be.visible');
        cy.contains('custom message').should('be.visible');
    });
    it('error if 404 with key and message', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/${koodistoUri}/2`, { fixture: 'kuntaKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodisto/${koodistoUri}/2`);
        cy.intercept(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, (req) => {
            req.reply({
                statusCode: 404,
                delay: 10,
                body: { errorKey: 'custom key', errorMessage: 'custom message' },
            });
        });
        cy.get(`[name="${koodistoUri}-csv"]`).click();
        cy.contains('custom key').should('be.visible');
        cy.contains('custom message').should('be.visible');
    });
});
