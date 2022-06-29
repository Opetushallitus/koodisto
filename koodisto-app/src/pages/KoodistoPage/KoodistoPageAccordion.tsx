import React, { useEffect, useState } from 'react';
import { Accordion } from '../../components/Accordion';
import { useIntl, FormattedMessage } from 'react-intl';
import KoodistoRelationsTable from './KoodistoRelationsTable';
import { KoodiTable } from './KoodiTable';
import { PageKoodisto, Koodi } from '../../types';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { ButtonLabelPrefix } from '../KoodistoTablePage/KoodistoTablePage';
import { IconWrapper } from '../../components/IconWapper';
import { useNavigate, createSearchParams } from 'react-router-dom';
import Spin from '@opetushallitus/virkailija-ui-components/Spin';
import { fetchKoodistoKoodis } from '../../api/koodi';

const SISALTYY_KOODISTOIHIN_ID = 0;
const SISALTAA_KOODISTOT_ID = 1;
const RINNASTUU_KOODISTOIHIN_ID = 2;
const KOODIT_ID = 3;

type KoodistoPageAccordionProps = {
    koodisto: PageKoodisto;
};

const KoodistoPageAccordion = ({ koodisto }: KoodistoPageAccordionProps) => {
    const { rinnastuuKoodistoihin, sisaltyyKoodistoihin, sisaltaaKoodistot } = koodisto;
    const [koodiList, setKoodiList] = useState<Koodi[] | undefined>(undefined);
    useEffect(() => {
        (async () => setKoodiList(await fetchKoodistoKoodis(koodisto.koodistoUri, koodisto.versio)))();
    }, [koodisto]);

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
            localizedHeadingTitle: (koodiList && (
                <>
                    <FormattedMessage
                        id={'TAULUKKO_KOODIT_OTSIKKO'}
                        defaultMessage={'Koodit ({count})'}
                        values={{ count: koodiList?.length || 0 }}
                    />
                    <Button
                        name={'TAULUKKO_LISAA_KOODI_BUTTON'}
                        onClick={() =>
                            navigate({
                                pathname: '/koodi/edit/',
                                search: `?${createSearchParams({ koodistoUri: koodisto.koodistoUri })}`,
                            })
                        }
                    >
                        <ButtonLabelPrefix>
                            <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                        </ButtonLabelPrefix>
                        <FormattedMessage id={'TAULUKKO_LISAA_KOODI_BUTTON'} defaultMessage={'Luo uusi koodi'} />
                    </Button>
                </>
            )) || <Spin size={'small'} />,
            panelComponent: (koodiList && <KoodiTable koodiList={koodiList} />) || <Spin />,
        },
    ];

    return <Accordion data={data} />;
};

export default KoodistoPageAccordion;
