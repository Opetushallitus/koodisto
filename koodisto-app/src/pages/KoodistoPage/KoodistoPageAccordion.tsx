import React from 'react';
import { Accordion } from '../../components/Accordion';
import { FormattedMessage } from 'react-intl';
import { KoodistoRelationsTable } from './KoodistoRelationsTable';
import { Koodi, KoodistoRelation, PageKoodisto } from '../../types';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { IconWrapper } from '../../components/IconWapper';
import { useNavigate, createSearchParams, useParams } from 'react-router-dom';
import Spin from '@opetushallitus/virkailija-ui-components/Spin';
import { UseFieldArrayReturn } from 'react-hook-form';
import { ButtonLabelPrefix } from '../../components/Containers';
import { KoodiTable } from '../../components/Table';

const SISALTYY_KOODISTOIHIN_ID = 0;
const SISALTAA_KOODISTOT_ID = 1;
const RINNASTUU_KOODISTOIHIN_ID = 2;
const KOODIT_ID = 3;

type KoodistoPageAccordionProps = {
    rinnastuuKoodistoihin: KoodistoRelation[];
    sisaltyyKoodistoihin: KoodistoRelation[];
    sisaltaaKoodistot: KoodistoRelation[];
    koodiList?: Koodi[];
    editable?: boolean;
    rinnastuuKoodistoihinReturn?: UseFieldArrayReturn<PageKoodisto>;
    sisaltyyKoodistoihinReturn?: UseFieldArrayReturn<PageKoodisto>;
    sisaltaaKoodistotReturn?: UseFieldArrayReturn<PageKoodisto>;
};

const KoodistoPageAccordion: React.FC<KoodistoPageAccordionProps> = ({
    rinnastuuKoodistoihin,
    sisaltyyKoodistoihin,
    sisaltaaKoodistot,
    rinnastuuKoodistoihinReturn,
    sisaltyyKoodistoihinReturn,
    sisaltaaKoodistotReturn,
    koodiList,
    editable,
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
                </>
            ),
            panelComponent: (
                <KoodistoRelationsTable
                    fieldArrayReturn={sisaltyyKoodistoihinReturn}
                    koodistoRelations={sisaltyyKoodistoihin}
                    editable={!!editable}
                />
            ),
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
                </>
            ),
            panelComponent: (
                <KoodistoRelationsTable
                    fieldArrayReturn={rinnastuuKoodistoihinReturn}
                    koodistoRelations={rinnastuuKoodistoihin}
                    editable={!!editable}
                />
            ),
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
                </>
            ),
            panelComponent: (
                <KoodistoRelationsTable
                    fieldArrayReturn={sisaltaaKoodistotReturn}
                    koodistoRelations={sisaltaaKoodistot}
                    editable={!!editable}
                />
            ),
        },
        ...(!editable
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
