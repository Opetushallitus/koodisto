import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useIntl } from 'react-intl';
import Select from '@opetushallitus/virkailija-ui-components/Select';
import type { ValueType } from 'react-select';
import type { SelectOption } from '../../types';
import { SelectContainer } from '../../components/Containers';

type Props = {
    version: number;
    versions: number;
};

export const VersionPicker: React.FC<Props> = ({ version, versions }) => {
    const { formatMessage } = useIntl();
    const navigate = useNavigate();
    const location = useLocation();
    const options: SelectOption[] = [...Array.from(Array(versions).keys())].map((v) => ({
        label: `${v + 1}`,
        value: `${v + 1}`,
    }));
    return (
        <SelectContainer>
            <Select
                placeholder={formatMessage({
                    id: 'KOODIVERSIO_DROPDOWN',
                    defaultMessage: 'Koodiversio',
                })}
                value={options.find((option) => option.value === `${version}`)}
                options={options}
                onChange={(value: ValueType<SelectOption>) => {
                    navigate(
                        [(value as SelectOption).value, ...location.pathname.split('/').reverse().slice(1)]
                            .reverse()
                            .join('/')
                    );
                }}
            />
        </SelectContainer>
    );
};
