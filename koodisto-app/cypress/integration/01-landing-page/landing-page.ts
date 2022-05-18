import { API_INTERNAL_PATH, BASE_PATH } from '../../../src/context/constants';

describe('The landing page', () => {
    it('shows koodistos on landing page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { fixture: 'codes.json' });
        cy.visit(`${BASE_PATH}`);
        cy.contains('aluehallintovirasto').should('be.visible');
    });
});
