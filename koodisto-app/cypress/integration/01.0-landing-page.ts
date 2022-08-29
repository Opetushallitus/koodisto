import { API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';

describe('The landing page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('shows koodistos on landing page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { fixture: 'codes.json' });
        cy.visit(`${BASE_PATH}`);
        cy.contains('aluehallintovirasto').should('be.visible');
    });
    it('shows paging component', () => {
        cy.contains('Sivu 1 / 9').should('be.visible');
    });
    it('Paging cannot go back while at first page', () => {
        cy.get('button[name=PREVIOUS_PAGE]').should('be.visible').should('be.disabled');
        cy.get('button[name=FIRST_PAGE]').should('be.visible').should('be.disabled');
    });
    it('Paging can get to next page', () => {
        cy.get('button[name=NEXT_PAGE]').should('be.visible').click();
        cy.contains('Sivu 2 / 9').should('be.visible');
    });
    it('Paging can get to last page', () => {
        cy.get('button[name=LAST_PAGE]').should('be.visible').click();
        cy.contains('Sivu 9 / 9').should('be.visible');
    });
    it('Paging cannot go forward while at last page', () => {
        cy.get('button[name=NEXT_PAGE]').should('be.visible').should('be.disabled');
        cy.get('button[name=LAST_PAGE]').should('be.visible').should('be.disabled');
    });
    it('Paging can get to previous page', () => {
        cy.get('button[name=PREVIOUS_PAGE]').should('be.visible').click();
        cy.contains('Sivu 8 / 9').should('be.visible');
    });
    it('Paging can get to first page', () => {
        cy.get('button[name=FIRST_PAGE]').should('be.visible').click();
        cy.contains('Sivu 1 / 9').should('be.visible');
    });
    it('Paging resets when filter changes', () => {
        cy.get('button[name=LAST_PAGE]').should('be.visible').click();
        cy.get('input').eq(1).type('maakunta');
        cy.contains('Sivu 1 / 1').should('be.visible');
    });
});
