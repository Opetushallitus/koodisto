import React from 'react';
import { default as OPModal } from '@opetushallitus/virkailija-ui-components/Modal';
import ModalBody from '@opetushallitus/virkailija-ui-components/ModalBody';
import ModalFooter from '@opetushallitus/virkailija-ui-components/ModalFooter';
import ModalHeader from '@opetushallitus/virkailija-ui-components/ModalHeader';
import styled from 'styled-components';

const StyledOPModal = styled(OPModal)`
    border: 1px solid #979797;
    border-top: 3px solid #159ecb;
    border-radius: 0;
    background-color: #ffffff;
    box-shadow: 0 9px 7px 0 rgba(0, 0, 0, 0.5);
`;
const StyledModalHeader = styled(ModalHeader)`
    border-bottom: 1px solid rgba(151, 151, 151, 0.5);
`;
const StyledModalBodyFrame = styled.div`
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
`;
const StyledModalBodyField = styled.div`
    display: flex;
    flex-direction: column;
    flex-wrap: nowrap;
    padding-right: 1rem;
`;
const StyledModalFooter = styled(ModalFooter)`
    border-top: none;
`;
const StyledModalFooterFrame = styled.div`
    display: flex;
    flex-direction: row;
    flex-wrap: wrap;
    justify-content: flex-end;
`;
type Props = {
    onClose?: () => void;
    header?: React.ReactNode;
    body?: React.ReactNode;
    footer?: React.ReactNode;
};

export const Modal: React.FC<Props> = ({ onClose, footer, header, body }: Props) => {
    return (
        <StyledOPModal open onClose={onClose}>
            {header && <StyledModalHeader onClose={onClose}>{header}</StyledModalHeader>}
            <ModalBody>
                <StyledModalBodyFrame>
                    <StyledModalBodyField>{body}</StyledModalBodyField>
                </StyledModalBodyFrame>
            </ModalBody>
            <StyledModalFooter>
                <StyledModalFooterFrame>{footer}</StyledModalFooterFrame>
            </StyledModalFooter>
        </StyledOPModal>
    );
};
