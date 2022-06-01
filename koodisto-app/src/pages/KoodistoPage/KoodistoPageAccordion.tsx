import React from 'react';
import { Accordion } from '../../components/Accordion';
import { useIntl } from 'react-intl';
import KoodistoRelationsTable from './KoodistoRelationsTable';
import { KoodiTable } from './KoodiTable';
import { PageKoodisto } from '../../types';

const SISALTYY_KOODISTOIHIN_ID = 0;
const SISALTAA_KOODISTOT_ID = 1;
const RINNASTUU_KOODISTOIHIN_ID = 2;
const KOODIT_ID = 3;

type KoodistoPageAccordionProps = {
    koodisto: PageKoodisto;
};

const KoodistoPageAccordion = ({ koodisto }: KoodistoPageAccordionProps) => {
    const { rinnastuuKoodistoihin, sisaltyyKoodistoihin, sisaltaaKoodistot, koodiList } = koodisto;
    const { formatMessage } = useIntl();
    const data = [
        {
            id: SISALTYY_KOODISTOIHIN_ID,
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_SISALTYY_KOODISTOIHIN_OTSIKKO',
                    defaultMessage: 'Sisältyy koodistoihin ({count})',
                },
                { count: sisaltyyKoodistoihin.length }
            ),
            panelComponent: <KoodistoRelationsTable koodistoRelations={sisaltyyKoodistoihin} />,
        },
        {
            id: RINNASTUU_KOODISTOIHIN_ID,
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_RINNASTUU_KOODISTOIHIN_OTSIKKO',
                    defaultMessage: 'Rinnastuu koodistoihin ({count})',
                },
                { count: rinnastuuKoodistoihin.length }
            ),
            panelComponent: <KoodistoRelationsTable koodistoRelations={rinnastuuKoodistoihin} />,
        },
        {
            id: SISALTAA_KOODISTOT_ID,
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_SISALTAA_KOODISTOT_OTSIKKO',
                    defaultMessage: 'Sisältää koodistot ({count})',
                },
                { count: sisaltaaKoodistot.length }
            ),
            panelComponent: <KoodistoRelationsTable koodistoRelations={sisaltaaKoodistot} />,
        },
        {
            id: KOODIT_ID,
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_KOODIT_OTSIKKO',
                    defaultMessage: 'Koodit ({count})',
                },
                { count: koodiList.length }
            ),
            panelComponent: <KoodiTable koodiList={koodiList} />,
        },
    ];

    return <Accordion data={data} />;
};

export default KoodistoPageAccordion;
