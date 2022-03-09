import * as React from 'react';
import { useMemo } from 'react';
import { useAtom } from 'jotai';
import { Column } from 'react-table';
import { Koodisto, koodistoAtom } from '../../../api/koodisto';
import Table, { DefaultColumnFilter } from '../../Table/Table';

const KoodistoTable = () => {
    const [atomData] = useAtom(koodistoAtom);
    const data = useMemo<Koodisto[]>(() => {
        const sortedAtom = atomData;
        sortedAtom.sort((a, b) => a.koodistoUri.localeCompare(b.koodistoUri));
        return sortedAtom;
    }, [atomData]);
    const columns = React.useMemo<Column<Koodisto>[]>(
        () => [
            {
                id: 'koodistoUri',
                Header: 'koodistoUri',
                accessor: 'koodistoUri',
                Filter: DefaultColumnFilter,
                filter: 'text',
            },
            {
                id: 'versio',
                Header: 'versio',
                accessor: (values) => {
                    return values.latestKoodistoVersio.versio;
                },
            },
            {
                id: 'voimassaAlkuPvm',
                Header: 'voimassaAlkuPvm',
                accessor: (values) => {
                    return values.latestKoodistoVersio.voimassaAlkuPvm;
                },
            },
            {
                id: 'voimassaLoppuPvm',
                Header: 'voimassaLoppuPvm',
                accessor: (values) => {
                    return values.latestKoodistoVersio.voimassaLoppuPvm;
                },
            },
        ],
        []
    );

    return <Table<Koodisto> columns={columns} data={data} />;
};
export default KoodistoTable;
