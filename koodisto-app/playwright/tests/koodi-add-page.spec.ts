import { expect, test } from '@playwright/test';

import { mockGetKoodiKoodisto, mockGetKoodisto, mockKoodiPage, mockKoodiPageKoodisto, mockRoutes } from '../routes';
import { API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';
import kuntaKoodisto from '../fixtures/kuntaKoodisto.json';
import kuntaKoodistoKoodit from '../fixtures/kuntaKoodistoKoodit.json';
import koodiPage from '../fixtures/koodiPage.json';
import koodiPageKoodisto from '../fixtures/koodiPageKoodisto.json';

test('Koodi add page', async ({ page }) => {
    await mockRoutes(page);
    await mockGetKoodisto(page, 'kunta', 2, kuntaKoodisto);
    await mockGetKoodiKoodisto(page, 'kunta', 2, kuntaKoodistoKoodit);
    await mockKoodiPage(page, 'kunta_020', 2, koodiPage);
    await mockKoodiPageKoodisto(page, 'kunta', koodiPageKoodisto);

    await test.step('shows testi koodisto on koodisto view page', async () => {
        await page.goto(`${BASE_PATH}/koodisto/view/kunta/2`);
        await expect(page.getByRole('heading', { name: 'kunta' })).toBeVisible();
    });

    await test.step('shows add new koodi button and can open page', async () => {
        await page.locator('button[name="TAULUKKO_LISAA_KOODI_BUTTON"]').click();
        await expect(page.locator('h1')).toHaveText('Lisää koodi');
    });

    await test.step('can enter data', async () => {
        await page.locator('input[name="koodiArvo"]').fill('arvo');

        await page.locator('input[name="metadata[0][nimi]"]').fill('nimi-arvo');
        await page.locator('input[name="metadata[1][nimi]"]').fill('nimi-arvo');
        await page.locator('input[name="metadata[2][nimi]"]').fill('nimi-arvo');

        await page.locator('input[name="metadata[0][lyhytNimi]"]').fill('lyhyt-arvo');
        await page.locator('input[name="metadata[1][lyhytNimi]"]').fill('lyhyt-arvo');
        await page.locator('input[name="metadata[2][lyhytNimi]"]').fill('lyhyt-arvo');

        await page
            .getByText('Alkupäivämäärä')
            .locator('..')
            .locator('input[type=text]')
            .fill('1.1.2022', { force: true });
        await page.keyboard.press('Enter');
        await page.keyboard.press('Enter');

        await page
            .getByText('Loppupäivämäärä')
            .locator('..')
            .locator('input[type=text]')
            .fill('1.1.2023', { force: true });
        await page.keyboard.press('Enter');
        await page.keyboard.press('Enter');

        await page.locator('textarea[name="metadata[0][kuvaus]"]').fill('kuvaus-arvo');
        await page.locator('textarea[name="metadata[1][kuvaus]"]').fill('kuvaus-arvo');
        await page.locator('textarea[name="metadata[2][kuvaus]"]').fill('kuvaus-arvo');
    });

    await test.step('can save changes and open view page', async () => {
        await page.route(`${API_INTERNAL_PATH}/koodi/kunta`, async (route) => {
            if (route.request().method() !== 'POST') {
                await route.fallback();
                return;
            }
            expect(route.request().postDataJSON().koodiArvo).toBe('arvo');
            expect(route.request().postDataJSON().voimassaAlkuPvm).toBe('2022-01-01');
            expect(route.request().postDataJSON().voimassaLoppuPvm).toBe('2023-01-01');
            expect(route.request().postDataJSON().metadata).toStrictEqual([
                { kieli: 'FI', nimi: 'nimi-arvo', lyhytNimi: 'lyhyt-arvo', kuvaus: 'kuvaus-arvo' },
                { kieli: 'SV', nimi: 'nimi-arvo', lyhytNimi: 'lyhyt-arvo', kuvaus: 'kuvaus-arvo' },
                { kieli: 'EN', nimi: 'nimi-arvo', lyhytNimi: 'lyhyt-arvo', kuvaus: 'kuvaus-arvo' },
            ]);
            await route.fulfill({ json: koodiPage });
        });
        await page.route(`${API_INTERNAL_PATH}/koodi/kunta_arvo/1`, async (route) => {
            await route.fulfill({ json: koodiPage });
        });

        await page.locator('button[name="KOODI_TALLENNA"]').click();
        await expect(page.locator('h1')).toHaveText('Akaa loytyy');
    });
});
