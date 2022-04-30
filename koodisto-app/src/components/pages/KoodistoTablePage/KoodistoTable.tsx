import * as React from 'react';
import { useMemo, useState } from 'react';
import { useAtom } from 'jotai';
import { Column, Row } from 'react-table';
import { koodistoAtom, TablePageKoodisto } from '../../../api/koodisto';
import { FormattedDate, FormattedMessage, useIntl } from 'react-intl';
import { ButtonLabelPrefix, HeaderContainer } from './KoodistoTablePage';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import IconWrapper from '../../IconWapper/IconWrapper';
import downloadCsv from '../../../utils/downloadCsv';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import Table, { TextFilterComponent, SelectFilterComponent } from '../../Table/Table';
type KoodistoTableProps = {
    handleLisaaKoodistoRyhma: () => void;
};
const uploadCsv = (koodistoUri: string) => {
    console.info(koodistoUri);
};

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

export type SelectOptionType = {
    value: string;
    label: string;
};

const KoodistoTable: React.FC<KoodistoTableProps> = ({ handleLisaaKoodistoRyhma }) => {
    const [atomData] = useAtom(koodistoAtom);
    const { formatMessage } = useIntl();
    const data = useMemo<TablePageKoodisto[]>(() => {
        atomData.sort((a, b) => a.koodistoUri.localeCompare(b.koodistoUri));
        return [...atomData];
    }, [atomData]);
    const [filteredRows, setFilteredRows] = useState<Row<TablePageKoodisto>[]>([]);
    const columns = React.useMemo<Column<TablePageKoodisto>[]>(
        () => [
            {
                Header: formatMessage({ id: 'TAULUKKO_KOODISTORYHMA_OTSIKKO', defaultMessage: 'Koodistoryhma' }),
                columns: [
                    {
                        id: 'ryhmaTieto',
                        accessor: (values: TablePageKoodisto) => ({ label: values.ryhmaNimi, value: values.ryhmaId }),
                        Filter: SelectFilterComponent,
                        filter: (rows, _columnIds: string[], filterValue: SelectOptionType[]) =>
                            rows.filter((row) =>
                                filterValue.length > 0
                                    ? filterValue
                                          .map((option: SelectOptionType) => option.value)
                                          .includes(row.values.ryhmaTieto.value)
                                    : rows
                            ),
                        Cell: ({ value, row }: { value: SelectOptionType; row: Row<TablePageKoodisto> }) => (
                            <Link to={`koodistoRyhma/${row.original.ryhmaId}`}>{value.label}</Link>
                        ),
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_NIMI_OTSIKKO', defaultMessage: 'Nimi' }),
                columns: [
                    {
                        id: 'nimi',
                        accessor: (values: TablePageKoodisto) =>
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
                        Cell: ({ value, row }: { value: string; row: Row<TablePageKoodisto> }) => (
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
                        accessor: (values: TablePageKoodisto) =>
                            values.voimassaLoppuPvm && (
                                <TableCellText>
                                    <FormattedDate value={values.voimassaLoppuPvm} />
                                </TableCellText>
                            ),
                    },
                ],
            },
            {
                id: 'downloadCsv',
                Header: '',
                accessor: (values: TablePageKoodisto) => (
                    <TableCellText>
                        <IconWrapper
                            name={`${values.koodistoUri}-uploadicon`}
                            icon="el:download"
                            inline={true}
                            onClick={() => downloadCsv(values.koodistoUri)}
                        />
                    </TableCellText>
                ),
            },
            {
                id: 'uploadCsv',
                Header: '',
                accessor: (values: TablePageKoodisto) => (
                    <TableCellText>
                        <IconWrapper icon="el:upload" inline={true} onClick={() => uploadCsv(values.koodistoUri)} />
                    </TableCellText>
                ),
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
                        <FormattedMessage
                            id={'TAULUKKO_KUVAUS_OTSIKKO'}
                            defaultMessage={'Voit rajata koodistoryhmällä tai nimellä'}
                        />
                    </InfoText>
                </HeaderContentDivider>
                <Button onClick={handleLisaaKoodistoRyhma}>
                    <ButtonLabelPrefix>
                        <IconWrapper icon="el:plus" inline={true} fontSize={'0.6rem'} />
                    </ButtonLabelPrefix>
                    <FormattedMessage id={'TAULUKKO_LISAA_KOODISTO_BUTTON'} defaultMessage={'Luo uusi koodisto'} />
                </Button>
            </HeaderContainer>
            <Table<TablePageKoodisto>
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
