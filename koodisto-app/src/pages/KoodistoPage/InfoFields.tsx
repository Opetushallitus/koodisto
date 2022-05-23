import React from 'react';
import { FormattedDate, FormattedMessage, useIntl } from 'react-intl';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { Locale, PageKoodisto } from '../../types/types';

const InfoContainer = styled.div`
    display: flex;
    flex-direction: column;
`;

const InfoRow = styled.div`
    display: flex;
    flex-direction: row;
    > * {
        &:first-child {
            min-width: 11rem;
            max-width: 11rem;
        }
    }
`;

const InfoValue = styled.span`
    color: #2a2a2a;
`;

interface InfoFieldsProps {
    kuvaus: string;
    koodisto: PageKoodisto;
}

const InfoFields = ({ koodisto, kuvaus }: InfoFieldsProps) => {
    const { locale } = useIntl();
    return (
        <InfoContainer>
            <InfoRow>
                <FormattedMessage id={'KOODISTOSIVU_AVAIN_URI_TUNNUS'} defaultMessage={'URI-tunnus'} tagName={'span'} />
                <Link to={koodisto.resourceUri}>{koodisto.resourceUri}</Link>
            </InfoRow>
            <InfoRow>
                <FormattedMessage
                    id={'KOODISTOSIVU_AVAIN_KOODISTORYHMA'}
                    defaultMessage={'Koodistoryhmä'}
                    tagName={'span'}
                />
                <InfoValue>{koodisto.codesGroupUri}</InfoValue>
            </InfoRow>
            <InfoRow>
                <FormattedMessage id={'KOODISTOSIVU_AVAIN_VOIMASSA'} defaultMessage={'Voimassa'} tagName={'span'} />
                <InfoValue>
                    <FormattedDate value={koodisto.voimassaAlkuPvm} />
                </InfoValue>
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
                <InfoValue>{koodisto.organisaatioNimi?.[locale as Locale]}</InfoValue>
            </InfoRow>
            <InfoRow>
                <FormattedMessage id={'KOODISTOSIVU_AVAIN_PAIVITETTY'} defaultMessage={'Päivitetty'} tagName={'span'} />
                <InfoValue>
                    <FormattedDate value={koodisto.paivitysPvm} />
                </InfoValue>
            </InfoRow>
        </InfoContainer>
    );
};

export default InfoFields;
