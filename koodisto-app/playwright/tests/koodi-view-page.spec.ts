import { expect, test } from '@playwright/test';

import { mockRoutes, mockKoodiPage, mockKoodiPageKoodisto } from '../routes';
import { BASE_PATH } from '../../src/context/constants';
import koodiPage from '../fixtures/koodiPage.json';
import koodiPageKoodisto from '../fixtures/koodiPageKoodisto.json';

test('Koodi view page', async ({ page }) => {
    await mockRoutes(page);
    await mockKoodiPage(page, 'get_1', 1, koodiPage);
    await mockKoodiPageKoodisto(page, 'kunta', koodiPageKoodisto);

    await test.step('Renders page', async () => {
        await page.goto(`${BASE_PATH}/koodi/view/get_1/1`);
        await expect(page.getByRole('heading', { name: 'Akaa loytyy' })).toBeVisible();
        const href = await page.getByRole('link', { name: 'kunta' }).getAttribute('href');
        expect(href).toContain('/koodisto-service/ui/koodisto/view/kunta/2');
    });
});
