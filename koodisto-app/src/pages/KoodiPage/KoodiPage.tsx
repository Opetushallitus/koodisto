import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { FormattedMessage } from 'react-intl';
import type { PageKoodi } from '../../types';
import { fetchPageKoodi } from '../../api/koodisto';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { Loading } from '../../components/Loading';
import { KoodiPageAccordion } from './KoodiPageAccordion';
import { KoodiInfo } from './KoodiInfo';
import { CrumbTrail } from './CrumbTrail';
import { VersionPicker } from './VersionPicker';
import {
    MainHeaderContainer,
    HeadingDivider,
    MainHeaderButtonsContainer,
    MainContainer,
} from '../../components/Containers';

const KoodiPresentation: React.FC<PageKoodi> = ({ koodi, koodisto }: PageKoodi) => {
    const [lang] = useAtom(casMeLangAtom);
    return (
        <>
            <CrumbTrail koodi={koodi} koodisto={koodisto} />
            <MainHeaderContainer>
                <HeadingDivider>
                    <h1>{translateMetadata({ metadata: koodi.metadata, lang })?.nimi}</h1>
                    <VersionPicker version={koodi.versio} versions={koodi.versions} />
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

const KoodiPage: React.FC = () => {
    const { koodiUri, koodiVersio } = useParams();
    const [pageData, setPageData] = useState<PageKoodi | undefined>();

    useEffect(() => {
        setPageData(undefined);
        if (koodiUri && koodiVersio) {
            (async () => {
                const data = await fetchPageKoodi(koodiUri, +koodiVersio);
                setPageData(data);
            })();
        }
    }, [koodiUri, koodiVersio]);

    return pageData ? <KoodiPresentation {...pageData} /> : <Loading />;
};

export default KoodiPage;
