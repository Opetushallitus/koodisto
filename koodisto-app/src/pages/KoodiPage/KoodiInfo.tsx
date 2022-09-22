import React from 'react';
import { useIntl } from 'react-intl';
import styled from 'styled-components';
import { capitalize } from 'lodash';
import InfoFields from '../../components/InfoFields';
import DateRange from '../../components/DateRange';
import UpdatedAt from '../../components/UpdatedAt';
import { Koodi } from '../../types';

const NameContainer = styled.ul`
    list-style-type: none;
    padding: 0;
    margin: 0;

    li div {
        display: inline-block;
        min-width: 2rem;
    }
`;

export const KoodiInfo: React.FC<{ koodi: Koodi }> = ({ koodi }) => {
    const { formatMessage } = useIntl();
    const fields = [
        {
            header: {
                id: 'KOODISTOSIVU_AVAIN_TILA',
                defaultMessage: 'Tila',
            },
            value: formatMessage({
                id: `TILA_${koodi.tila}`,
                defaultMessage: capitalize(koodi.tila),
            }),
        },
        {
            header: {
                id: 'NIMI',
                defaultMessage: 'Nimi',
            },
            value: (
                <NameContainer>
                    {koodi.metadata.map((meta) => (
                        <li key={meta.kieli} className={'kieli-nimi'}>
                            <div>{meta.kieli}</div>
                            <div>{meta.nimi}</div>
                        </li>
                    ))}
                </NameContainer>
            ),
        },
        {
            header: {
                id: 'KOODIARVO',
                defaultMessage: 'Koodiarvo',
            },
            value: koodi.koodiArvo,
        },
        {
            header: {
                id: 'KOODISTOSIVU_AVAIN_URI_TUNNUS',
                defaultMessage: 'URI-tunnus',
            },
            value: koodi.koodiUri,
        },
        {
            header: {
                id: 'VOIMASSA',
                defaultMessage: 'Voimassa',
            },
            value: <DateRange from={koodi.voimassaAlkuPvm} to={koodi.voimassaLoppuPvm} />,
        },
        {
            header: {
                id: 'KUVAUS',
                defaultMessage: 'Kuvaus',
            },
            value: koodi.metadata[0].kuvaus || '-',
        },
        {
            header: {
                id: 'UPDATED',
                defaultMessage: 'Päivitetty',
            },
            value: <UpdatedAt at={koodi.paivitysPvm} by={koodi.paivittajaOid} />,
        },
    ];

    return <InfoFields fields={fields} />;
};
