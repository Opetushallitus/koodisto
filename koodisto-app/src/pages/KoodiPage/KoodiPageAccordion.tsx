import React from 'react';
import { Accordion } from '../../components/Accordion';
import { useIntl, FormattedMessage } from 'react-intl';
import { KoodiRelationsTable } from './KoodiRelationsTable';
import { Koodi, KoodiList, KoodiRelation } from '../../types';
import { UseFieldArrayReturn } from 'react-hook-form';
import styled from 'styled-components';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { StyledPopup } from '../../components/Modal/Modal';
import { KoodiSuhdeModal } from './KoodiSuhdeModal';
import { fetchPageKoodi } from '../../api/koodi';
import { uniqWith } from 'lodash';

type KoodiPageAccordionProps = {
    koodi: Koodi;
    editable?: boolean;
    sisaltyyKoodeihinReturn?: UseFieldArrayReturn<Koodi, 'sisaltyyKoodeihin'>;
    sisaltaaKooditReturn?: UseFieldArrayReturn<Koodi, 'sisaltaaKoodit'>;
    rinnastuuKoodeihinReturn?: UseFieldArrayReturn<Koodi, 'rinnastuuKoodeihin'>;
};
const AccordionContainer = styled.div`
    padding-top: 1rem;
`;
const AccordionHeaderContainer = styled.div`
    display: flex;
    flex-direction: row;
`;
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

    const addRelationsToForm = async (fromKoodiList: KoodiList[]) => {
        const joinRelations = (p: KoodiRelation[], c: KoodiRelation[]) =>
            uniqWith([...p, ...c], (a, b) => a.koodiUri === b.koodiUri && a.koodiVersio === b.koodiVersio);

        const newRelations = await (
            await Promise.all(
                fromKoodiList.map(async (fromKoodi) => fetchPageKoodi(fromKoodi.koodiUri, fromKoodi.versio))
            )
        ).reduce(
            async (p, c) => ({
                sisaltyyKoodeihin: [...(await p).sisaltyyKoodeihin, ...(c?.sisaltyyKoodeihin || [])],
                sisaltaaKoodit: [...(await p).sisaltaaKoodit, ...(c?.sisaltaaKoodit || [])],
                rinnastuuKoodeihin: [...(await p).rinnastuuKoodeihin, ...(c?.rinnastuuKoodeihin || [])],
            }),
            Promise.resolve({
                sisaltyyKoodeihin: [],
                sisaltaaKoodit: [],
                rinnastuuKoodeihin: [],
            } as { sisaltyyKoodeihin: KoodiRelation[]; sisaltaaKoodit: KoodiRelation[]; rinnastuuKoodeihin: KoodiRelation[] })
        );

        sisaltaaKooditReturn?.replace(joinRelations(koodi.sisaltaaKoodit, newRelations.sisaltaaKoodit));
        sisaltyyKoodeihinReturn?.replace(joinRelations(koodi.sisaltyyKoodeihin, newRelations.sisaltyyKoodeihin));
        rinnastuuKoodeihinReturn?.replace(joinRelations(koodi.rinnastuuKoodeihin, newRelations.rinnastuuKoodeihin));
    };

    return (
        <AccordionContainer>
            <AccordionHeaderContainer>
                <FormattedMessage id={'KOODI_SUHTEET_TITLE'} defaultMessage={'Koodin suhteet'} tagName={'h2'} />
                {editable && (
                    <StyledPopup
                        trigger={
                            <Button name={'KOODI_KOPIOI_SUHTEET_BUTTON'} variant={'text'}>
                                <FormattedMessage
                                    id={'KOODI_KOPIOI_SUHTEET_BUTTON'}
                                    defaultMessage={'Kopioi suhteet koodilta'}
                                />
                            </Button>
                        }
                        modal
                    >
                        {(close: () => void) => (
                            <KoodiSuhdeModal
                                relationSources={[
                                    { koodistoUri: koodi.koodisto.koodistoUri, versio: koodi.koodisto.versio },
                                ]}
                                save={(koodiList) => koodiList.length && addRelationsToForm(koodiList)}
                                close={close}
                            />
                        )}
                    </StyledPopup>
                )}
            </AccordionHeaderContainer>
            <Accordion data={data} />
        </AccordionContainer>
    );
};
