import React, { useMemo } from 'react';
import { Column, Row } from 'react-table';
import { useIntl } from 'react-intl';
import { Link } from 'react-router-dom';
import { Table } from '../../components/Table';
import { translateMultiLocaleText } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLocaleAtom } from '../../api/kayttooikeus';
import type { KoodiRelation } from '../../types';

export const RelationTable: React.FC<{ relations: KoodiRelation[] }> = ({
    relations,
}: {
    relations: KoodiRelation[];
}) => {
    const { formatMessage } = useIntl();
    const [locale] = useAtom(casMeLocaleAtom);
    const data = useMemo<KoodiRelation[]>(() => {
        return [...relations];
    }, [relations]);
    const columns = useMemo<Column<KoodiRelation>[]>(
        () => [
            {
                id: 'koodisto',
                Header: formatMessage({ id: 'TAULUKKO_KOODISTO_OTSIKKO', defaultMessage: 'Koodisto' }),
                accessor: (relation: KoodiRelation) =>
                    translateMultiLocaleText({
                        multiLocaleText: relation.koodistoNimi,
                        locale,
                        defaultValue: relation.koodistoNimi.fi,
                    }),
            },
            {
                id: 'nimi',
                Header: formatMessage({ id: 'TAULUKKO_NIMI_OTSIKKO', defaultMessage: 'Nimi' }),
                Cell: ({ row }: { row: Row<KoodiRelation> }) => (
                    <Link to={`/koodi/view/${row.original.koodiUri}/${row.original.koodiVersio}`}>
                        {translateMultiLocaleText({
                            multiLocaleText: row.original.nimi,
                            locale,
                            defaultValue: row.original.koodiUri,
                        })}
                    </Link>
                ),
            },
            {
                id: 'versio',
                Header: formatMessage({ id: 'TAULUKKO_VERSIO_OTSIKKO', defaultMessage: 'Versio' }),
                accessor: (relation: KoodiRelation) => relation.koodiVersio,
            },
            {
                id: 'kuvaus',
                Header: formatMessage({ id: 'TAULUKKO_VERSIO_KUVAUS', defaultMessage: 'Kuvaus' }),
                accessor: (relation: KoodiRelation) =>
                    translateMultiLocaleText({
                        multiLocaleText: relation.kuvaus,
                        locale,
                        defaultValue: relation.koodiUri,
                    }),
            },
        ],
        [formatMessage, locale]
    );
    return <Table<KoodiRelation> columns={columns} data={data} />;
};
