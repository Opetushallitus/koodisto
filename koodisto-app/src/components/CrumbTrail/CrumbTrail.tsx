import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { IconWrapper } from '../IconWapper';
import { FormattedMessage } from 'react-intl';
import React, { ReactNode } from 'react';

const Container = styled.div`
    height: 3rem;
    display: flex;
    align-items: center;
    padding: 0 10vw 0 10vh;
    justify-content: flex-start;
    min-width: 3rem;
`;

export type Crumb = {
    label: ReactNode;
    path?: string;
};

type Props = { trail: Crumb[] };

const Home = () => (
    <div key="home">
        <Link to={'/'}>
            <IconWrapper icon="codicon:home" />
            &nbsp;
            <FormattedMessage id={'KOODISTOPALVELU_OTSIKKO'} defaultMessage={'Koodistopalvelu'} />
        </Link>
    </div>
);

export const CrumbTrail: React.FC<Props> = ({ trail }) => (
    <Container>
        <Home />
        {trail.map((crumb, index) => {
            const { label, path } = crumb;
            return (
                <div key={index}>
                    &nbsp;&gt;&nbsp;
                    {path ? <Link to={path}>{label}</Link> : <>{label}</>}
                </div>
            );
        })}
    </Container>
);
