// ***********************************************
// This example commands.js shows you how to
// create various custom commands and overwrite
// existing commands.
//
// For more comprehensive examples of custom
// commands please read more here:
// https://on.cypress.io/custom-commands
// ***********************************************
//
//
// -- This is a parent command --
// Cypress.Commands.add('login', (email, password) => { ... })
//
//
// -- This is a child command --
// Cypress.Commands.add('drag', { prevSubject: 'element'}, (subject, options) => { ... })
//
//
// -- This is a dual command --
// Cypress.Commands.add('dismiss', { prevSubject: 'optional'}, (subject, options) => { ... })
//
//
// -- This will overwrite an existing command --
// Cypress.Commands.overwrite('visit', (originalFn, url, options) => { ... })
import 'cypress-file-upload';
import { API_INTERNAL_PATH, API_STATUS_PATH } from '../../src/context/constants';

Cypress.Commands.add('mockBaseIntercepts', () => {
    cy.intercept(`${API_INTERNAL_PATH}/koodistoryhma`, { fixture: 'allRyhmat.json' });
    cy.intercept(`${API_STATUS_PATH}`, { fixture: 'status.json' });
    cy.intercept(`${API_INTERNAL_PATH}/koodisto`, { fixture: 'codes.json' });
});
