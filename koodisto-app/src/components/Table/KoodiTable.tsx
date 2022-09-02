import React, { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { KoodiList, Kieli, SelectOptionType } from '../../types';
import { useIntl, FormattedDate } from 'react-intl';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import { ColumnDef, CellContext, Row } from '@tanstack/react-table';
import { Table } from './Table';
import { sortBy } from 'lodash';

type Props = {
    koodiList: KoodiList[];
    modal?: boolean;
    setSelected?: (selected: KoodiList[]) => void;
};

const resolveName = (koodi: KoodiList, lang: Kieli): string =>
    translateMetadata({ metadata: koodi.metadata, lang })?.nimi || koodi.koodiUri;

export const KoodiTable: React.FC<Props> = ({ koodiList, modal, setSelected }) => {
    const { formatMessage } = useIntl();
    const [lang] = useAtom(casMeLangAtom);
    const data = useMemo<KoodiList[]>(
        () => sortBy([...koodiList], (koodi) => resolveName(koodi, lang)),
        [koodiList, lang]
    );
    const [, setFilteredCount] = useState<number>(data.length);

    const columns = React.useMemo<ColumnDef<KoodiList>[]>(
        () => [
            ...((!!modal && [
                {
                    header: formatMessage({ id: 'TAULUKKO_KOODISTO_OTSIKKO', defaultMessage: 'Koodisto' }),
                    columns: [
                        {
                            id: 'koodistoUri',
                            header: '',
                            accessorFn: (item: KoodiList) => ({
                                label: item.koodistoNimi || item.koodistoUri,
                                value: item.koodistoUri,
                            }),
                            filterFn: (row: Row<KoodiList>, columnId: string, value: SelectOptionType[]) =>
                                !!value.find((a) => a.value === (row.getValue(columnId) as SelectOptionType).value) ||
                                value.length === 0,
                            cell: (koodisto: CellContext<KoodiList, SelectOptionType>) => koodisto.getValue().label,
                        },
                    ],
                },
            ]) ||
                []),
            {
                header: formatMessage({ id: 'TAULUKKO_KOODI_NIMI', defaultMessage: 'Nimi' }),
                columns: [
                    {
                        id: 'nimi',
                        header: '',
                        enableColumnFilter: true,
                        filterFn: (row, _columnId, value) => {
                            return (
                                row.original.koodiArvo.toLowerCase().includes(value.toLowerCase()) ||
                                row.original.metadata.find((a) => a.nimi.toLowerCase().includes(value.toLowerCase())) ||
                                value.length === 0
                            );
                        },
                        meta: {
                            filterPlaceHolder: formatMessage({
                                id: 'KOODI_TAULUKKO_FILTTERI_PLACEHOLDER',
                                defaultMessage: 'Hae nimellä tai koodiarvolla',
                            }),
                        },
                        accessorFn: (values: KoodiList) => values.koodiArvo,
                        cell: (info) => (
                            <Link to={`/koodi/view/${info.row.original.koodiUri}/${info.row.original.versio}`}>
                                {resolveName(info.row.original, lang)}
                            </Link>
                        ),
                    },
                ],
            },
            {
                header: formatMessage({ id: 'TAULUKKO_KOODI_KOODIARVO', defaultMessage: 'Koodiarvo' }),
                columns: [
                    {
                        id: 'koodiarvo',
                        header: '',
                        cell: (info) => <div>{info.row.original.koodiArvo}</div>,
                    },
                ],
            },
            {
                header: formatMessage({ id: 'TAULUKKO_KOODI_VERSIO', defaultMessage: 'Versio' }),
                columns: [
                    {
                        id: 'versio',
                        cell: (info) => <div>{info.row.original.versio}</div>,
                    },
                ],
            },
            ...((!modal && [
                {
                    header: formatMessage({ id: 'TAULUKKO_KOODI_VOIMASSA', defaultMessage: 'Voimassa' }),
                    columns: [
                        {
                            id: 'voimassa',
                            cell: (info: CellContext<KoodiList, unknown>) => (
                                <FormattedDate value={info.row.original.voimassaAlkuPvm} />
                            ),
                        },
                    ],
                },
                {
                    header: formatMessage({ id: 'TAULUKKO_KOODI_PAIVITETTY', defaultMessage: 'Päivitetty' }),
                    columns: [
                        {
                            id: 'paivitetty',
                            cell: (info: CellContext<KoodiList, unknown>) => (
                                <FormattedDate value={info.row.original.paivitysPvm} />
                            ),
                        },
                    ],
                },
            ]) ||
                []),
        ],
        [formatMessage, lang, modal]
    );

    return (
        <Table<KoodiList>
            modal={modal}
            columns={columns}
            data={data}
            setSelected={setSelected}
            onFilter={(rows) => setFilteredCount(rows.length)}
            pageSize={20}
        />
    );
};
