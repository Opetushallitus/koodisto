import { expect, test } from '@playwright/test';

import { mockRoutes, mockKoodiPage, mockKoodiPageKoodisto } from '../routes';
import { API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';
import codes from '../fixtures/codes.json';
import koodiPage from '../fixtures/koodiPage.json';
import koodiPageKoodisto from '../fixtures/koodiPageKoodisto.json';

test('Koodi Edit page', async ({ page }) => {
    await mockRoutes(page);
    await mockKoodiPage(page, 'kunta_020', 2, koodiPage);
    await mockKoodiPageKoodisto(page, 'kunta', koodiPageKoodisto);

    await test.step('Contains delete button', async () => {
        await page.goto(`${BASE_PATH}/koodi/edit/kunta_020/2`);
        await expect(page.locator('button[name=KOODI_POISTA]')).toBeVisible();
    });

    await test.step('Delete button opens dialog', async () => {
        await page.locator('button[name=KOODI_POISTA]').click();
        await expect(page.locator('#close-dialog')).toBeVisible();
    });

    await test.step('Delete dialog can be closed', async () => {
        await page.locator('#close-dialog').click();
        await expect(page.locator('#close-dialog')).not.toBeVisible();
    });

    await test.step('Delete action disabled if not confirmed', async () => {
        await page.locator('button[name=KOODI_POISTA]').click();
        await expect(page.locator('button[name=CONFIRMATION_ACTION]')).toBeDisabled();
    });

    await test.step('Delete action available after confirmation', async () => {
        await page.locator('input[name=CONFIRMATION_CHECK]').click({ force: true });
        await expect(page.locator('button[name=CONFIRMATION_ACTION]')).toBeEnabled();
    });

    await test.step('Delete redirects to koodisto page', async () => {
        await page.route(`${API_INTERNAL_PATH}/koodi/kunta_020/2`, async (route) => {
            if (route.request().method() !== 'DELETE') {
                await route.fallback();
                return;
            }
            await route.fulfill({ status: 204 });
        });
        await page.route(`${API_INTERNAL_PATH}/koodisto`, async (route) => {
            if (route.request().method() !== 'GET') {
                await route.fallback();
                return;
            }
            await route.fulfill({ json: codes });
        });

        await page.locator('button[name=CONFIRMATION_ACTION]').click({ force: true });
        await page.waitForURL('/koodisto-service/ui/koodisto/view/kunta/2');
    });
});
