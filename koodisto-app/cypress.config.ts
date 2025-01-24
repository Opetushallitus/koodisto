import { defineConfig } from 'cypress';

const { rm } = require('fs');

export default defineConfig({
    defaultCommandTimeout: 10000,
    pageLoadTimeout: 15000,
    video: true,
    e2e: {
        setupNodeEvents(on, config) {
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
        },
        baseUrl: 'http://devaaja:devaaja@localhost:3000/',
    },
});
