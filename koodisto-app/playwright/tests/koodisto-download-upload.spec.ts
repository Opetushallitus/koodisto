import { expect, test } from '@playwright/test';
import * as fs from 'fs/promises';
import path from 'path';

import { mockRoutes } from '../routes';
import { API_BASE_PATH, API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';
import arvosanat from '../fixtures/arvosanat.json';
import arvosanatKoodisto from '../fixtures/arvosanatKoodisto.json';

test('CSV functionality tests', async ({ page }) => {
    await mockRoutes(page);

    await test.step('shows download button on koodisto page', async () => {
        await page.route(`${API_INTERNAL_PATH}/koodi/koodisto/arvosanat/1`, async (route) => {
            await route.fulfill({ json: arvosanat });
        });
        await page.route(`${API_INTERNAL_PATH}/koodisto/arvosanat/1`, async (route) => {
            await route.fulfill({ json: arvosanatKoodisto });
        });
        await page.goto(`${BASE_PATH}/koodisto/view/arvosanat/1`);
        await expect(page.locator(`[name="arvosanat-csv"]`)).toBeVisible();
    });

    await test.step('can download arvosanat', async () => {
        await page.route(`${API_BASE_PATH}/json/arvosanat/koodi*`, async (route) => {
            await route.fulfill({ json: arvosanat });
        });

        await page.locator(`[name="arvosanat-csv"]`).click();
        const [download] = await Promise.all([
            page.waitForEvent('download'),
            page.locator(`[name="arvosanat-download"]`).click(),
        ]);
        await download.saveAs('./' + download.suggestedFilename());
        const file = await fs.readFile('./' + download.suggestedFilename(), 'utf16le');
        expect(file.length).toBeGreaterThan(100);
        expect(file).toContain('Godkänt');
    });

    await test.step('upload csv file', async () => {
        await page.goto(`${BASE_PATH}/koodisto/view/arvosanat/1`);
        await page.locator(`[name="arvosanat-csv"]`).click();
        const [upload] = await Promise.all([
            page.waitForEvent('filechooser'),
            page.getByText('Valitse tiedosto').click(),
        ]);
        await upload.setFiles(path.join(__dirname, `../fixtures/arvosanat_upload.csv`));
        await expect(page.getByText('Cypress_Muokattu')).toBeVisible();
        await page.route(`${API_INTERNAL_PATH}/koodi/upsert/arvosanat`, async (route) => {
            expect(route.request().postDataJSON()[0].metadata[0]).toStrictEqual({
                nimi: 'Cypress_Muokattu',
                lyhytNimi: 'Hyväksytty',
                kieli: 'FI',
            });
            await route.fulfill({ json: arvosanatKoodisto });
        });
        await page.getByText('Tallenna').click();
    });

    await test.step('upload csv file with empty line', async () => {
        await page.goto(`${BASE_PATH}/koodisto/view/arvosanat/1`);
        await page.locator(`[name="arvosanat-csv"]`).click();
        const [upload] = await Promise.all([
            page.waitForEvent('filechooser'),
            page.getByText('Valitse tiedosto').click(),
        ]);
        await upload.setFiles(path.join(__dirname, `../fixtures/arvosanat_upload_empty_line.csv`));
        await expect(page.getByText('Cypress_Muokattu')).toBeVisible();
        await page.route(`${API_INTERNAL_PATH}/koodi/upsert/arvosanat`, async (route) => {
            expect(route.request().postDataJSON()[0].metadata[0]).toStrictEqual({
                nimi: 'Cypress_Muokattu',
                lyhytNimi: 'Hyväksytty',
                kieli: 'FI',
            });
            await route.fulfill({ json: arvosanatKoodisto });
        });
        await page.getByText('Tallenna').click();
    });
});
