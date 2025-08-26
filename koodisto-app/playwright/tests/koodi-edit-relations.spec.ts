import { expect, test } from '@playwright/test';

import { mockRoutes, mockKoodiPage, mockKoodiPageKoodisto } from '../routes';
import { API_INTERNAL_PATH, BASE_PATH } from '../../src/context/constants';
import relationKoodi from '../fixtures/relationKoodi.json';
import koodiPageKoodisto from '../fixtures/koodiPageKoodisto.json';
import oppilaitosKoodi from '../fixtures/oppilaitosKoodit.json';
import aluehallintovirastoKoodit from '../fixtures/aluehallintovirastoKoodit.json';
import saameKoodi from '../fixtures/07/saameKoodi.json';
import aidinkielijakirjallisuusKoodit from '../fixtures/07/aidinkielijakirjallisuusKoodit.json';
import aidinkielijakirjallisuusKoodisto from '../fixtures/07/aidinkielijakirjallisuusKoodisto.json';
import aidinkielijakirjallisuusFi from '../fixtures/07/aidinkielijakirjallisuusFi.json';
import aidinkielijakirjallisuusFi2 from '../fixtures/07/aidinkielijakirjallisuusFi2.json';
import aidinkielijakirjallisuusFiSe from '../fixtures/07/aidinkielijakirjallisuusFiSe.json';

test('Koodi Edit page can edit relations', async ({ page }) => {
    await mockRoutes(page);
    await mockKoodiPage(page, 'kunta_020', 2, relationKoodi);
    await mockKoodiPageKoodisto(page, 'kunta', koodiPageKoodisto);

    await test.step('renders view page', async () => {
        await page.goto(`${BASE_PATH}/koodi/view/kunta_020/2`);
        await expect(page.getByRole('heading', { name: 'Akaa' })).toBeVisible();
    });

    await test.step('shows edit button and can open edit page', async () => {
        await page.locator('button[name=KOODISIVU_MUOKKAA_KOODIA_BUTTON]').click();
        await expect(page.getByText('Muokkaa koodia')).toBeVisible();
        await expect(page.getByRole('link', { name: 'kunta' })).toBeVisible();
    });

    await test.step('shows within relations and can click', async () => {
        await page.getByText('Sisältyy koodeihin (2)').click();
    });

    await test.step('shows relations add button and can click', async () => {
        await page.route(`${API_INTERNAL_PATH}/koodi/koodisto/oppilaitosnumero/1`, async (route) => {
            await route.fulfill({ json: oppilaitosKoodi });
        });
        await page.route(`${API_INTERNAL_PATH}/koodi/koodisto/aluehallintovirasto/1`, async (route) => {
            await route.fulfill({ json: aluehallintovirastoKoodit });
        });
        await page.route(`${API_INTERNAL_PATH}/koodi/koodisto/aluehallintovirasto/2`, async (route) => {
            await route.fulfill({ json: [] });
        });
        await page.getByRole('button', { name: 'Lisää koodeja' }).click();
    });

    await test.step('shows koodis and can select', async () => {
        await page.getByRole('row', { name: 'oppilaitosnumero Kankaisten' }).getByRole('checkbox').click();
        await page.locator('button[name=SUHDEMODAL_VALITSE]').click();
    });

    await test.step('shows added relations', async () => {
        await expect(page.getByText('Sisältyy koodeihin (3)')).toBeVisible();
    });

    await test.step('can save added relations', async () => {
        await page.route(`${API_INTERNAL_PATH}/koodi`, async (route) => {
            if (route.request().method() !== 'PUT') {
                await route.fallback();
                return;
            }
            expect(route.request().postDataJSON().sisaltyyKoodeihin).toHaveLength(3);
            await route.fulfill({ json: relationKoodi });
        });
        await page.locator('button[name="KOODI_TALLENNA"]').click();
        await page.getByText('Koodi tallennettiin').click();
    });

    await test.step('shows edit button and can open edit page', async () => {
        await page.locator('button[name=KOODISIVU_MUOKKAA_KOODIA_BUTTON]').click();
        await expect(page.getByText('Muokkaa koodia')).toBeVisible();
    });

    await test.step('shows includes relations and can click', async () => {
        await page.getByText('Sisältää koodit (7)').click();
        await page.getByRole('row', { name: 'seutukunta Etelä-Pirkanmaa 1' }).getByRole('button').click();
        await page.getByRole('row', { name: 'maakunta Pirkanmaa 1 Pirkanmaa' }).getByRole('button').click();
        await page.getByRole('row', { name: 'maakunta Pirkanmaa 2 Pirkanmaa' }).getByRole('button').click();
        await page.getByRole('row', { name: 'elinkeino-, liikenne- ja ympä' }).getByRole('button').click();
        await expect(page.getByText('Sisältää koodit (3)')).toBeVisible();
    });

    await test.step('can save removed relations', async () => {
        await page.route(`${API_INTERNAL_PATH}/koodi`, async (route) => {
            if (route.request().method() !== 'PUT') {
                await route.fallback();
                return;
            }
            expect(route.request().postDataJSON().sisaltaaKoodit).toHaveLength(3);
            await route.fulfill({ json: relationKoodi });
        });
        await page.locator('button[name="KOODI_TALLENNA"]').click();
        await page.getByText('Koodi tallennettiin').click();
    });

    await test.step('can copy relations from other koodi', async () => {
        await page.route(`${API_INTERNAL_PATH}/koodi/aidinkielijakirjallisuus_se/1`, async (route) => {
            await route.fulfill({ json: saameKoodi });
        });
        await page.route(`${API_INTERNAL_PATH}/koodi/koodisto/aidinkielijakirjallisuus/1`, async (route) => {
            await route.fulfill({ json: aidinkielijakirjallisuusKoodit });
        });
        await page.route(`${API_INTERNAL_PATH}/koodisto/aidinkielijakirjallisuus/1`, async (route) => {
            await route.fulfill({ json: aidinkielijakirjallisuusKoodisto });
        });

        await page.goto(`${BASE_PATH}/koodi/edit/aidinkielijakirjallisuus_se/1`);
        await page.locator('button[name=KOODI_KOPIOI_SUHTEET_BUTTON]').click();
        await expect(page.getByText('Valitse koodit')).toBeVisible();
        await page.getByRole('row', { name: 'Koodisto Nimi Koodiarvo Versio' }).getByRole('checkbox').click();
        await expect(page.getByText('Valitse (3)')).toBeVisible();

        await page.route(`${API_INTERNAL_PATH}/koodi/aidinkielijakirjallisuus_fi/1`, async (route) => {
            await route.fulfill({ json: aidinkielijakirjallisuusFi });
        });
        await page.route(`${API_INTERNAL_PATH}/koodi/aidinkielijakirjallisuus_fi2/1`, async (route) => {
            await route.fulfill({ json: aidinkielijakirjallisuusFi2 });
        });
        await page.route(`${API_INTERNAL_PATH}/koodi/aidinkielijakirjallisuus_fise/1`, async (route) => {
            await route.fulfill({ json: aidinkielijakirjallisuusFiSe });
        });

        await page.locator('button[name=SUHDEMODAL_VALITSE]').click();
        await expect(page.getByText('Sisältyy koodeihin (3)')).toBeVisible();
        await expect(page.getByText('Sisältää koodit (1)')).toBeVisible();
        await expect(page.getByText('Rinnastuu koodeihin (2)')).toBeVisible();
    });
});
