import * as React from 'react';
import styled from 'styled-components';

import Spin from '@opetushallitus/virkailija-ui-components/Spin';
const LoadingDiv = styled.div`
    padding-top: 2rem;
    display: flex;
    justify-content: center;
    align-items: center;
`;

const Loading: React.FC = ({ children }) => {
    return (
        <LoadingDiv>
            <Spin />
            {children}
        </LoadingDiv>
    );
};
export default Loading;
