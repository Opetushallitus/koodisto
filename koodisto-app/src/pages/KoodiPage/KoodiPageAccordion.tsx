import React from 'react';
import { Accordion } from '../../components/Accordion';
import { useIntl } from 'react-intl';
import { RelationTable } from './RelationTable';
import type { PageKoodi } from '../../types';

export const KoodiPageAccordion: React.FC<Pick<PageKoodi, 'koodi'>> = ({ koodi }: Pick<PageKoodi, 'koodi'>) => {
    const { formatMessage } = useIntl();
    const data = [
        {
            id: 'is-incuded',
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_SISALTYY_KOODEIHIN_OTSIKKO',
                    defaultMessage: 'Sisältyy koodeihin ({count})',
                },
                { count: koodi.withinCodeElements.length }
            ),
            panelComponent: <RelationTable relations={koodi.withinCodeElements} />,
        },
        {
            id: 'includes',
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_SISALTAA_KOODIT_OTSIKKO',
                    defaultMessage: 'Sisältää koodit ({count})',
                },
                { count: koodi.includesCodeElements.length }
            ),
            panelComponent: <RelationTable relations={koodi.includesCodeElements} />,
        },
        {
            id: 'levels-with',
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_RINNASTUU_KOODEIHIN_OTSIKKO',
                    defaultMessage: 'Rinnastuu koodeihin ({count})',
                },
                { count: koodi.levelsWithCodeElements.length }
            ),
            panelComponent: <RelationTable relations={koodi.levelsWithCodeElements} />,
        },
    ];

    return <Accordion data={data} />;
};
