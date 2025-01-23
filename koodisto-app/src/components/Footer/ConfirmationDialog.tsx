import React from 'react';
import styled from 'styled-components';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import Checkbox from '@opetushallitus/virkailija-ui-components/Checkbox';
import { FormattedMessage, MessageDescriptor } from 'react-intl';

const CloseWrapper = styled.div`
    position: absolute;
    top: 0;
    right: 0.4rem;
    font-size: x-large;
    color: #bbbbbb;
    cursor: pointer;
`;

const ButtonWrapper = styled.div`
    margin: 1rem 0;
`;

const ContentWrapper = styled.div`
    margin: 1rem;
    > div > label {
        margin-right: 0.5rem;
    }
`;

export const ConfirmationDialog: React.FC<{
    close?: () => void;
    action: () => void;
    confirmationMessage: MessageDescriptor;
    buttonText: MessageDescriptor;
    children: JSX.Element;
}> = ({ close, action, confirmationMessage, buttonText, children }) => {
    const [confirmed, setConfirmed] = React.useState<boolean>(false);
    const toggle = () => setConfirmed(!confirmed);
    return (
        <ContentWrapper>
            <CloseWrapper id="close-dialog" role="button" onClick={close}>
                &times;
            </CloseWrapper>
            {children}
            <div>
                <Checkbox name="CONFIRMATION_CHECK" onChange={toggle} checked={confirmed} />
                <FormattedMessage {...confirmationMessage} />
            </div>
            <ButtonWrapper>
                <Button name="CONFIRMATION_ACTION" onClick={action} disabled={!confirmed}>
                    <FormattedMessage {...buttonText} />
                </Button>
            </ButtonWrapper>
        </ContentWrapper>
    );
};
