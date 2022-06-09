import React, { useMemo, useState, useRef } from 'react';
import { useAtom } from 'jotai';
import { Column, Row } from 'react-table';
import { koodistoListAtom } from '../../api/koodisto';
import { FormattedDate, FormattedMessage, useIntl } from 'react-intl';
import { ButtonLabelPrefix, HeaderContainer } from './KoodistoTablePage';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { IconWrapper } from '../../components/IconWapper';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { Table, SelectFilterComponent, TextFilterComponent } from '../../components/Table';
import { ListKoodisto, SelectOptionType } from '../../types';

type KoodistoTableProps = unknown;

const HeaderContentDivider = styled.div`
    display: inline-flex;
    align-items: baseline;

    > * {
        &:first-child {
            padding-right: 1rem;
        }
    }
`;

const InfoText = styled.span`
    color: #666666;
`;

const TableCellText = styled.span`
    color: #0a789c;
`;

const KoodistoTable: React.FC<KoodistoTableProps> = () => {
    const [atomData] = useAtom(koodistoListAtom);
    const { formatMessage } = useIntl();
    const data = useMemo<ListKoodisto[]>(() => {
        atomData.sort((a, b) => a.koodistoUri.localeCompare(b.koodistoUri));
        return [...atomData];
    }, [atomData]);
    const [filteredRows, setFilteredRows] = useState<Row<ListKoodisto>[]>([]);
    const resetFilters = useRef<() => void | undefined>();
    const columns = React.useMemo<Column<ListKoodisto>[]>(
        () => [
            {
                Header: formatMessage({ id: 'TAULUKKO_KOODISTORYHMA_OTSIKKO', defaultMessage: 'Koodistoryhma' }),
                columns: [
                    {
                        id: 'ryhmaTieto',
                        accessor: (values: ListKoodisto) => ({ label: values.ryhmaNimi, value: values.ryhmaUri }),
                        Filter: SelectFilterComponent,
                        filter: (rows, _columnIds: string[], filterValue: SelectOptionType[]) =>
                            rows.filter((row) =>
                                filterValue.length > 0
                                    ? filterValue
                                          .map((option: SelectOptionType) => option.value)
                                          .includes(row.values.ryhmaTieto.value)
                                    : rows
                            ),
                        Cell: ({ value, row }: { value: SelectOptionType; row: Row<ListKoodisto> }) => (
                            <Link to={`/koodistoRyhma/${row.original.ryhmaUri}`}>{value.label}</Link>
                        ),
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_NIMI_OTSIKKO', defaultMessage: 'Nimi' }),
                columns: [
                    {
                        id: 'nimi',
                        accessor: (values: ListKoodisto) =>
                            values.nimi ||
                            formatMessage(
                                {
                                    id: 'TAULUKKO_NIMI_PUUTTUU_KOODISTOLTA',
                                    defaultMessage: 'Nimi puuttuu {koodistoUri}',
                                },
                                { koodistoUri: values.koodistoUri }
                            ),
                        Filter: TextFilterComponent,
                        filter: 'text',
                        Cell: ({ value, row }: { value: string; row: Row<ListKoodisto> }) => (
                            <Link to={`koodisto/${row.original.koodistoUri}/${row.original.versio}`}>{value}</Link>
                        ),
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_VOIMASSA_ALKU_PVM_OTSIKKO', defaultMessage: 'Voimassa alkaen' }),
                columns: [
                    {
                        id: 'voimassaAlkuPvm',
                        accessor: (values) =>
                            values.voimassaAlkuPvm && (
                                <TableCellText>
                                    <FormattedDate value={values.voimassaAlkuPvm} />
                                </TableCellText>
                            ),
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_VOIMASSA_LOPPU_PVM_OTSIKKO', defaultMessage: 'Voimassa asti' }),
                columns: [
                    {
                        id: 'voimassaLoppuPvm',
                        accessor: (values: ListKoodisto) =>
                            values.voimassaLoppuPvm && (
                                <TableCellText>
                                    <FormattedDate value={values.voimassaLoppuPvm} />
                                </TableCellText>
                            ),
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_KOODIMAARA', defaultMessage: 'Koodien lkm' }),
                columns: [
                    {
                        id: 'koodiCount',
                        accessor: (values: ListKoodisto) =>
                            values.koodiCount && <TableCellText>{values.koodiCount}</TableCellText>,
                    },
                ],
            },
        ],
        [formatMessage]
    );

    return (
        <>
            <HeaderContainer>
                <HeaderContentDivider>
                    <FormattedMessage
                        id={'TAULUKKO_OTSIKKO'}
                        defaultMessage={'Koodistot ({filteredCount} / {totalCount})'}
                        values={{ filteredCount: filteredRows.length, totalCount: data.length }}
                        tagName={'h2'}
                    />
                    <InfoText>
                        {resetFilters.current ? (
                            <Button id="resetFilters" variant="text" onClick={resetFilters.current}>
                                <FormattedMessage
                                    id={'TAULUKKO_NOLLAA_SUODATTIMET'}
                                    defaultMessage={'Tyhjennä hakuehdot'}
                                />
                            </Button>
                        ) : (
                            <FormattedMessage
                                id={'TAULUKKO_KUVAUS_OTSIKKO'}
                                defaultMessage={'Voit rajata koodistoryhmällä tai nimellä'}
                            />
                        )}
                    </InfoText>
                </HeaderContentDivider>
                <Button>
                    <ButtonLabelPrefix>
                        <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                    </ButtonLabelPrefix>
                    <FormattedMessage id={'TAULUKKO_LISAA_KOODISTO_BUTTON'} defaultMessage={'Luo uusi koodisto'} />
                </Button>
            </HeaderContainer>
            <Table<ListKoodisto>
                columns={columns}
                data={data}
                onFilter={(rows) => {
                    setFilteredRows(rows);
                }}
                resetFilters={resetFilters}
            />
        </>
    );
};
export default KoodistoTable;
