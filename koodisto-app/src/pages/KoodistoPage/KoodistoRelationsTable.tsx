import * as React from 'react';
import { useMemo, useCallback } from 'react';
import { useIntl, FormattedMessage } from 'react-intl';
import { Table } from '../../components/Table';
import { Link } from 'react-router-dom';
import { sortBy } from 'lodash';
import { KoodistoRelation, ListKoodisto, PageKoodisto, Locale } from '../../types';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { IconWrapper } from '../../components/IconWapper';
import { KoodistoSuhdeModal } from './KoodistoSuhdeModal';
import { ColumnDef, CellContext } from '@tanstack/react-table';
import { UseFieldArrayReturn } from 'react-hook-form';
import { StyledPopup } from '../../components/Modal/Modal';
import { ButtonLabelPrefix } from '../../components/Containers';

type KoodistoRelationsTableProps = {
    koodistoRelations: KoodistoRelation[];
    editable: boolean;
    fieldArrayReturn?: UseFieldArrayReturn<PageKoodisto>;
};

const RemoveSuhdeButton: React.FC<{ onClick: (e: React.ChangeEvent<HTMLInputElement>) => void }> = ({ onClick }) => {
    return (
        <Button name={'TAULUKKO_POISTA_KOODISTOSUHTEITA_BUTTON'} onClick={onClick} variant={'text'}>
            <ButtonLabelPrefix>
                <IconWrapper icon={'ci:trash-full'} inline={true} height={'1.2rem'} />
            </ButtonLabelPrefix>
        </Button>
    );
};

export const KoodistoRelationsTable: React.FC<KoodistoRelationsTableProps> = ({
    koodistoRelations,
    editable,
    fieldArrayReturn,
}) => {
    const { formatMessage, locale } = useIntl();
    const data = useMemo<KoodistoRelation[]>(
        () =>
            sortBy(
                [...koodistoRelations],
                (koodistoRelation) => koodistoRelation.nimi?.[locale as Locale] || koodistoRelation.koodistoUri
            ),
        [koodistoRelations, locale]
    );

    const removeKoodistoFromRelations = useCallback(
        (index: number) => {
            fieldArrayReturn && fieldArrayReturn.remove(index);
        },
        [fieldArrayReturn]
    );
    const addNewKoodistoToRelations = useCallback(
        (koodisto: ListKoodisto[]) => {
            fieldArrayReturn &&
                fieldArrayReturn.replace([
                    ...koodistoRelations,
                    ...koodisto.map((a) => ({
                        koodistoUri: a.koodistoUri,
                        koodistoVersio: a.versio,
                        nimi: { fi: a.nimi || '', sv: a.nimi || '', en: a.nimi || '' },
                        kuvaus: { fi: a.kuvaus || '', sv: a.kuvaus || '', en: a.kuvaus || '' },
                        passive: false,
                    })),
                ]);
        },
        [fieldArrayReturn, koodistoRelations]
    );
    const columns = React.useMemo<ColumnDef<KoodistoRelation>[]>(
        () => [
            {
                header: formatMessage({ id: 'TAULUKKO_KOODISTO_OTSIKKO', defaultMessage: 'Koodisto' }),
                columns: [
                    {
                        id: 'nimi',
                        cell: (info) => (
                            <Link
                                to={`/koodisto/view/${info.row.original.koodistoUri}/${info.row.original.koodistoVersio}`}
                            >
                                {info.row.original.nimi?.[locale as keyof typeof info.row.original.nimi] ||
                                    info.row.original.nimi?.['fi']}
                            </Link>
                        ),
                    },
                ],
            },
            {
                header: formatMessage({ id: 'TAULUKKO_VERSIO_OTSIKKO', defaultMessage: 'Versio' }),
                columns: [
                    {
                        id: 'koodistoVersio',
                        header: '',
                        enableColumnFilter: false,
                        accessorKey: 'koodistoVersio',
                    },
                ],
            },
            {
                header: formatMessage({ id: 'TAULUKKO_KUVAUS_OTSIKKO', defaultMessage: 'Kuvaus' }),
                columns: [
                    {
                        id: 'kuvaus',
                        header: '',
                        enableColumnFilter: false,
                        accessorFn: (relation: KoodistoRelation) =>
                            relation.kuvaus?.[locale as keyof typeof relation.kuvaus] || relation.kuvaus?.['fi'],
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
                            cell: (info: CellContext<KoodistoRelation, never>) => (
                                <RemoveSuhdeButton onClick={() => removeKoodistoFromRelations(info.row.index)} />
                            ),
                        },
                    ],
                },
            ]) ||
                []),
        ],
        [editable, formatMessage, locale, removeKoodistoFromRelations]
    );

    return (
        <>
            <Table<KoodistoRelation> columns={columns} data={data} pageSize={10}></Table>
            {editable && (
                <StyledPopup
                    trigger={
                        <Button name={'TAULUKKO_LISAA_KOODISTOSUHTEITA_BUTTON'} variant={'text'}>
                            <ButtonLabelPrefix>
                                <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                            </ButtonLabelPrefix>
                            <FormattedMessage
                                id={'TAULUKKO_LISAA_KOODISTOJA_BUTTON'}
                                defaultMessage={'Lisää koodistoja'}
                            />
                        </Button>
                    }
                    modal
                >
                    {
                        ((close) => (
                            <KoodistoSuhdeModal
                                oldRelations={koodistoRelations}
                                save={(a) => addNewKoodistoToRelations(a)}
                                close={close}
                            />
                        )) as (close: () => void) => React.ReactNode
                    }
                </StyledPopup>
            )}
        </>
    );
};
