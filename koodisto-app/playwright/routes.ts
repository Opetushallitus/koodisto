import { Page } from '@playwright/test';

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
