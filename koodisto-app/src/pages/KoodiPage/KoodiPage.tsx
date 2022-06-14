import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { FormattedMessage, useIntl } from 'react-intl';
import styled from 'styled-components';
import type { PageKoodi } from '../../types';
import { fetchPageKoodi } from '../../api/koodisto';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import Select from '@opetushallitus/virkailija-ui-components/Select';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { Loading } from '../../components/Loading';
import { KoodiPageAccordion } from './KoodiPageAccordion';
import { KoodiInfo } from './KoodiInfo';
import { CrumbTrail } from './CrumbTrail';

const MainContainer = styled.div`
    flex-grow: 1;
    display: flex;
    flex-direction: column;
    justify-content: flex-start;
    box-sizing: border-box;
    background-color: #ffffff;
    padding: 0 15rem 0 15rem;
`;
const MainHeaderContainer = styled.div`
    display: inline-flex;
    flex-direction: row;
    align-items: baseline;
    padding: 0 15rem 0 15rem;
    justify-content: space-between;
`;

const MainHeaderButtonsContainer = styled.div`
    display: flex;
    flex-direction: column;

    > * {
        :not(:last-child) {
            margin: 0 0 1rem 0;
        }
    }
`;

const HeadingDivider = styled.div`
    display: flex;
    align-items: center;

    > * {
        &:first-child {
            margin-right: 3rem;
        }
    }
`;

const SelectContainer = styled.div`
    width: 8rem;
`;

const KoodiPresentation: React.FC<PageKoodi> = ({ koodi, koodisto }: PageKoodi) => {
    const { formatMessage } = useIntl();
    const [lang] = useAtom(casMeLangAtom);
    return (
        <>
            <CrumbTrail koodi={koodi} koodisto={koodisto} />
            <MainHeaderContainer>
                <HeadingDivider>
                    <h1>{translateMetadata({ metadata: koodi.metadata, lang })?.nimi}</h1>
                    <SelectContainer>
                        <Select
                            placeholder={formatMessage({
                                id: 'KOODIVERSIO_DROPDOWN',
                                defaultMessage: 'Koodiversio',
                            })}
                        />
                    </SelectContainer>
                </HeadingDivider>
                <MainHeaderButtonsContainer>
                    <Button variant={'outlined'}>
                        <FormattedMessage id={'KOODISIVU_MUOKKAA_KOODIA_BUTTON'} defaultMessage={'Muokkaa koodia'} />
                    </Button>
                </MainHeaderButtonsContainer>
            </MainHeaderContainer>
            <MainContainer>
                <KoodiInfo koodi={koodi} />
                <KoodiPageAccordion koodi={koodi} />
            </MainContainer>
        </>
    );
};

const KoodiContainer: React.FC = () => {
    const { koodiUri, koodiVersio } = useParams();
    const [pageData, setPageData] = useState<PageKoodi | undefined>();

    useEffect(() => {
        if (koodiUri && koodiVersio) {
            (async () => {
                const data = await fetchPageKoodi(koodiUri, +koodiVersio);
                setPageData(data);
            })();
        }
    }, [koodiUri, koodiVersio]);

    return pageData ? <KoodiPresentation {...pageData} /> : <Loading />;
};

export default KoodiContainer;
