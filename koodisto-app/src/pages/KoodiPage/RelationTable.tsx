import React, { useMemo } from 'react';
import { Column, Row } from 'react-table';
import { useIntl } from 'react-intl';
import { Link } from 'react-router-dom';
import { Table } from '../../components/Table';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import type { PageKoodiRelation } from '../../types';

export const RelationTable: React.FC<{ relations: PageKoodiRelation[] }> = ({
    relations,
}: {
    relations: PageKoodiRelation[];
}) => {
    const { formatMessage } = useIntl();
    const [lang] = useAtom(casMeLangAtom);
    const data = useMemo<PageKoodiRelation[]>(() => {
        return [...relations];
    }, [relations]);
    const columns = useMemo<Column<PageKoodiRelation>[]>(
        () => [
            {
                id: 'koodisto',
                Header: formatMessage({ id: 'TAULUKKO_KOODISTO_OTSIKKO', defaultMessage: 'Koodisto' }),
                accessor: (relation: PageKoodiRelation) =>
                    translateMetadata({ metadata: relation.parentMetadata, lang })?.nimi,
            },
            {
                id: 'nimi',
                Header: formatMessage({ id: 'TAULUKKO_NIMI_OTSIKKO', defaultMessage: 'Nimi' }),
                Cell: ({ row }: { row: Row<PageKoodiRelation> }) => (
                    <Link to={`/koodi/${row.original.codeElementUri}/${row.original.codeElementVersion}`}>
                        {translateMetadata({ metadata: row.original.relationMetadata, lang })?.nimi}
                    </Link>
                ),
            },
            {
                id: 'versio',
                Header: formatMessage({ id: 'TAULUKKO_VERSIO_OTSIKKO', defaultMessage: 'Versio' }),
                accessor: (relation: PageKoodiRelation) => relation.codeElementVersion,
            },
            {
                id: 'kuvaus',
                Header: formatMessage({ id: 'TAULUKKO_VERSIO_KUVAUS', defaultMessage: 'Kuvaus' }),
                accessor: (relation: PageKoodiRelation) =>
                    relation.relationMetadata.find((x) => x.kieli === lang)?.kuvaus || '-',
            },
        ],
        [formatMessage, lang]
    );
    return <Table<PageKoodiRelation> columns={columns} data={data} />;
};
