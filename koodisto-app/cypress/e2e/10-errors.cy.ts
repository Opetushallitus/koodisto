import { API_BASE_PATH, API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';

const koodistoUri = 'kunta';
Cypress.on('uncaught:exception', (err, runnable) => {
    return false;
});
beforeEach(() => {
    cy.task('deleteFolder', Cypress.config('downloadsFolder'));
});
describe('Errors', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
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
        cy.visit(`${BASE_PATH}/koodisto/view/${koodistoUri}/2`);
        cy.intercept(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, (req) => {
            req.reply({
                statusCode: 500,
                delay: 10,
            });
        });
        cy.get(`[name="${koodistoUri}-csv"]`).click();
        cy.contains('server.error.500').should('be.visible');
    });
    it('error if 400', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/${koodistoUri}/2`, { fixture: 'kuntaKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodisto/view/${koodistoUri}/2`);
        cy.intercept(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, (req) => {
            req.reply({
                statusCode: 400,
                delay: 10,
                body: 'custom message',
            });
        });
        cy.get(`[name="${koodistoUri}-csv"]`).click();
        cy.contains('Selainvirhe 400, ota yhteyttä ylläpitoon (yhteisetpalvelut@opintopolku.fi)').should('be.visible');
        cy.contains('custom message').should('be.visible');
    });
    it('error if 404', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/${koodistoUri}/2`, { fixture: 'kuntaKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodisto/view/${koodistoUri}/2`);
        cy.intercept(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, (req) => {
            req.reply({
                statusCode: 404,
                delay: 10,
                body: 'custom message',
            });
        });
        cy.get(`[name="${koodistoUri}-csv"]`).click();
        cy.contains('Tietuetta ei löytynyt palvelimelta').should('be.visible');
        cy.contains('custom message').should('be.visible');
    });
    it("error if i'm a teapot", () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/${koodistoUri}/2`, { fixture: 'kuntaKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodisto/view/${koodistoUri}/2`);
        cy.intercept(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, (req) => {
            req.reply({
                statusCode: 418,
                delay: 10,
                body: "i'm a teapot",
            });
        });
        cy.get(`[name="${koodistoUri}-csv"]`).click();
        cy.contains('Selainvirhe 418, ota yhteyttä ylläpitoon (yhteisetpalvelut@opintopolku.fi)').should('be.visible');
        cy.contains("i'm a teapot").should('be.visible');
    });
    it('Redirects to index page on non-existent URL', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { fixture: 'codes.json' });
        cy.visit(`${BASE_PATH}/some/non-existent/path`);
        cy.location().should((location) => expect(location.pathname).to.equal('/koodisto-service/ui/'));
    });
});
