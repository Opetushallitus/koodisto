import {
    UseColumnOrderInstanceProps,
    UseColumnOrderState,
    UseExpandedHooks,
    UseExpandedInstanceProps,
    UseExpandedOptions,
    UseExpandedRowProps,
    UseExpandedState,
    UseFiltersColumnOptions,
    UseFiltersColumnProps,
    UseFiltersInstanceProps,
    UseFiltersOptions,
    UseFiltersState,
    UseGlobalFiltersColumnOptions,
    UseGlobalFiltersInstanceProps,
    UseGlobalFiltersOptions,
    UseGlobalFiltersState,
    UseGroupByCellProps,
    UseGroupByColumnOptions,
    UseGroupByColumnProps,
    UseGroupByHooks,
    UseGroupByInstanceProps,
    UseGroupByOptions,
    UseGroupByRowProps,
    UseGroupByState,
    UsePaginationInstanceProps,
    UsePaginationOptions,
    UsePaginationState,
    UseResizeColumnsColumnOptions,
    UseResizeColumnsColumnProps,
    UseResizeColumnsOptions,
    UseResizeColumnsState,
    UseRowSelectHooks,
    UseRowSelectInstanceProps,
    UseRowSelectOptions,
    UseRowSelectRowProps,
    UseRowSelectState,
    UseRowStateCellProps,
    UseRowStateInstanceProps,
    UseRowStateOptions,
    UseRowStateRowProps,
    UseRowStateState,
    UseSortByColumnOptions,
    UseSortByColumnProps,
    UseSortByHooks,
    UseSortByInstanceProps,
    UseSortByOptions,
    UseSortByState,
} from 'react-table';

declare module 'react-table' {
    // take this file as-is, or comment out the sections that don't apply to your plugin configuration

    export type TableOptions<D extends object> = {} & UseExpandedOptions<D> &
        UseFiltersOptions<D> &
        UseGlobalFiltersOptions<D> &
        UseGroupByOptions<D> &
        UsePaginationOptions<D> &
        UseResizeColumnsOptions<D> &
        UseRowSelectOptions<D> &
        UseRowStateOptions<D> &
        UseSortByOptions<D> &
        Record<string, unknown>;

    export type Hooks<D extends object> = {} & UseExpandedHooks<D> &
        UseGroupByHooks<D> &
        UseRowSelectHooks<D> &
        UseSortByHooks<D>;

    export type TableInstance<D extends object> = {} & UseColumnOrderInstanceProps<D> &
        UseExpandedInstanceProps<D> &
        UseFiltersInstanceProps<D> &
        UseGlobalFiltersInstanceProps<D> &
        UseGroupByInstanceProps<D> &
        UsePaginationInstanceProps<D> &
        UseRowSelectInstanceProps<D> &
        UseRowStateInstanceProps<D> &
        UseSortByInstanceProps<D>;

    export type TableState<D extends object> = {} & UseColumnOrderState<D> &
        UseExpandedState<D> &
        UseFiltersState<D> &
        UseGlobalFiltersState<D> &
        UseGroupByState<D> &
        UsePaginationState<D> &
        UseResizeColumnsState<D> &
        UseRowSelectState<D> &
        UseRowStateState<D> &
        UseSortByState<D>;

    export type ColumnInterface<D extends object> = {} & UseFiltersColumnOptions<D> &
        UseGlobalFiltersColumnOptions<D> &
        UseGroupByColumnOptions<D> &
        UseResizeColumnsColumnOptions<D> &
        UseSortByColumnOptions<D>;

    export type ColumnInstance<D extends object> = {} & UseFiltersColumnProps<D> &
        UseGroupByColumnProps<D> &
        UseResizeColumnsColumnProps<D> &
        UseSortByColumnProps<D>;

    export type Cell<D extends object> = {} & UseGroupByCellProps<D> & UseRowStateCellProps<D>;

    export type Row<D extends object> = {} & UseExpandedRowProps<D> &
        UseGroupByRowProps<D> &
        UseRowSelectRowProps<D> &
        UseRowStateRowProps<D>;
}
