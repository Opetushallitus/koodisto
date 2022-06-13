import React from 'react';
import { Accordion } from '../../components/Accordion';
import { useIntl, FormattedMessage } from 'react-intl';
import KoodistoRelationsTable from './KoodistoRelationsTable';
import { KoodiTable } from './KoodiTable';
import { PageKoodisto } from '../../types';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { ButtonLabelPrefix } from '../KoodistoTablePage/KoodistoTablePage';
import { IconWrapper } from '../../components/IconWapper';
import { useNavigate } from 'react-router-dom';

const SISALTYY_KOODISTOIHIN_ID = 0;
const SISALTAA_KOODISTOT_ID = 1;
const RINNASTUU_KOODISTOIHIN_ID = 2;
const KOODIT_ID = 3;

type KoodistoPageAccordionProps = {
    koodisto: PageKoodisto;
};

const KoodistoPageAccordion = ({ koodisto }: KoodistoPageAccordionProps) => {
    const { rinnastuuKoodistoihin, sisaltyyKoodistoihin, sisaltaaKoodistot, koodiList } = koodisto;
    const navigate = useNavigate();
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
            localizedHeadingTitle: (
                <>
                    <FormattedMessage
                        id={'TAULUKKO_KOODIT_OTSIKKO'}
                        defaultMessage={'Koodit ({count})'}
                        values={{ count: koodiList.length }}
                    />
                    <Button onClick={() => navigate('/koodi/edit/')}>
                        <ButtonLabelPrefix>
                            <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                        </ButtonLabelPrefix>
                        <FormattedMessage id={'TAULUKKO_LISAA_KOODI_BUTTON'} defaultMessage={'Luo uusi koodi'} />
                    </Button>
                </>
            ),
            panelComponent: <KoodiTable koodiList={koodiList} />,
        },
    ];

    return <Accordion data={data} />;
};

export default KoodistoPageAccordion;
