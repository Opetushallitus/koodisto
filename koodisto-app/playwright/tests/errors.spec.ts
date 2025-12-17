import { expect, test } from '@playwright/test';

import { mockRoutes } from '../routes';
import { API_BASE_PATH, API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';
import codes from '../fixtures/codes.json';
import kuntaKoodisto from '../fixtures/kuntaKoodisto.json';

test.describe('Errors', async () => {
    const koodistoUri = 'kunta';

    test.beforeEach(async ({ page }) => {
        await mockRoutes(page);
        await page.route(`${API_INTERNAL_PATH}/koodisto/${koodistoUri}/2`, async (route) => {
            await route.fulfill({ json: kuntaKoodisto });
        });
    });

    test('shows error boudary if codes fails', async ({ page }) => {
        await page.route(`${API_INTERNAL_PATH}/koodisto`, async (route) => {
            await route.fulfill({
                status: 404,
                body: '404 Not Found!',
            });
        });
        await page.goto(BASE_PATH);
        await expect(page.getByText('Service Unavailable')).toBeVisible();
    });

    test('error if 500', async ({ page }) => {
        await page.route(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, async (route) => {
            await route.fulfill({ status: 500 });
        });

        await page.goto(`${BASE_PATH}/koodisto/view/${koodistoUri}/2`);
        await page.locator(`[name="${koodistoUri}-csv"]`).click();
        await expect(page.getByText('server.error.500')).toBeVisible();
    });

    test('error if 400', async ({ page }) => {
        await page.route(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, async (route) => {
            await route.fulfill({
                status: 400,
                body: 'custom message',
            });
        });

        await page.goto(`${BASE_PATH}/koodisto/view/${koodistoUri}/2`);
        await page.locator(`[name="${koodistoUri}-csv"]`).click();
        await expect(
            page.getByText('Selainvirhe 400, ota yhteyttä ylläpitoon (yhteisetpalvelut@opintopolku.fi)')
        ).toBeVisible();
        await expect(page.getByText('custom message')).toBeVisible();
    });

    test('error if 404', async ({ page }) => {
        await page.route(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, async (route) => {
            await route.fulfill({
                status: 404,
                body: 'custom message',
            });
        });
        await page.goto(`${BASE_PATH}/koodisto/view/${koodistoUri}/2`);
        await page.locator(`[name="${koodistoUri}-csv"]`).click();
        await expect(page.getByText('Tietuetta ei löytynyt palvelimelta')).toBeVisible();
        await expect(page.getByText('custom message')).toBeVisible();
    });

    test('error if 418', async ({ page }) => {
        await page.route(`${API_BASE_PATH}/json/${koodistoUri}/koodi*`, async (route) => {
            await route.fulfill({
                status: 418,
                body: 'custom error',
            });
        });

        await page.goto(`${BASE_PATH}/koodisto/view/${koodistoUri}/2`);
        await page.locator(`[name="${koodistoUri}-csv"]`).click();
        await expect(
            page.getByText('Selainvirhe 418, ota yhteyttä ylläpitoon (yhteisetpalvelut@opintopolku.fi)')
        ).toBeVisible();
        await expect(page.getByText('custom error')).toBeVisible();
    });

    test('Redirects to index page on non-existent URL', async ({ page }) => {
        await page.route(`${API_INTERNAL_PATH}/koodisto`, async (route) => {
            await route.fulfill({ json: codes });
        });

        await page.goto(`${BASE_PATH}/some/non-existent/path`);
        await page.waitForURL('/koodisto-service/ui');
    });
});
