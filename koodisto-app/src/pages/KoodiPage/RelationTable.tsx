import React, { useMemo } from 'react';
import { useIntl } from 'react-intl';
import { Link } from 'react-router-dom';
import { Table } from '../../components/Table';
import { translateMultiLocaleText } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLocaleAtom } from '../../api/kayttooikeus';
import type { KoodiRelation } from '../../types';
import { ColumnDef } from '@tanstack/react-table';

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
    const columns = useMemo<ColumnDef<KoodiRelation>[]>(
        () => [
            {
                id: 'koodisto',
                header: formatMessage({ id: 'TAULUKKO_KOODISTO_OTSIKKO', defaultMessage: 'Koodisto' }),
                accessorFn: (relation: KoodiRelation) =>
                    translateMultiLocaleText({
                        multiLocaleText: relation.koodistoNimi,
                        locale,
                        defaultValue: relation.koodistoNimi.fi,
                    }),
            },
            {
                id: 'nimi',
                header: formatMessage({ id: 'TAULUKKO_NIMI_OTSIKKO', defaultMessage: 'Nimi' }),
                cell: (info) => (
                    <Link to={`/koodi/view/${info.row.original.koodiUri}/${info.row.original.koodiVersio}`}>
                        {translateMultiLocaleText({
                            multiLocaleText: info.row.original.nimi,
                            locale,
                            defaultValue: info.row.original.koodiUri,
                        })}
                    </Link>
                ),
            },
            {
                id: 'versio',
                header: formatMessage({ id: 'TAULUKKO_VERSIO_OTSIKKO', defaultMessage: 'Versio' }),
                accessorFn: (relation: KoodiRelation) => relation.koodiVersio,
            },
            {
                id: 'kuvaus',
                header: formatMessage({ id: 'TAULUKKO_VERSIO_KUVAUS', defaultMessage: 'Kuvaus' }),
                accessorFn: (relation: KoodiRelation) =>
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
