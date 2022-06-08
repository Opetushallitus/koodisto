import { API_INTERNAL_PATH, BASE_PATH } from '../../../src/context/constants';

describe('The landing page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('shows koodistos on landing page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { fixture: 'codes.json' });
        cy.visit(`${BASE_PATH}`);
        cy.contains('aluehallintovirasto').should('be.visible');
    });
    it('Reset filters widget works and is shown conditionally', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { fixture: 'codes.json' });
        cy.visit(`${BASE_PATH}`);
        // filters are empty, show default text
        cy.contains('Voit rajata koodistoryhmällä tai nimellä').should('be.visible');
        cy.get('#resetFilters').should('not.exist');
        // define a filter
        cy.get('input').last().type('test it');
        // filter defined, should show reset filters
        cy.contains('Voit rajata koodistoryhmällä tai nimellä').should('not.exist');
        cy.get('#resetFilters').should('be.visible');
        // reset filters
        cy.get('#resetFilters').click();
        // verify results
        cy.get('input').last().should('be.empty');
        cy.contains('Voit rajata koodistoryhmällä tai nimellä').should('be.visible');
        cy.get('#resetFilters').should('not.exist');
    });
});
