import { API_BASE_PATH } from '../context/constants';
import { atom, Getter } from 'jotai';
import { ApiDate, Kieli, Koodi, Metadata } from '../types/types';
import { casMeLangAtom } from './kayttooikeus';
import { parseApiDate, translateMetadata } from '../utils/utils';
import axios from 'axios';
import { errorHandlingWrapper } from './errorHandling';

export type Koodisto = {
    koodistoUri: string;
    versio: number;
    voimassaAlkuPvm?: Date;
    voimassaLoppuPvm?: Date;
    nimi?: string;
    organisaatioOid: string;
    ryhmaNimi?: string;
};
type KoodistoApi = {
    koodistoUri: string;
    latestKoodistoVersio: {
        metadata: Metadata[];
        versio: number;
        voimassaAlkuPvm?: ApiDate;
        voimassaLoppuPvm?: ApiDate;
    };
    organisaatioOid: string;
    ryhmaMetadata: Metadata[];
};
type KoodistoRyhma = {
    id: number;
    koodistoRyhmaUri: string;
    koodistos: KoodistoApi[];
    metadata: Metadata[];
};
const urlAtom = atom<string>(`${API_BASE_PATH}/codes`);
export const koodistoRyhmaAtom = atom<Promise<KoodistoRyhma[]>>(async (get: Getter) => {
    const { data } = await axios.get<KoodistoRyhma[]>(get(urlAtom));
    return data;
});
const distinctTypeFilter = (a: KoodistoApi, i: number, array: KoodistoApi[]): boolean =>
    array.findIndex((b: KoodistoApi) => b.koodistoUri === a.koodistoUri) === i;

const koodistoApiToKoodisto = (a: KoodistoApi, lang: Kieli): Koodisto => {
    const nimi = translateMetadata(a.latestKoodistoVersio.metadata, lang);
    const ryhmaNimi = translateMetadata(a.ryhmaMetadata, lang);
    return {
        koodistoUri: a.koodistoUri,
        versio: a.latestKoodistoVersio.versio,
        voimassaAlkuPvm: a.latestKoodistoVersio.voimassaAlkuPvm && parseApiDate(a.latestKoodistoVersio.voimassaAlkuPvm),
        voimassaLoppuPvm:
            a.latestKoodistoVersio.voimassaLoppuPvm && parseApiDate(a.latestKoodistoVersio.voimassaLoppuPvm),
        organisaatioOid: a.organisaatioOid,
        nimi,
        ryhmaNimi,
    };
};

export const koodistoAtom = atom<Koodisto[]>((get: Getter) => {
    const lowerLang = get(casMeLangAtom);
    const lang = lowerLang.toUpperCase() as Kieli;
    return get(koodistoRyhmaAtom)
        .flatMap((a: KoodistoRyhma) => a.koodistos.map((k: KoodistoApi) => ({ ...k, ryhmaMetadata: a.metadata })))
        .filter(distinctTypeFilter)
        .map((a) => koodistoApiToKoodisto(a, lang));
});

export const fetchKoodisto = async (koodistoUri: string): Promise<Koodi[] | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.get<Koodi[]>(`${API_BASE_PATH}/json/${koodistoUri}/koodi`);
        return data;
    });
};
