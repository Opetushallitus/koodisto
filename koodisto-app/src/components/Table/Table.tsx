import * as React from 'react';
import styled from 'styled-components';
import { Cell, Column, FilterProps, HeaderGroup, Row, useFilters, useTable } from 'react-table';
import { useIntl } from 'react-intl';
import { useEffect } from 'react';

const TableContainer = styled.div`
    overflow-x: scroll;
    overflow-y: hidden;
    padding: 1rem;
    border: 1px solid #cccccc;
`;
const Table = styled.table`
    width: 100%;
    border-spacing: 0;
`;
const Th = styled.th`
    text-align: left;
    border-bottom: 1px solid rgba(151, 151, 151, 0.5);
`;
const Td = styled.td``;
const Tr = styled.tr``;
const Tbody = styled.tbody`
    ${Tr}:nth-child(even) {
        background: rgba(245, 245, 245, 1);
    }

    ${Tr}:nth-child(odd) {
        background: rgba(255, 255, 255, 0);
    }
`;

export const DefaultColumnFilter = <T extends Record<string, unknown>>({
    column: { filterValue, preFilteredRows, setFilter },
}: FilterProps<T>) => {
    const count = preFilteredRows.length;
    const { formatMessage } = useIntl();
    return (
        <input
            value={filterValue || ''}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                setFilter(e.target.value || undefined);
            }}
            placeholder={formatMessage(
                {
                    id: 'TAULUKKO_VAKIO_FILTTERI',
                    defaultMessage: 'HAETAAN {count} KOODISTOSTA',
                },
                { count }
            )}
        />
    );
};
type TableProps<T extends object> = {
    columns: Column<T>[];
    data: T[];
};
const TableComponent = <T extends object>({
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
            <Table {...getTableProps()}>
                <thead>
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
                </thead>
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
            </Table>
        </TableContainer>
    );
};

export default TableComponent;
