import React, { ErrorInfo } from 'react';
import ReactDOM from 'react-dom';
import axios from 'axios';
import Cookies from 'universal-cookie';
import App from './App';
import { Provider } from 'jotai';
import { ROOT_OID } from './context/constants';
import ErrorPage from './components/pages/ErrorPage/ErrorPage';
import Loading from './components/pages/Loading/Loading';
import Raamit from './components/Raamit/Raamit';

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
ReactDOM.render(
    <React.StrictMode>
        <Provider>
            <ErrorBoundary>
                <React.Suspense fallback={<Loading />}>
                    <Raamit>
                        <App />
                    </Raamit>
                </React.Suspense>
            </ErrorBoundary>
        </Provider>
    </React.StrictMode>,
    document.getElementById('root')
);
