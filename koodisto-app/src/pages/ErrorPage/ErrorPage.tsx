import * as React from 'react';
import sad from './sad.png';
import board from './board.png';
import styled from 'styled-components';

type Props = {
    children?: React.ReactNode;
};

const ErrorContainer = styled.div`
    display: flex;
    justify-content: center;
    align-items: center;
    color: #ffffff;
`;
const ErrorBackground = styled.div`
    width: 820px;
    height: 500px;
    background-image: url(${board});
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: space-evenly;
`;
export const ErrorPage = (props: Props) => {
    return (
        <ErrorContainer>
            <ErrorBackground>
                <div>
                    <img src={sad} alt={''} />
                </div>
                {props.children}
            </ErrorBackground>
        </ErrorContainer>
    );
};
