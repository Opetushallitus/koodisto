import * as React from 'react';
import styled from 'styled-components';
import { Cell, Column, FilterProps, HeaderGroup, Row, useFilters, useTable } from 'react-table';
import { useEffect } from 'react';
import { useIntl } from 'react-intl';
import Input from '@opetushallitus/virkailija-ui-components/Input';
import Select from '@opetushallitus/virkailija-ui-components/Select';
import { ValueType } from 'react-select';
import { SelectOptionType } from '../pages/KoodistoTable/KoodistoTable';

const TableContainer = styled.div`
    overflow-x: scroll;
    overflow-y: hidden;
    padding: 1rem;
    border: 1px solid #cccccc;
`;
const TableElement = styled.table`
    width: 100%;
    min-height: 60vh;
    border-spacing: 0;
`;
const Th = styled.th`
    text-align: left;
`;
const Td = styled.td`
    font-family: Roboto, Arial Unicode MS, Arial, sans-serif;
    font-weight: 400;
    color: #0a789c;
`;
const Tr = styled.tr``;
const Thead = styled.thead`
    ${Tr}:first-child ${Th} {
        border-bottom: 1px solid rgba(151, 151, 151, 0.5);
    }
`;

const Tbody = styled.tbody`
    ${Tr}:nth-child(even) {
        background: rgba(245, 245, 245, 1);
    }

    ${Tr}:nth-child(odd) {
        background: rgba(255, 255, 255, 0);
    }
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

type TableProps<T extends object> = {
    columns: Column<T>[];
    data: T[];
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
    column: { filterValue, preFilteredRows, setFilter, id },
}: FilterProps<T>) => {
    const { formatMessage } = useIntl();
    const uniqueOptions = Array.from(
        new Map(preFilteredRows.map(({ values }) => [values[id].value, values[id]])).values()
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

const Table = <T extends object>({
    columns,
    data,
    onFilter,
}: TableProps<T> & { onFilter?: (rows: Row<T>[]) => void }) => {
    const defaultColumn = React.useMemo(
        () => ({
            Filter: <></>,
        }),
        []
    );
    const { filteredRows, getTableProps, getTableBodyProps, headerGroups, rows, prepareRow } = useTable<T>(
        { columns, data, defaultColumn },
        useFilters
    );
    useEffect(() => {
        onFilter && onFilter(filteredRows);
    }, [filteredRows, onFilter]);
    return (
        <TableContainer>
            <TableElement {...getTableProps()}>
                <Thead>
                    {headerGroups.map((headerGroup: HeaderGroup<T>) => (
                        // eslint-disable-next-line react/jsx-key
                        <Tr {...headerGroup.getHeaderGroupProps()}>
                            {headerGroup.headers.map((column: HeaderGroup<T>) => (
                                // eslint-disable-next-line react/jsx-key
                                <Th {...column.getHeaderProps()}>
                                    {column.render('Header')}
                                    <div>{column.canFilter ? column.render('Filter') : null}</div>
                                </Th>
                            ))}
                        </Tr>
                    ))}
                </Thead>
                <Tbody {...getTableBodyProps()}>
                    {rows.map((row: Row<T>) => {
                        prepareRow(row);
                        return (
                            // eslint-disable-next-line react/jsx-key
                            <Tr {...row.getRowProps()}>
                                {row.cells.map((cell: Cell<T>) => {
                                    // eslint-disable-next-line react/jsx-key
                                    return <Td {...cell.getCellProps()}>{cell.render('Cell')}</Td>;
                                })}
                            </Tr>
                        );
                    })}
                </Tbody>
            </TableElement>
        </TableContainer>
    );
};

export default Table;
