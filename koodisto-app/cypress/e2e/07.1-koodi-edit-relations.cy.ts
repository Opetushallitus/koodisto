import { BASE_PATH, API_INTERNAL_PATH } from '../../src/context/constants';

describe('The Koodi Edit page can edit relations', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('renders view page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodi/kunta_020/2`, { fixture: 'relationKoodi.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'koodiPageKoodisto.json' });
        cy.visit(`${BASE_PATH}/koodi/view/kunta_020/2`);
        cy.contains('Akaa').should('be.visible');
    });
    it('shows edit button and can open edit page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodi/kunta_020/2`, { fixture: 'relationKoodi.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'koodiPageKoodisto.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.get('button[name=KOODISIVU_MUOKKAA_KOODIA_BUTTON]').should('be.visible').click();
        cy.contains('Muokkaa koodia').should('be.visible');
        cy.contains('kunta').should('be.visible');
    });
    it('shows within relations and can click', () => {
        cy.get('div').contains('Sisältyy koodeihin (2)').should('be.visible').click();
    });
    it('shows relations add button and can click', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodi/koodisto/oppilaitosnumero/1`, { fixture: 'oppilaitosKoodit.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/koodisto/aluehallintovirasto/1`, {
            fixture: 'aluehallintovirastoKoodit.json',
        });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/koodisto/aluehallintovirasto/2`, []);

        cy.get('button[name=TAULUKKO_LISAA_KOODISUHTEITA_BUTTON]').filter(':visible').click();
    });
    it('shows koodis and can select', () => {
        cy.get('input[type=checkbox]').eq(1).click();
        cy.get('button[name=SUHDEMODAL_VALITSE]').click();
    });
    it('shows added relations', () => {
        cy.get('div').contains('Sisältyy koodeihin (3)').should('be.visible');
    });
    it('can save added relations', () => {
        cy.intercept('PUT', `${API_INTERNAL_PATH}/koodi`, (req) => {
            expect(req.body.sisaltyyKoodeihin.length).to.eq(3);
            req.reply({ fixture: 'relationKoodi.json' });
        });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/kunta_020/2`, { fixture: 'koodiPage.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'koodiPageKoodisto.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.get('button[name="KOODI_TALLENNA"]').should('be.visible').click();
        cy.contains('Koodi tallennettiin').should('be.visible').click();
    });
    it('shows edit button and can open edit page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodi/kunta_020/2`, { fixture: 'koodiPage.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'koodiPageKoodisto.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.get('button[name=KOODISIVU_MUOKKAA_KOODIA_BUTTON]').should('be.visible').click();
        cy.contains('Muokkaa koodia').should('be.visible');
    });
    it('shows includes relations and can click', () => {
        cy.get('div').contains('Sisältää koodit (7)').should('be.visible').click();
        cy.get('button[name=TAULUKKO_POISTA_KOODISUHTEITA_BUTTON]').filter(':visible').eq(0).click();
        cy.get('button[name=TAULUKKO_POISTA_KOODISUHTEITA_BUTTON]').filter(':visible').eq(0).click();
        cy.get('button[name=TAULUKKO_POISTA_KOODISUHTEITA_BUTTON]').filter(':visible').eq(0).click();
        cy.get('button[name=TAULUKKO_POISTA_KOODISUHTEITA_BUTTON]').filter(':visible').eq(0).click();
        cy.get('div').contains('Sisältää koodit (3)').should('be.visible');
    });
    it('can save removed relations', () => {
        cy.intercept('PUT', `${API_INTERNAL_PATH}/koodi`, (req) => {
            expect(req.body.sisaltaaKoodit.length).to.eq(3);
            req.reply({ fixture: 'relationKoodi.json' });
        });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/kunta_020/2`, { fixture: 'koodiPage.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta/2`, { fixture: 'koodiPageKoodisto.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodisto/kunta`, { fixture: 'koodiPageKoodisto.json' });
        cy.get('button[name="KOODI_TALLENNA"]').should('be.visible').click();
        cy.contains('Koodi tallennettiin').should('be.visible').click();
    });
    it('can copy relations from other koodi', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodi/aidinkielijakirjallisuus_se/1`, { fixture: '07/saameKoodi.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/koodisto/aidinkielijakirjallisuus/1`, {
            fixture: '07/aidinkielijakirjallisuusKoodit.json',
        });
        cy.visit(`${BASE_PATH}/koodi/edit/aidinkielijakirjallisuus_se/1`);

        cy.intercept(`${API_INTERNAL_PATH}/koodisto/aidinkielijakirjallisuus/1`, {
            fixture: '07/aidinkielijakirjallisuusKoodisto.json',
        });
        cy.get('button[name=KOODI_KOPIOI_SUHTEET_BUTTON]').should('be.visible').click();
        cy.contains('Valitse koodit').should('be.visible');
        cy.get('input[type=checkbox]').eq(0).click();
        cy.contains('Valitse (3)').should('be.visible');

        cy.intercept(`${API_INTERNAL_PATH}/koodi/aidinkielijakirjallisuus_fi/1`, {
            fixture: '07/aidinkielijakirjallisuusFi.json',
        });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/aidinkielijakirjallisuus_fi2/1`, {
            fixture: '07/aidinkielijakirjallisuusFi2.json',
        });
        cy.intercept(`${API_INTERNAL_PATH}/koodi/aidinkielijakirjallisuus_fise/1`, {
            fixture: '07/aidinkielijakirjallisuusFiSe.json',
        });
        cy.get('button[name=SUHDEMODAL_VALITSE]').should('be.visible').click();
        cy.contains('Sisältyy koodeihin (3)').should('be.visible');
        cy.contains('Sisältää koodit (1)').should('be.visible');
        cy.contains('Rinnastuu koodeihin (2)').should('be.visible');
    });
});
