import * as React from 'react';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import {
    MainHeaderContainer,
    MainContainer,
    MainContainerRow,
    MainContainerRowTitle,
    MainContainerRowContent,
} from '../../components/Containers';
import { FormattedMessage } from 'react-intl';
import { CrumbTrail } from '../../components/KoodistoPathContainer';
import { fetchPageKoodi } from '../../api/koodi';
import { useForm, UseFormReturn } from 'react-hook-form';
import { Koodi } from '../../types';
import { Loading } from '../../components/Loading';
import Input from '@opetushallitus/virkailija-ui-components/Input';
import { Footer } from '../../components/Footer';

export const KoodiMuokkausPage: React.FC = () => {
    const { koodiUri, koodiVersio } = useParams();
    const [loading, setLoading] = useState<boolean>(false);
    const formReturn = useForm<Koodi>({
        shouldUseNativeValidation: true,
        defaultValues: { metadata: [{ kieli: 'FI' }, { kieli: 'SV' }, { kieli: 'EN' }] },
    });
    useEffect(() => {
        if (koodiUri && koodiVersio) {
            (async () => {
                setLoading(true);
                const data = await fetchPageKoodi(koodiUri, +koodiVersio);
                data && formReturn.reset(data);
                setLoading(false);
            })();
        }
    }, [koodiUri, koodiVersio, formReturn]);
    const save = (a: Koodi) => {
        console.log(a);
    };
    return (loading && <Loading />) || <KoodiMuokkausPageComponent {...formReturn} save={save} />;
};
const KoodiMuokkausPageComponent: React.FC<{ save: (a: Koodi) => void } & UseFormReturn<Koodi>> = ({
    register,
    handleSubmit,
    save,
}) => {
    const { koodiUri, koodiVersio } = useParams();
    return (
        <>
            <CrumbTrail trail={[{ key: koodiUri || 'newKoodiUri', label: koodiUri || '' }]} />
            <MainHeaderContainer>
                <FormattedMessage id={'KOODI_MUOKKAA_SIVU_TITLE'} defaultMessage={'Muokkaa koodia'} tagName={'h1'} />
            </MainHeaderContainer>
            <MainContainer>
                <MainContainerRow>
                    <MainContainerRowTitle id={'FIELD_TITLE_koodiArvo'} defaultMessage={'Arvo'} />
                    <MainContainerRowContent>
                        <Input {...register('koodiArvo')} />
                    </MainContainerRowContent>
                </MainContainerRow>
            </MainContainer>
            <Footer
                returnPath={(koodiUri && `/koodi/view/${koodiUri}/${koodiVersio}`) || '/'}
                save={handleSubmit((a) => save(a))}
                localisationPrefix={'KOODI'}
            />
        </>
    );
};
