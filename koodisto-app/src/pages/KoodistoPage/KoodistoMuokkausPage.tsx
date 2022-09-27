import * as React from 'react';
import { useEffect, useState } from 'react';
import {
    fetchPageKoodisto,
    updateKoodisto,
    createKoodisto,
    deleteKoodisto,
    createKoodistoVersion,
    koodistoListAtom,
} from '../../api/koodisto';
import { useParams, useNavigate } from 'react-router-dom';
import { useForm, useFieldArray } from 'react-hook-form';
import { PageKoodisto, SelectOption, Kieli } from '../../types';
import { Loading } from '../../components/Loading';
import Input from '@opetushallitus/virkailija-ui-components/Input';
import { FormattedMessage, useIntl } from 'react-intl';
import { CrumbTrail } from '../../components/CrumbTrail';
import { translateMetadata } from '../../utils';
import { useAtom } from 'jotai';
import { useResetAtom } from 'jotai/utils';
import { casMeLangAtom } from '../../api/kayttooikeus';
import { DatePickerController, SelectController, InputArrayController } from '../../components/controllers';
import { success } from '../../components/Notification';
import {
    MainHeaderContainer,
    MainContainerRow,
    MainContainer,
    MainContainerRowTitle,
    MainContainerRowTitleMandatory,
    MainContainerRowContent,
} from '../../components/Containers';
import { koodistoRyhmaOptionsAtom } from '../../api/koodistoRyhma';
import { organisaatioSelectAtom } from '../../api/organisaatio';
import { Footer, ConfirmationDialog } from '../../components/Footer';
import KoodistoPageAccordion from './KoodistoPageAccordion';

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

const versioningSuccess = () => {
    success({
        title: (
            <FormattedMessage
                id={'KOODISTO_VERSIOINTI_OK_TITLE'}
                defaultMessage={'Koodisto versioitiin onnistuneesti.'}
            />
        ),
        message: (
            <FormattedMessage
                id={'KOODISTO_VERSIOINTI_OK_MESSAGE'}
                defaultMessage={'Koodistosta on luotu versio sekä luonnos.'}
            />
        ),
    });
};

const deleteSuccess = () => {
    success({
        title: (
            <FormattedMessage id={'KOODISTO_POISTA_OK_TITLE'} defaultMessage={'Koodisto poistettiin onnistuneesti.'} />
        ),
        message: <FormattedMessage id={'KOODISTO_POISTA_OK_MESSAGE'} defaultMessage={'Koodisto on poistettu'} />,
    });
};

export const KoodistoMuokkausPage: React.FC = () => {
    const { versio, koodistoUri } = useParams();
    const navigate = useNavigate();
    const { formatMessage } = useIntl();
    const [lang] = useAtom(casMeLangAtom);
    const [koodistoRyhmaOptions] = useAtom<SelectOption[]>(koodistoRyhmaOptionsAtom);
    const [organisaatioSelect] = useAtom<SelectOption[]>(organisaatioSelectAtom);
    const refreshKoodistoList = useResetAtom(koodistoListAtom);
    const [loading, setLoading] = useState<boolean>(false);
    const [disabled, setDisabled] = useState<boolean>(false);
    const versioNumber = versio ? +versio : undefined;
    const isEditing = koodistoUri && versioNumber;
    const { control, register, handleSubmit, reset, getValues } = useForm<PageKoodisto>({
        shouldUseNativeValidation: true,
        defaultValues: {
            tila: 'LUONNOS',
            metadata: [{ kieli: 'FI' }, { kieli: 'SV' }, { kieli: 'EN' }],
        },
    });
    const sisaltyyKoodistoihinFieldArray = useFieldArray<PageKoodisto, 'sisaltyyKoodistoihin'>({
        control,
        name: 'sisaltyyKoodistoihin',
    });
    const rinnastuuKoodistoihinFieldArray = useFieldArray<PageKoodisto, 'rinnastuuKoodistoihin'>({
        control,
        name: 'rinnastuuKoodistoihin',
    });
    const sisaltaaKoodistotFieldArray = useFieldArray<PageKoodisto, 'sisaltaaKoodistot'>({
        control,
        name: 'sisaltaaKoodistot',
    });
    const koodistonMetadata = translateMetadata({ metadata: getValues('metadata'), lang });
    useEffect(() => {
        (async () => {
            if (isEditing) {
                setLoading(true);
                const koodistoData = await fetchPageKoodisto({ koodistoUri, versio: versioNumber, lang });
                reset(koodistoData);
                setDisabled(koodistoData?.tila !== 'LUONNOS');
                setLoading(false);
            }
        })();
    }, [isEditing, koodistoUri, lang, reset, versioNumber]);
    if (loading) return <Loading />;
    const save = async (koodisto: PageKoodisto) => {
        if (isEditing) await persist(koodisto, updateKoodisto);
        else await persist(koodisto, createKoodisto);
    };
    const persist = async (
        koodisto: PageKoodisto,
        persistFunction: (props: { koodisto: PageKoodisto; lang: Kieli }) => Promise<PageKoodisto | undefined>
    ) => {
        setLoading(true);
        const updated = await persistFunction({ koodisto, lang });
        setLoading(false);
        if (updated) {
            refreshKoodistoList();
            successNotification(updated.koodistoUri);
            reset(updated);
            navigate(`/koodisto/view/${updated.koodistoUri}/${updated.versio}`);
        }
    };

    const modifyAction = async (
        koodisto: PageKoodisto,
        action: (koodisto: PageKoodisto) => Promise<PageKoodisto | undefined>,
        showNotification: () => void
    ) => {
        setLoading(true);
        if (await action(koodisto)) {
            refreshKoodistoList();
            showNotification();
            navigate(`/koodisto/view/${koodisto.koodistoUri}`);
        } else {
            setLoading(false);
        }
    };

    const versioningAction = async (koodisto: PageKoodisto) =>
        modifyAction(
            koodisto,
            async (koodisto) => createKoodistoVersion(koodisto.koodistoUri, versioNumber || 1, lang),
            versioningSuccess
        );

    const deleteAction = async (koodisto: PageKoodisto) =>
        modifyAction(
            koodisto,
            async (koodisto) => {
                return (await deleteKoodisto(koodisto)) ? koodisto : undefined;
            },
            deleteSuccess
        );

    return (
        <>
            <CrumbTrail trail={[{ label: koodistonMetadata?.nimi || '' }]} />
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
                        <MainContainerRowTitleMandatory
                            id={'FIELD_TITLE_koodistoRyhmaUri'}
                            defaultMessage={'Koodistoryhmä'}
                        />
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
                                disabled={disabled}
                            />
                        </MainContainerRowContent>
                    </MainContainerRow>
                    <MainContainerRow>
                        <InputArrayController<PageKoodisto>
                            control={control}
                            getValues={getValues}
                            title={{ id: 'FIELD_ROW_TITLE_NIMI', defaultMessage: 'Nimi' }}
                            fieldPath={'nimi'}
                            rules={{
                                required: formatMessage({
                                    id: 'NIMI_PAKOLLINEN',
                                    defaultMessage: 'Syötä nimi',
                                }),
                            }}
                            disabled={disabled}
                            mandatory
                        />
                    </MainContainerRow>
                    <MainContainerRow>
                        <MainContainerRowTitleMandatory
                            id={'FIELD_TITLE_voimassaAlkuPvm'}
                            defaultMessage={'Voimassa'}
                        />
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
                                disabled={disabled}
                            />
                        </MainContainerRowContent>
                    </MainContainerRow>
                    <MainContainerRow>
                        <MainContainerRowTitle id={'FIELD_TITLE_voimassaLoppuPvm'} defaultMessage={'Voimassa loppu'} />
                        <MainContainerRowContent>
                            <DatePickerController<PageKoodisto>
                                name={'voimassaLoppuPvm'}
                                control={control}
                                disabled={disabled}
                            />
                        </MainContainerRowContent>
                    </MainContainerRow>
                    <MainContainerRow>
                        <MainContainerRowTitleMandatory
                            id={'FIELD_TITLE_organisaatioNimi'}
                            defaultMessage={'Organisaatio'}
                        />
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
                                disabled={disabled}
                            />
                        </MainContainerRowContent>
                    </MainContainerRow>
                    <MainContainerRow>
                        <MainContainerRowTitle id={'FIELD_TITLE_omistaja'} defaultMessage={'Omistaja'} />
                        <MainContainerRowContent>
                            <Input {...register('omistaja')} disabled={disabled} />
                        </MainContainerRowContent>
                    </MainContainerRow>
                    <MainContainerRow>
                        <InputArrayController<PageKoodisto>
                            large={true}
                            control={control}
                            getValues={getValues}
                            title={{ id: 'FIELD_ROW_TITLE_KUVAUS', defaultMessage: 'Kuvaus' }}
                            fieldPath={'kuvaus'}
                            disabled={disabled}
                        />
                    </MainContainerRow>
                    {isEditing && (
                        <KoodistoPageAccordion
                            editable
                            sisaltyyKoodistoihin={getValues('sisaltyyKoodistoihin') || []}
                            sisaltyyKoodistoihinReturn={sisaltyyKoodistoihinFieldArray}
                            rinnastuuKoodistoihin={getValues('rinnastuuKoodistoihin') || []}
                            rinnastuuKoodistoihinReturn={rinnastuuKoodistoihinFieldArray}
                            sisaltaaKoodistot={getValues('sisaltaaKoodistot') || []}
                            sisaltaaKoodistotReturn={sisaltaaKoodistotFieldArray}
                            koodiList={[]}
                            disabled={disabled}
                        />
                    )}
                </form>
            </MainContainer>
            <Footer
                state={getValues().tila}
                latest={versioNumber === Math.max(...(getValues().koodistoVersio || []))}
                returnPath={(koodistoUri && `/koodisto/view/${koodistoUri}/${versio}`) || '/'}
                save={handleSubmit((a) => save(a))}
                localisationPrefix={'KOODISTO'}
                versionDialog={(close: () => void) => (
                    <ConfirmationDialog
                        action={() => {
                            versioningAction(getValues());
                            close();
                        }}
                        close={close}
                        confirmationMessage={{
                            id: 'KOODISTO_VERSIOI_CONFIRMATION',
                            defaultMessage: 'Kyllä, versioidaan koodisto ja sen kaikki koodit',
                        }}
                        buttonText={{ id: 'VAHVISTA_VERSIOINTI', defaultMessage: 'Vahvista versiointi' }}
                    >
                        <>
                            <FormattedMessage
                                id={'KOODISTO_VERSIOI_TITLE'}
                                defaultMessage={'Versioi koodisto'}
                                tagName={'h2'}
                            />
                            <FormattedMessage
                                id={'KOODISTO_VERSIOI_DESCRIPTION'}
                                defaultMessage={'Koodistosta ja kaikista sen koodeista luodaan uusi versio'}
                                tagName={'p'}
                            />
                        </>
                    </ConfirmationDialog>
                )}
                removeDialog={(close: () => void) => (
                    <ConfirmationDialog
                        action={() => {
                            deleteAction(getValues());
                            close();
                        }}
                        close={close}
                        confirmationMessage={{
                            id: 'KOODISTO_POISTA_CONFIRMATION',
                            defaultMessage:
                                'Kyllä, koodisto sekä kaikki koodit ja koodistojen suhteet poistetaan lopullisesti',
                        }}
                        buttonText={{ id: 'VAHVISTA_POISTO', defaultMessage: 'Vahvista poisto' }}
                    >
                        <>
                            <FormattedMessage
                                id={'KOODISTO_POISTA_TITLE'}
                                defaultMessage={'Poista koodisto'}
                                tagName={'h2'}
                            />
                            <FormattedMessage
                                id={'KOODISTO_POISTA_DESCRIPTION'}
                                defaultMessage={
                                    'Koodisto ja kaikki sen sisältämät koodit suhteineen poistetaan lopullisesti. Operaatiota ei voi peruuttaa.'
                                }
                                tagName={'p'}
                            />
                        </>
                    </ConfirmationDialog>
                )}
            />
        </>
    );
};
