import React, { useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { KoodiList } from '../../types';
import { useIntl, FormattedDate } from 'react-intl';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import { ColumnDef, CellContext } from '@tanstack/react-table';
import { Table } from './Table';
import { sortBy } from 'lodash';

type Props = {
    koodiList: KoodiList[];
    modal?: boolean;
    setSelected?: (selected: KoodiList[]) => void;
};
export const KoodiTable: React.FC<Props> = ({ koodiList, modal, setSelected }) => {
    const { formatMessage } = useIntl();
    const [lang] = useAtom(casMeLangAtom);
    const data = useMemo<KoodiList[]>(() => sortBy([...koodiList], (a) => a.koodiArvo), [koodiList]);
    const [, setFilteredCount] = useState<number>(data.length);

    const columns = React.useMemo<ColumnDef<KoodiList>[]>(
        () => [
            ...((!!modal && [
                {
                    header: formatMessage({ id: 'TAULUKKO_KOODISTORUI_NIMI', defaultMessage: 'Koodisto' }),
                    columns: [
                        {
                            id: 'koodistoUri',
                            cell: (info: CellContext<KoodiList, unknown>) => info.row.original.koodistoNimi || '',
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
                                {translateMetadata({ metadata: info.row.original.metadata, lang })?.nimi}
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
            modal
            columns={columns}
            data={data}
            setSelected={setSelected}
            onFilter={(rows) => setFilteredCount(rows.length)}
            pageSize={20}
        />
    );
};
