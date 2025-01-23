import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { FormattedMessage } from 'react-intl';
import type { Koodi } from '../../types';
import { fetchPageKoodi } from '../../api/koodi';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { Loading } from '../../components/Loading';
import { KoodiPageAccordion } from './KoodiPageAccordion';
import { KoodiInfo } from './KoodiInfo';
import { KoodiCrumbTrail } from './KoodiCrumbTrail';
import VersionPicker from '../../components/VersionPicker';
import {
    MainHeaderContainer,
    HeadingDivider,
    MainHeaderButtonsContainer,
    MainContainer,
} from '../../components/Containers';

const KoodiPresentation: React.FC<{ koodi: Koodi }> = ({ koodi }) => {
    const [lang] = useAtom(casMeLangAtom);
    const navigate = useNavigate();
    return (
        <>
            <KoodiCrumbTrail koodi={koodi} />
            <MainHeaderContainer>
                <HeadingDivider>
                    <h1>{translateMetadata({ metadata: koodi.metadata, lang })?.nimi}</h1>
                    <VersionPicker version={koodi.versio} versions={koodi.koodiVersio} />
                </HeadingDivider>
                <MainHeaderButtonsContainer>
                    <Button
                        name={'KOODISIVU_MUOKKAA_KOODIA_BUTTON'}
                        variant={'outlined'}
                        onClick={() => navigate(`/koodi/edit/${koodi.koodiUri}/${koodi.versio}`)}
                    >
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

export const KoodiPage: React.FC = () => {
    const { koodiUri, koodiVersio } = useParams();
    const [pageData, setPageData] = useState<Koodi | undefined>();

    useEffect(() => {
        setPageData(undefined);
        if (koodiUri && koodiVersio) {
            (async () => {
                const data = await fetchPageKoodi(koodiUri, +koodiVersio);
                setPageData(data);
            })();
        }
    }, [koodiUri, koodiVersio]);

    return pageData ? <KoodiPresentation koodi={pageData} /> : <Loading />;
};
