import { expect, Page } from '@playwright/test';

import { API_INTERNAL_PATH, API_STATUS_PATH } from '../src/context/constants';

import allRyhmat from './fixtures/allRyhmat.json';
import codes from './fixtures/codes.json';
import kuntaKoodisto from './fixtures/kuntaKoodisto.json';
import kuntaKoodistoKoodit from './fixtures/kuntaKoodistoKoodit.json';

export const mockRoutes = async (page: Page) => {
    await page.route(API_STATUS_PATH, async (route) => {
        await route.fulfill({ body: '1800' });
    });

    await page.route(`${API_INTERNAL_PATH}/koodistoryhma`, async (route) => {
        if (route.request().method() !== 'GET') {
            await route.fallback();
            return;
        }
        await route.fulfill({ json: allRyhmat });
    });

    await page.route(`${API_INTERNAL_PATH}/koodisto`, async (route) => {
        if (route.request().method() !== 'GET') {
            await route.fallback();
            return;
        }
        await route.fulfill({ json: codes });
    });

    await page.route(`${API_INTERNAL_PATH}/koodisto/kunta/2`, async (route) => {
        if (route.request().method() !== 'GET') {
            await route.fallback();
            return;
        }
        await route.fulfill({ json: kuntaKoodisto });
    });

    await page.route(`${API_INTERNAL_PATH}/koodi/koodisto/kunta/2`, async (route) => {
        if (route.request().method() !== 'GET') {
            await route.fallback();
            return;
        }
        await route.fulfill({ json: kuntaKoodistoKoodit });
    });
};

export const mockGetKoodistoRyhma = async (page: Page, name: string, koodistoryhma: unknown) => {
    await page.route(`${API_INTERNAL_PATH}/koodistoryhma/${name}`, async (route) => {
        await route.fulfill({ json: koodistoryhma });
    });
    await page.route(`${API_INTERNAL_PATH}/koodistoryhma/${name}/`, async (route) => {
        await route.fulfill({ json: koodistoryhma });
    });
};

export const mockPostKoodistoRyhma = async (page: Page, expected: { nimi: { fi: string; sv: string; en: string } }) => {
    await page.route(`${API_INTERNAL_PATH}/koodistoryhma`, async (route) => {
        if (route.request().method() !== 'POST') {
            await route.fallback();
            return;
        }
        expect(route.request().postDataJSON()).toStrictEqual(expected);
        await route.fulfill({ json: { koodistoRyhmaUri: expected.nimi.fi } });
    });
};

export const mockDeleteKoodistoRyhma = async (page: Page) => {
    await page.route(`${API_INTERNAL_PATH}/koodistoryhma`, async (route) => {
        if (route.request().method() !== 'DELETE') {
            await route.fallback();
            return;
        }
        await route.fulfill({ body: '' });
    });
};

export const mockPutKoodistoRyhma = async (
    page: Page,
    name: string,
    expected: { nimi: { fi: string; sv: string; en: string } }
) => {
    await page.route(`${API_INTERNAL_PATH}/koodistoryhma/${name}`, async (route) => {
        if (route.request().method() !== 'PUT') {
            await route.fallback();
            return;
        }
        expect(route.request().postDataJSON()).toStrictEqual(expected);
        await route.fulfill({ json: { koodistoRyhmaUri: name } });
    });
};

export const mockGetKoodisto = async (page: Page, name: string, version: number, koodisto: unknown) => {
    await page.route(`${API_INTERNAL_PATH}/koodisto/${name}/${version}`, async (route) => {
        await route.fulfill({ json: koodisto });
    });
};

export const mockGetKoodiKoodisto = async (page: Page, name: string, version: number, koodit: unknown) => {
    await page.route(`${API_INTERNAL_PATH}/koodi/koodisto/${name}/${version}`, async (route) => {
        await route.fulfill({ json: koodit });
    });
};

export const mockPutKoodisto = async (
    page: Page,
    expect: (body: Record<string, unknown>) => Promise<void>,
    returnValue: unknown
) => {
    await page.route(`${API_INTERNAL_PATH}/koodisto`, async (route) => {
        if (route.request().method() !== 'PUT') {
            await route.fallback();
            return;
        }
        await expect(route.request().postDataJSON());
        await route.fulfill({ json: returnValue });
    });
};

export const mockPostKoodisto = async (page: Page, name: string, version: number, returnValue: unknown) => {
    await page.route(`${API_INTERNAL_PATH}/koodisto/${name}/${version}`, async (route) => {
        if (route.request().method() !== 'POST') {
            await route.fallback();
            return;
        }
        await route.fulfill({ status: 201, json: returnValue });
    });
};

export const mockDeleteKoodisto = async (page: Page, name: string, version: number) => {
    await page.route(`${API_INTERNAL_PATH}/koodisto/${name}/${version}`, async (route) => {
        if (route.request().method() !== 'DELETE') {
            await route.fallback();
            return;
        }
        await route.fulfill({ status: 204 });
    });
};

export const mockKoodiPage = async (page: Page, name: string, version: number, fixture: unknown) => {
    await page.route(`${API_INTERNAL_PATH}/koodi/${name}/${version}`, async (route) => {
        if (route.request().method() !== 'GET') {
            await route.fallback();
            return;
        }
        await route.fulfill({ json: fixture });
    });
};

export const mockKoodiPageKoodisto = async (page: Page, name: string, fixture: unknown) => {
    await page.route(`${API_INTERNAL_PATH}/koodisto/${name}`, async (route) => {
        if (route.request().method() !== 'GET') {
            await route.fallback();
            return;
        }
        await route.fulfill({ json: fixture });
    });
};
