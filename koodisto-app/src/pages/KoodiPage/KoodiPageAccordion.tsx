import React from 'react';
import { Accordion } from '../../components/Accordion';
import { useIntl } from 'react-intl';
import { RelationTable } from './RelationTable';
import { Koodi } from '../../types';

export const KoodiPageAccordion: React.FC<{ koodi: Koodi }> = ({ koodi }) => {
    const { formatMessage } = useIntl();
    const data = [
        {
            id: 'is-incuded',
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_SISALTYY_KOODEIHIN_OTSIKKO',
                    defaultMessage: 'Sisältyy koodeihin ({count})',
                },
                { count: koodi.sisaltyyKoodeihin.length }
            ),
            panelComponent: <RelationTable relations={koodi.sisaltyyKoodeihin} />,
        },
        {
            id: 'includes',
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_SISALTAA_KOODIT_OTSIKKO',
                    defaultMessage: 'Sisältää koodit ({count})',
                },
                { count: koodi.sisaltaaKoodit.length }
            ),
            panelComponent: <RelationTable relations={koodi.sisaltaaKoodit} />,
        },
        {
            id: 'levels-with',
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_RINNASTUU_KOODEIHIN_OTSIKKO',
                    defaultMessage: 'Rinnastuu koodeihin ({count})',
                },
                { count: koodi.rinnastuuKoodeihin.length }
            ),
            panelComponent: <RelationTable relations={koodi.rinnastuuKoodeihin} />,
        },
    ];

    return <Accordion data={data} />;
};
