import { BASE_PATH, API_INTERNAL_PATH } from '../../../src/context/constants';

describe('Koodi view page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('Renders page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodi/get_1/1`, { fixture: 'koodiPage.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodi/view/get_1/1`);
        cy.contains('Akaa').should('be.visible');
        cy.contains('a', 'kunta').and('have.attr', 'href').and('include', 'koodisto/view/kunta/2');
    });
});
