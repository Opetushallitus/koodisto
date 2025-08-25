import { defineConfig, devices } from '@playwright/test';

const CI = !!process.env.CI || !!process.env.CODEBUILD_BUILD_ID;

export default defineConfig({
    testDir: './playwright/tests',
    fullyParallel: true,
    forbidOnly: CI,
    retries: CI ? 2 : 0,
    workers: 2,
    reporter: CI ? [['junit', { outputFile: '/playwright/playwright-results.xml' }]] : 'html',
    use: {
        baseURL: 'http://localhost:8686/',
        trace: 'on',
    },
    projects: [
        {
            name: 'chromium',
            use: { ...devices['Desktop Chrome'] },
        },
    ],
});
