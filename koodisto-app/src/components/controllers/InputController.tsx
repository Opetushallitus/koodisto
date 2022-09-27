import { Controller } from 'react-hook-form';
import * as React from 'react';
import { ControllerProps } from '../../types';
import Input from '@opetushallitus/virkailija-ui-components/Input';

type Props<T> = ControllerProps<T>;
export const InputController = <T,>({ name, control, disabled, rules }: Props<T>) => {
    return (
        <Controller
            control={control}
            rules={rules}
            name={name}
            render={({ field: { ref: _ref, value, ...rest }, fieldState: { invalid } }) => {
                return <Input isDisabled={disabled} value={value} id={name} {...rest} error={invalid} />;
            }}
        />
    );
};
