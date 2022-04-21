import * as React from 'react';
import styled from 'styled-components';
import { Cell, Column, HeaderGroup, Row, useFilters, useTable } from 'react-table';
import { useEffect } from 'react';

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

export default TableComponent;
