import { Control, Controller, Path } from 'react-hook-form';
import DatePickerInput from '@opetushallitus/virkailija-ui-components/DatePickerInput';
import * as React from 'react';
import { DayPickerProps } from 'react-day-picker/types/Props';
type Props<T> = {
    control: Control<T>;
    validationErrors: { [x: string]: unknown };
    name: Path<T>;
    disabled?: boolean;
    dayPickerProps?: DayPickerProps;
    rules?: { required: boolean | string };
};
export const DatePickerController = <T,>({
    name,
    control,
    validationErrors,
    dayPickerProps,
    disabled,
    rules,
}: Props<T>) => {
    return (
        <Controller
            control={control}
            rules={rules}
            name={name}
            render={({ field: { ref: _ref, value, ...controllerRest } }) => {
                return (
                    <DatePickerInput
                        value={value as Date}
                        error={!!validationErrors[name]}
                        dayPickerProps={dayPickerProps}
                        inputProps={{ disabled }}
                        {...controllerRest}
                    />
                );
            }}
        />
    );
};
