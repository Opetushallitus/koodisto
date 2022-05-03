import { API_BASE_PATH, BASE_PATH } from '../../../src/context/constants';

describe('The Koodisto View page', () => {
    it('shows testi koodisto on koodisto view page', () => {
        cy.intercept(`${API_BASE_PATH}/codes/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodisto/kunta/2`);
        cy.contains('kunta').should('be.visible');
    });
});
