import { BASE_PATH, API_INTERNAL_PATH } from '../../../src/context/constants';

describe('The Koodisto View page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('shows testi koodisto on koodisto view page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodisto/kunta/2`);
        cy.contains('kunta').should('be.visible');
    });
    it('Koodi list may be filtered', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodisto/kunta/2`);
        cy.get('.accordion > div').last().click();
        cy.get('tbody > tr:visible').should('have.length', 630);

        // filter by name
        cy.get('input').last().type('mouhijärvi');
        cy.get('tbody > tr:visible').should('have.length', 1);

        // clear filter
        cy.get('#clear-filter').click();
        cy.get('tbody > tr:visible').should('have.length', 630);

        // filter by koodiArvo
        cy.get('input').last().type('493');
        cy.get('tbody > tr:visible').should('have.length', 1);
    });
});
