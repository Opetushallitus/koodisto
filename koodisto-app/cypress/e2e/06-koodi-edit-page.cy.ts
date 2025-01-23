import { BASE_PATH, API_INTERNAL_PATH } from '../../src/context/constants';

describe('Koodi edit page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('renders view page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodi/kunta_020/2`, { fixture: 'koodiPage.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodi/view/kunta_020/2`);
        cy.contains('Akaa').should('be.visible');
    });
    it('shows edit button and can open edit page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodi/kunta_020/2`, { fixture: 'koodiPage.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.get('button[name=KOODISIVU_MUOKKAA_KOODIA_BUTTON]').should('be.visible').click();
        cy.contains('Muokkaa koodia').should('be.visible');
        cy.contains('kunta').should('be.visible');
    });
    it('can modify koodi', () => {
        cy.intercept('PUT', `${API_INTERNAL_PATH}/koodi`, (req) => {
            expect(req.body.metadata).to.eqls([
                { kieli: 'FI', nimi: 'Akaa muokattu', lyhytNimi: 'lyhyt nimi', kuvaus: '', kasite: 'kasite' },
                { kieli: 'SV', nimi: 'Akaa', kuvaus: '' },
                { kieli: 'EN', nimi: 'Akaa', kuvaus: '' },
            ]);
            req.reply({ fixture: 'koodiPage.json' });
        });
        cy.get('input[name="metadata[0][nimi]"]')
            .should('be.visible')
            .should('have.value', 'Akaa loytyy')
            .clear()
            .type('Akaa muokattu');
        cy.get('input[name="metadata[0][lyhytNimi]"]')
            .should('be.visible')
            .should('have.value', '')
            .clear()
            .type('lyhyt nimi');
        cy.get('input[name="metadata[0][kasite]"]')
            .should('be.visible')
            .should('have.value', '')
            .clear()
            .type('kasite');
        cy.intercept(`${API_INTERNAL_PATH}/koodi/kunta_020/2`, { fixture: 'koodiPage.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.get('button[name="KOODI_TALLENNA"]').should('be.visible').click();
        cy.contains('Tallennettiin koodi uri:lla kunta_020').should('be.visible');
    });
});
