import React from 'react';
import { FormattedMessage } from 'react-intl';
import styled from 'styled-components';
import { KoodistoPageKoodisto } from '../../../api/koodisto';

const InfoContainer = styled.div`
    display: flex;
    flex-direction: column;
`;

const InfoRow = styled.div`
    display: flex;
    flex-direction: row;
    > * {
        &:first-child {
            width: 11rem;
        }
    }
`;

const InfoValue = styled.span`
    color: #2a2a2a;
`;

interface InfoFieldsProps {
    ryhmaNimi: string | undefined;
    kuvaus: string;
    koodisto: KoodistoPageKoodisto;
}

const InfoFields = ({ koodisto, ryhmaNimi, kuvaus }: InfoFieldsProps) => {
    return (
        <InfoContainer>
            <InfoRow>
                <FormattedMessage id={'KOODISTOSIVU_AVAIN_URI_TUNNUS'} defaultMessage={'URI-tunnus'} tagName={'span'} />
                <a href={koodisto?.resourceUri || ''}>{koodisto?.resourceUri}</a>
            </InfoRow>
            <InfoRow>
                <FormattedMessage
                    id={'KOODISTOSIVU_AVAIN_KOODISTORYHMA'}
                    defaultMessage={'Koodistoryhmä'}
                    tagName={'span'}
                />
                <InfoValue>{ryhmaNimi || koodisto?.codesGroupUri}</InfoValue>
            </InfoRow>
            <InfoRow>
                <FormattedMessage id={'KOODISTOSIVU_AVAIN_VOIMASSA'} defaultMessage={'Voimassa'} tagName={'span'} />
                <InfoValue>{koodisto.voimassaAlkuPvm}</InfoValue>
            </InfoRow>
            <InfoRow>
                <FormattedMessage id={'KOODISTOSIVU_AVAIN_KUVAUS'} defaultMessage={'Kuvaus'} tagName={'span'} />
                <InfoValue>{kuvaus}</InfoValue>
            </InfoRow>
            <InfoRow>
                <FormattedMessage
                    id={'KOODISTOSIVU_AVAIN_ORGANISAATIO'}
                    defaultMessage={'Organisaatio'}
                    tagName={'span'}
                />
                <InfoValue>{koodisto.organisaatioOid} TODO organisaation nimi</InfoValue>
            </InfoRow>
            <InfoRow>
                <FormattedMessage id={'KOODISTOSIVU_AVAIN_PAIVITETTY'} defaultMessage={'Päivitetty'} tagName={'span'} />
                <InfoValue>{koodisto.paivitysPvm}</InfoValue>
            </InfoRow>
        </InfoContainer>
    );
};

export default InfoFields;
