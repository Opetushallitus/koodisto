import { Controller } from 'react-hook-form';
import * as React from 'react';
import { ControllerProps, SelectOption } from '../../types';
import { FastSelect } from './FastSelect';

type Props<T> = ControllerProps<T> & {
    options: SelectOption[];
};
export const SelectController = <T,>({ name, control, options, disabled, rules }: Props<T>) => {
    return (
        <Controller
            control={control}
            rules={rules}
            name={name}
            render={({ field: { ref: _ref, value, ...rest }, fieldState: { invalid } }) => {
                return (
                    <FastSelect
                        isDisabled={disabled}
                        value={value as { label: string; value: string }}
                        id={name}
                        {...rest}
                        error={invalid}
                        options={options}
                    />
                );
            }}
        />
    );
};
