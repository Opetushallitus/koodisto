import * as React from 'react';
import { useMemo } from 'react';
import { Column, Row } from 'react-table';
import { KoodistoRelation } from '../../api/koodisto';
import { useIntl } from 'react-intl';
import Table from '../../components/Table/Table';
import { Link } from 'react-router-dom';

type KoodistoRelationsTableProps = {
    koodistoRelations: KoodistoRelation[];
};

const KoodistoRelationsTable: React.FC<KoodistoRelationsTableProps> = ({ koodistoRelations }) => {
    const { formatMessage, locale } = useIntl();
    const data = useMemo<KoodistoRelation[]>(() => {
        return [...koodistoRelations];
    }, [koodistoRelations]);
    const columns = React.useMemo<Column<KoodistoRelation>[]>(
        () => [
            {
                id: 'nimi',
                Header: formatMessage({ id: 'TAULUKKO_KOODISTO_OTSIKKO', defaultMessage: 'Koodisto' }),
                Cell: ({ row }: { row: Row<KoodistoRelation> }) => (
                    <Link to={`/koodisto/${row.original.codesUri}/${row.original.codesVersion}`}>
                        {row.original.nimi[locale as keyof typeof row.original.nimi] || row.original.nimi['fi']}
                    </Link>
                ),
            },
            {
                id: 'codesVersion',
                Header: formatMessage({ id: 'TAULUKKO_VERSIO_OTSIKKO', defaultMessage: 'Versio' }),
                accessor: 'codesVersion',
            },
            {
                id: 'kuvaus',
                Header: formatMessage({ id: 'TAULUKKO_VERSIO_KUVAUS', defaultMessage: 'Kuvaus' }),
                accessor: (relation: KoodistoRelation) =>
                    relation.kuvaus[locale as keyof typeof relation.kuvaus] || relation.kuvaus['fi'],
            },
        ],
        [formatMessage, locale]
    );

    return <Table<KoodistoRelation> columns={columns} data={data} />;
};
export default KoodistoRelationsTable;
