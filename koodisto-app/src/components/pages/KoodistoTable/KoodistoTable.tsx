import * as React from 'react';
import { useMemo, useState } from 'react';
import { useAtom } from 'jotai';
import { Column, Row } from 'react-table';
import { Koodisto, koodistoAtom } from '../../../api/koodisto';
import Table, { DefaultColumnFilter } from '../../Table/Table';
import { FormattedDate, FormattedMessage, useIntl } from 'react-intl';
import { HeaderContainer } from '../../../App';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import IconWrapper from '../../IconWapper/IconWrapper';
type KoodistoTableProps = {
    handleLisaaKoodistoRyhma: () => void;
};
const KoodistoTable: React.FC<KoodistoTableProps> = ({ handleLisaaKoodistoRyhma }) => {
    const [atomData] = useAtom(koodistoAtom);
    const { formatMessage } = useIntl();
    const data = useMemo<Koodisto[]>(() => {
        const sortedAtom = atomData.map((a) => {
            return { ...a, name: 'foo' };
        });
        sortedAtom.sort((a, b) => a.koodistoUri.localeCompare(b.koodistoUri));
        return sortedAtom;
    }, [atomData]);
    const [filteredRows, setFilteredRows] = useState<Row<Koodisto>[]>([]);
    const columns = React.useMemo<Column<Koodisto>[]>(
        () => [
            {
                id: 'ryhmaNimi',
                Header: <FormattedMessage id={'TAULUKKO_KOODISTORYHMA_OTSIKKO'} defaultMessage={'KOODISTORYHMA'} />,
                accessor: (values: Koodisto) => {
                    return (
                        values.ryhmaNimi ||
                        formatMessage(
                            {
                                id: 'TAULUKKO_NIMI_PUUTTUU_KOODISTOLTA',
                                defaultMessage: 'NIMI PUUTTUU {koodistoUri}',
                            },
                            { koodistoUri: values.koodistoUri }
                        )
                    );
                },
                Filter: DefaultColumnFilter,
                filter: 'text',
            },
            {
                id: 'nimi',
                Header: <FormattedMessage id={'TAULUKKO_NIMI_OTSIKKO'} defaultMessage={'NIMI'} />,
                accessor: (values: Koodisto) => {
                    return (
                        values.nimi ||
                        formatMessage(
                            {
                                id: 'TAULUKKO_NIMI_PUUTTUU_KOODISTOLTA',
                                defaultMessage: 'NIMI PUUTTUU {koodistoUri}',
                            },
                            { koodistoUri: values.koodistoUri }
                        )
                    );
                },
                Filter: DefaultColumnFilter,
                filter: 'text',
            },
            {
                id: 'voimassaAlkuPvm',
                Header: (
                    <FormattedMessage id={'TAULUKKO_VOIMASSA_ALKU_PVM_OTSIKKO'} defaultMessage={'VOIMASSA ALKU PVM'} />
                ),
                accessor: (values) => {
                    return values.voimassaAlkuPvm && <FormattedDate value={values.voimassaAlkuPvm} />;
                },
            },
            {
                id: 'voimassaLoppuPvm',
                Header: (
                    <FormattedMessage
                        id={'TAULUKKO_VOIMASSA_LOPPU_PVM_OTSIKKO'}
                        defaultMessage={'VOIMASSA LOPPU PVM'}
                    />
                ),
                accessor: (values: Koodisto) => {
                    return values.voimassaLoppuPvm && <FormattedDate value={values.voimassaLoppuPvm} />;
                },
            },
        ],
        [formatMessage]
    );

    return (
        <>
            <HeaderContainer>
                <FormattedMessage
                    id={'TAULUKKO_OTSIKKO'}
                    defaultMessage={'OTSIKKO ({filteredCount} / {totalCount})'}
                    values={{ filteredCount: filteredRows.length, totalCount: data.length }}
                    tagName={'h1'}
                />
                <FormattedMessage id={'TAULUKKO_KUVAUS_OTSIKKO'} defaultMessage={'VOIT RAJATA HAKUA'} />
                <Button onClick={handleLisaaKoodistoRyhma}>
                    <IconWrapper icon="el:plus-sign" inline={true} />
                    <FormattedMessage
                        id={'TAULUKKO_LISAA_KOODISTORYHMA_BUTTON'}
                        defaultMessage={'LUO UUSI KOODISTORYHMA'}
                    />
                </Button>
                <Button>
                    <IconWrapper icon="el:plus-sign" inline={true} />
                    <FormattedMessage id={'TAULUKKO_LISAA_KOODISTO_BUTTON'} defaultMessage={'LUO UUSI KOODISTO'} />
                </Button>
            </HeaderContainer>
            <Table<Koodisto>
                columns={columns}
                data={data}
                onFilter={(rows) => {
                    setFilteredRows(rows);
                }}
            />
        </>
    );
};
export default KoodistoTable;
