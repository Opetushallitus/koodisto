import { expect, test } from '@playwright/test';

import { mockGetKoodisto, mockGetKoodiKoodisto, mockRoutes, mockPostKoodisto } from '../routes';
import { API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';
import kuntaKoodisto from '../fixtures/kuntaKoodisto.json';
import kuntaKoodistoKoodit from '../fixtures/kuntaKoodistoKoodit.json';
import codes from '../fixtures/codes.json';

test('Koodisto Edit page', async ({ page }) => {
    await mockRoutes(page);
    await mockGetKoodisto(page, 'kunta', 2, kuntaKoodisto);
    await mockGetKoodiKoodisto(page, 'kunta', 2, kuntaKoodistoKoodit);

    await test.step('Contains version button', async () => {
        await page.goto(`${BASE_PATH}/koodisto/edit/kunta/2`);
        await expect(page.locator('button[name=KOODISTO_VERSIOI]')).toBeVisible();
    });
    await test.step('Version button opens dialog', async () => {
        await page.locator('button[name=KOODISTO_VERSIOI]').click();
        await expect(page.locator('button[name=CONFIRMATION_ACTION]')).toBeDisabled();
        await expect(page.locator('#close-dialog')).toBeVisible();
    });
    await test.step('Version dialog can be closed', async () => {
        await page.locator('#close-dialog').click();
        await expect(page.locator('#close-dialog')).not.toBeVisible();
    });
    await test.step('Version action available after confirmation', async () => {
        await page.locator('button[name=KOODISTO_VERSIOI]').click();
        await page.locator('input[name=CONFIRMATION_CHECK]').click({ force: true });
        await expect(page.locator('button[name=CONFIRMATION_ACTION]')).toBeEnabled();
    });
    await test.step('Redirects to newly created version', async () => {
        await mockPostKoodisto(page, 'kunta', 2, kuntaKoodisto);
        await page.route(`${API_INTERNAL_PATH}/koodisto`, async (route) => {
            await route.fulfill({ json: codes });
        });
        await page.locator('button[name=CONFIRMATION_ACTION]').click({ force: true });
        await page.waitForURL(/\/koodisto-service\/ui\/koodisto\/view\/kunta\/2$/);
    });
});
