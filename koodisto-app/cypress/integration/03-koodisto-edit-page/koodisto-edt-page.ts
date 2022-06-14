import { BASE_PATH, API_INTERNAL_PATH } from '../../../src/context/constants';

describe('The Koodisto View page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('shows testi koodisto on koodisto view page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodisto/view/kunta/2`);
        cy.contains('kunta').should('be.visible');
    });
    it('shows edit button and can click', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.get('button[name="KOODISTOSIVU_MUOKKAA_KOODISTOA_BUTTON"]').should('be.visible').click();
        cy.contains('Muokkaa koodistoa').should('be.visible');
    });
    it('shows edit button and can click', () => {
        cy.intercept('PUT', `${API_INTERNAL_PATH}/koodisto`, (req) => {
            expect(req.body.metadata).to.eqls([
                { kieli: 'FI', nimi: 'kunta muokattu', kuvaus: 'kunta' },
                { kieli: 'SV', nimi: 'kommun', kuvaus: 'kommun' },
                { kieli: 'EN', nimi: 'municipality', kuvaus: 'municipality' },
            ]);
            req.reply({ fixture: 'kuntaKoodisto.json' });
        });
        cy.get('input[name="metadata.0.nimi"]')
            .should('be.visible')
            .should('have.value', 'kunta')
            .clear()
            .type('kunta muokattu');
        cy.get('button[name="KOODISTO_TALLENNA"]').should('be.visible').click();
        cy.contains('Tallennettiin koodisto uri:lla kunta').should('be.visible');
    });
});
