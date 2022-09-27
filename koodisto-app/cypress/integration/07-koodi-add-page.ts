import { BASE_PATH, API_INTERNAL_PATH } from '../../src/context/constants';

describe('Koodi add page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('shows testi koodisto on koodisto view page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/koodisto/kunta/2`, { fixture: 'kuntaKoodistoKoodit.json' });
        cy.visit(`${BASE_PATH}/koodisto/view/kunta/2`);
        cy.contains('kunta').should('be.visible');
    });
    it('shows add new koodi button and can open page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.get('button[name="TAULUKKO_LISAA_KOODI_BUTTON"]').should('be.visible').click();
        cy.get('h1').contains('Lisää koodi').should('be.visible');
    });
    it('can enter data', () => {
        cy.get('input[name="koodiArvo"]').type('arvo');

        cy.get('input[name="metadata[0][nimi]"]').type('nimi-arvo');
        cy.get('input[name="metadata[1][nimi]"]').type('nimi-arvo');
        cy.get('input[name="metadata[2][nimi]"]').type('nimi-arvo');

        cy.get('input[name="metadata[0][lyhytNimi]"]').type('lyhyt-arvo');
        cy.get('input[name="metadata[1][lyhytNimi]"]').type('lyhyt-arvo');
        cy.get('input[name="metadata[2][lyhytNimi]"]').type('lyhyt-arvo');

        cy.get('div')
            .contains('Alkupäivämäärä')
            .should('be.visible')
            .parent()
            .find('input[type=text]')
            .should('be.visible')
            .type('1.1.2022{enter}{enter}', { force: true });

        cy.get('div')
            .contains('Loppupäivämäärä')
            .should('be.visible')
            .parent()
            .find('input[type=text]')
            .should('be.visible')
            .type('1.1.2023{enter}{enter}', { force: true });

        cy.get('textarea[name="metadata[0][kuvaus]"]').type('kuvaus-arvo');
        cy.get('textarea[name="metadata[1][kuvaus]"]').type('kuvaus-arvo');
        cy.get('textarea[name="metadata[2][kuvaus]"]').type('kuvaus-arvo');
    });
    it('can save changes and open view page', () => {
        cy.intercept('POST', `${API_INTERNAL_PATH}/koodi/kunta`, (req) => {
            expect(req.body.koodiArvo).to.eq('arvo');
            expect(req.body.voimassaAlkuPvm).to.eq('2022-01-01');
            expect(req.body.voimassaLoppuPvm).to.eq('2023-01-01');
            expect(req.body.metadata).to.eqls([
                { kieli: 'FI', nimi: 'nimi-arvo', lyhytNimi: 'lyhyt-arvo', kuvaus: 'kuvaus-arvo' },
                { kieli: 'SV', nimi: 'nimi-arvo', lyhytNimi: 'lyhyt-arvo', kuvaus: 'kuvaus-arvo' },
                { kieli: 'EN', nimi: 'nimi-arvo', lyhytNimi: 'lyhyt-arvo', kuvaus: 'kuvaus-arvo' },
            ]);
            req.reply({ fixture: 'koodiPage.json' });
        });
        cy.intercept('GET', `${API_INTERNAL_PATH}/koodi/kunta_arvo/1`, {
            fixture: 'koodiPage.json',
        });

        cy.intercept(`${API_INTERNAL_PATH}/koodi/kunta_020/2`, { fixture: 'koodiPage.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.get('button[name="KOODI_TALLENNA"]').should('be.visible').click();
        cy.get('h1').contains('Akaa').should('be.visible');
    });
});
