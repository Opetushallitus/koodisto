import React, { useMemo } from 'react';
import { Koodi } from '../../types';
import { Column, Row } from 'react-table';
import { useIntl, FormattedDate } from 'react-intl';
import { Table, getTextFilterComponent } from '../../components/Table';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';

type Props = { koodiList: Koodi[] };
export const KoodiTable: React.FC<Props> = ({ koodiList }) => {
    const { formatMessage } = useIntl();
    const [lang] = useAtom(casMeLangAtom);
    const data = useMemo<Koodi[]>(() => {
        koodiList.sort((a, b) => a.koodiArvo.localeCompare(b.koodiArvo));
        return [...koodiList];
    }, [koodiList]);

    const columns = React.useMemo<Column<Koodi>[]>(
        () => [
            {
                Header: formatMessage({ id: 'TAULUKKO_KOODI_KOODIARVO', defaultMessage: 'Koodiarvo' }),
                columns: [
                    {
                        id: 'koodiarvo',
                        accessor: (values: Koodi) =>
                            `${values.koodiArvo} ${translateMetadata({ metadata: values.metadata, lang })?.nimi || ''}`,

                        Cell: ({ row }: { row: Row<Koodi> }) => <div>{row.original.koodiArvo}</div>,
                        Filter: getTextFilterComponent({
                            id: 'KOODI_TAULUKKO_FILTTERI_PLACEHOLDER',
                            defaultMessage: 'Hae nimellä tai koodiarvolla',
                        }),
                        filter: 'text',
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_KOODI_VERSIO', defaultMessage: 'Versio' }),
                columns: [
                    {
                        id: 'versio',
                        Cell: ({ row }: { row: Row<Koodi> }) => <div>{row.original.versio}</div>,
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_KOODI_NIMI', defaultMessage: 'Nimi' }),
                columns: [
                    {
                        id: 'nimi',
                        Cell: ({ row }: { row: Row<Koodi> }) => (
                            <div>{translateMetadata({ metadata: row.original.metadata, lang })?.nimi}</div>
                        ),
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_KOODI_VOIMASSA', defaultMessage: 'Voimassa' }),
                columns: [
                    {
                        id: 'voimassa',
                        Cell: ({ row }: { row: Row<Koodi> }) => <FormattedDate value={row.original.voimassaAlkuPvm} />,
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_KOODI_PAIVITETTY', defaultMessage: 'Päivitetty' }),
                columns: [
                    {
                        id: 'paivitetty',
                        Cell: ({ row }: { row: Row<Koodi> }) => <FormattedDate value={row.original.paivitysPvm} />,
                    },
                ],
            },
        ],
        [formatMessage, lang]
    );

    return <Table<Koodi> columns={columns} data={data} />;
};
