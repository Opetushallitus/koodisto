import { BASE_PATH, API_INTERNAL_PATH } from '../../src/context/constants';

describe('Koodisto Edit page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/koodisto/kunta/2`, { fixture: 'kuntaKoodistoKoodit.json' });
        cy.visit(`${BASE_PATH}/koodisto/edit/kunta/2`);
    });
    it('Contains delete button', () => {
        cy.get('button[name=KOODISTO_POISTA]').should('be.visible');
    });
    it('Delete button opens dialog', () => {
        cy.get('button[name=KOODISTO_POISTA]').should('be.visible').click();
        cy.get('#close-dialog').should('be.visible');
    });
    it('Delete dialog can be closed', () => {
        cy.get('button[name=KOODISTO_POISTA]').should('be.visible').click();
        cy.get('#close-dialog').click().should('not.exist');
    });
    it('Delete action disabled if not confirmed', () => {
        cy.get('button[name=KOODISTO_POISTA]').should('be.visible').click();
        cy.get('button[name=CONFIRMATION_ACTION]').should('be.visible').should('be.disabled');
    });
    it('Delete action available after confirmation', () => {
        cy.get('button[name=KOODISTO_POISTA]').should('be.visible').click();
        cy.get('input[name=CONFIRMATION_CHECK]').should('be.visible').click({ force: true });
        cy.get('button[name=CONFIRMATION_ACTION]').should('be.visible').should('not.be.disabled');
    });
    it('Delete redirects to list page after last version is deleted', () => {
        cy.intercept('DELETE', `${API_INTERNAL_PATH}/koodisto/kunta/2`, { statusCode: 204 });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { body: [] });
        cy.get('button[name=KOODISTO_POISTA]').should('be.visible').click();
        cy.get('input[name=CONFIRMATION_CHECK]').should('be.visible').click({ force: true });
        cy.get('button[name=CONFIRMATION_ACTION]').should('be.visible').click({ force: true });
        cy.location().should((location) => {
            expect(location.pathname).to.eq('/koodisto-app/');
        });
    });
});
