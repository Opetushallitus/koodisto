import React from 'react';
import { FormattedDate, FormattedMessage } from 'react-intl';
import styled from 'styled-components';
import { PageKoodisto } from '../../types';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom, casMeLocaleAtom } from '../../api/kayttooikeus';

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
    koodisto: PageKoodisto;
}

const InfoFields = ({ koodisto }: InfoFieldsProps) => {
    const [lang] = useAtom(casMeLangAtom);
    const [locale] = useAtom(casMeLocaleAtom);
    const kuvaus = translateMetadata({ metadata: koodisto.metadata, lang })?.kuvaus || '';
    const ryhmaNimi = translateMetadata({ metadata: koodisto.koodistoRyhmaMetadata, lang })?.nimi || '';
    return (
        <InfoContainer>
            <InfoRow>
                <FormattedMessage id={'KOODISTOSIVU_AVAIN_URI_TUNNUS'} defaultMessage={'URI-tunnus'} tagName={'span'} />
                <InfoValue>
                    <a href={koodisto.resourceUri}>{koodisto.resourceUri}</a>
                </InfoValue>
            </InfoRow>
            <InfoRow>
                <FormattedMessage
                    id={'KOODISTOSIVU_AVAIN_KOODISTORYHMA'}
                    defaultMessage={'Koodistoryhmä'}
                    tagName={'span'}
                />
                <InfoValue>{ryhmaNimi}</InfoValue>
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
                <InfoValue>{koodisto.organisaatioNimi?.[locale]}</InfoValue>
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
