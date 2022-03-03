import { BASE_PATH } from '../../../src/context/constants';

describe('The landing page', () => {
    it('shows koodisto on landing page', () => {
        cy.visit(`${BASE_PATH}`);
    });
});
