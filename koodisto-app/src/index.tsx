import React, { ErrorInfo, ReactNode } from 'react';
import { createRoot } from 'react-dom/client';
import axios from 'axios';
import { Provider, useAtom } from 'jotai';
import { ROOT_OID } from './context/constants';
import './index.css';
import { ErrorPage } from './pages/ErrorPage';
import { Loading } from './components/Loading';
import { Raamit } from './components/Raamit';
import createTheme from 'virkailija-ui-components/createTheme';
import { ThemeProvider } from 'styled-components';
import { casMeLocaleAtom } from './api/kayttooikeus';
import { IntlProvider } from 'react-intl';
import { lokalisaatioMessagesAtom } from './api/lokalisaatio';
import App from './App';
import { statusAtom } from './api/status';

const theme = createTheme();

const decodeCookieValue = (value: string): string => {
    try {
        return decodeURIComponent(value);
    } catch {
        return value;
    }
};

const getCookie = (name: string): string | undefined => {
    const value = document.cookie
        .split('; ')
        .map((cookie) => cookie.split('='))
        .find(([key]) => key === name)
        ?.slice(1)
        .join('=');
    return value ? decodeCookieValue(value) : undefined;
};

axios.interceptors.request.use((config) => {
    if (config?.headers) {
        config.headers['Caller-Id'] = `${ROOT_OID}.koodisto-app`;
        config.headers['CSRF'] = getCookie('CSRF');
    }
    return config;
});

type ErrorBoundaryProps = {
    children?: ReactNode;
};

export class ErrorBoundary extends React.Component<ErrorBoundaryProps, { hasError: boolean }> {
    constructor(props: ErrorBoundaryProps) {
        super(props);
        this.state = { hasError: false };
    }

    static getDerivedStateFromError() {
        return { hasError: true };
    }

    componentDidCatch(error: Error, errorInfo: ErrorInfo) {
        console.error(error, errorInfo);
    }

    render() {
        const { hasError } = this.state;
        if (hasError) {
            return <ErrorPage>Service Unavailable</ErrorPage>;
        }
        return this.props.children;
    }
}

const Initialize = ({ children }: { children?: ReactNode }) => {
    useAtom(statusAtom);
    const [casMeLocale] = useAtom(casMeLocaleAtom);
    const [messages] = useAtom(lokalisaatioMessagesAtom);
    return (
        <IntlProvider locale={casMeLocale} defaultLocale={'fi'} messages={messages}>
            {children}
        </IntlProvider>
    );
};

const rootElement = document.getElementById('root');

if (!rootElement) {
    throw new Error('Root element not found');
}

createRoot(rootElement).render(
    <React.StrictMode>
        <ThemeProvider theme={theme}>
            <Provider>
                <ErrorBoundary>
                    <React.Suspense fallback={<Loading />}>
                        <Initialize>
                            <Raamit>
                                <App />
                            </Raamit>
                        </Initialize>
                    </React.Suspense>
                </ErrorBoundary>
            </Provider>
        </ThemeProvider>
    </React.StrictMode>
);
