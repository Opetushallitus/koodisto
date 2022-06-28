import * as React from 'react';
import { useEffect, useState } from 'react';
import { fetchPageKoodisto, updateKoodisto, createKoodisto } from '../../api/koodisto';
import { useParams, useNavigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { PageKoodisto, SelectOption } from '../../types';
import { Loading } from '../../components/Loading';
import Input from '@opetushallitus/virkailija-ui-components/Input';
import { FormattedMessage, useIntl } from 'react-intl';
import { CrumbTrail } from '../../components/CrumbTrail';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { casMeLangAtom } from '../../api/kayttooikeus';
import { DatePickerController, SelectController, InputArrayController } from '../../components/controllers';
import { success } from '../../components/Notification';
import {
    MainHeaderContainer,
    MainContainerRow,
    MainContainer,
    MainContainerRowTitle,
    MainContainerRowContent,
} from '../../components/Containers';
import { koodistoRyhmaOptionsAtom } from '../../api/koodistoRyhma';
import { organisaatioSelectAtom } from '../../api/organisaatio';
import { Footer } from '../../components/Footer';

const successNotification = (koodistoUri: string) => {
    success({
        title: (
            <FormattedMessage
                id={'KOODISTO_TALLENNUS_MESSAGE_TITLE'}
                defaultMessage={'Koodisto tallennettiin onnistuneesti.'}
            />
        ),
        message: (
            <FormattedMessage
                id={'KOODISTO_TALLENNUS_MESSAGE'}
                defaultMessage={'Tallennettiin koodisto uri:lla {koodistoUri}'}
                values={{ koodistoUri }}
            />
        ),
    });
};

export const KoodistoMuokkausPage: React.FC = () => {
    const { versio, koodistoUri } = useParams();
    const navigate = useNavigate();
    const { formatMessage } = useIntl();
    const [lang] = useAtom(casMeLangAtom);
    const [koodistoRyhmaOptions] = useAtom<SelectOption[]>(koodistoRyhmaOptionsAtom);
    const [organisaatioSelect] = useAtom<SelectOption[]>(organisaatioSelectAtom);
    const [loading, setLoading] = useState<boolean>(false);
    const versioNumber = versio ? +versio : undefined;
    const { control, register, handleSubmit, reset, getValues } = useForm<PageKoodisto>({
        shouldUseNativeValidation: true,
        defaultValues: { metadata: [{ kieli: 'FI' }, { kieli: 'SV' }, { kieli: 'EN' }] },
    });
    const koodistonMetadata = translateMetadata({ metadata: getValues('metadata'), lang });
    useEffect(() => {
        (async () => {
            if (koodistoUri && versioNumber) {
                setLoading(true);
                const koodistoData = await fetchPageKoodisto({ koodistoUri, versio: versioNumber, lang });
                reset(koodistoData);
                setLoading(false);
            }
        })();
    }, [koodistoUri, lang, reset, versioNumber]);
    if (loading) return <Loading />;
    const save = async (koodisto: PageKoodisto) => {
        if (koodistoUri) await update(koodisto);
        else await create(koodisto);
    };
    const update = async (koodisto: PageKoodisto) => {
        setLoading(true);
        const updated = await updateKoodisto({ koodisto, lang });
        setLoading(false);
        if (updated) {
            successNotification(updated.koodistoUri);
            reset(updated);
            navigate(`/koodisto/view/${updated.koodistoUri}/${updated.versio}`);
        }
    };
    const create = async (koodisto: PageKoodisto) => {
        setLoading(true);
        const created = await createKoodisto({ koodisto, lang });
        setLoading(false);
        if (created) {
            successNotification(created.koodistoUri);
            reset(created);
            navigate(`/koodisto/view/${created.koodistoUri}/${created.versio}`);
        }
    };
    return (
        <>
            <CrumbTrail trail={[{ key: koodistoUri || 'new', label: koodistonMetadata?.nimi || '' }]} />
            <MainHeaderContainer>
                <FormattedMessage
                    id={'KOODISTO_MUOKKAA_SIVU_TITLE'}
                    defaultMessage={'Muokkaa koodistoa'}
                    tagName={'h1'}
                />
            </MainHeaderContainer>
            <MainContainer>
                <form>
                    <MainContainerRow>
                        <MainContainerRowTitle id={'FIELD_TITLE_koodistoRyhmaUri'} defaultMessage={'Koodistoryhmä*'} />
                        <MainContainerRowContent>
                            <SelectController
                                control={control}
                                name={'koodistoRyhmaUri'}
                                options={koodistoRyhmaOptions}
                                rules={{
                                    required: formatMessage({
                                        id: 'RYHMA_PAKOLLINEN',
                                        defaultMessage: 'Valitse koodisto-ryhmä.',
                                    }),
                                }}
                            />
                        </MainContainerRowContent>
                    </MainContainerRow>
                    <MainContainerRow>
                        <InputArrayController<PageKoodisto>
                            control={control}
                            getValues={getValues}
                            title={{ id: 'FIELD_ROW_TITLE_NIMI', defaultMessage: 'Nimi*' }}
                            fieldPath={'nimi'}
                            rules={{
                                required: formatMessage({
                                    id: 'NIMI_PAKOLLINEN',
                                    defaultMessage: 'Syötä nimi',
                                }),
                            }}
                        />
                    </MainContainerRow>
                    <MainContainerRow>
                        <MainContainerRowTitle id={'FIELD_TITLE_voimassaAlkuPvm'} defaultMessage={'Voimassa'} />
                        <MainContainerRowContent>
                            <DatePickerController<PageKoodisto>
                                name={'voimassaAlkuPvm'}
                                control={control}
                                rules={{
                                    required: formatMessage({
                                        id: 'ALKUPVM_PAKOLLINEN',
                                        defaultMessage: 'Valitse aloituspäivämäärä.',
                                    }),
                                }}
                            />
                        </MainContainerRowContent>
                    </MainContainerRow>
                    <MainContainerRow>
                        <MainContainerRowTitle id={'FIELD_TITLE_voimassaLoppuPvm'} defaultMessage={'Voimassa loppu'} />
                        <MainContainerRowContent>
                            <DatePickerController<PageKoodisto> name={'voimassaLoppuPvm'} control={control} />
                        </MainContainerRowContent>
                    </MainContainerRow>
                    <MainContainerRow>
                        <MainContainerRowTitle id={'FIELD_TITLE_organisaatioNimi'} defaultMessage={'Organisaatio*'} />
                        <MainContainerRowContent width={25}>
                            <SelectController
                                control={control}
                                name={'organisaatioOid'}
                                options={organisaatioSelect}
                                rules={{
                                    required: formatMessage({
                                        id: 'ORGANISAATIO_PAKOLLINEN',
                                        defaultMessage: 'organisaatio.',
                                    }),
                                }}
                            />
                        </MainContainerRowContent>
                    </MainContainerRow>
                    <MainContainerRow>
                        <MainContainerRowTitle id={'FIELD_TITLE_omistaja'} defaultMessage={'Omistaja'} />
                        <MainContainerRowContent>
                            <Input {...register('omistaja')} />
                        </MainContainerRowContent>
                    </MainContainerRow>
                    <MainContainerRow>
                        <InputArrayController<PageKoodisto>
                            large={true}
                            control={control}
                            getValues={getValues}
                            title={{ id: 'FIELD_ROW_TITLE_KUVAUS', defaultMessage: 'Kuvaus' }}
                            fieldPath={'kuvaus'}
                        />
                    </MainContainerRow>
                </form>
            </MainContainer>
            <Footer
                returnPath={(koodistoUri && `/koodisto/view/${koodistoUri}/${versio}`) || '/'}
                save={handleSubmit((a) => save(a))}
                localisationPrefix={'KOODISTO'}
            />
        </>
    );
};
