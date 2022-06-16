import { Control, Controller, Path } from 'react-hook-form';
import * as React from 'react';
import { SelectOption } from '../types';
import { FastSelect } from './FastSelect';

type Props<T> = {
    control: Control<T>;
    validationErrors: { [x: string]: unknown };
    name: Path<T>;
    options: SelectOption[];
    disabled?: boolean;
    rules?: { required: boolean | string };
};
export const SelectController = <T,>({ name, control, validationErrors, options, disabled, rules }: Props<T>) => {
    return (
        <Controller
            control={control}
            rules={rules}
            name={name}
            render={({ field: { ref: _ref, value, ...rest } }) => {
                return (
                    <FastSelect
                        isDisabled={disabled}
                        value={value as { label: string; value: string }}
                        id={name}
                        {...rest}
                        error={!!validationErrors[name]}
                        options={options}
                    />
                );
            }}
        />
    );
};
