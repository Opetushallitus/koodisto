import { expect, test } from '@playwright/test';

import { mockRoutes } from '../routes';
import { BASE_PATH } from '../../src/context/constants';

test('The landing page', async ({ page }) => {
    await mockRoutes(page);

    await test.step('shows koodistos on landing page', async () => {
        await page.goto(BASE_PATH);
        await expect(page.getByText('2.asteen pohjakoulutus 2021')).toBeVisible();
    });
    await test.step('shows paging component', async () => {
        await expect(page.getByText('Sivu 1 / 9')).toBeVisible();
    });
    await test.step('Paging cannot go back while at first page', async () => {
        await expect(page.locator('button[name=PREVIOUS_PAGE]')).toBeDisabled();
        await expect(page.locator('button[name=FIRST_PAGE]')).toBeDisabled();
    });
    await test.step('Paging can get to next page', async () => {
        await page.locator('button[name=NEXT_PAGE]').click();
        await expect(page.getByText('Sivu 2 / 9')).toBeVisible();
    });
    await test.step('Paging can get to last page', async () => {
        await page.locator('button[name=LAST_PAGE]').click();
        await expect(page.getByText('Sivu 9 / 9')).toBeVisible();
    });
    await test.step('Paging cannot go forward while at last page', async () => {
        await expect(page.locator('button[name=NEXT_PAGE]')).toBeDisabled();
        await expect(page.locator('button[name=LAST_PAGE]')).toBeDisabled();
    });
    await test.step('Paging can get to previous page', async () => {
        await page.locator('button[name=PREVIOUS_PAGE]').click();
        await expect(page.getByText('Sivu 8 / 9')).toBeVisible();
    });
    await test.step('Paging can get to first page', async () => {
        await page.locator('button[name=FIRST_PAGE]').click();
        await expect(page.getByText('Sivu 1 / 9')).toBeVisible();
    });
    await test.step('Paging resets when filter changes', async () => {
        await page.locator('button[name=LAST_PAGE]').click();
        await page.locator('[id="filter-container-koodistoUri"]').fill('maakunta');
        await expect(page.getByText('Sivu 1 / 1')).toBeVisible();
    });
    await test.step('Reset filter', async () => {
        await page.locator('#clear-filter').click();
        await expect(page.getByText('Sivu 1 / 9')).toBeVisible();
    });
    await test.step('Filters are saved and not cleared on navigation', async () => {
        await page.locator('[id="filter-container-ryhmaUri"]').click();
        await page.locator('[id="filter-container-ryhmaUri"] input').fill('Koski');
        await page.keyboard.press('Enter');
        await expect(page.getByText('Koodistot (86 / 406)')).toBeVisible();
        await page.goto(BASE_PATH);
        await expect(page.getByText('Koodistot (86 / 406)')).toBeVisible();
        await page.getByRole('row', { name: 'Koski', exact: true }).locator('svg').first().click();
        await expect(page.getByText('Koodistot (406 / 406)')).toBeVisible();
        await page.goto(BASE_PATH);
        await expect(page.getByText('Koodistot (406 / 406)')).toBeVisible();
    });
    await test.step('Filter by koodistoUri', async () => {
        await page.locator('[id="filter-container-koodistoUri"]').fill('2astee');
        await expect(page.getByText('Koodistot (1 / 406)')).toBeVisible();
        await page.locator('#clear-filter').click();
    });
    await test.step('Sort by first column', async () => {
        await expect(
            page.getByRole('row', { name: 'Haun koodistot 2.asteen' }).getByRole('link').first()
        ).toBeVisible();
        await page.getByText('Koodistoryhmä', { exact: true }).click();
        await expect(page.getByRole('link', { name: 'Virta-JTP', exact: true }).first()).toBeVisible();
        await page.getByText('Koodistoryhmä', { exact: true }).click();
        await expect(page.getByRole('link', { name: 'Alueet', exact: true }).first()).toBeVisible();
        await page.getByText('Koodistoryhmä', { exact: true }).click();
        await expect(
            page.getByRole('row', { name: 'Haun koodistot 2.asteen' }).getByRole('link').first()
        ).toBeVisible();
    });
});
