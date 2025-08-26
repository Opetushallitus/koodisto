import { expect, test } from '@playwright/test';

import { mockRoutes, mockKoodiPage, mockKoodiPageKoodisto } from '../routes';
import { API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';
import koodiPage from '../fixtures/koodiPage.json';
import koodiPageKoodisto from '../fixtures/koodiPageKoodisto.json';

test('Koodi edit page', async ({ page }) => {
    await mockRoutes(page);
    await mockKoodiPage(page, 'kunta_020', 2, koodiPage);
    await mockKoodiPageKoodisto(page, 'kunta', koodiPageKoodisto);

    await test.step('renders view page', async () => {
        await page.goto(`${BASE_PATH}/koodi/view/kunta_020/2`);
        await expect(page.getByRole('heading', { name: 'Akaa loytyy' })).toBeVisible();
    });

    await test.step('shows edit button and can open edit page', async () => {
        await page.locator('button[name=KOODISIVU_MUOKKAA_KOODIA_BUTTON]').click();
        await expect(page.getByText('Muokkaa koodia')).toBeVisible();
        await expect(page.getByRole('link', { name: 'kunta' })).toBeVisible();
    });

    await test.step('can modify koodi', async () => {
        await page.route(`${API_INTERNAL_PATH}/koodi`, async (route) => {
            expect(route.request().postDataJSON().metadata).toStrictEqual([
                { kieli: 'FI', nimi: 'Akaa muokattu', lyhytNimi: 'lyhyt nimi', kuvaus: '', kasite: 'kasite' },
                { kieli: 'SV', nimi: 'Akaa', kuvaus: '' },
                { kieli: 'EN', nimi: 'Akaa', kuvaus: '' },
            ]);
            await route.fulfill({ json: koodiPage });
        });

        await page.locator('input[name="metadata[0][nimi]"]').clear();
        await page.locator('input[name="metadata[0][nimi]"]').fill('Akaa muokattu');
        await page.locator('input[name="metadata[0][lyhytNimi]"]').clear();
        await page.locator('input[name="metadata[0][lyhytNimi]"]').fill('lyhyt nimi');
        await page.locator('input[name="metadata[0][kasite]"]').clear();
        await page.locator('input[name="metadata[0][kasite]"]').fill('kasite');
        await page.locator('button[name="KOODI_TALLENNA"]').click();
        await expect(page.getByText('Tallennettiin koodi uri:lla kunta_020')).toBeVisible();
    });
});
