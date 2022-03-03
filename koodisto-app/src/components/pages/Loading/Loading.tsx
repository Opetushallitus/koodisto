import * as React from 'react';
import styled from 'styled-components';

const LoadingDiv = styled.div`
    padding-top: 2rem;
    display: flex;
    justify-content: center;
    align-items: center;
`;

const Loading = () => {
    return <LoadingDiv>LOADING...</LoadingDiv>;
};
export default Loading;
