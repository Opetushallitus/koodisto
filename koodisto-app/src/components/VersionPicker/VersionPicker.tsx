import React from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useIntl } from 'react-intl';
import { dropRight } from 'lodash';
import Select from '@opetushallitus/virkailija-ui-components/Select';
import type { ValueType } from 'react-select';
import type { SelectOption } from '../../types';
import { SelectContainer } from '..//Containers';

type Props = {
    version: number;
    versions: number[];
};

export const VersionPicker: React.FC<Props> = ({ version, versions }) => {
    const { formatMessage } = useIntl();
    const navigate = useNavigate();
    const location = useLocation();
    const options: SelectOption[] = [...versions].sort().map((v) => ({
        label: formatMessage(
            {
                id: 'VERSIO_DROPDOWN_LABEL',
                defaultMessage: 'Versio {versio}',
            },
            { versio: v }
        ),
        value: `${v}`,
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
                    navigate([...dropRight(location.pathname.split('/')), (value as SelectOption).value].join('/'));
                }}
            />
        </SelectContainer>
    );
};
