import { expect, test } from '@playwright/test';

import { mockRoutes, mockKoodiPage, mockKoodiPageKoodisto } from '../routes';
import { BASE_PATH } from '../../src/context/constants';
import koodiPagePartial from '../fixtures/koodiPage-partial.json';
import koodiPageKoodisto from '../fixtures/koodiPageKoodisto.json';

test('Koodi view with partial data page', async ({ page }) => {
    await mockRoutes(page);
    await mockKoodiPage(page, 'get_1', 1, koodiPagePartial);
    await mockKoodiPageKoodisto(page, 'kunta', koodiPageKoodisto);

    await test.step('Renders page', async () => {
        await page.goto(`${BASE_PATH}/koodi/view/get_1/1`);
        await expect(page.getByRole('listitem').filter({ hasText: 'FI' }).first()).toBeVisible();
        await expect(page.getByText('SVAkaa')).toBeVisible();
        await expect(page.getByText('ENAkaa')).toBeVisible();
        await expect(page.getByRole('listitem').filter({ hasText: 'FI' }).nth(1)).toBeVisible();
        await expect(page.getByRole('listitem').filter({ hasText: /^SV$/ })).toBeVisible();
        await expect(page.getByRole('listitem').filter({ hasText: /^EN$/ })).toBeVisible();
    });
});
