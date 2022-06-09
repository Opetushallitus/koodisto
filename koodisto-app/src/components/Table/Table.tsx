import * as React from 'react';
import styled from 'styled-components';
import { Cell, Column, FilterProps, HeaderGroup, Row, useFilters, useTable } from 'react-table';
import { useEffect } from 'react';
import { useIntl, MessageDescriptor } from 'react-intl';
import Input from '@opetushallitus/virkailija-ui-components/Input';
import Select from '@opetushallitus/virkailija-ui-components/Select';
import { ValueType } from 'react-select';
import { SelectOptionType } from '../../types';

const TableContainer = styled.div`
    overflow-x: scroll;
    overflow-y: hidden;
    padding: 1rem;
    border: 1px solid #cccccc;
`;
const TableElement = styled.table`
    width: 100%;
    min-height: 20vh;
    max-height: 60vh;
    border-spacing: 0;
`;
const Th = styled.th`
    text-align: left;
`;
const Td = styled.td`
    font-family: Roboto, Arial Unicode MS, Arial, sans-serif;
    font-weight: 400;
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

export const TextFilterComponent = <T extends Record<string, unknown>>({
    column: { filterValue, preFilteredRows, setFilter },
    placeholder = {
        id: 'TAULUKKO_VAKIO_FILTTERI',
        defaultMessage: 'Haetaan {count} koodistosta',
    },
}: FilterProps<T> & { placeholder?: MessageDescriptor }) => {
    const count = preFilteredRows.length;
    const { formatMessage } = useIntl();
    return (
        <InputContainer>
            <Input
                value={filterValue || ''}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                    setFilter(e.target.value || undefined);
                }}
                placeholder={formatMessage(placeholder, { count })}
            />
        </InputContainer>
    );
};

export const SelectFilterComponent = <T extends Record<string, unknown>>({
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
                    defaultMessage: 'Valitse arvo listalta',
                })}
                isMulti={true}
                value={filterValue || []}
                options={uniqueOptions}
            />
        </SelectContainer>
    );
};

export const Table = <T extends object>({
    columns,
    data,
    onFilter,
    resetFilters,
}: TableProps<T> & {
    onFilter?: (rows: Row<T>[]) => void;
    resetFilters?: React.MutableRefObject<(() => void) | undefined>;
}) => {
    const defaultColumn = React.useMemo(
        () => ({
            Filter: <></>,
        }),
        []
    );
    const { state, filteredRows, getTableProps, getTableBodyProps, headerGroups, rows, prepareRow, setAllFilters } =
        useTable<T>({ columns, data, defaultColumn }, useFilters);
    useEffect(() => {
        onFilter && onFilter(filteredRows);
    }, [filteredRows, onFilter]);
    useEffect(() => {
        if (resetFilters) {
            resetFilters.current = state.filters.length ? () => setAllFilters([]) : undefined;
        }
    }, [resetFilters, state.filters.length, setAllFilters]);
    return (
        <TableContainer>
            <TableElement {...getTableProps()}>
                <Thead>
                    {headerGroups.map((headerGroup: HeaderGroup<T>) => {
                        const { key: headerGroupKey, ...headerGroupRest } = headerGroup.getHeaderGroupProps();
                        return (
                            <Tr {...headerGroupRest} key={headerGroupKey}>
                                {headerGroup.headers.map((column: HeaderGroup<T>) => {
                                    const { key: getHeaderPropsKey, ...getHeaderPropsRest } = column.getHeaderProps();
                                    return (
                                        <Th {...getHeaderPropsRest} key={getHeaderPropsKey}>
                                            {column.render('Header')}
                                            <div>{column.canFilter ? column.render('Filter') : null}</div>
                                        </Th>
                                    );
                                })}
                            </Tr>
                        );
                    })}
                </Thead>
                <Tbody {...getTableBodyProps()}>
                    {rows.map((row: Row<T>) => {
                        prepareRow(row);
                        const { key: rowKey, ...rowRest } = row.getRowProps();
                        return (
                            <Tr {...rowRest} key={rowKey}>
                                {row.cells.map((cell: Cell<T>) => {
                                    const { key: cellKey, ...cellRest } = cell.getCellProps();
                                    return (
                                        <Td {...cellRest} key={cellKey}>
                                            {cell.render('Cell')}
                                        </Td>
                                    );
                                })}
                            </Tr>
                        );
                    })}
                </Tbody>
            </TableElement>
        </TableContainer>
    );
};
