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
});
