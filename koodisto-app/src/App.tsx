import React, { useState } from 'react';
import KoodistoTable from './components/pages/KoodistoTable/KoodistoTable';
import Loading from './components/pages/Loading/Loading';
import styled from 'styled-components';

const PageBase = styled.div`
    width: 100%;
    display: flex;
    justify-content: space-between;
    flex-direction: column;
    flex-wrap: nowrap;
    min-height: 100vh;
    background: #f5f5f5;
`;
const MainContainer = styled.div`
    margin: 1rem 2rem;
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    box-sizing: border-box;
    border: 1px solid #cccccc;
    background-color: #ffffff;
`;
export const HeaderContainer = styled.div`
    padding: 1rem 1rem;
    border: 1px solid #cccccc;
    align-items: center;
`;
const ContentContainer = styled.div`
    padding: 2rem;
    display: block;
    max-width: 100%;
`;
const App: React.FC = () => {
    const [koodistoRyhmaModalVisible, setKoodistoRyhmaModalVisible] = useState<boolean>(false);
    const handleLisaaKoodistoRyhma = () => {
        setKoodistoRyhmaModalVisible(true);
    };
    return (
        <PageBase>
            <MainContainer>
                <React.Suspense fallback={<Loading />}>
                    <ContentContainer>
                        <KoodistoTable handleLisaaKoodistoRyhma={handleLisaaKoodistoRyhma} />
                        {koodistoRyhmaModalVisible && <div>FOO</div>}
                    </ContentContainer>
                </React.Suspense>
            </MainContainer>
        </PageBase>
    );
};

export default App;
