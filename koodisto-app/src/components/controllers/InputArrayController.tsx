import { MessageDescriptor, useIntl, FormattedMessage } from 'react-intl';
import { FieldPath, UseFormGetValues, useFieldArray, Controller, ArrayPath, Path, FieldArray } from 'react-hook-form';
import { ControllerProps, KoodiMetadata } from '../../types';
import styled from 'styled-components';
import { IconWrapper } from '../IconWapper';
import * as React from 'react';
import { MainContainerRowTitle, MainContainerRowTitleMandatory, MainContainerRowContent } from '../Containers';
import Textarea from '@opetushallitus/virkailija-ui-components/Textarea';
import Input from '@opetushallitus/virkailija-ui-components/Input';

const Container = styled.div`
    display: flex;
    flex-direction: row;
    padding: 0;
`;
const Column = styled.div`
    margin-right: 1rem;
`;
const TitleContainer = styled.div`
    display: flex;
    flex-direction: row;

    > div {
        padding-right: 1rem;
    }
`;
export const Direction = styled.div<{ large?: boolean }>`
    display: flex;
    flex-direction: ${(props) => (props.large ? 'column' : 'row')};

    textarea {
        resize: none;
        box-sizing: border-box;
        width: ${(props) => (props.large ? 60 : 19)}vw;
    }
`;
type Props<T> = Omit<ControllerProps<T>, 'name'> & {
    title: MessageDescriptor;
    fieldPath: keyof KoodiMetadata;
    getValues: UseFormGetValues<T>;
    large?: boolean;
    mandatory?: boolean;
};
export const InputArrayController = <T extends { metadata: KoodiMetadata[] }>({
    title,
    fieldPath,
    control,
    rules,
    disabled,
    getValues,
    large,
    mandatory,
}: Props<T>) => {
    const { formatMessage } = useIntl();
    const { fields, update } = useFieldArray<T, ArrayPath<T>, keyof KoodiMetadata | 'id'>({
        control,
        name: 'metadata' as ArrayPath<T>,
    });
    const copyToFields = (): void => {
        const firstValue = getValues(`metadata` as Path<T>) as KoodiMetadata[];
        update(1, { ...firstValue[1], [fieldPath]: firstValue[0][fieldPath] } as FieldArray<T, ArrayPath<T>>);
        update(2, { ...firstValue[2], [fieldPath]: firstValue[0][fieldPath] } as FieldArray<T, ArrayPath<T>>);
    };
    const LabelContainer = mandatory ? MainContainerRowTitleMandatory : MainContainerRowTitle;
    return (
        <Container>
            <LabelContainer {...title} />
            <Direction large={large}>
                {fields.map((field, index) => (
                    <Column key={field.id}>
                        <TitleContainer>
                            <FormattedMessage
                                id={`FIELD_TITLE_${field.kieli}`}
                                defaultMessage={field.kieli}
                                tagName={'div'}
                            />
                            {index === 0 && (
                                <div
                                    title={formatMessage({
                                        id: 'KOPIOI_MUIHIN_KIELIIN',
                                        defaultMessage: 'Kopioi muihin kieliin',
                                    })}
                                    onClick={() => copyToFields()}
                                >
                                    <IconWrapper
                                        icon="ci:copy"
                                        color={'gray'}
                                        height={'1rem'}
                                        name={'KOPIOI_MUIHIN_KIELIIN'}
                                    />
                                </div>
                            )}
                        </TitleContainer>
                        <Controller
                            control={control}
                            rules={rules}
                            name={`metadata[${index}][${fieldPath}]` as FieldPath<T>}
                            render={({ field: { ref: _ref, value, ...controllerRest }, fieldState: { invalid } }) => {
                                return (
                                    (large && (
                                        <Textarea
                                            disabled={disabled}
                                            rows={5}
                                            value={value || ''}
                                            error={invalid}
                                            inputProps={{ disabled }}
                                            {...controllerRest}
                                        />
                                    )) || (
                                        <MainContainerRowContent>
                                            <Input
                                                disabled={disabled}
                                                value={value || ''}
                                                error={invalid}
                                                inputProps={{ disabled }}
                                                {...controllerRest}
                                            />
                                        </MainContainerRowContent>
                                    )
                                );
                            }}
                        />
                    </Column>
                ))}
            </Direction>
        </Container>
    );
};
