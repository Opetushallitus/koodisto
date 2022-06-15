import React, { useEffect, useState } from 'react';
import { FormattedMessage, useIntl } from 'react-intl';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { useNavigate, useParams } from 'react-router-dom';
import Select from '@opetushallitus/virkailija-ui-components/Select';
import { fetchPageKoodisto } from '../../api/koodisto';
import { translateMetadata } from '../../utils';
import { PageKoodisto, SelectOptionType } from '../../types';
import { Loading } from '../../components/Loading';
import InfoFields from './InfoFields';
import KoodistoPageAccordion from './KoodistoPageAccordion';
import { CSVFunctionModal } from '../../modals/CSVFunctionModal';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import {
    MainHeaderContainer,
    HeadingDivider,
    SelectContainer,
    MainHeaderButtonsContainer,
    MainContainer,
} from '../../components/Containers';
import { KoodistoPathContainer } from '../../components/KoodistoPathContainer';

export const KoodistoPage: React.FC = () => {
    const { versio, koodistoUri } = useParams();
    const versioNumber = versio ? +versio : undefined;
    const navigate = useNavigate();
    const { formatMessage } = useIntl();
    const [lang] = useAtom(casMeLangAtom);
    const [koodisto, setKoodisto] = useState<PageKoodisto | undefined>();
    const [uploadCsvVisible, setUploadCsvVisible] = useState<boolean>(false);

    const incomingVersioOption: SelectOptionType = {
        label: formatMessage(
            {
                id: 'KOODISTOSIVU_VERSIO_DROPDOWN_LABEL',
                defaultMessage: 'Versio {versio}',
            },
            { versio: versio || '' }
        ),
        value: versio || '',
    };
    useEffect(() => {
        (async () => {
            if (koodistoUri && versioNumber) {
                const koodistoData = await fetchPageKoodisto({ koodistoUri, versio: versioNumber, lang });
                setKoodisto(koodistoData);
            }
        })();
    }, [koodistoUri, lang, versioNumber]);

    if (!koodisto) {
        return <Loading />;
    }
    const koodistonMetadata = translateMetadata({ metadata: koodisto.metadata, lang });
    const versioOptions = koodisto.koodiVersio.map((a) => {
        return {
            label: formatMessage(
                {
                    id: 'KOODISTOSIVU_VERSIO_DROPDOWN_LABEL',
                    defaultMessage: 'Versio {versio}',
                },
                { versio: a }
            ),
            value: a.toString(),
        };
    });
    return (
        <>
            <KoodistoPathContainer path={[koodistonMetadata?.nimi || '']} />
            <MainHeaderContainer>
                <HeadingDivider>
                    <h1>{koodistonMetadata?.nimi}</h1>
                    <SelectContainer>
                        <Select
                            onChange={(value) => {
                                if ((value as SelectOptionType).value !== incomingVersioOption.value) {
                                    navigate(`/koodisto/${koodisto.koodistoUri}/${(value as SelectOptionType).value}`);
                                }
                            }}
                            placeholder={formatMessage({
                                id: 'KOODISTOVERSIO_DROPDOWN',
                                defaultMessage: 'Koodistoversio',
                            })}
                            value={incomingVersioOption}
                            options={versioOptions}
                        />
                    </SelectContainer>
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
                <InfoFields koodisto={koodisto} />
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
