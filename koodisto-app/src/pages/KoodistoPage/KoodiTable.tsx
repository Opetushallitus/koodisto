import React, { useMemo } from 'react';
import { Koodi } from '../../types';
import { Column, Row } from 'react-table';
import { useIntl, FormattedDate } from 'react-intl';
import { Table } from '../../components/Table';
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
                id: 'koodiarvo',
                Header: formatMessage({ id: 'TAULUKKO_KOODI_KOODIARVO', defaultMessage: 'Koodiarvo' }),
                Cell: ({ row }: { row: Row<Koodi> }) => {
                    return <div>{row.original.koodiArvo}</div>;
                },
            },
            {
                id: 'versio',
                Header: formatMessage({ id: 'TAULUKKO_KOODI_VERSIO', defaultMessage: 'Versio' }),
                Cell: ({ row }: { row: Row<Koodi> }) => {
                    return <div>{row.original.versio}</div>;
                },
            },
            {
                id: 'nimi',
                Header: formatMessage({ id: 'TAULUKKO_KOODI_NIMI', defaultMessage: 'Nimi' }),
                Cell: ({ row }: { row: Row<Koodi> }) => {
                    const metadata = translateMetadata({ metadata: row.original.metadata, lang });
                    return <div>{metadata?.nimi}</div>;
                },
            },
            {
                id: 'voimassa',
                Header: formatMessage({ id: 'TAULUKKO_KOODI_VOIMASSA', defaultMessage: 'Voimassa' }),
                Cell: ({ row }: { row: Row<Koodi> }) => {
                    return <FormattedDate value={row.original.voimassaAlkuPvm} />;
                },
            },
            {
                id: 'paivitetty',
                Header: formatMessage({ id: 'TAULUKKO_KOODI_PAIVITETTY', defaultMessage: 'Päivitetty' }),
                Cell: ({ row }: { row: Row<Koodi> }) => {
                    return <FormattedDate value={row.original.paivitysPvm} />;
                },
            },
        ],
        [formatMessage, lang]
    );
    return <Table<Koodi> columns={columns} data={data} />;
};
