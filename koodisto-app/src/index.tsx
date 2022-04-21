import React, { ErrorInfo } from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';
import Cookies from 'universal-cookie';
import { Provider, useAtom } from 'jotai';
import { ROOT_OID } from './context/constants';
import './index.css';
import ErrorPage from './components/pages/ErrorPage/ErrorPage';
import Loading from './components/pages/Loading/Loading';
import Raamit from './components/Raamit/Raamit';
import createTheme from '@opetushallitus/virkailija-ui-components/createTheme';
import { ThemeProvider } from 'styled-components';
import { casMeLangAtom } from './api/kayttooikeus';
import { IntlProvider } from 'react-intl';
import { lokalisaatioMessagesAtom } from './api/lokalisaatio';
import KoodistoApp from './KoodistoApp';

const theme = createTheme();
const cookies = new Cookies();
axios.interceptors.request.use((config) => {
    if (config?.headers) {
        config.headers['Caller-Id'] = `${ROOT_OID}.koodisto-app`;
        config.headers['CSRF'] = cookies.get('CSRF');
    }
    return config;
});
export class ErrorBoundary extends React.Component<unknown, { hasError: boolean }> {
    constructor(props: unknown) {
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

const Initialize: React.FC = ({ children }) => {
    const [casMeLang] = useAtom(casMeLangAtom);
    const [messages] = useAtom(lokalisaatioMessagesAtom);
    return (
        <IntlProvider locale={casMeLang} defaultLocale={'fi'} messages={messages}>
            {children}
        </IntlProvider>
    );
};
ReactDOM.render(
    <React.StrictMode>
        <ThemeProvider theme={theme}>
            <Provider>
                <ErrorBoundary>
                    <React.Suspense fallback={<Loading />}>
                        <Initialize>
                            <Raamit>
                                <KoodistoApp />
                            </Raamit>
                        </Initialize>
                    </React.Suspense>
                </ErrorBoundary>
            </Provider>
        </ThemeProvider>
    </React.StrictMode>,
    document.getElementById('root')
);
