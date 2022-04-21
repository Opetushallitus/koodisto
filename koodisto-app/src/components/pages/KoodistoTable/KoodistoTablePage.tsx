import React, { useState } from 'react';
import KoodistoTable from './KoodistoTable';
import Loading from '../Loading/Loading';
import styled from 'styled-components';
import Notification from '../../Notification/Notification';
import { FormattedMessage } from 'react-intl';
import IconWrapper from '../../IconWapper/IconWrapper';
import Button from '@opetushallitus/virkailija-ui-components/Button';

const PageBase = styled.div`
    width: 100%;
    display: flex;
    justify-content: space-between;
    flex-direction: column;
    flex-wrap: nowrap;
    min-height: 87vh;
`;
const MainContainer = styled.div`
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    box-sizing: border-box;
    background-color: #ffffff;
`;
const MainHeaderContainer = styled.div`
    display: inline-flex;
    flex-direction: row;
    align-items: center;
    padding: 3em 6rem 0 6rem;
    justify-content: space-between;
`;

export const HeaderContainer = styled.div`
    display: flex;
    padding: 1rem 1rem;
    align-items: center;
    justify-content: space-between;
    border: 1px solid #cccccc;
`;
const ContentContainer = styled.div`
    padding: 4rem 6rem 0 6rem;
    display: block;
    max-width: 100%;
`;

export const ButtonLabelPrefix = styled.span`
    display: flex;
    align-items: center;
    padding-right: 0.3rem;
`;
const KoodistoTablePage: React.FC = () => {
    const [koodistoRyhmaModalVisible, setKoodistoRyhmaModalVisible] = useState<boolean>(false);
    const handleLisaaKoodistoRyhma = () => {
        setKoodistoRyhmaModalVisible(true);
    };
    return (
        <PageBase>
            <MainHeaderContainer>
                <FormattedMessage id={'TAULUKKOSIVU_OTSIKKO'} defaultMessage={'Koodistojen ylläpito'} tagName={'h1'} />
                <Button variant={'text'}>
                    <ButtonLabelPrefix>
                        <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                    </ButtonLabelPrefix>
                    <FormattedMessage
                        id={'TAULUKKO_LISAA_KOODISTORYHMA_BUTTON'}
                        defaultMessage={'Luo / poista koodistoryhmä'}
                    />
                </Button>
            </MainHeaderContainer>
            <Notification />
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

export default KoodistoTablePage;
