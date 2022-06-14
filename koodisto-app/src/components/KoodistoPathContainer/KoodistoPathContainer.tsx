import styled from 'styled-components';
import { Link } from 'react-router-dom';
import { IconWrapper } from '../IconWapper';
import { FormattedMessage } from 'react-intl';
import React from 'react';

const Container = styled.div`
    height: 3rem;
    display: flex;
    align-items: center;
    padding-left: 15rem;
    justify-content: flex-start;
    min-width: 3rem;
`;

type Crumb = {
    label: string;
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

export const KoodistoPathContainer: React.FC<Props> = ({ trail }) => (
    <Container>
        <Home />
        {trail.map(({ label, path }) => (
            <div key={label}>
                &nbsp;&gt;&nbsp;
                {path ? <Link to={path}>{label}</Link> : label}
            </div>
        ))}
    </Container>
);
