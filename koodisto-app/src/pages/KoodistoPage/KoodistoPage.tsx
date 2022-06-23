import React, { useEffect, useState } from 'react';
import { FormattedMessage } from 'react-intl';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { useNavigate, useParams } from 'react-router-dom';
import { fetchPageKoodisto } from '../../api/koodisto';
import { translateMetadata } from '../../utils';
import { PageKoodisto } from '../../types';
import { Loading } from '../../components/Loading';
import { KoodistoInfo } from './KoodistoInfo';
import KoodistoPageAccordion from './KoodistoPageAccordion';
import { CSVFunctionModal } from '../../modals/CSVFunctionModal';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import {
    MainHeaderContainer,
    HeadingDivider,
    MainHeaderButtonsContainer,
    MainContainer,
} from '../../components/Containers';
import { CrumbTrail } from '../../components/KoodistoPathContainer';
import VersionPicker from '../../components/VersionPicker';
import { ErrorPage } from '../ErrorPage';

export const KoodistoPage: React.FC = () => {
    const { versio, koodistoUri } = useParams();
    const versioNumber = versio ? +versio : undefined;
    const navigate = useNavigate();
    const [lang] = useAtom(casMeLangAtom);
    const [koodisto, setKoodisto] = useState<PageKoodisto | undefined>();
    const [uploadCsvVisible, setUploadCsvVisible] = useState<boolean>(false);

    useEffect(() => {
        (async () => {
            if (koodistoUri && versioNumber) {
                const koodistoData = await fetchPageKoodisto({ koodistoUri, versio: versioNumber, lang });
                setKoodisto(koodistoData);
            }
        })();
    }, [koodistoUri, lang, versioNumber]);

    if (!(koodistoUri && versio)) return <ErrorPage />;
    if (!koodisto) {
        return <Loading />;
    }
    const koodistonMetadata = translateMetadata({ metadata: koodisto.metadata, lang });

    return (
        <>
            <CrumbTrail trail={[{ key: koodistoUri, label: koodistonMetadata?.nimi || '' }]} />
            <MainHeaderContainer>
                <HeadingDivider>
                    <h1>{koodistonMetadata?.nimi}</h1>
                    <VersionPicker version={+(versio || 1)} versions={koodisto.koodistoVersio.length} />
                </HeadingDivider>
                <MainHeaderButtonsContainer>
                    <Button
                        name={'KOODISTOSIVU_MUOKKAA_KOODISTOA_BUTTON'}
                        variant={'outlined'}
                        onClick={() => navigate(`/koodisto/edit/${koodistoUri}/${versio}`)}
                    >
                        <FormattedMessage
                            id={'KOODISTOSIVU_MUOKKAA_KOODISTOA_BUTTON'}
                            defaultMessage={'Muokkaa koodistoa'}
                        />
                    </Button>
                    <Button variant={'outlined'} onClick={() => setUploadCsvVisible(true)} name={`${koodistoUri}-csv`}>
                        <FormattedMessage
                            id={'KOODISTOSIVU_TUO_VIE_KOODISTO_BUTTON'}
                            defaultMessage={'Lataa / tuo koodisto'}
                        />
                    </Button>
                </MainHeaderButtonsContainer>
            </MainHeaderContainer>
            <MainContainer>
                <KoodistoInfo {...koodisto} />
                <KoodistoPageAccordion koodisto={koodisto} />
            </MainContainer>
            {uploadCsvVisible && koodistoUri && (
                <CSVFunctionModal
                    koodistoUri={koodistoUri}
                    koodistoVersio={versioNumber}
                    closeUploader={() => setUploadCsvVisible(false)}
                />
            )}
        </>
    );
};
