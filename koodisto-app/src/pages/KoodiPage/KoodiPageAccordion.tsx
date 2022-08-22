import React from 'react';
import { Accordion } from '../../components/Accordion';
import { useIntl } from 'react-intl';
import { KoodiRelationsTable } from './KoodiRelationsTable';
import { Koodi } from '../../types';
import { UseFieldArrayReturn } from 'react-hook-form';

type KoodiPageAccordionProps = {
    koodi: Koodi;
    editable?: boolean;
    sisaltyyKoodeihinReturn?: UseFieldArrayReturn<Koodi>;
    sisaltaaKooditReturn?: UseFieldArrayReturn<Koodi>;
    rinnastuuKoodeihinReturn?: UseFieldArrayReturn<Koodi>;
};
export const KoodiPageAccordion: React.FC<KoodiPageAccordionProps> = ({
    koodi,
    editable,
    sisaltyyKoodeihinReturn,
    sisaltaaKooditReturn,
    rinnastuuKoodeihinReturn,
}) => {
    const { formatMessage } = useIntl();
    const data = [
        {
            id: 'is-incuded',
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_SISALTYY_KOODEIHIN_OTSIKKO',
                    defaultMessage: 'Sisältyy koodeihin ({count})',
                },
                { count: koodi.sisaltyyKoodeihin?.length }
            ),
            panelComponent: (
                <KoodiRelationsTable
                    relationSources={koodi?.koodisto?.sisaltyyKoodistoihin?.map((a) => ({
                        koodistoUri: a.koodistoUri,
                        versio: a.koodistoVersio,
                    }))}
                    editable={!!editable}
                    relations={koodi.sisaltyyKoodeihin || []}
                    fieldArrayReturn={sisaltyyKoodeihinReturn}
                />
            ),
        },
        {
            id: 'includes',
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_SISALTAA_KOODIT_OTSIKKO',
                    defaultMessage: 'Sisältää koodit ({count})',
                },
                { count: koodi.sisaltaaKoodit?.length }
            ),
            panelComponent: (
                <KoodiRelationsTable
                    relationSources={koodi?.koodisto?.sisaltaaKoodistot?.map((a) => ({
                        koodistoUri: a.koodistoUri,
                        versio: a.koodistoVersio,
                    }))}
                    editable={!!editable}
                    relations={koodi.sisaltaaKoodit || []}
                    fieldArrayReturn={sisaltaaKooditReturn}
                />
            ),
        },
        {
            id: 'levels-with',
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_RINNASTUU_KOODEIHIN_OTSIKKO',
                    defaultMessage: 'Rinnastuu koodeihin ({count})',
                },
                { count: koodi.rinnastuuKoodeihin?.length }
            ),
            panelComponent: (
                <KoodiRelationsTable
                    relationSources={koodi?.koodisto?.rinnastuuKoodistoihin?.map((a) => ({
                        koodistoUri: a.koodistoUri,
                        versio: a.koodistoVersio,
                    }))}
                    editable={!!editable}
                    relations={koodi.rinnastuuKoodeihin || []}
                    fieldArrayReturn={rinnastuuKoodeihinReturn}
                />
            ),
        },
    ];

    return <Accordion data={data} />;
};
