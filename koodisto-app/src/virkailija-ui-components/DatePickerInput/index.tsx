import * as React from 'react';
import styled from 'styled-components';
import { DayPicker, type PropsBase } from '@daypicker/react';
import { format as formatDate, isValid as isValidDate, parse as parseDate } from 'date-fns';

import DatePickerStyle from '../DatePickerStyle';
import Input, { InputProps } from '../Input';
import InputIcon from '../InputIcon';
import isString from '../utils/isString';

const firstYear = new Date(0).getFullYear();

const removeLeadingZeros = (value: string) => {
    if (isString(value)) {
        return value.replace(/\b0/g, '');
    }

    return value;
};

const formatDateFn = (value: Date | number, format: string) => {
    return formatDate(value, format);
};

const parseDateFn = (value: string | undefined, format: string) => {
    if (!isString(value) || value === '') {
        return undefined;
    }

    const parsedDate = parseDate(value, format, new Date());

    if (!isValidDate(parsedDate)) {
        return undefined;
    }

    const parseIsDeterministic = removeLeadingZeros(value) === removeLeadingZeros(formatDate(parsedDate, format));

    if (!parseIsDeterministic) {
        return undefined;
    }

    if (parsedDate.getFullYear() < firstYear) {
        return undefined;
    }
    return parsedDate || undefined;
};

const defaultClassNames = {
    overlay: 'DatePicker__ DatePickerOverlay__',
    overlayWrapper: 'DatePickerOverlayWrapper__',
    container: 'DatePickerInput__',
};

const noop = () => undefined;

const DatePickerInputContainer = styled.div`
    position: relative;
`;

export type DatePickerDayPickerProps = Omit<PropsBase, 'mode' | 'required'>;

export type DatePickerInputProps = {
    value?: Date | string;
    placeholder?: string;
    error?: boolean;
    showIcon?: boolean;
    format?: string;
    classNames?: Record<string, string>;
    inputProps?: InputProps;
    dayPickerProps?: DatePickerDayPickerProps;
    onChange?: (date: Date | undefined) => void;
};

const formatInputValue = (value: Date | string | undefined, format: string): string => {
    if (value instanceof Date && isValidDate(value)) {
        return formatDateFn(value, format);
    }
    return typeof value === 'string' ? value : '';
};

export const DatePickerInput = ({
    value,
    placeholder = '',
    format = 'd.M.yyyy',
    onChange = noop,
    inputProps = {},
    error = false,
    showIcon = true,
    classNames: classNamesProp = {},
    dayPickerProps = {},
}: DatePickerInputProps) => {
    const wrapperRef = React.useRef<HTMLDivElement>(null);
    const [isOpen, setIsOpen] = React.useState(false);
    const [month, setMonth] = React.useState(value instanceof Date ? value : undefined);
    const [inputValue, setInputValue] = React.useState(formatInputValue(value, format));

    React.useEffect(() => {
        setInputValue(formatInputValue(value, format));
        if (value instanceof Date && isValidDate(value)) {
            setMonth(value);
        }
    }, [format, value]);

    React.useEffect(() => {
        if (!isOpen) {
            return undefined;
        }

        const closeOnOutsideClick = (event: MouseEvent) => {
            if (!wrapperRef.current?.contains(event.target as Node)) {
                setIsOpen(false);
            }
        };

        document.addEventListener('mousedown', closeOnOutsideClick);
        return () => document.removeEventListener('mousedown', closeOnOutsideClick);
    }, [isOpen]);

    const classNames = {
        ...classNamesProp,
        overlay: `${defaultClassNames.overlay} ${classNamesProp.overlay || ''}`,
        overlayWrapper: `${defaultClassNames.overlayWrapper} ${classNamesProp.overlayWrapper || ''}`,
        container: `${defaultClassNames.container} ${classNamesProp.container || ''}`,
    };

    const selectedDate = value instanceof Date && isValidDate(value) ? value : undefined;
    const disabled = !!inputProps.disabled;

    const open = () => {
        if (!disabled) {
            setIsOpen(true);
        }
    };

    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const nextValue = event.target.value;
        setInputValue(nextValue);
        inputProps.onChange?.(event);

        if (nextValue === '') {
            onChange(undefined);
            return;
        }

        const parsedDate = parseDateFn(nextValue, format);
        if (parsedDate) {
            setMonth(parsedDate);
            onChange(parsedDate);
        }
    };

    const handleInputKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
        inputProps.onKeyDown?.(event);
        if (event.defaultPrevented) {
            return;
        }
        if (event.key === 'Escape') {
            setIsOpen(false);
            return;
        }
        if (event.key === 'Enter') {
            const parsedDate = parseDateFn(inputValue, format);
            if (parsedDate) {
                onChange(parsedDate);
                setInputValue(formatDateFn(parsedDate, format));
                setMonth(parsedDate);
            }
            setIsOpen(false);
        }
    };

    const handleInputClick = (event: React.MouseEvent<HTMLInputElement>) => {
        inputProps.onClick?.(event);
        open();
    };

    const handleInputFocus = (event: React.FocusEvent<HTMLInputElement>) => {
        inputProps.onFocus?.(event);
        open();
    };

    const handleMonthChange = (nextMonth: Date) => {
        setMonth(nextMonth);
        dayPickerProps.onMonthChange?.(nextMonth);
    };

    const handleSelect = (selected: Date | undefined) => {
        onChange(selected);
        if (selected) {
            setInputValue(formatDateFn(selected, format));
            setMonth(selected);
            setIsOpen(false);
        } else {
            setInputValue('');
        }
    };

    return (
        <DatePickerInputContainer ref={wrapperRef} className={classNames.container}>
            <DatePickerStyle />
            <Input
                {...inputProps}
                value={inputValue}
                suffix={showIcon && <InputIcon type="event" />}
                error={error}
                placeholder={placeholder}
                onChange={handleInputChange}
                onClick={handleInputClick}
                onFocus={handleInputFocus}
                onKeyDown={handleInputKeyDown}
            />
            {isOpen && (
                <div className={classNames.overlayWrapper}>
                    <DayPicker
                        {...dayPickerProps}
                        className={`${classNames.overlay} ${dayPickerProps.className || ''}`}
                        mode="single"
                        selected={selectedDate}
                        month={dayPickerProps.month || month}
                        onMonthChange={handleMonthChange}
                        onSelect={handleSelect}
                    />
                </div>
            )}
        </DatePickerInputContainer>
    );
};

export default DatePickerInput;
