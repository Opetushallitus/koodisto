import React, { useState, useEffect } from 'react';
import styled from 'styled-components';
import { FormattedMessage } from 'react-intl';
import { IconWrapper } from '../../components/IconWapper';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { KoodistoRyhmaModal } from '../../modals/KoodistoRyhmaModal';
import { useParams, useNavigate } from 'react-router-dom';
import { KoodistoTable } from '../../components/Table';
import { ButtonLabelPrefix } from '../../components/Containers';
import { StyledPopup } from '../../components/Modal/Modal';

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

const ContentContainer = styled.div`
    padding: 4rem 6rem 0 6rem;
    display: block;
    max-width: 100%;
`;
const contentStyle = { width: '500px' };
export const KoodistoTablePage: React.FC = () => {
    const { koodistoRyhmaUri } = useParams();
    const navigate = useNavigate();
    const [koodistoRyhmaModalVisible, setKoodistoRyhmaModalVisible] = useState<boolean>(false);
    useEffect(() => {
        setKoodistoRyhmaModalVisible(!!koodistoRyhmaUri);
    }, [koodistoRyhmaUri]);

    return (
        <>
            <MainHeaderContainer>
                <FormattedMessage id={'TAULUKKOSIVU_OTSIKKO'} defaultMessage={'Koodistojen ylläpito'} tagName={'h1'} />
                <StyledPopup
                    open={koodistoRyhmaModalVisible}
                    onClose={() => setKoodistoRyhmaModalVisible(false)}
                    position="bottom right"
                    trigger={
                        <Button variant={'text'} name={'TAULUKKO_LISAA_KOODISTORYHMA_BUTTON'}>
                            <ButtonLabelPrefix>
                                <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                            </ButtonLabelPrefix>
                            <FormattedMessage
                                id={'TAULUKKO_LISAA_KOODISTORYHMA_BUTTON'}
                                defaultMessage={'Luo / poista koodistoryhmä'}
                            />
                        </Button>
                    }
                    {...{ contentStyle }}
                >
                    {(close: () => void) => (
                        <KoodistoRyhmaModal
                            koodistoRyhmaUri={koodistoRyhmaUri}
                            closeModal={() => {
                                close();
                                navigate('/');
                            }}
                        />
                    )}
                </StyledPopup>
            </MainHeaderContainer>
            <MainContainer>
                <ContentContainer>
                    <KoodistoTable />
                </ContentContainer>
            </MainContainer>
        </>
    );
};
