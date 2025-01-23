import { BASE_PATH, API_INTERNAL_PATH } from '../../src/context/constants';

describe('Koodisto Edit page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/koodisto/kunta/2`, { fixture: 'kuntaKoodistoKoodit.json' });
        cy.visit(`${BASE_PATH}/koodisto/edit/kunta/2`);
    });
    it('Contains version button', () => {
        cy.get('button[name=KOODISTO_VERSIOI]').should('be.visible');
    });
    it('Version button opens dialog', () => {
        cy.get('button[name=KOODISTO_VERSIOI]').should('be.visible').click();
        cy.get('#close-dialog').should('be.visible');
    });
    it('Version dialog can be closed', () => {
        cy.get('button[name=KOODISTO_VERSIOI]').should('be.visible').click();
        cy.get('#close-dialog').click().should('not.exist');
    });
    it('Version action disabled if not confirmed', () => {
        cy.get('button[name=KOODISTO_VERSIOI]').should('be.visible').click();
        cy.get('button[name=CONFIRMATION_ACTION]').should('be.visible').should('be.disabled');
    });
    it('Version action available after confirmation', () => {
        cy.get('button[name=KOODISTO_VERSIOI]').should('be.visible').click();
        cy.get('input[name=CONFIRMATION_CHECK]').should('be.visible').click({ force: true });
        cy.get('button[name=CONFIRMATION_ACTION]').should('be.visible').should('not.be.disabled');
    });
    it('Redirects to newly created version', () => {
        cy.intercept('POST', `${API_INTERNAL_PATH}/koodisto/kunta/2`, {
            statusCode: 201,
            fixture: 'kuntaKoodisto.json',
        });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { fixture: 'codes.json' });
        cy.get('button[name=KOODISTO_VERSIOI]').should('be.visible').click();
        cy.get('input[name=CONFIRMATION_CHECK]').should('be.visible').click({ force: true });
        cy.get('button[name=CONFIRMATION_ACTION]').should('be.visible').click({ force: true });
        cy.location().should((location) => {
            expect(location.pathname).to.eq('/koodisto-service/ui/koodisto/view/kunta/2');
        });
    });
});
