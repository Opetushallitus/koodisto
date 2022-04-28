import React from 'react';
import KoodistoTablePage from './components/pages/KoodistoTablePage/KoodistoTablePage';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import styled from 'styled-components';
import Notification from './components/Notification/Notification';
import KoodistoPage from './components/pages/KoodistoPage/KoodistoPage';
import Loading from './components/pages/Loading/Loading';

export const PageBase = styled.div`
    width: 100%;
    display: flex;
    justify-content: space-between;
    flex-direction: column;
    flex-wrap: nowrap;
    min-height: 87vh;
`;

const App: React.FC = () => {
    return (
        <BrowserRouter basename={'/koodisto-app'}>
            <Notification />
            <PageBase>
                <React.Suspense fallback={<Loading />}>
                    <Routes>
                        <Route path={'/'} element={<KoodistoTablePage />} />
                        <Route path="/koodisto/:koodistoUri/:versio" element={<KoodistoPage />} />
                    </Routes>
                </React.Suspense>
            </PageBase>
        </BrowserRouter>
    );
};

export default App;
