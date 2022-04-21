import * as React from 'react';
import { useMemo, useState } from 'react';
import { useAtom } from 'jotai';
import { Column, FilterProps, Row } from 'react-table';
import { Koodisto, koodistoAtom } from '../../../api/koodisto';
import { FormattedDate, FormattedMessage, useIntl } from 'react-intl';
import { ButtonLabelPrefix, HeaderContainer } from './KoodistoTablePage';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import IconWrapper from '../../IconWapper/IconWrapper';
import downloadCsv from '../../../utils/downloadCsv';
import styled from 'styled-components';
import { Link } from 'react-router-dom';
import Input from '@opetushallitus/virkailija-ui-components/Input';
import Select from '@opetushallitus/virkailija-ui-components/Select';
import { ValueType } from 'react-select';
import TableComponent from '../../Table/TableComponent';
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

const SelectContainer = styled.div`
    min-width: 17rem;
    margin-bottom: 1rem;
    margin-right: 1rem;
`;

const InputContainer = styled.div`
    max-width: 25rem;
    margin-bottom: 1rem;
`;

export type SelectOptionType = {
    value: string;
    label: string;
};

export const NimiColumnFilterComponent = <T extends Record<string, unknown>>({
    column: { filterValue, preFilteredRows, setFilter },
}: FilterProps<T>) => {
    const count = preFilteredRows.length;
    const { formatMessage } = useIntl();
    return (
        <InputContainer>
            <Input
                value={filterValue || ''}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    setFilter(e.target.value || undefined);
                }}
                placeholder={formatMessage(
                    {
                        id: 'TAULUKKO_VAKIO_FILTTERI',
                        defaultMessage: 'Haetaan {count} koodistosta',
                    },
                    { count }
                )}
            />
        </InputContainer>
    );
};

export const SelectColumnFilterComponent = <T extends Record<string, unknown>>({
    column: { filterValue, preFilteredRows, setFilter },
}: FilterProps<T>) => {
    const { formatMessage } = useIntl();

    const uniqueOptions = Array.from(
        new Map(preFilteredRows.map(({ values: { ryhmaTieto } }) => [ryhmaTieto.value, ryhmaTieto])).values()
    );
    return (
        <SelectContainer>
            <Select
                onChange={(values: ValueType<SelectOptionType>) => setFilter(values)}
                placeholder={formatMessage({
                    id: 'TAULUKKO_DROPDOWN_FILTTERI',
                    defaultMessage: 'Valitse Ryhmä listalta',
                })}
                isMulti={true}
                value={filterValue || []}
                options={uniqueOptions}
            />
        </SelectContainer>
    );
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
                Header: formatMessage({ id: 'TAULUKKO_KOODISTORYHMA_OTSIKKO', defaultMessage: 'Koodistoryhma' }),
                columns: [
                    {
                        id: 'ryhmaTieto',
                        accessor: (values: Koodisto) => ({ label: values.ryhmaNimi, value: values.ryhmaId }),
                        cell: (values: Koodisto) => {
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
                        Filter: SelectColumnFilterComponent,
                        filter: (rows, _columnIds: string[], filterValue: SelectOptionType[]) =>
                            rows.filter((row) =>
                                filterValue.length > 0
                                    ? filterValue
                                          .map((option: SelectOptionType) => option.value)
                                          .includes(row.values.ryhmaTieto.value)
                                    : rows
                            ),
                        Cell: ({ value }: { value: SelectOptionType }) => <Link to="/">{value.label}</Link>,
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_NIMI_OTSIKKO', defaultMessage: 'Nimi' }),
                columns: [
                    {
                        id: 'nimi',
                        accessor: (values: Koodisto) => {
                            return (
                                values.nimi ||
                                formatMessage(
                                    {
                                        id: 'TAULUKKO_NIMI_PUUTTUU_KOODISTOLTA',
                                        defaultMessage: 'Nimi puuttuu {koodistoUri}',
                                    },
                                    { koodistoUri: values.koodistoUri }
                                )
                            );
                        },
                        Filter: NimiColumnFilterComponent,
                        filter: 'text',
                        Cell: ({ value }: { value: string }) => <Link to="/">{value}</Link>,
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_VOIMASSA_ALKU_PVM_OTSIKKO', defaultMessage: 'Voimassa alkaen' }),
                columns: [
                    {
                        id: 'voimassaAlkuPvm',
                        accessor: (values) => {
                            return values.voimassaAlkuPvm && <FormattedDate value={values.voimassaAlkuPvm} />;
                        },
                    },
                ],
            },
            {
                Header: formatMessage({ id: 'TAULUKKO_VOIMASSA_LOPPU_PVM_OTSIKKO', defaultMessage: 'Voimassa asti' }),
                columns: [
                    {
                        id: 'voimassaLoppuPvm',
                        accessor: (values: Koodisto) => {
                            return values.voimassaLoppuPvm && <FormattedDate value={values.voimassaLoppuPvm} />;
                        },
                    },
                ],
            },
            {
                id: 'downloadCsv',
                Header: '',
                accessor: (values: Koodisto) => {
                    return (
                        <IconWrapper
                            name={`${values.koodistoUri}-uploadicon`}
                            icon="el:download"
                            inline={true}
                            onClick={() => downloadCsv(values.koodistoUri)}
                        />
                    );
                },
            },
            {
                id: 'uploadCsv',
                Header: '',
                accessor: (values: Koodisto) => {
                    return <IconWrapper icon="el:upload" inline={true} onClick={() => uploadCsv(values.koodistoUri)} />;
                },
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
            <TableComponent<Koodisto>
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
