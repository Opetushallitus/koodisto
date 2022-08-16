import * as React from 'react';
import { useMemo, useState, useCallback } from 'react';
import { useIntl, FormattedMessage } from 'react-intl';
import { Table } from '../../components/Table';
import { Link } from 'react-router-dom';
import { KoodistoRelation, ListKoodisto, PageKoodisto } from '../../types';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { ButtonLabelPrefix } from '../KoodistoTablePage/KoodistoTablePage';
import { IconWrapper } from '../../components/IconWapper';
import { SuhdeModal } from './SuhdeModal';
import { ColumnDef, CellContext } from '@tanstack/react-table';
import { UseFieldArrayReturn } from 'react-hook-form';

type KoodistoRelationsTableProps = {
    koodistoRelations: KoodistoRelation[];
    editable: boolean;
    fieldArrayReturn?: UseFieldArrayReturn<PageKoodisto>;
};

const AddSuhdeButton: React.FC<{ onClick: (e: React.ChangeEvent<HTMLInputElement>) => void }> = ({ onClick }) => {
    return (
        <Button name={'TAULUKKO_LISAA_KOODISTOSUHTEITA_BUTTON'} onClick={onClick} variant={'text'}>
            <ButtonLabelPrefix>
                <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
            </ButtonLabelPrefix>
            <FormattedMessage id={'TAULUKKO_LISAA_KOODISTOJA_BUTTON'} defaultMessage={'Lisää koodistoja'} />
        </Button>
    );
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

const KoodistoRelationsTable: React.FC<KoodistoRelationsTableProps> = ({
    koodistoRelations,
    editable,
    fieldArrayReturn,
}) => {
    const { formatMessage, locale } = useIntl();

    const [showSuhdeModal, setShowSuhdeModal] = useState<boolean>(false);
    const showModal = (e: React.ChangeEvent<HTMLInputElement>) => {
        e.stopPropagation();
        setShowSuhdeModal(true);
    };
    const data = useMemo<KoodistoRelation[]>(() => {
        return [...koodistoRelations];
    }, [koodistoRelations]);

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
                        status: 'NEW' as const,
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
                            <Link to={`/koodisto/${info.row.original.koodistoUri}/${info.row.original.koodistoVersio}`}>
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
            <Table<KoodistoRelation> columns={columns} data={data}>
                {editable && <AddSuhdeButton onClick={showModal} />}
            </Table>
            {showSuhdeModal && (
                <SuhdeModal
                    oldRelations={koodistoRelations}
                    save={(a) => addNewKoodistoToRelations(a)}
                    close={() => setShowSuhdeModal(false)}
                />
            )}
        </>
    );
};
export default KoodistoRelationsTable;
