import React from 'react';
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
const HeaderContainer = styled.div`
    display: flex;
    flex-direction: row;
    justify-content: space-between;
    padding: 1rem 2rem;
    border-bottom: 1px solid #cccccc;
    align-items: center;
`;
const ContentContainer = styled.div`
    padding: 2rem;
    display: block;
    max-width: 100%;
`;
const App = () => {
    return (
        <PageBase>
            <MainContainer>
                <HeaderContainer>
                    <h1>koodisto</h1>
                </HeaderContainer>
                <React.Suspense fallback={<Loading />}>
                    <ContentContainer>
                        <KoodistoTable />
                    </ContentContainer>
                </React.Suspense>
            </MainContainer>
        </PageBase>
    );
};

export default App;
