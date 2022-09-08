/// <reference types="cypress" />
// ***********************************************************
// This example plugins/index.js can be used to load plugins
//
// You can change the location of this file or turn off loading
// the plugins file with the 'pluginsFile' configuration option.
//
// You can read more here:
// https://on.cypress.io/plugins-guide
// ***********************************************************

// This function is called when a project is opened or re-opened (e.g. due to
// the project's config changing)

const { rm } = require('fs');
/**
 * @type {Cypress.PluginConfig}
 */
// eslint-disable-next-line no-unused-vars
module.exports = (on, config) => {
    require('@cypress/code-coverage/task')(on, config);
    // `on` is used to hook into various events Cypress emits
    // `config` is the resolved Cypress config
    on('task', {
        deleteFolder(folderName) {
            console.info('deleting folder %s', folderName);
            return new Promise((resolve, reject) => {
                rm(folderName, { maxRetries: 10, recursive: true, force: true }, (err) => {
                    if (err) {
                        console.error(err);
                        return reject(err);
                    }
                    resolve(null);
                });
            });
        },
    });
    return config;
};
