import React from 'react';
import KoodistoTablePage from './components/pages/KoodistoTable/KoodistoTablePage';
import { BrowserRouter, Route, Routes } from 'react-router-dom';

const App: React.FC = () => {
    return (
        <BrowserRouter basename={'/koodisto-app'}>
            <Routes>
                <Route path="/" element={<KoodistoTablePage />} />
            </Routes>
        </BrowserRouter>
    );
};

export default App;
