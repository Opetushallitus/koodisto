import { defineConfig } from 'eslint/config';
import eslint from '@eslint/js';
import tseslint from 'typescript-eslint';
import react from 'eslint-plugin-react';
import jsxA11y from 'eslint-plugin-jsx-a11y';
import eslintPluginPrettierRecommended from 'eslint-plugin-prettier/recommended';

export default defineConfig([
    eslint.configs.recommended,
    tseslint.configs.strict,
    tseslint.configs.stylistic,
    react.configs.flat.recommended,
    jsxA11y.flatConfigs.recommended,
    eslintPluginPrettierRecommended,
    {
        settings: {
            react: {
                version: 'detect',
            },
        },

        rules: {
            'jsx-a11y/click-events-have-key-events': 1,
            '@typescript-eslint/consistent-type-definitions': ['error', 'type'],
            '@typescript-eslint/no-unused-vars': [
                'error',
                {
                    argsIgnorePattern: '^_',
                    varsIgnorePattern: '^_',
                    caughtErrorsIgnorePattern: '^_',
                },
            ],
        },
    },
    {
        // Note: there should be no other properties in this object
        ignores: [
            'build/*',
            'src/react-app-env.d.ts',
            'node_modules/*',
            'playwright-report/*',
            'eslint.config.js',
            'webpack.config.js',
            'mock-api/*',
            'test-results/*',
            'public/*',
            'lang/*',
        ],
    },
]);
