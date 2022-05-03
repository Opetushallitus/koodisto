import React from 'react';
import Accordion from '../../Accordion/Accordion';
import { useIntl } from 'react-intl';
import { KoodistoRelation } from '../../../api/koodisto';
import KoodistoRelationsTable from './KoodistoRelationsTable';

const SISALTYY_KOODISTOIHIN_ID = 0;
const SISALTAA_KOODISTOT_ID = 1;
const RINNASTUU_KOODISTOIHIN_ID = 2;

type KoodistoPageAccordionProps = {
    levelsWithCodes: KoodistoRelation[];
    withinCodes: KoodistoRelation[];
    includesCodes: KoodistoRelation[];
};

const KoodistoPageAccordion = (props: KoodistoPageAccordionProps) => {
    const { levelsWithCodes, withinCodes, includesCodes } = props;
    const { formatMessage } = useIntl();
    const data = [
        {
            id: SISALTYY_KOODISTOIHIN_ID,
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_SISALTYY_KOODISTOIHIN_OTSIKKO',
                    defaultMessage: 'Sisältyy koodistoihin ({count})',
                },
                { count: withinCodes.length }
            ),
            panelComponent: <KoodistoRelationsTable koodistoRelations={withinCodes} />,
        },
        {
            id: RINNASTUU_KOODISTOIHIN_ID,
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_RINNASTUU_KOODISTOIHIN_OTSIKKO',
                    defaultMessage: 'Rinnastuu koodistoihin ({count})',
                },
                { count: levelsWithCodes.length }
            ),
            panelComponent: <KoodistoRelationsTable koodistoRelations={levelsWithCodes} />,
        },
        {
            id: SISALTAA_KOODISTOT_ID,
            localizedHeadingTitle: formatMessage(
                {
                    id: 'TAULUKKO_SISALTAA_KOODISTOT_OTSIKKO',
                    defaultMessage: 'Sisältää koodistot ({count})',
                },
                { count: includesCodes.length }
            ),
            panelComponent: <KoodistoRelationsTable koodistoRelations={includesCodes} />,
        },
    ];

    return <Accordion data={data} />;
};

export default KoodistoPageAccordion;
