import { Controller } from 'react-hook-form';
import DatePickerInput from '@opetushallitus/virkailija-ui-components/DatePickerInput';
import * as React from 'react';
import { DayPickerProps } from 'react-day-picker/types/Props';
import { ControllerProps } from '../../types';

type Props<T> = ControllerProps<T> & {
    dayPickerProps?: DayPickerProps;
};
export const DatePickerController = <T,>({ name, control, dayPickerProps, disabled, rules }: Props<T>) => {
    return (
        <Controller
            control={control}
            rules={rules}
            name={name}
            render={({ field: { ref: _ref, value, ...controllerRest }, fieldState: { invalid } }) => {
                return (
                    <DatePickerInput
                        value={value as Date}
                        error={invalid}
                        dayPickerProps={dayPickerProps}
                        inputProps={{ disabled }}
                        {...controllerRest}
                    />
                );
            }}
        />
    );
};
