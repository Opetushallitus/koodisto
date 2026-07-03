import path from 'node:path';
import { fileURLToPath } from 'node:url';
import HtmlWebpackPlugin from 'html-webpack-plugin';
import ForkTsCheckerWebpackPlugin from 'fork-ts-checker-webpack-plugin';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);
const appPath = (...segments) => path.resolve(__dirname, ...segments);
const publicPath = '/koodisto-service/';

const isProduction = process.env.NODE_ENV === 'production';
const shouldUseSourceMap = process.env.GENERATE_SOURCEMAP !== 'false';
const imageInlineSizeLimit = Number(process.env.IMAGE_INLINE_SIZE_LIMIT || 10000);

const createProxy = (target, context) => ({ target, context, changeOrigin: true });

const mockApiProxy = createProxy('http://localhost:9000', [
    '/kayttooikeus-service',
    '/organisaatio-service',
    '/lokalisointi',
]);

export default {
    target: 'browserslist',
    mode: isProduction ? 'production' : 'development',
    bail: isProduction,
    stats: 'errors-warnings',
    devtool: isProduction ? (shouldUseSourceMap ? 'source-map' : false) : 'cheap-module-source-map',
    devServer: {
        client: {
            overlay: {
                errors: true,
                warnings: false,
            },
        },
        historyApiFallback: {
            disableDotRule: true,
            index: publicPath,
        },
        host: '127.0.0.1',
        port: process.env.PLAYWRIGHT === 'true' ? 8686 : 3001,
        proxy:
            process.env.PLAYWRIGHT === 'true'
                ? [mockApiProxy]
                : [
                      createProxy('http://localhost:8080', [
                          '/koodisto-service/static',
                          '/koodisto-service/rest',
                          '/koodisto-service/internal',
                      ]),
                      mockApiProxy,
                  ],
        static: {
            directory: appPath('public'),
            publicPath,
        },
    },
    entry: appPath('src', 'index.tsx'),
    output: {
        path: appPath('build'),
        pathinfo: !isProduction,
        filename: isProduction ? 'static/js/[name].[contenthash:8].js' : 'static/js/[name].bundle.js',
        chunkFilename: isProduction ? 'static/js/[name].[contenthash:8].chunk.js' : 'static/js/[name].chunk.js',
        assetModuleFilename: 'static/media/[name].[hash][ext]',
        publicPath,
    },
    cache: {
        type: 'filesystem',
        name: isProduction ? 'production' : 'development',
        buildDependencies: {
            config: [__filename],
            tsconfig: [appPath('tsconfig.json')],
        },
    },
    resolve: {
        alias: {
            'virkailija-ui-components': appPath('src', 'virkailija-ui-components'),
        },
        extensions: ['.tsx', '.ts', '.jsx', '.js', '.mjs', '.json'],
    },
    module: {
        rules: [
            shouldUseSourceMap && {
                enforce: 'pre',
                test: /\.(js|mjs|jsx|ts|tsx|css)$/,
                loader: 'source-map-loader',
                exclude: [/virkailija-ui-components/, /fast-memoize/],
            },
            {
                test: /\.[jt]sx?$/,
                exclude: /node_modules/,
                loader: 'ts-loader',
                options: {
                    transpileOnly: !isProduction,
                },
            },
            {
                test: /\.css$/,
                use: ['style-loader', 'css-loader'],
                sideEffects: true,
            },
            {
                test: /\.(bmp|gif|jpe?g|png)$/i,
                type: 'asset',
                parser: {
                    dataUrlCondition: {
                        maxSize: imageInlineSizeLimit,
                    },
                },
            },
            {
                test: /\.svg$/i,
                type: 'asset/inline',
            },
            {
                test: /\.(eot|otf|ttf|woff2?)$/i,
                type: 'asset/resource',
            },
        ].filter(Boolean),
    },
    plugins: [
        new HtmlWebpackPlugin({
            filename: 'index.html',
            template: appPath('public', 'index.html'),
            favicon: appPath('public', 'favicon.ico'),
            chunks: ['main'],
        }),
        new ForkTsCheckerWebpackPlugin({
            typescript: {
                configOverwrite: {
                    compilerOptions: {
                        sourceMap: isProduction ? shouldUseSourceMap : true,
                        noEmit: true,
                        incremental: true,
                        tsBuildInfoFile: appPath('node_modules', '.cache', 'tsconfig.tsbuildinfo'),
                    },
                },
                diagnosticOptions: {
                    syntactic: true,
                },
                mode: 'write-references',
            },
        }),
    ],
};
