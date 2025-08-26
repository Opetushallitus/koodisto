import { expect, test } from '@playwright/test';

import { mockGetKoodisto, mockGetKoodiKoodisto, mockRoutes, mockDeleteKoodisto } from '../routes';
import { API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';
import kuntaKoodisto from '../fixtures/kuntaKoodisto.json';
import kuntaKoodistoKoodit from '../fixtures/kuntaKoodistoKoodit.json';

test('Koodisto Edit page', async ({ page }) => {
    await mockRoutes(page);
    await mockGetKoodisto(page, 'kunta', 2, kuntaKoodisto);
    await mockGetKoodiKoodisto(page, 'kunta', 2, kuntaKoodistoKoodit);

    await test.step('Contains delete button', async () => {
        await page.goto(`${BASE_PATH}/koodisto/edit/kunta/2`);
        await expect(page.locator('button[name=KOODISTO_POISTA]')).toBeVisible();
    });

    await test.step('Delete button opens dialog', async () => {
        await page.locator('button[name=KOODISTO_POISTA]').click();
        await expect(page.locator('#close-dialog')).toBeVisible();
    });

    await test.step('Delete dialog can be closed', async () => {
        await expect(page.locator('#close-dialog')).toBeVisible();
        await page.locator('#close-dialog').click();
        await expect(page.locator('#close-dialog')).not.toBeVisible();
    });

    await test.step('Delete action disabled if not confirmed', async () => {
        await page.locator('button[name=KOODISTO_POISTA]').click();
        await expect(page.locator('button[name=CONFIRMATION_ACTION]')).toBeDisabled();
    });

    await test.step('Delete action available after confirmation', async () => {
        await page.locator('input[name=CONFIRMATION_CHECK]').click({ force: true });
        await expect(page.locator('button[name=CONFIRMATION_ACTION]')).toBeEnabled();
    });

    await test.step('Delete redirects to list page after last version is deleted', async () => {
        await mockDeleteKoodisto(page, 'kunta', 2);
        await page.route(`${API_INTERNAL_PATH}/koodisto`, async (route) => {
            await route.fulfill({ json: [] });
        });
        await page.locator('button[name=CONFIRMATION_ACTION]').click({ force: true });
        await page.waitForURL(/\/koodisto-service\/ui\/$/);
    });
});
