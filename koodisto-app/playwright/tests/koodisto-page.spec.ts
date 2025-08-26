import { expect, test } from '@playwright/test';

import { mockGetKoodisto, mockGetKoodiKoodisto, mockRoutes } from '../routes';
import { BASE_PATH } from '../../src/context/constants';
import kuntaKoodisto from '../fixtures/kuntaKoodisto.json';
import kuntaKoodistoKoodit from '../fixtures/kuntaKoodistoKoodit.json';

test('The Koodisto View page', async ({ page }) => {
    await mockRoutes(page);
    await mockGetKoodisto(page, 'kunta', 2, kuntaKoodisto);
    await mockGetKoodiKoodisto(page, 'kunta', 2, kuntaKoodistoKoodit);

    await test.step('shows testi koodisto on koodisto view page', async () => {
        await page.goto(`${BASE_PATH}/koodisto/view/kunta/2`);
        await expect(page.getByRole('heading', { name: 'kunta' })).toBeVisible();
    });

    await test.step('Should expand koodi list by default', async () => {
        await expect(page.locator('#accordion__panel-3')).toBeVisible();
    });

    await test.step('Koodi list may be filtered', async () => {
        await page.goto(`${BASE_PATH}/koodisto/view/kunta/2`);
        await expect(page.locator('tbody > tr:visible')).toHaveCount(20);

        // filter by name
        await page.getByRole('textbox', { name: 'Hae nimellä tai koodiarvolla' }).fill('Vaala');
        await expect(page.locator('tbody > tr:visible')).toHaveCount(1);

        // clear filter
        await page.locator('#clear-filter').click();
        await expect(page.locator('tbody > tr:visible')).toHaveCount(20);

        // filter by koodiArvo
        await page.getByRole('textbox', { name: 'Hae nimellä tai koodiarvolla' }).fill('785');
        await expect(page.locator('tbody > tr:visible')).toHaveCount(1);
    });
});
