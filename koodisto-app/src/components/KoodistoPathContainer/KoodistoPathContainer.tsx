import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { IconWrapper } from '../IconWapper';
import { FormattedMessage } from 'react-intl';
import React from 'react';

const Container = styled.div`
    height: 3rem;
    display: flex;
    align-items: center;
    padding: 0 10vw 0 10vh;
    justify-content: flex-start;
    min-width: 3rem;
`;
type Props = { path: string[] };

export const KoodistoPathContainer: React.FC<Props> = ({ path }) => {
    return (
        <Container>
            <div>
                <Link to={'/'}>
                    <IconWrapper icon="codicon:home" />
                    &nbsp;
                    <FormattedMessage id={'KOODISTOPALVELU_OTSIKKO'} defaultMessage={'Koodistopalvelu'} />
                </Link>
            </div>
            {path.map((a) => (
                <div key={a}>&nbsp;&gt;&nbsp;{a}</div>
            ))}
        </Container>
    );
};
