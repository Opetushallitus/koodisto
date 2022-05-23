import { API_BASE_PATH, API_INTERNAL_PATH } from '../context/constants';
import { atom, Getter } from 'jotai';
import { ApiDate, Kieli, Koodi, ListKoodisto, Metadata, PageKoodisto, UpsertKoodi } from '../types';
import { casMeLangAtom } from './kayttooikeus';
import { parseApiDate, translateMetadata } from '../utils';
import { errorHandlingWrapper } from './errorHandling';
import axios from 'axios';
import { fetchOrganisaatioNimi } from './organisaatio';

const urlAtom = atom<string>(`${API_INTERNAL_PATH}/koodisto`);

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

type ApiRyhmaMetadata = {
    id: number;
    uri: string;
    kieli: Kieli;
    nimi: string;
};
type ApiBaseKoodisto = {
    koodistoUri: string;
    versio: number;
    voimassaAlkuPvm: ApiDate;
    voimassaLoppuPvm: ApiDate;
    metadata: Metadata[];
};
type ApiPageKoodisto = ApiBaseKoodisto & {
    resourceUri: string;
    omistaja: string | null;
    organisaatioOid: string;
    lukittu: boolean | null;
    codesGroupUri: string;
    paivitysPvm: ApiDate;
    paivittajaOid: string;
    tila: string;
    codesVersions: number[];
    withinCodes: KoodistoRelation[];
    includesCodes: KoodistoRelation[];
    levelsWithCodes: KoodistoRelation[];
};
type ApiListKoodisto = ApiBaseKoodisto & {
    koodistoRyhmaMetadata: ApiRyhmaMetadata[];
    koodiCount: number;
};

const apiKoodistoListToKoodistoList = (a: ApiListKoodisto, lang: Kieli): ListKoodisto => {
    const nimi = translateMetadata(a.metadata, lang)?.nimi;
    const ryhmaNimi = translateMetadata(
        !!a.koodistoRyhmaMetadata ? a.koodistoRyhmaMetadata : [{ kieli: 'FI', nimi: 'N/A' }],
        lang
    )?.nimi;
    return {
        ryhmaId: a.koodistoRyhmaMetadata?.[0]?.id || undefined,
        koodistoUri: a.koodistoUri,
        versio: a.versio,
        voimassaAlkuPvm: a.voimassaAlkuPvm && parseApiDate(a.voimassaAlkuPvm),
        voimassaLoppuPvm: a.voimassaLoppuPvm && parseApiDate(a.voimassaLoppuPvm),
        nimi,
        ryhmaNimi,
        koodiCount: a.koodiCount,
    };
};

const apiKoodistoListAtom = atom<Promise<ApiListKoodisto[]>>(async (get: Getter) => {
    const { data } = await axios.get<ApiListKoodisto[]>(get(urlAtom));
    return data;
});

export const koodistoListAtom = atom<ListKoodisto[]>((get: Getter) => {
    const lang = get(casMeLangAtom);
    return get(apiKoodistoListAtom).map((a) => apiKoodistoListToKoodistoList(a, lang));
});

export const fetchKoodiListByKoodisto = async ({
    koodistoUri,
    koodistoVersio,
}: {
    koodistoUri: string;
    koodistoVersio?: number;
}): Promise<Koodi[] | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.get<Koodi[]>(`${API_BASE_PATH}/json/${koodistoUri}/koodi`, {
            params: koodistoVersio !== undefined ? { koodistoVersio } : {},
        });
        return data;
    });
};

export const createKoodisto = async (koodistoUri: string, koodi: UpsertKoodi): Promise<number | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.post<number>(`${API_BASE_PATH}/codeelement/${koodistoUri}`, koodi);
        return data;
    });
};
export const updateKoodisto = async (koodi: UpsertKoodi): Promise<number | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.put<number>(`${API_BASE_PATH}/codeelement/save`, koodi);
        return data;
    });
};

const mapApiPageKoodistoToPageKoodisto = (api: ApiPageKoodisto): PageKoodisto => {
    return {
        ...api,
        voimassaAlkuPvm: api.voimassaAlkuPvm && parseApiDate(api.voimassaAlkuPvm),
        voimassaLoppuPvm: api.voimassaLoppuPvm && parseApiDate(api.voimassaLoppuPvm),
    };
};
export const batchUpsertKoodi = async (
    koodistoUri: string,
    koodi: UpsertKoodi[]
): Promise<PageKoodisto | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.post<ApiPageKoodisto>(`${API_INTERNAL_PATH}/koodi/${koodistoUri}`, koodi);
        return mapApiPageKoodistoToPageKoodisto(data);
    });
};

export const fetchPageKoodisto = async (koodistoUri: string, versio: number): Promise<PageKoodisto | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data: apiPageKoodisto } = await axios.get<ApiPageKoodisto>(
            `${API_BASE_PATH}/codes/${koodistoUri}/${versio}`
        );
        if (apiPageKoodisto) {
            const organisaatioNimi = await fetchOrganisaatioNimi(apiPageKoodisto.organisaatioOid);
            return { ...mapApiPageKoodistoToPageKoodisto(apiPageKoodisto), organisaatioNimi };
        } else {
            return undefined;
        }
    });
};
