import React, { useMemo, useCallback } from 'react';
import { useIntl, FormattedMessage } from 'react-intl';
import { Link } from 'react-router-dom';
import { Table } from '../../components/Table';
import { translateMultiLocaleText, metadataToMultiLocaleText } from '../../utils';
import { useAtom } from 'jotai';
import { sortBy, uniqWith } from 'lodash';
import { casMeLocaleAtom } from '../../api/kayttooikeus';
import type { KoodiRelation, Koodi, KoodiList, SelectOptionType, Locale } from '../../types';
import { ColumnDef, CellContext, Row } from '@tanstack/react-table';
import { UseFieldArrayReturn } from 'react-hook-form';
import { KoodiSuhdeModal } from './KoodiSuhdeModal';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { IconWrapper } from '../../components/IconWapper';
import { ModalPopup } from '../../components/Modal/Modal';
import { ButtonLabelPrefix } from '../../components/Containers';

type RelationTableProps = {
    editable: boolean;
    relations: KoodiRelation[];
    fieldArrayReturn?:
        | UseFieldArrayReturn<Koodi, 'sisaltaaKoodit'>
        | UseFieldArrayReturn<Koodi, 'sisaltyyKoodeihin'>
        | UseFieldArrayReturn<Koodi, 'rinnastuuKoodeihin'>;
    relationSources?: { koodistoUri: string; versio: number }[];
};
const RemoveSuhdeButton: React.FC<{ onClick: (e: React.ChangeEvent<HTMLInputElement>) => void }> = ({ onClick }) => {
    return (
        <Button name={'TAULUKKO_POISTA_KOODISUHTEITA_BUTTON'} onClick={onClick} variant={'text'}>
            <ButtonLabelPrefix>
                <IconWrapper icon={'ci:trash-full'} inline={true} height={'1.2rem'} />
            </ButtonLabelPrefix>
        </Button>
    );
};
export const KoodiRelationsTable: React.FC<RelationTableProps> = ({
    relations,
    fieldArrayReturn,
    editable,
    relationSources = [],
}) => {
    const { formatMessage } = useIntl();
    const [locale] = useAtom(casMeLocaleAtom);
    const data = useMemo<KoodiRelation[]>(
        () =>
            sortBy([...relations], (koodiRelation) => koodiRelation.nimi?.[locale as Locale] || koodiRelation.koodiUri),
        [relations, locale]
    );
    const removeKoodiFromRelations = useCallback(
        (uri: string, versio: number) => {
            const index = fieldArrayReturn?.fields.findIndex(
                (value) => value.koodiUri === uri && value.koodiVersio === versio
            );
            fieldArrayReturn && fieldArrayReturn.remove(index);
        },
        [fieldArrayReturn]
    );
    const addNewKoodiToRelations = useCallback(
        (koodiList: KoodiList[]) => {
            fieldArrayReturn &&
                fieldArrayReturn.replace(
                    uniqWith(
                        [
                            ...relations,
                            ...koodiList.map((a: KoodiList) => ({
                                koodistoNimi: {
                                    fi: a.koodistoNimi || '',
                                    sv: a.koodistoNimi || '',
                                    en: a.koodistoNimi || '',
                                },
                                nimi: metadataToMultiLocaleText(a.metadata, 'nimi'),
                                kuvaus: metadataToMultiLocaleText(a.metadata, 'kuvaus'),
                                koodiUri: a.koodiUri,
                                koodiVersio: a.versio,
                            })),
                        ],
                        (a, b) => a.koodiUri === b.koodiUri && a.koodiVersio === b.koodiVersio
                    )
                );
        },
        [fieldArrayReturn, relations]
    );
    const columns = useMemo<ColumnDef<KoodiRelation>[]>(() => {
        const resolveName = (relation: KoodiRelation) =>
            translateMultiLocaleText({
                multiLocaleText: relation.koodistoNimi,
                locale,
                defaultValue: relation.koodistoNimi?.fi || '',
            });
        return [
            {
                header: formatMessage({ id: 'TAULUKKO_KOODISTO_OTSIKKO', defaultMessage: 'Koodisto' }),
                columns: [
                    {
                        id: 'koodisto',
                        header: '',
                        accessorFn: (relation: KoodiRelation) => {
                            const label = resolveName(relation);
                            return {
                                label,
                                value: label,
                            };
                        },
                        sortingFn: (a: Row<KoodiRelation>, b: Row<KoodiRelation>) =>
                            resolveName(a.original).localeCompare(resolveName(b.original)),
                        filterFn: (row, columnId, value: SelectOptionType[]) =>
                            !!value.find((a) => a.value === (row.getValue(columnId) as SelectOptionType).value) ||
                            value.length === 0,
                        cell: (koodisto) => koodisto.getValue().label,
                    },
                ],
            },
            {
                header: formatMessage({ id: 'TAULUKKO_NIMI_OTSIKKO', defaultMessage: 'Nimi' }),
                columns: [
                    {
                        id: 'nimi',
                        header: '',
                        accessorFn: (relation: KoodiRelation) =>
                            translateMultiLocaleText({
                                multiLocaleText: relation.nimi,
                                locale,
                                defaultValue: relation.koodiUri,
                            }),
                        cell: (info) => (
                            <Link to={`/koodi/view/${info.row.original.koodiUri}/${info.row.original.koodiVersio}`}>
                                {info.getValue()}
                            </Link>
                        ),
                        meta: {
                            filterPlaceHolder: {
                                id: 'KOODI_RELAATIO_TAULUKKO_HAKU_APUTEKSTI',
                                defaultMessage: 'Hae nimellä',
                            },
                        },
                    },
                ],
            },
            {
                header: formatMessage({ id: 'TAULUKKO_VERSIO_OTSIKKO', defaultMessage: 'Versio' }),
                columns: [
                    {
                        id: 'versio',
                        enableColumnFilter: false,
                        header: '',
                        accessorFn: (relation: KoodiRelation) => relation.koodiVersio,
                    },
                ],
            },
            {
                header: formatMessage({ id: 'TAULUKKO_VERSIO_KUVAUS', defaultMessage: 'Kuvaus' }),
                columns: [
                    {
                        id: 'kuvaus',
                        enableColumnFilter: false,
                        header: '',
                        accessorFn: (relation: KoodiRelation) =>
                            translateMultiLocaleText({
                                multiLocaleText: relation.kuvaus,
                                locale,
                                defaultValue: relation.koodiUri,
                            }),
                    },
                ],
            },
            ...((editable && [
                {
                    header: '',
                    id: 'poista',
                    columns: [
                        {
                            id: 'poista',
                            header: '',
                            enableColumnFilter: false,
                            cell: (info: CellContext<KoodiRelation, never>) => (
                                <RemoveSuhdeButton
                                    onClick={() =>
                                        removeKoodiFromRelations(
                                            info.row.original.koodiUri,
                                            info.row.original.koodiVersio
                                        )
                                    }
                                />
                            ),
                        },
                    ],
                },
            ]) ||
                []),
        ];
    }, [editable, formatMessage, locale, removeKoodiFromRelations]);

    const suhdeModal = useCallback(
        (close) => (
            <KoodiSuhdeModal
                relationSources={relationSources || []}
                save={addNewKoodiToRelations}
                close={() => {
                    close();
                }}
            />
        ),
        [addNewKoodiToRelations, relationSources]
    );
    return (
        <>
            <Table<KoodiRelation> columns={columns} data={data} pageSize={20} />{' '}
            {editable && (
                <ModalPopup
                    trigger={
                        <Button
                            disabled={relationSources.length === 0}
                            name={'TAULUKKO_LISAA_KOODISUHTEITA_BUTTON'}
                            variant={'text'}
                        >
                            <ButtonLabelPrefix>
                                <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                            </ButtonLabelPrefix>
                            <FormattedMessage id={'TAULUKKO_LISAA_KOODEJA_BUTTON'} defaultMessage={'Lisää koodeja'} />
                        </Button>
                    }
                >
                    {suhdeModal}
                </ModalPopup>
            )}
        </>
    );
};
