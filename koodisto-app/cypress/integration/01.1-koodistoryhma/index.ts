import { API_INTERNAL_PATH, BASE_PATH } from '../../../src/context/constants';

describe('The landing page', () => {
    beforeEach(() => {
        cy.mockBaseIntercepts();
    });
    it('shows koodistoryhma button on landing page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { fixture: 'codes.json' });
        cy.visit(`${BASE_PATH}`);
        cy.get('button[name=TAULUKKO_LISAA_KOODISTORYHMA_BUTTON]').should('be.visible');
    });
    it('can click koodistoryhma button on landing page', () => {
        cy.intercept(`${API_INTERNAL_PATH}/koodistoryhma/empty`, { fixture: 'emptyRyhma0.json' });
        cy.get('button[name=TAULUKKO_LISAA_KOODISTORYHMA_BUTTON]').should('be.visible').click();
    });
    it('shows the koodistoryhma modal in add mode', () => {
        cy.get('div').contains('Luo uusi koodistoryhmä').should('be.visible');
    });
    it('can add a new ryhma', () => {
        cy.intercept('POST', `${API_INTERNAL_PATH}/koodistoryhma`, (req) => {
            expect(req.body.nimi).to.eql({ fi: 'foo', sv: 'foo', en: 'foo' });
            req.reply({ koodistoRyhmaUri: 'foo' });
        });
        cy.get('input[name=fi]').type('foo');
        cy.get('input[name=sv]').type('foo');
        cy.get('input[name=en]').type('foo');
        cy.intercept(`${API_INTERNAL_PATH}/koodistoryhma/empty`, { fixture: 'emptyRyhma1.json' });
        cy.get('button[name=KOODISTO_RYHMA_LUO_UUSI]').should('be.visible').click();
    });
    it('can add a new ryhma with copied values', () => {
        cy.intercept('POST', `${API_INTERNAL_PATH}/koodistoryhma`, (req) => {
            expect(req.body.nimi).to.eql({ fi: 'bar', sv: 'bar', en: 'bar' });
            req.reply({ koodistoRyhmaUri: 'bar' });
        });
        cy.get('input[name=fi]').type('bar');
        cy.get('svg[name=KOPIOI_MUIHIN_NIMIIN]').should('be.visible').click();
        cy.intercept(`${API_INTERNAL_PATH}/koodistoryhma/empty`, { fixture: 'emptyRyhma1.json' });
        cy.get('button[name=KOODISTO_RYHMA_LUO_UUSI]').should('be.visible').click();
    });
    it('can delete empty ryhma', () => {
        cy.intercept('DELETE', `${API_INTERNAL_PATH}/koodistoryhma/foo`, '');
        cy.intercept(`${API_INTERNAL_PATH}/koodistoryhma/empty`, { fixture: 'emptyRyhma0.json' });
        cy.get('button[name=POISTA_KOODISTORYHMA-foo]').should('be.visible').click();
    });
    it('can close the modal', () => {
        cy.get('button[name=KOODISTO_RYHMA_SULJE]').should('be.visible').click();
    });
    it('can open modal in edit mode', () => {
        cy.intercept('GET', `${API_INTERNAL_PATH}/koodistoryhma/haunkoodistot`, { fixture: 'haunkoodistot.json' });
        cy.intercept(`${API_INTERNAL_PATH}/koodistoryhma/empty`, { fixture: 'emptyRyhma0.json' });
        cy.get('a').contains('Haun koodistot').should('be.visible').click();
        cy.get('div').contains('Muokkaa koodistoryhmää').should('be.visible');
    });
    it('can edit old ryhma', () => {
        cy.get('input[name=fi]').clear().type('foo');
        cy.intercept('PUT', `${API_INTERNAL_PATH}/koodistoryhma/haunkoodistot`, (req) => {
            expect(req.body.nimi).to.eql({ fi: 'foo', sv: 'Haun koodistot', en: 'Haun koodistot' });
            req.reply({ koodistoRyhmaUri: 'haunkoodistot' });
        });
        cy.get('button[name=KOODISTO_RYHMA_TALLENNA]').should('be.visible').click();
    });
});
