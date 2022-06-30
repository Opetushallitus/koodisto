import React from 'react';
import { Accordion } from '../../components/Accordion';
import { FormattedMessage } from 'react-intl';
import KoodistoRelationsTable from './KoodistoRelationsTable';
import { KoodiTable } from './KoodiTable';
import { Koodi, KoodistoRelation } from '../../types';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { ButtonLabelPrefix } from '../KoodistoTablePage/KoodistoTablePage';
import { IconWrapper } from '../../components/IconWapper';
import { useNavigate, createSearchParams, useParams } from 'react-router-dom';
import Spin from '@opetushallitus/virkailija-ui-components/Spin';

const SISALTYY_KOODISTOIHIN_ID = 0;
const SISALTAA_KOODISTOT_ID = 1;
const RINNASTUU_KOODISTOIHIN_ID = 2;
const KOODIT_ID = 3;

type KoodistoPageAccordionProps = {
    rinnastuuKoodistoihin: KoodistoRelation[];
    sisaltyyKoodistoihin: KoodistoRelation[];
    sisaltaaKoodistot: KoodistoRelation[];
    koodiList?: Koodi[];
    editMode?: boolean;
};
const AddSuhdeButton: React.FC<{ name: string }> = ({ name }) => {
    return (
        <Button name={name}>
            <ButtonLabelPrefix>
                <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
            </ButtonLabelPrefix>
            <FormattedMessage id={'TAULUKKO_LISAA_KOODISTOJA_BUTTON'} defaultMessage={'Lisää koodistoja'} />
        </Button>
    );
};
const KoodistoPageAccordion: React.FC<KoodistoPageAccordionProps> = ({
    rinnastuuKoodistoihin,
    sisaltyyKoodistoihin,
    sisaltaaKoodistot,
    koodiList,
    editMode,
}) => {
    const { koodistoUri, versio } = useParams();
    const navigate = useNavigate();
    const data = [
        {
            id: SISALTYY_KOODISTOIHIN_ID,
            localizedHeadingTitle: (
                <>
                    <FormattedMessage
                        id={'TAULUKKO_SISALTYY_KOODISTOIHIN_OTSIKKO'}
                        defaultMessage={'Sisältyy koodistoihin ({count})'}
                        values={{ count: sisaltyyKoodistoihin.length }}
                    />
                    {editMode && <AddSuhdeButton name={'TAULUKKO_LISAA_SISALTYY_KOODISTOJA_BUTTON'} />}
                </>
            ),
            panelComponent: <KoodistoRelationsTable koodistoRelations={sisaltyyKoodistoihin} />,
        },
        {
            id: RINNASTUU_KOODISTOIHIN_ID,
            localizedHeadingTitle: (
                <>
                    <FormattedMessage
                        id={'TAULUKKO_RINNASTUU_KOODISTOIHIN_OTSIKKO'}
                        defaultMessage={'Rinnastuu koodistoihin ({count})'}
                        values={{ count: rinnastuuKoodistoihin.length }}
                    />
                    {editMode && <AddSuhdeButton name={'TAULUKKO_LISAA_RINNASTUU_KOODISTOJA_BUTTON'} />}
                </>
            ),
            panelComponent: <KoodistoRelationsTable koodistoRelations={rinnastuuKoodistoihin} />,
        },
        {
            id: SISALTAA_KOODISTOT_ID,
            localizedHeadingTitle: (
                <>
                    <FormattedMessage
                        id={'TAULUKKO_SISALTAA_KOODISTOT_OTSIKKO'}
                        defaultMessage={'Sisältää koodistot ({count})'}
                        values={{ count: sisaltaaKoodistot.length }}
                    />
                    {editMode && <AddSuhdeButton name={'TAULUKKO_LISAA_SISALTAA_KOODISTOJA_BUTTON'} />}
                </>
            ),
            panelComponent: <KoodistoRelationsTable koodistoRelations={sisaltaaKoodistot} />,
        },
        ...(!editMode
            ? [
                  {
                      id: KOODIT_ID,
                      localizedHeadingTitle: (koodiList && (
                          <>
                              <FormattedMessage
                                  id={'TAULUKKO_KOODIT_OTSIKKO'}
                                  defaultMessage={'Koodit ({count})'}
                                  values={{ count: koodiList?.length || 0 }}
                              />
                              {koodistoUri && versio && (
                                  <Button
                                      name={'TAULUKKO_LISAA_KOODI_BUTTON'}
                                      onClick={() =>
                                          navigate({
                                              pathname: '/koodi/edit/',
                                              search: `?${createSearchParams({ koodistoUri, koodistoVersio: versio })}`,
                                          })
                                      }
                                  >
                                      <ButtonLabelPrefix>
                                          <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                                      </ButtonLabelPrefix>
                                      <FormattedMessage
                                          id={'TAULUKKO_LISAA_KOODI_BUTTON'}
                                          defaultMessage={'Luo uusi koodi'}
                                      />
                                  </Button>
                              )}
                          </>
                      )) || <Spin size={'small'} />,
                      panelComponent: (koodiList && <KoodiTable koodiList={koodiList} />) || <Spin />,
                  },
              ]
            : []),
    ];

    return <Accordion data={data} />;
};

export default KoodistoPageAccordion;
