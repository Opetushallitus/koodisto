import { BASE_PATH, API_INTERNAL_PATH } from '../../src/context/constants';

describe('Koodi view with partial data page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('Renders page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodi/get_1/1`, { fixture: 'koodiPage-partial.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodi/view/get_1/1`);
        cy.get('.kieli-nimi').should(($lis) => {
            expect($lis).to.have.length(6);
            expect($lis.eq(0)).to.contain('FI');
            expect($lis.eq(1)).to.contain('SV');
            expect($lis.eq(2)).to.contain('EN');
        });
    });
});
