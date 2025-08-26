import { expect, test } from '@playwright/test';

import { mockGetKoodisto, mockGetKoodiKoodisto, mockRoutes, mockPutKoodisto } from '../routes';
import { BASE_PATH } from '../../src/context/constants';
import kuntaKoodisto from '../fixtures/kuntaKoodisto.json';
import kuntaKoodistoKoodit from '../fixtures/kuntaKoodistoKoodit.json';

test('The Koodisto Edit page can edit relations', async ({ page }) => {
    await mockRoutes(page);
    await mockGetKoodisto(page, 'kunta', 2, kuntaKoodisto);
    await mockGetKoodiKoodisto(page, 'kunta', 2, kuntaKoodistoKoodit);

    await test.step('shows testi koodisto on koodisto view page', async () => {
        await page.goto(`${BASE_PATH}/koodisto/view/kunta/2`);
        await expect(page.getByRole('heading', { name: 'kunta' })).toBeVisible();
    });
    await test.step('shows edit button and can open edit page', async () => {
        await page.locator('button[name="KOODISTOSIVU_MUOKKAA_KOODISTOA_BUTTON"]').click();
        await expect(page.getByText('Muokkaa koodistoa')).toBeVisible();
    });
    await test.step('shows within relations and can click', async () => {
        await page.getByText('Sisältyy koodistoihin').click();
    });
    await test.step('shows relations add button and can click', async () => {
        await page.getByRole('button', { name: 'Lisää koodistoja' }).click();
    });
    await test.step('shows koodistos and can select', async () => {
        await page.getByRole('row', { name: 'Haun koodistot 2.asteen' }).getByRole('checkbox').click();
        await page.locator('button[name=SUHDEMODAL_VALITSE]').click();
    });
    await test.step('shows added relations', async () => {
        await expect(page.getByText('Sisältyy koodistoihin (3)')).toBeVisible();
    });
    await test.step('can save added relations', async () => {
        await mockPutKoodisto(
            page,
            async (body: Record<string, unknown>) => {
                expect(body.withinCodes).toHaveLength(3);
            },
            kuntaKoodisto
        );
        await page.locator('button[name="KOODISTO_TALLENNA"]').click();
        await page.getByText('Koodisto tallennettiin').click();
    });
    await test.step('shows edit button and can open edit page', async () => {
        await page.locator('button[name="KOODISTOSIVU_MUOKKAA_KOODISTOA_BUTTON"]').click();
        await expect(page.getByText('Muokkaa koodistoa')).toBeVisible();
    });
    await test.step('shows includes relations and can click', async () => {
        await page.getByText('Sisältää koodistot (9)').click();
        await page.getByRole('row', { name: 'elinkeino-, liikenne- ja ympä' }).getByRole('button').click();
        await page.getByRole('row', { name: 'kielisuhde 2 Kielisuhde jakaa' }).getByRole('button').click();
        await page.getByRole('row', { name: 'kielisuhde 1 Kielisuhde jakaa' }).getByRole('button').click();
        await page.getByRole('row', { name: 'laanit 1997-2009 1' }).getByRole('button').click();
        await expect(page.getByText('Sisältää koodistot (5)')).toBeVisible();
    });
    await test.step('can save removed relations', async () => {
        await mockPutKoodisto(
            page,
            async (body: Record<string, unknown>) => {
                expect(body.includesCodes).toHaveLength(5);
            },
            kuntaKoodisto
        );
        await page.locator('button[name="KOODISTO_TALLENNA"]').click();
        await page.getByText('Koodisto tallennettiin').click();
    });
});
