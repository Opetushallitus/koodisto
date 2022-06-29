import { BASE_PATH, API_INTERNAL_PATH } from '../../src/context/constants';

describe('The Koodisto Add page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('shows landing page and can click add button', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { fixture: 'codes.json' });
        cy.visit(`${BASE_PATH}`);
        cy.contains('Luo uusi koodisto').should('be.visible').click();
    });
    it('can enter field values', () => {
        cy.get('div[id="koodistoRyhmaUri"]')
            .should('be.visible')
            .find('input[type=text]')
            .should('be.visible')
            .type('Va{enter}{enter}', { force: true });
        cy.get('div[id="organisaatioOid"]')
            .should('be.visible')
            .find('input[type=text]')
            .should('be.visible')
            .type('csc{enter}{enter}', { force: true });
        cy.get('div')
            .contains('Voimassa')
            .should('be.visible')
            .parent()
            .find('input[type=text]')
            .should('be.visible')
            .type('1.1.2022{enter}{enter}', { force: true });
    });
    it('can save changes and open view page', () => {
        cy.intercept('POST', `${API_INTERNAL_PATH}/koodisto/koute`, (req) => {
            console.log(req.body.metadataList);
            expect(req.body.metadataList).to.eqls([
                { kieli: 'FI', nimi: 'nimi-1655458944744', kuvaus: 'nimi-1655458944744' },
                { kieli: 'SV', nimi: 'nimi-1655458944744' },
                { kieli: 'EN', nimi: 'nimi-1655458944744' },
            ]);
            req.reply({ fixture: 'koodistoPostResponse.json' });
        });
        cy.intercept('GET', `${API_INTERNAL_PATH}/koodisto/nimi1655458944744/1`, {
            fixture: 'koodistoPostGetResponse.json',
        });

        cy.get('input[name="metadata[0][nimi]"]').should('be.visible').type('nimi-1655458944744');
        cy.get('input[name="metadata[1][nimi]"]').should('be.visible').type('nimi-1655458944744');
        cy.get('input[name="metadata[2][nimi]"]').should('be.visible').type('nimi-1655458944744');
        cy.get('textarea[name="metadata[0][kuvaus]"]').should('be.visible').type('nimi-1655458944744');
        cy.get('button[name="KOODISTO_TALLENNA"]').should('be.visible').click();
        cy.get('h1').contains('nimi-1655458944744').should('be.visible');
    });
});
