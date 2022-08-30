import React, { useEffect, useState } from 'react';
import { FormattedMessage } from 'react-intl';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { useNavigate, useParams } from 'react-router-dom';
import { fetchPageKoodisto } from '../../api/koodisto';
import { translateMetadata } from '../../utils';
import { PageKoodisto, KoodiList } from '../../types';
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
import { CrumbTrail } from '../../components/CrumbTrail';
import VersionPicker from '../../components/VersionPicker';
import { ErrorPage } from '../ErrorPage';
import { fetchKoodistoKoodis } from '../../api/koodi';

export const KoodistoPage: React.FC = () => {
    const { versio, koodistoUri } = useParams();
    const versioNumber = versio ? +versio : undefined;
    const navigate = useNavigate();
    const [lang] = useAtom(casMeLangAtom);
    const [koodisto, setKoodisto] = useState<PageKoodisto | undefined>();
    const [uploadCsvVisible, setUploadCsvVisible] = useState<boolean>(false);
    const [koodiList, setKoodiList] = useState<KoodiList[] | undefined>(undefined);
    useEffect(() => {
        if (koodistoUri && versioNumber) {
            (async () => setKoodiList(await fetchKoodistoKoodis(koodistoUri, versioNumber)))();
        } else {
            setKoodiList([]);
        }
    }, [koodistoUri, versioNumber]);
    useEffect(() => {
        if (koodistoUri && versioNumber) {
            (async () => {
                const koodistoData = await fetchPageKoodisto({ koodistoUri, versio: versioNumber, lang });
                setKoodisto(koodistoData);
            })();
        }
    }, [koodistoUri, lang, versioNumber]);

    if (!(koodistoUri && versio)) return <ErrorPage />;
    if (!koodisto) {
        return <Loading />;
    }
    const koodistonMetadata = translateMetadata({ metadata: koodisto.metadata, lang });
    const disabled = koodisto.tila !== 'LUONNOS';

    return (
        <>
            <CrumbTrail trail={[{ label: koodistonMetadata?.nimi || '' }]} />
            <MainHeaderContainer>
                <HeadingDivider>
                    <h1>{koodistonMetadata?.nimi}</h1>
                    <VersionPicker version={+(versio || 1)} versions={koodisto.koodistoVersio} />
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
                    <Button
                        variant={'outlined'}
                        onClick={() => setUploadCsvVisible(true)}
                        name={`${koodistoUri}-csv`}
                        disabled={disabled}
                    >
                        <FormattedMessage
                            id={'KOODISTOSIVU_TUO_VIE_KOODISTO_BUTTON'}
                            defaultMessage={'Lataa / tuo koodisto'}
                        />
                    </Button>
                </MainHeaderButtonsContainer>
            </MainHeaderContainer>
            <MainContainer>
                <KoodistoInfo {...koodisto} />
                <KoodistoPageAccordion
                    koodiList={koodiList}
                    rinnastuuKoodistoihin={koodisto.rinnastuuKoodistoihin}
                    sisaltaaKoodistot={koodisto.sisaltaaKoodistot}
                    sisaltyyKoodistoihin={koodisto.sisaltyyKoodistoihin}
                    disabled={disabled}
                />
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
