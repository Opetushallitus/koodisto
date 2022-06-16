import { MessageDescriptor, useIntl, FormattedMessage } from 'react-intl';
import { FieldPath, Control, UseFormRegister, UseFormGetValues, UseFormSetValue, useFieldArray } from 'react-hook-form';
import { Metadata, PageKoodisto } from '../../types';
import styled from 'styled-components';
import { IconWrapper } from '../../components/IconWapper';
import * as React from 'react';
import { MainContainerRowTitle } from '../../components/Containers';
import Textarea from '@opetushallitus/virkailija-ui-components/Textarea';

const Container = styled.div`
    display: flex;
    flex-direction: row;
    padding: 0;
`;
const Column = styled.div`
    margin-right: 1rem;
`;
export const Direction = styled.div<{ large?: boolean }>`
    display: flex;
    flex-direction: ${(props) => (props.large ? 'column' : 'row')};
    textarea {
        resize: none;
        box-sizing: border-box;
        width: 60vw;
        width: ${(props) => (props.large ? 60 : 19)}vw;
    }
`;
export const InputArray = ({
    title,
    fieldPath,
    control,
    register,
    getValues,
    setValue,
    large,
}: {
    title: MessageDescriptor;
    fieldPath: FieldPath<Metadata>;
    control: Control<PageKoodisto>;
    register: UseFormRegister<PageKoodisto>;
    getValues: UseFormGetValues<PageKoodisto>;
    setValue: UseFormSetValue<PageKoodisto>;
    large?: boolean;
}) => {
    const { formatMessage } = useIntl();
    const { fields } = useFieldArray({
        control,
        name: 'metadata',
    });
    const copyToNames = (): void => {
        const firstValue = getValues(`metadata.0.${fieldPath}`);
        setValue(`metadata.1.${fieldPath}`, firstValue);
        setValue(`metadata.2.${fieldPath}`, firstValue);
    };
    return (
        <Container>
            <MainContainerRowTitle {...title} />
            <Direction large={large}>
                {fields.map((field, index) => (
                    <Column key={field.id}>
                        <FormattedMessage id={`FIELD_TITLE_${field.kieli}`} defaultMessage={field.kieli} />{' '}
                        <Textarea
                            rows={(large && 5) || 1}
                            {...register(`metadata.${index}.${fieldPath}`)}
                            suffix={
                                index === 0 && (
                                    <div
                                        title={formatMessage({
                                            id: 'KOPIOI_MUIHIN_NIMIIN',
                                            defaultMessage: 'Kopioi muihin kieliin',
                                        })}
                                        onClick={() => copyToNames()}
                                    >
                                        <IconWrapper
                                            icon="ci:copy"
                                            color={'gray'}
                                            height={'1.5rem'}
                                            name={'KOPIOI_MUIHIN_NIMIIN'}
                                        />
                                    </div>
                                )
                            }
                        />
                    </Column>
                ))}
            </Direction>
        </Container>
    );
};
