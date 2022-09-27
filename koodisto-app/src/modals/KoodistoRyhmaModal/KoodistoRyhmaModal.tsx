import * as React from 'react';
import { useEffect, useState } from 'react';
import { Modal, Footer } from '../../components/Modal';
import Button from '@opetushallitus/virkailija-ui-components/Button';
import { FormattedMessage, useIntl } from 'react-intl';
import { useForm } from 'react-hook-form';
import Input from '@opetushallitus/virkailija-ui-components/Input';
import { IconWrapper } from '../../components/IconWapper';
import { success } from '../../components/Notification';
import { KoodistoRyhma } from '../../types';
import { EmptyKoodistoRyhmaList } from './EmptyKoodistoRyhmaList';
import {
    fetchEmptyKoodistoRyhma,
    fetchKoodistoRyhma,
    updateKoodistoRyhma,
    createKoodistoRyhma,
} from '../../api/koodistoRyhma';
import { MainContainerRowTitleMandatory } from '../../components/Containers';

type Props = {
    koodistoRyhmaUri?: string;
    closeModal: () => void;
};
type AddKoodistoRyhma = {
    fi: string;
    sv: string;
    en: string;
};

export const KoodistoRyhmaModal: React.FC<Props> = ({ koodistoRyhmaUri, closeModal }) => {
    const { formatMessage } = useIntl();

    const [emptyKoodistoRyhma, setEmptyKoodistoRyhma] = useState<KoodistoRyhma[]>([]);
    const { register, handleSubmit, getValues, setValue, reset } = useForm<AddKoodistoRyhma>({
        shouldUseNativeValidation: true,
    });
    useEffect(() => {
        (async () => {
            const emptyRyhmat = await fetchEmptyKoodistoRyhma();
            emptyRyhmat && setEmptyKoodistoRyhma(emptyRyhmat);
        })();
    }, []);
    useEffect(() => {
        (async () => {
            if (!!koodistoRyhmaUri) {
                const ryhma = await fetchKoodistoRyhma(koodistoRyhmaUri);
                if (!!ryhma) {
                    setValue('fi', ryhma.nimi.fi);
                    setValue('sv', ryhma.nimi.sv);
                    setValue('en', ryhma.nimi.en);
                }
            }
        })();
    }, [koodistoRyhmaUri, setValue]);

    const update = async (uri: string, nimi: { fi: string; sv: string; en: string }) => {
        const updated = await updateKoodistoRyhma(uri, { nimi });
        updated &&
            success({
                title: (
                    <FormattedMessage
                        id={'KOODISTO_RYHMA_TALLENNUS_MESSAGE_TITLE'}
                        defaultMessage={'Koodistoryhmä tallennettiin onnistuneesti.'}
                    />
                ),
                message: (
                    <FormattedMessage
                        id={'KOODISTO_RYHMA_TALLENNUS_MESSAGE'}
                        defaultMessage={'Tallennettiin koodistorymä uri:lla {koodistoRyhmaUri}'}
                        values={{ koodistoRyhmaUri: updated?.koodistoRyhmaUri }}
                    />
                ),
            });
    };
    const create = async (nimi: { fi: string; sv: string; en: string }) => {
        const created = await createKoodistoRyhma({ nimi });
        created &&
            success({
                title: (
                    <FormattedMessage
                        id={'KOODISTO_RYHMA_LUONTI_MESSAGE_TITLE'}
                        defaultMessage={'Uusi koodistoryhmä luotiin onnistuneesti.'}
                    />
                ),
                message: (
                    <FormattedMessage
                        id={'KOODISTO_RYHMA_LUONTI_MESSAGE'}
                        defaultMessage={'Luotiin koodistorymä uri:lla {koodistoRyhmaUri}'}
                        values={{ koodistoRyhmaUri: created?.koodistoRyhmaUri }}
                    />
                ),
            });
        const data = await fetchEmptyKoodistoRyhma();
        data && setEmptyKoodistoRyhma(data);
        reset();
    };

    const copyToNames = (): void => {
        const muutosTiedot = getValues();
        setValue('sv', muutosTiedot['fi']);
        setValue('en', muutosTiedot['fi']);
    };
    return (
        <Modal
            body={
                <>
                    <h2>
                        {(koodistoRyhmaUri && (
                            <FormattedMessage id={'KOODISTO_RYHMA_TITLE'} defaultMessage={'Muokkaa koodistoryhmää'} />
                        )) || (
                            <FormattedMessage
                                id={'KOODISTO_RYHMA_TITLE_UUSI'}
                                defaultMessage={'Luo uusi koodistoryhmä'}
                            />
                        )}
                    </h2>
                    <form>
                        <MainContainerRowTitleMandatory id={'KOODISTO_RYHMA_FI'} defaultMessage={'FI'} />
                        <Input
                            {...register('fi', {
                                required: formatMessage({
                                    id: 'FI_NIMI_PAKOLLINEN',
                                    defaultMessage: 'Syötä suomenkielinen nimi.',
                                }),
                            })}
                            suffix={
                                <div
                                    title={formatMessage({
                                        id: 'KOPIOI_MUIHIN_NIMIIN',
                                        defaultMessage: 'Kopioi muihin kieliin',
                                    })}
                                    onClick={() => copyToNames()}
                                >
                                    <IconWrapper
                                        icon="ci:copy"
                                        color={'gray'}
                                        height={'1.5rem'}
                                        name={'KOPIOI_MUIHIN_NIMIIN'}
                                    />
                                </div>
                            }
                        />
                        <MainContainerRowTitleMandatory id={'KOODISTO_RYHMA_SV'} defaultMessage={'SV'} />
                        <Input
                            {...register('sv', {
                                required: formatMessage({
                                    id: 'SV_NIMI_PAKOLLINEN',
                                    defaultMessage: 'Syötä ruotsinkielinen nimi',
                                }),
                            })}
                        />
                        <MainContainerRowTitleMandatory id={'KOODISTO_RYHMA_EN'} defaultMessage={'EN'} />
                        <Input
                            {...register('en', {
                                required: formatMessage({
                                    id: 'EN_NIMI_PAKOLLINEN',
                                    defaultMessage: 'Syötä englanninkielinen nimi',
                                }),
                            })}
                        />
                        {(koodistoRyhmaUri && (
                            <Button
                                disabled={false}
                                onClick={handleSubmit((a) => update(koodistoRyhmaUri, a))}
                                name={'KOODISTO_RYHMA_TALLENNA'}
                            >
                                <FormattedMessage id={'KOODISTO_RYHMA_TALLENNA'} defaultMessage={'Tallenna'} />
                            </Button>
                        )) || (
                            <Button disabled={false} onClick={handleSubmit(create)} name={'KOODISTO_RYHMA_LUO_UUSI'}>
                                <FormattedMessage id={'KOODISTO_RYHMA_LUO_UUSI'} defaultMessage={'Luo ryhmä'} />
                            </Button>
                        )}
                    </form>
                    {emptyKoodistoRyhma.length > 0 && (
                        <EmptyKoodistoRyhmaList
                            setEmptyKoodistoRyhma={setEmptyKoodistoRyhma}
                            emptyKoodistoRyhma={emptyKoodistoRyhma}
                        />
                    )}
                </>
            }
            footer={
                <Footer>
                    <Button onClick={closeModal} name={'KOODISTO_RYHMA_SULJE'}>
                        <FormattedMessage id={'KOODISTO_RYHMA_SULJE'} defaultMessage={'Sulje'} />
                    </Button>
                </Footer>
            }
            onClose={closeModal}
        />
    );
};
