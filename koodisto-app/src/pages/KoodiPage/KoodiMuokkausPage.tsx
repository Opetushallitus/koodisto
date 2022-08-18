import * as React from 'react';
import { useEffect, useState } from 'react';
import { useParams, useNavigate, useSearchParams } from 'react-router-dom';
import {
    MainHeaderContainer,
    MainContainer,
    MainContainerRow,
    MainContainerRowTitle,
    MainContainerRowContent,
} from '../../components/Containers';
import { FormattedMessage, useIntl } from 'react-intl';
import { KoodiCrumbTrail } from '../KoodiPage/KoodiCrumbTrail';
import { fetchPageKoodi, updateKoodi, createKoodi, deleteKoodi } from '../../api/koodi';
import { useForm, UseFormReturn, useFieldArray, ArrayPath } from 'react-hook-form';
import { Koodi, KoodiRelation } from '../../types';
import { Loading } from '../../components/Loading';
import Input from '@opetushallitus/virkailija-ui-components/Input';
import { Footer, ConfirmationDialog } from '../../components/Footer';
import { DatePickerController, InputArrayController } from '../../components/controllers';
import { success } from '../../components/Notification';
import { KoodiPageAccordion } from './KoodiPageAccordion';

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
    const navigate = useNavigate();
    const [loading, setLoading] = useState<boolean>(false);
    const [disabled, setDisabled] = useState<boolean>(false);
    const formReturn = useForm<Koodi>({
        shouldUseNativeValidation: true,
        defaultValues: {
            tila: 'LUONNOS',
            metadata: [{ kieli: 'FI' }, { kieli: 'SV' }, { kieli: 'EN' }],
            koodisto: { koodistoUri: newKoodiKoodistoUri || '' },
        },
    });
    useEffect(() => {
        if (isEditing) {
            (async () => {
                setLoading(true);
                const data = await fetchPageKoodi(koodiUri, +koodiVersio);
                data && formReturn.reset(data);
                setDisabled(data?.tila !== 'LUONNOS');
                setLoading(false);
            })();
        }
    }, [koodiUri, koodiVersio, formReturn, isEditing]);
    const save = async (koodi: Koodi) => {
        if (isEditing) await persist(koodi, updateKoodi);
        else await persist(koodi, createKoodi);
    };
    const persist = async (koodi: Koodi, persistFunction: (koodi: Koodi) => Promise<Koodi | undefined>) => {
        setLoading(true);
        const data = await persistFunction(koodi);
        setLoading(false);
        if (data) {
            successNotification(data.koodiUri);
            formReturn.reset(data);
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
            navigate(`/koodisto/view/${koodi.koodisto.koodistoUri}`);
        } else {
            setLoading(false);
        }
    };

    const deleteAction = async (koodi: Koodi) =>
        modifyAction(koodi, async (koodi) => ((await deleteKoodi(koodi)) ? koodi : undefined), deleteSuccess);

    return (
        (loading && <Loading />) || (
            <KoodiMuokkausPageComponent
                {...formReturn}
                save={save}
                deleteAction={deleteAction}
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
        disabled: boolean;
        koodistoUri?: string;
        isEditing: boolean;
        versio: number;
    } & UseFormReturn<Koodi>
> = ({ register, handleSubmit, save, deleteAction, control, getValues, isEditing, versio, disabled, koodistoUri }) => {
    const { koodiUri, koodiVersio } = useParams();
    const { formatMessage } = useIntl();
    const sisaltyyKoodeihinReturn = useFieldArray<Koodi, ArrayPath<Koodi>, keyof KoodiRelation | 'id'>({
        control,
        name: 'sisaltyyKoodeihin' as ArrayPath<Koodi>,
    });
    const rinnastuuKoodeihinReturn = useFieldArray<Koodi, ArrayPath<Koodi>, keyof KoodiRelation | 'id'>({
        control,
        name: 'rinnastuuKoodeihin' as ArrayPath<Koodi>,
    });
    const sisaltaaKooditReturn = useFieldArray<Koodi, ArrayPath<Koodi>, keyof KoodiRelation | 'id'>({
        control,
        name: 'sisaltaaKoodit' as ArrayPath<Koodi>,
    });
    return (
        <>
            {<KoodiCrumbTrail koodi={getValues()} koodistoUriParam={koodistoUri} />}
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
                    <MainContainerRowTitle id={'FIELD_TITLE_koodiArvo'} defaultMessage={'Arvo'} />
                    <MainContainerRowContent>
                        <Input {...register('koodiArvo')} disabled={disabled} />
                    </MainContainerRowContent>
                </MainContainerRow>
                <MainContainerRow>
                    <InputArrayController<Koodi>
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
                        disabled={disabled}
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
                    <MainContainerRowTitle id={'FIELD_TITLE_voimassaAlkuPvm'} defaultMessage={'Voimassa'} />
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
                    <MainContainerRowTitle id={'FIELD_TITLE_voimassaLoppuPvm'} defaultMessage={'Voimassa loppu'} />
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
                        koodi={getValues() || undefined}
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
