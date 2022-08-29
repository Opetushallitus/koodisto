import { BASE_PATH } from '../../src/context/constants';

describe('The landing page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('shows koodistos on landing page', () => {
        cy.visit(`${BASE_PATH}`);
        cy.contains('aluehallintovirasto').should('be.visible');
    });
});
