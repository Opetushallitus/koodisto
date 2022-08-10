import React from 'react';
import { useIntl } from 'react-intl';
import type { PageKoodisto } from '../../types';
import InfoFields from '../../components/InfoFields';
import DateRange from '../../components/DateRange';
import UpdatedAt from '../../components/UpdatedAt';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom, casMeLocaleAtom } from '../../api/kayttooikeus';

export const KoodistoInfo: React.FC<PageKoodisto> = ({
    resourceUri,
    koodistoRyhmaMetadata,
    voimassaAlkuPvm,
    voimassaLoppuPvm,
    metadata,
    organisaatioNimi,
    paivitysPvm,
    paivittajaOid,
    tila,
}) => {
    const { formatMessage } = useIntl();
    const [lang] = useAtom(casMeLangAtom);
    const [locale] = useAtom(casMeLocaleAtom);
    const fields = [
        {
            header: {
                id: 'KOODISTOSIVU_AVAIN_TILA',
                defaultMessage: 'Tila',
            },
            value: formatMessage({
                id: `TILA_${tila}`,
            }),
        },
        {
            header: {
                id: 'KOODISTOSIVU_AVAIN_URI_TUNNUS',
                defaultMessage: 'URI-tunnus',
            },
            value: <a href={resourceUri}>{resourceUri}</a>,
        },
        {
            header: {
                id: 'KOODISTOSIVU_AVAIN_KOODISTORYHMA',
                defaultMessage: 'Koodistoryhmä',
            },
            value: translateMetadata({ metadata: koodistoRyhmaMetadata, lang })?.nimi || '',
        },
        {
            header: {
                id: 'VOIMASSA',
                defaultMessage: 'Voimassa',
            },
            value: <DateRange from={voimassaAlkuPvm} to={voimassaLoppuPvm} />,
        },
        {
            header: {
                id: 'KOODISTOSIVU_AVAIN_ORGANISAATIO',
                defaultMessage: 'Organisaatio',
            },
            value: organisaatioNimi?.[locale],
        },
        {
            header: {
                id: 'KUVAUS',
                defaultMessage: 'Kuvaus',
            },
            value: translateMetadata({ metadata, lang })?.kuvaus || '',
        },
        {
            header: {
                id: 'PAIVITETTY',
                defaultMessage: 'Päivitetty',
            },
            value: <UpdatedAt at={paivitysPvm} by={paivittajaOid} />,
        },
    ];

    return <InfoFields fields={fields} />;
};
