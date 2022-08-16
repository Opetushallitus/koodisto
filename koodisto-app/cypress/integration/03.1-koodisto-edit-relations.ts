import { BASE_PATH, API_INTERNAL_PATH } from '../../src/context/constants';

describe('The Koodisto Edit page can edit relations', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('shows testi koodisto on koodisto view page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/koodisto/kunta/2`, { fixture: 'kuntaKoodistoKoodit.json' });
        cy.visit(`${BASE_PATH}/koodisto/view/kunta/2`);
        cy.contains('kunta').should('be.visible');
    });
    it('shows edit button and can open edit page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.get('button[name="KOODISTOSIVU_MUOKKAA_KOODISTOA_BUTTON"]').should('be.visible').click();
        cy.contains('Muokkaa koodistoa').should('be.visible');
    });
    it('shows within relations and can click', () => {
        cy.get('div').contains('Sisältyy koodistoihin').should('be.visible').click();
    });
    it('shows relations add button and can click', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { fixture: 'codes.json' });
        cy.get('button[name=TAULUKKO_LISAA_KOODISTOSUHTEITA_BUTTON]').filter(':visible').click();
    });
    it('shows koodistos and can select', () => {
        cy.get('input[type=checkbox]').eq(1).click();
        cy.get('button[name=SUHDEMODAL_LISAA]').click();
    });
    it('shows added relations', () => {
        cy.get('div').contains('Sisältyy koodistoihin (3)').should('be.visible');
    });
    it('can save added relations', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.intercept('PUT', `${API_INTERNAL_PATH}/koodisto`, (req) => {
            expect(req.body.withinCodes.length).to.eq(3);
            req.reply({ fixture: 'kuntaKoodisto.json' });
        });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/koodisto/kunta/2`, { fixture: 'kuntaKoodistoKoodit.json' });
        cy.get('button[name="KOODISTO_TALLENNA"]').should('be.visible').click();
        cy.contains('Koodisto tallennettiin').should('be.visible').click();
    });
    it('shows edit button and can open edit page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.get('button[name="KOODISTOSIVU_MUOKKAA_KOODISTOA_BUTTON"]').should('be.visible').click();
        cy.contains('Muokkaa koodistoa').should('be.visible');
    });
    it('shows includes relations and can click', () => {
        cy.get('div').contains('Sisältää koodistot (9)').should('be.visible').click();
        cy.get('button[name=TAULUKKO_POISTA_KOODISTOSUHTEITA_BUTTON]').filter(':visible').eq(0).click();
        cy.get('button[name=TAULUKKO_POISTA_KOODISTOSUHTEITA_BUTTON]').filter(':visible').eq(0).click();
        cy.get('button[name=TAULUKKO_POISTA_KOODISTOSUHTEITA_BUTTON]').filter(':visible').eq(0).click();
        cy.get('button[name=TAULUKKO_POISTA_KOODISTOSUHTEITA_BUTTON]').filter(':visible').eq(0).click();
        cy.get('div').contains('Sisältää koodistot (5)').should('be.visible');
    });
    it('can save removed relations', () => {
        cy.intercept('PUT', `${API_INTERNAL_PATH}/koodisto`, (req) => {
            expect(req.body.includesCodes.length).to.eq(5);
            req.reply({ fixture: 'kuntaKoodisto.json' });
        });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'kuntaKoodisto.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/koodisto/kunta/2`, { fixture: 'kuntaKoodistoKoodit.json' });
        cy.get('button[name="KOODISTO_TALLENNA"]').should('be.visible').click();
        cy.contains('Koodisto tallennettiin').should('be.visible').click();
    });
});
