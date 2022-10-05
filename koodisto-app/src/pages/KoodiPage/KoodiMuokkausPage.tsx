import * as React from 'react';
import { useEffect, useState } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import {
    MainHeaderContainer,
    MainContainer,
    MainContainerRow,
    MainContainerRowTitle,
    MainContainerRowTitleMandatory,
    MainContainerRowContent,
} from '../../components/Containers';
import { FormattedMessage, useIntl } from 'react-intl';
import { useResetAtom } from 'jotai/utils';
import { KoodiCrumbTrail } from '../KoodiPage/KoodiCrumbTrail';
import { fetchPageKoodi, updateKoodi, createKoodi, deleteKoodi, fetchKoodistoKoodis } from '../../api/koodi';
import { koodistoListAtom } from '../../api/koodisto';
import { useForm, UseFormReturn, useFieldArray } from 'react-hook-form';
import { Koodi, KoodiList } from '../../types';
import { Loading } from '../../components/Loading';
import { Footer, ConfirmationDialog } from '../../components/Footer';
import { DatePickerController, InputArrayController, InputController } from '../../components/controllers';
import { success } from '../../components/Notification';
import { KoodiPageAccordion } from './KoodiPageAccordion';
import Button from '@opetushallitus/virkailija-ui-components/Button';

const successNotification = (koodiUri: string) => {
    success({
        title: (
            <FormattedMessage
                id={'KOODI_TALLENNUS_MESSAGE_TITLE'}
                defaultMessage={'Koodi tallennettiin onnistuneesti.'}
            />
        ),
        message: (
            <FormattedMessage
                id={'KOODI_TALLENNUS_MESSAGE'}
                defaultMessage={'Tallennettiin koodi uri:lla {koodiUri}'}
                values={{ koodiUri }}
            />
        ),
    });
};

const deleteSuccess = () => {
    success({
        title: <FormattedMessage id={'KOODI_POISTA_OK_TITLE'} defaultMessage={'Koodi poistettiin onnistuneesti.'} />,
        message: <FormattedMessage id={'KOODI_POISTA_OK_MESSAGE'} defaultMessage={'Koodi on poistettu'} />,
    });
};

export const KoodiMuokkausPage: React.FC = () => {
    const { koodiUri, koodiVersio } = useParams();
    const isEditing = koodiUri && koodiVersio;
    const [searchParams] = useSearchParams();
    const newKoodiKoodistoUri = searchParams.get('koodistoUri');
    const newKoodiKoodistoVersio = searchParams.get('koodistoVersio');
    const navigate = useNavigate();
    const refreshKoodistoList = useResetAtom(koodistoListAtom);
    const [loading, setLoading] = useState<boolean>(false);
    const [disabled, setDisabled] = useState<boolean>(false);
    const [koodit, setKoodit] = useState<KoodiList[]>([]);
    const formReturn = useForm<Koodi>({
        shouldUseNativeValidation: true,
        defaultValues: {
            tila: 'LUONNOS',
            metadata: [{ kieli: 'FI' }, { kieli: 'SV' }, { kieli: 'EN' }],
            koodisto: { koodistoUri: newKoodiKoodistoUri || '' },
        },
    });
    useEffect(() => {
        setLoading(true);
        if (isEditing) {
            (async () => {
                const pageKoodi = await fetchPageKoodi(koodiUri, +koodiVersio);
                pageKoodi && formReturn.reset(pageKoodi);
                const koodiList =
                    (await (pageKoodi?.koodisto &&
                        fetchKoodistoKoodis(pageKoodi.koodisto.koodistoUri, Number(pageKoodi.koodisto.versio)))) || [];
                koodiList && setKoodit(koodiList);
                setDisabled(pageKoodi?.tila !== 'LUONNOS');
            })();
        } else {
            (async () => {
                const koodiList =
                    (await (newKoodiKoodistoUri &&
                        newKoodiKoodistoVersio &&
                        fetchKoodistoKoodis(newKoodiKoodistoUri, Number(newKoodiKoodistoVersio)))) || [];
                koodiList && setKoodit(koodiList);
            })();
        }
        setLoading(false);
    }, [koodiUri, koodiVersio, formReturn, isEditing, newKoodiKoodistoUri, newKoodiKoodistoVersio]);
    const save = async (koodi: Koodi) => {
        if (isEditing) await persist(koodi, updateKoodi);
        else await persist(koodi, createKoodi, refreshKoodistoList);
    };
    const persist = async (
        koodi: Koodi,
        persistFunction: (koodi: Koodi) => Promise<Koodi | undefined>,
        callback?: () => void
    ) => {
        setLoading(true);
        const data = await persistFunction(koodi);
        setLoading(false);
        if (data) {
            successNotification(data.koodiUri);
            formReturn.reset(data);
            callback && callback();
            navigate(`/koodi/view/${data.koodiUri}/${data.versio}`);
        }
    };

    const modifyAction = async (
        koodi: Koodi,
        action: (koodi: Koodi) => Promise<Koodi | undefined>,
        showNotification: () => void
    ) => {
        setLoading(true);
        if (await action(koodi)) {
            showNotification();
            refreshKoodistoList();
            navigate(`/koodisto/view/${koodi.koodisto.koodistoUri}`);
        } else {
            setLoading(false);
        }
    };

    const deleteAction = async (koodi: Koodi) =>
        modifyAction(koodi, async (koodi) => ((await deleteKoodi(koodi)) ? koodi : undefined), deleteSuccess);

    const setNextNumber = async () => {
        const nextNumber = (
            (koodit
                .map((a) => a.koodiArvo)
                .map((a) => Number(a))
                .filter(Boolean)
                .reduce((p, c) => (p < c ? c : p), 0) || 0) + 1
        )
            .toString()
            .padStart(3, '0');
        formReturn.setValue('koodiArvo', nextNumber);
    };
    return (
        (loading && <Loading />) || (
            <KoodiMuokkausPageComponent
                {...formReturn}
                save={save}
                deleteAction={deleteAction}
                setNextNumber={setNextNumber}
                disabled={disabled}
                koodistoUri={newKoodiKoodistoUri || undefined}
                isEditing={!!isEditing}
                versio={+(koodiVersio || 0)}
            />
        )
    );
};
const KoodiMuokkausPageComponent: React.FC<
    {
        save: (a: Koodi) => void;
        deleteAction: (koodi: Koodi) => void;
        setNextNumber: () => void;
        disabled: boolean;
        koodistoUri?: string;
        isEditing: boolean;
        versio: number;
    } & UseFormReturn<Koodi>
> = ({
    register,
    handleSubmit,
    save,
    deleteAction,
    setNextNumber,
    control,
    getValues,
    isEditing,
    versio,
    disabled,
    koodistoUri,
}) => {
    const { koodiUri, koodiVersio } = useParams();
    const { formatMessage } = useIntl();
    const sisaltyyKoodeihinReturn = useFieldArray<Koodi, 'sisaltyyKoodeihin'>({
        control,
        name: 'sisaltyyKoodeihin',
    });
    const rinnastuuKoodeihinReturn = useFieldArray<Koodi, 'rinnastuuKoodeihin'>({
        control,
        name: 'rinnastuuKoodeihin',
    });
    const sisaltaaKooditReturn = useFieldArray<Koodi, 'sisaltaaKoodit'>({
        control,
        name: 'sisaltaaKoodit',
    });
    return (
        <>
            <KoodiCrumbTrail koodi={getValues()} koodistoUriParam={koodistoUri} />
            <MainHeaderContainer>
                {(isEditing && (
                    <FormattedMessage
                        id={'KOODI_MUOKKAA_SIVU_TITLE'}
                        defaultMessage={'Muokkaa koodia'}
                        tagName={'h1'}
                    />
                )) || <FormattedMessage id={'KOODI_LISAA_SIVU_TITLE'} defaultMessage={'Lisää koodi'} tagName={'h1'} />}
            </MainHeaderContainer>
            <MainContainer>
                <MainContainerRow>
                    <MainContainerRowTitleMandatory id={'FIELD_TITLE_koodiArvo'} defaultMessage={'Koodiarvo'} />
                    <MainContainerRowContent>
                        <InputController
                            control={control}
                            name={'koodiArvo'}
                            rules={{
                                required: formatMessage({
                                    id: 'KOODIARVO_PAKOLLINEN',
                                    defaultMessage: 'Syötä koodiarvo',
                                }),
                            }}
                            disabled={disabled}
                        />
                    </MainContainerRowContent>
                    <Button name={'KOODI_MUOKKAA_SEURAAVA_NUMERO'} variant={'text'} onClick={setNextNumber}>
                        <FormattedMessage
                            id={'KOODI_MUOKKAA_SEURAAVA_NUMERO'}
                            defaultMessage={'Seuraava juokseva numero'}
                        />
                    </Button>
                </MainContainerRow>
                <MainContainerRow>
                    <InputArrayController<Koodi>
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
                    <InputArrayController<Koodi>
                        control={control}
                        getValues={getValues}
                        title={{ id: 'FIELD_ROW_TITLE_LYHYTNIMI', defaultMessage: 'Lyhenne' }}
                        fieldPath={'lyhytNimi'}
                        disabled={disabled}
                    />
                </MainContainerRow>
                <MainContainerRow>
                    <MainContainerRowTitleMandatory
                        id={'FIELD_TITLE_voimassaAlkuPvm'}
                        defaultMessage={'Alkupäivämäärä'}
                    />
                    <MainContainerRowContent>
                        <DatePickerController<Koodi>
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
                    <MainContainerRowTitle id={'FIELD_TITLE_voimassaLoppuPvm'} defaultMessage={'Loppupäivämäärä'} />
                    <MainContainerRowContent>
                        <DatePickerController<Koodi> name={'voimassaLoppuPvm'} control={control} disabled={disabled} />
                    </MainContainerRowContent>
                </MainContainerRow>
                <MainContainerRow>
                    <InputArrayController<Koodi>
                        large={true}
                        control={control}
                        getValues={getValues}
                        title={{ id: 'FIELD_ROW_TITLE_KUVAUS', defaultMessage: 'Kuvaus' }}
                        fieldPath={'kuvaus'}
                        disabled={disabled}
                    />
                </MainContainerRow>
                {isEditing && (
                    <KoodiPageAccordion
                        editable
                        koodi={getValues()}
                        rinnastuuKoodeihinReturn={rinnastuuKoodeihinReturn}
                        sisaltaaKooditReturn={sisaltaaKooditReturn}
                        sisaltyyKoodeihinReturn={sisaltyyKoodeihinReturn}
                    />
                )}
            </MainContainer>
            <Footer
                state={getValues().tila}
                latest={versio === Math.max(...(getValues().koodiVersio || []))}
                locked={getValues().tila === 'HYVAKSYTTY' || getValues().koodiUri === undefined}
                returnPath={
                    (koodiUri && `/koodi/view/${koodiUri}/${koodiVersio}`) ||
                    (koodistoUri && `/koodisto/view/${koodistoUri}`) ||
                    '/'
                }
                save={handleSubmit((a) => save(a))}
                localisationPrefix={'KOODI'}
                removeDialog={(close: () => void) => (
                    <ConfirmationDialog
                        action={() => {
                            deleteAction(getValues());
                            close();
                        }}
                        close={close}
                        confirmationMessage={{
                            id: 'KOODI_POISTA_CONFIRMATION',
                            defaultMessage: 'Kyllä, poista koodi lopullisesti',
                        }}
                        buttonText={{ id: 'VAHVISTA_POISTO', defaultMessage: 'Vahvista poisto' }}
                    >
                        <>
                            <FormattedMessage
                                id={'KOODI_POISTA_TITLE'}
                                defaultMessage={'Poista koodi'}
                                tagName={'h2'}
                            />
                            <FormattedMessage
                                id={'KOODI_POISTA_DESCRIPTION'}
                                defaultMessage={'Koodi ja kaikki sen suhteet poistetaan'}
                                tagName={'p'}
                            />
                        </>
                    </ConfirmationDialog>
                )}
            />
        </>
    );
};
