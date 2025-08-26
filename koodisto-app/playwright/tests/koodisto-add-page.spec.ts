import { expect, test } from '@playwright/test';

import { mockRoutes } from '../routes';
import { API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';
import codes from '../fixtures/codes.json';
import koodistoPostResponse from '../fixtures/koodistoPostResponse.json';
import koodistoPostGetResponse from '../fixtures/koodistoPostGetResponse.json';

test('The Koodisto Add page', async ({ page }) => {
    await mockRoutes(page);
    await page.route(`${API_INTERNAL_PATH}/koodisto`, async (route) => {
        await route.fulfill({ json: codes });
    });

    await test.step('shows landing page and can click add button', async () => {
        await page.goto(BASE_PATH);
        await page.getByText('Luo uusi koodisto').click();
    });

    await test.step('can enter field values', async () => {
        await page.locator('div[id="koodistoRyhmaUri"] input[type=text]').fill('Va', { force: true });
        await page.keyboard.press('Enter');
        await page.keyboard.press('Enter');

        await page.locator('input[name="metadata[0][nimi]"]').fill('nimi-1655458944744');
        await page.locator('input[name="metadata[1][nimi]"]').fill('nimi-1655458944744');
        await page.locator('input[name="metadata[2][nimi]"]').fill('nimi-1655458944744');

        await page
            .getByText('Alkupäivämäärä')
            .locator('..')
            .locator('input[type=text]')
            .fill('1.1.2022', { force: true });
        await page.keyboard.press('Enter');
        await page.keyboard.press('Enter');

        await page.locator('div[id="organisaatioOid"] input[type=text]').fill('csc', { force: true });
        await page.keyboard.press('Enter');
        await page.keyboard.press('Enter');

        await page.locator('input[name="omistaja"]').fill('omistaja-input');

        await page.locator('textarea[name="metadata[0][kuvaus]"]').fill('nimi-1655458944744');
        await page.getByText('Kuvaus').locator('..').locator('svg[name="KOPIOI_MUIHIN_KIELIIN"]').click();
    });

    await test.step('can save changes and open view page', async () => {
        await page.route(`${API_INTERNAL_PATH}/koodi/koodisto/nimi1655458944744/1`, async (route) => {
            if (route.request().method() !== 'GET') {
                await route.fallback();
                return;
            }
            await route.fulfill({ json: [] });
        });
        await page.route(`${API_INTERNAL_PATH}/koodisto/koute`, async (route) => {
            if (route.request().method() !== 'POST') {
                await route.fallback();
                return;
            }
            expect(route.request().postDataJSON().metadataList).toStrictEqual([
                { kieli: 'FI', nimi: 'nimi-1655458944744', kuvaus: 'nimi-1655458944744' },
                { kieli: 'SV', nimi: 'nimi-1655458944744', kuvaus: 'nimi-1655458944744' },
                { kieli: 'EN', nimi: 'nimi-1655458944744', kuvaus: 'nimi-1655458944744' },
            ]);
            await route.fulfill({ json: koodistoPostResponse });
        });
        await page.route(`${API_INTERNAL_PATH}/koodisto/nimi1655458944744/1`, async (route) => {
            if (route.request().method() !== 'GET') {
                await route.fallback();
                return;
            }
            await route.fulfill({ json: koodistoPostGetResponse });
        });
        await page.locator('button[name="KOODISTO_TALLENNA"]').click();
        await expect(page.locator('h1')).toHaveText('nimi-1655458944744');
    });
});
