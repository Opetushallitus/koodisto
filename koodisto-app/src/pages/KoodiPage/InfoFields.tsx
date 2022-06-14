import React from 'react';
import { FormattedMessage, MessageDescriptor } from 'react-intl';
import styled from 'styled-components';

const InfoContainer = styled.div`
    display: flex;
    flex-direction: column;
`;

const InfoRow = styled.div`
    display: flex;
    flex-direction: row;
    > * {
        &:first-child {
            min-width: 11rem;
            max-width: 11rem;
        }
    }
`;

const InfoValue = styled.span`
    color: #2a2a2a;
`;

type InfoField = {
    header: MessageDescriptor;
    value: React.ReactNode;
};

type Props = {
    fields: InfoField[];
};

const InfoItem = ({ header, value }: InfoField) => (
    <InfoRow>
        <FormattedMessage tagName={'span'} {...header} />
        <InfoValue>{value}</InfoValue>
    </InfoRow>
);

export const InfoFields = ({ fields }: Props) => (
    <InfoContainer>
        {fields.map((field) => (
            <InfoItem key={field.header.id} {...field} />
        ))}
    </InfoContainer>
);
