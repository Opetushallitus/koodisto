import { expect, test } from '@playwright/test';

import { mockGetKoodiKoodisto, mockGetKoodisto, mockPutKoodisto, mockRoutes } from '../routes';
import { BASE_PATH } from '../../src/context/constants';
import kuntaKoodisto from '../fixtures/kuntaKoodisto.json';
import kuntaKoodistoKoodit from '../fixtures/kuntaKoodistoKoodit.json';

test('The Koodisto Edit page', async ({ page }) => {
    await mockRoutes(page);
    await mockGetKoodisto(page, 'kunta', 2, kuntaKoodisto);
    await mockGetKoodiKoodisto(page, 'kunta', 2, kuntaKoodistoKoodit);

    await test.step('shows testi koodisto on koodisto view page', async () => {
        await page.goto(`${BASE_PATH}/koodisto/view/kunta/2`);
        await expect(page.getByRole('heading', { name: 'kunta' })).toBeVisible();
    });

    await test.step('shows edit button and can click 1', async () => {
        await page.locator('button[name="KOODISTOSIVU_MUOKKAA_KOODISTOA_BUTTON"]').click();
        await expect(page.getByText('Muokkaa koodistoa')).toBeVisible();
    });

    await test.step('shows edit button and can click', async () => {
        await mockPutKoodisto(
            page,
            async (body: Record<string, unknown>) => {
                expect(body.metadataList).toStrictEqual([
                    { kieli: 'FI', nimi: 'kunta muokattu', kuvaus: 'kunta' },
                    { kieli: 'SV', nimi: 'kommun', kuvaus: 'kommun' },
                    { kieli: 'EN', nimi: 'municipality', kuvaus: 'municipality' },
                ]);
            },
            kuntaKoodisto
        );
        await expect(page.locator('input[name="metadata[0][nimi]"]')).toHaveValue('kunta');
        await page.locator('input[name="metadata[0][nimi]"]').clear();
        await page.locator('input[name="metadata[0][nimi]"]').fill('kunta muokattu');
        await page.locator('button[name="KOODISTO_TALLENNA"]').click();
        await expect(page.getByText('Tallennettiin koodisto uri:lla kunta').first()).toBeVisible();
    });

    await test.step('shows edit button and can click 2', async () => {
        await page.locator('button[name="KOODISTOSIVU_MUOKKAA_KOODISTOA_BUTTON"]').click();
        await expect(page.getByText('Muokkaa koodistoa')).toBeVisible();
        await mockPutKoodisto(
            page,
            async (body: Record<string, unknown>) => {
                expect(body.organisaatioOid).toBe('1.2.246.562.10.00000000001');
                expect(body.codesGroupUri).toBe('varda');
            },
            kuntaKoodisto
        );
        await page.locator('div[id="organisaatioOid"] input[type=text]').fill('ope', { force: true });
        await page.getByText('Opetushall').click();

        await page.locator('div[id="koodistoRyhmaUri"] input[type=text]').fill('Va', { force: true });
        await page.getByText('Varda').click();
        await page.locator('button[name="KOODISTO_TALLENNA"]').click();
        await expect(page.getByText('Tallennettiin koodisto uri:lla kunta').first()).toBeVisible();
    });
});
