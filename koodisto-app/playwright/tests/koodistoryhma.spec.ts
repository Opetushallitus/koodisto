import { expect, test } from '@playwright/test';

import {
    mockDeleteKoodistoRyhma,
    mockGetKoodistoRyhma,
    mockPostKoodistoRyhma,
    mockPutKoodistoRyhma,
    mockRoutes,
} from '../routes';
import { BASE_PATH } from '../../src/context/constants';
import emptyRyhma0 from '../fixtures/emptyRyhma0.json';
import emptyRyhma1 from '../fixtures/emptyRyhma1.json';
import haunkoodistot from '../fixtures/haunkoodistot.json';

test('Create koodistoryhmä', async ({ page }) => {
    await mockRoutes(page);

    await test.step('shows koodistoryhma button on landing page', async () => {
        await page.goto(BASE_PATH);
        await expect(page.locator('button[name=TAULUKKO_LISAA_KOODISTORYHMA_BUTTON]')).toBeVisible();
    });

    await test.step('can click koodistoryhma button on landing page', async () => {
        await mockGetKoodistoRyhma(page, 'empty', emptyRyhma0);
        await page.locator('button[name=TAULUKKO_LISAA_KOODISTORYHMA_BUTTON]').click();
        await expect(page.getByText('Luo uusi koodistoryhmä')).toBeVisible();
    });

    await test.step('can add a new ryhma', async () => {
        await mockPostKoodistoRyhma(page, { nimi: { fi: 'foo', sv: 'foo', en: 'foo' } });
        await page.locator('input[name=fi]').fill('foo');
        await page.locator('input[name=sv]').fill('foo');
        await page.locator('input[name=en]').fill('foo');
        await mockGetKoodistoRyhma(page, 'empty', emptyRyhma1);
        await page.locator('button[name=KOODISTO_RYHMA_LUO_UUSI]').click();
    });

    await test.step('can add a new ryhma with copied values', async () => {
        await mockPostKoodistoRyhma(page, { nimi: { fi: 'bar', sv: 'bar', en: 'bar' } });
        await page.locator('input[name=fi]').clear();
        await page.locator('input[name=fi]').fill('bar');
        await page.locator('svg[name=KOPIOI_MUIHIN_NIMIIN]').click();
        await mockGetKoodistoRyhma(page, 'empty', emptyRyhma1);
        await page.locator('button[name=KOODISTO_RYHMA_LUO_UUSI]').click();
    });

    await test.step('can delete empty ryhma', async () => {
        await mockGetKoodistoRyhma(page, 'empty', emptyRyhma0);
        await mockDeleteKoodistoRyhma(page);
        await page.locator('button[name=POISTA_KOODISTORYHMA-foo]').click();
    });

    await test.step('can close the modal', async () => {
        await page.locator('button[name=KOODISTO_RYHMA_SULJE]').click();
    });

    await test.step('can open modal in edit mode', async () => {
        await mockGetKoodistoRyhma(page, 'haunkoodistot', haunkoodistot);
        await page.getByRole('row', { name: 'Haun koodistot 2.asteen' }).getByRole('link').first().click();
        await expect(page.getByText('Muokkaa koodistoryhmää')).toBeVisible();
    });

    await test.step('can edit old ryhma', async () => {
        await page.locator('input[name=fi]').fill('foo');
        await mockPutKoodistoRyhma(page, 'haunkoodistot', {
            nimi: {
                fi: 'foo',
                sv: 'Haun koodistot',
                en: 'Haun koodistot',
            },
        });
        await page.locator('button[name=KOODISTO_RYHMA_TALLENNA]').click();
    });
});
