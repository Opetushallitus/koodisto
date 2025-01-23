import React from 'react';
import { Table } from '@tanstack/react-table';
import { FormattedMessage } from 'react-intl';
import styled from 'styled-components';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import Select from '@opetushallitus/virkailija-ui-components/Select';
import type { ValueType } from 'react-select';
import type { SelectOption, PageSize } from '../../types';
import { IconWrapper } from '../IconWapper';

const Pager = styled.section`
    margin-top: 1rem;
    display: flex;
    justify-content: right;
    align-items: center;
    gap: 0.1rem;
    width: 100%;
`;

const InfoBox = styled.span`
    padding-right: 0.5rem;
`;

const SelectContainer = styled.div`
    min-width: 5rem;
`;

const pageSizes: PageSize[] = [10, 20, 50, 100];

export const Paging = <T extends object>({ table }: { table: Table<T> }) => (
    <Pager>
        <InfoBox>
            <FormattedMessage id={'SIVUTUS_SIVU'} defaultMessage={'Sivu'} /> {table.getState().pagination.pageIndex + 1}{' '}
            / {table.getPageCount()}
        </InfoBox>
        <Button
            name="FIRST_PAGE"
            variant="outlined"
            onClick={() => table.setPageIndex(0)}
            disabled={!table.getCanPreviousPage()}
        >
            <IconWrapper icon="ci:first-page" height={'1.5rem'} />
        </Button>
        <Button
            name="PREVIOUS_PAGE"
            variant="outlined"
            onClick={() => table.previousPage()}
            disabled={!table.getCanPreviousPage()}
        >
            <IconWrapper icon="ci:chevron-left" height={'1.5rem'} />
        </Button>
        <SelectContainer>
            <Select
                name="CHANGE_PAGE_SIZE"
                menuPlacement="top"
                onChange={(option: ValueType<SelectOption>) =>
                    option && table.setPageSize(Number((option as SelectOption).value))
                }
                value={{
                    label: `${table.getState().pagination.pageSize}`,
                    value: `${table.getState().pagination.pageSize}`,
                }}
                options={pageSizes.map((pageSize) => ({ label: `${pageSize}`, value: `${pageSize}` }))}
            />
        </SelectContainer>
        <Button name="NEXT_PAGE" variant="outlined" onClick={() => table.nextPage()} disabled={!table.getCanNextPage()}>
            <IconWrapper icon="ci:chevron-right" height={'1.5rem'} />
        </Button>
        <Button
            name="LAST_PAGE"
            variant="outlined"
            onClick={() => table.setPageIndex(table.getPageCount() - 1)}
            disabled={!table.getCanNextPage()}
        >
            <IconWrapper icon="ci:last-page" height={'1.5rem'} />
        </Button>
    </Pager>
);
