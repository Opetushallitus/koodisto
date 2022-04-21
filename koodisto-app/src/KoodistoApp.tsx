import React from 'react';
import KoodistoTablePage from './components/pages/KoodistoTable/KoodistoTablePage';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

const KoodistoApp: React.FC = () => {
    return (
        <BrowserRouter basename={'/koodisto-app'}>
            <Routes>
                <Route path="/" element={<KoodistoTablePage />} />
            </Routes>
        </BrowserRouter>
    );
};

export default KoodistoApp;
