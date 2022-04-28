import { API_BASE_PATH } from '../context/constants';
import { atom, Getter } from 'jotai';
import { ApiDate, Kieli, Koodi, Metadata } from '../types/types';
import { casMeLangAtom } from './kayttooikeus';
import { parseApiDate, translateMetadata } from '../utils/utils';
import axios from 'axios';
import { errorHandlingWrapper } from './errorHandling';

export type TablePageKoodisto = {
    koodistoUri: string;
    versio: number;
    voimassaAlkuPvm?: Date;
    voimassaLoppuPvm?: Date;
    nimi?: string;
    organisaatioOid: string;
    ryhmaNimi?: string;
    ryhmaId?: number;
};

type KoodistoInRyhma = {
    koodistoUri: string;
    latestKoodistoVersio: {
        metadata: Metadata[];
        versio: number;
        voimassaAlkuPvm?: ApiDate;
        voimassaLoppuPvm?: ApiDate;
    };
    organisaatioOid: string;
    ryhmaMetadata: Metadata[];
    ryhmaId?: number;
};

export type KoodistoVersio = {
    versio: number;
    paivitysPvm: Date;
    voimassaAlkuPvm: ApiDate;
    voimassaLoppuPvm: ApiDate;
    tila: string;
    version: number;
    metadata: Metadata[];
};

export type KoodistoRelation = {
    codesUri: string;
    codesVersion: number;
    passive: boolean;
    nimi: {
        fi: string;
        sv: string;
        en: string;
    };
    kuvaus: {
        fi: string;
        sv: string;
        en: string;
    };
};
/*
export type KoodistoWithVersions = {
    koodistoUri: string;
    resourceUri: string;
    omistaja: string | null;
    organisaatioOid: string;
    lukittu: boolean | null;
    latestKoodistoVersio: KoodistoVersio;
    koodistoVersios: KoodistoVersio[];
};
 */

export type KoodistoPageKoodisto = {
    koodistoUri: string;
    resourceUri: string;
    omistaja: string | null;
    organisaatioOid: string;
    lukittu: boolean | null;
    codesGroupUri: string;
    version: number;
    versio: number;
    paivitysPvm: ApiDate;
    paivittajaOid: string;
    voimassaAlkuPvm: ApiDate;
    voimassaLoppuPvm: ApiDate;
    tila: string;
    metadata: Metadata[];
    codesVersions: number[];
    withinCodes: KoodistoRelation[];
    includesCodes: KoodistoRelation[];
    levelsWithCodes: KoodistoRelation[];
};

type KoodistoRyhma = {
    id: number;
    koodistoRyhmaUri: string;
    koodistos: KoodistoInRyhma[];
    metadata: Metadata[];
};
const urlAtom = atom<string>(`${API_BASE_PATH}/codes`);

export const koodistoRyhmaAtom = atom<Promise<KoodistoRyhma[]>>(async (get: Getter) => {
    const { data } = await axios.get<KoodistoRyhma[]>(get(urlAtom));
    return data;
});

const distinctTypeFilter = (a: KoodistoInRyhma, i: number, array: KoodistoInRyhma[]): boolean =>
    array.findIndex((b: KoodistoInRyhma) => b.koodistoUri === a.koodistoUri) === i;

const koodistoApiToKoodisto = (a: KoodistoInRyhma, lang: Kieli): TablePageKoodisto => {
    const nimi = translateMetadata(a.latestKoodistoVersio.metadata, lang)?.nimi;
    const ryhmaNimi = translateMetadata(a.ryhmaMetadata, lang)?.nimi;
    return {
        koodistoUri: a.koodistoUri,
        versio: a.latestKoodistoVersio.versio,
        voimassaAlkuPvm: a.latestKoodistoVersio.voimassaAlkuPvm && parseApiDate(a.latestKoodistoVersio.voimassaAlkuPvm),
        voimassaLoppuPvm:
            a.latestKoodistoVersio.voimassaLoppuPvm && parseApiDate(a.latestKoodistoVersio.voimassaLoppuPvm),
        organisaatioOid: a.organisaatioOid,
        nimi,
        ryhmaNimi,
        ryhmaId: a.ryhmaId,
    };
};

export const koodistoAtom = atom<TablePageKoodisto[]>((get: Getter) => {
    const lowerLang = get(casMeLangAtom);
    const lang = lowerLang.toUpperCase() as Kieli;
    return get(koodistoRyhmaAtom)
        .flatMap((a: KoodistoRyhma) =>
            a.koodistos.map((k: KoodistoInRyhma) => ({ ...k, ryhmaId: a.id, ryhmaMetadata: a.metadata }))
        )
        .filter(distinctTypeFilter)
        .map((a) => koodistoApiToKoodisto(a, lang));
});

export const fetchKoodisByKoodisto = async (koodistoUri: string): Promise<Koodi[] | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.get<Koodi[]>(`${API_BASE_PATH}/json/${koodistoUri}/koodi`);
        return data;
    });
};

export const fetchKoodistoByUriAndVersio = async (
    koodistoUri: string,
    versio: string
): Promise<KoodistoPageKoodisto | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.get<KoodistoPageKoodisto>(`${API_BASE_PATH}/codes/${koodistoUri}/${versio}`);
        return data;
    });
};
/*
export const fetchKoodistowithVersionsByUri = async (
    koodistoUri: string
): Promise<KoodistoWithVersions | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.get<KoodistoWithVersions>(`${API_BASE_PATH}/codes/${koodistoUri}`);
        return data;
    });
};
*/
