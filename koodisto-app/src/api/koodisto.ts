import { API_BASE_PATH, API_INTERNAL_PATH } from '../context/constants';
import { atom, Getter } from 'jotai';
import {
    ApiDate,
    Kieli,
    Koodi,
    ListKoodisto,
    Metadata,
    PageKoodisto,
    UpsertKoodi,
    KoodistoRelation,
    InsertKoodistoRyhma,
    KoodistoRyhma,
} from '../types';
import { casMeLangAtom } from './kayttooikeus';
import { parseApiDate, translateMetadata } from '../utils';
import { errorHandlingWrapper } from './errorHandling';
import axios from 'axios';
import { fetchOrganisaatioNimi } from './organisaatio';

const urlAtom = atom<string>(`${API_INTERNAL_PATH}/koodisto`);

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
    koodistoRyhmaMetadata: Metadata[];
    paivitysPvm: ApiDate;
    paivittajaOid: string;
    tila: string;
    koodiVersio: number[];
    sisaltyyKoodistoihin: KoodistoRelation[];
    sisaltaaKoodistot: KoodistoRelation[];
    rinnastuuKoodistoihin: KoodistoRelation[];
    koodiList: Koodi[];
};
type ApiListKoodisto = ApiBaseKoodisto & {
    koodistoRyhmaMetadata: ApiRyhmaMetadata[];
    koodiCount: number;
};

const apiKoodistoListToKoodistoList = (a: ApiListKoodisto, lang: Kieli): ListKoodisto => {
    const nimi = translateMetadata({ metadata: a.metadata, lang })?.nimi;
    const ryhmaNimi = translateMetadata({ metadata: a.koodistoRyhmaMetadata, lang })?.nimi;
    return {
        ryhmaUri: a.koodistoRyhmaMetadata?.[0]?.uri || undefined,
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

export const createKoodistoRyhma = async (koodistoRyhma: InsertKoodistoRyhma): Promise<KoodistoRyhma | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.post<KoodistoRyhma>(`${API_INTERNAL_PATH}/koodistoryhma`, koodistoRyhma);
        return data;
    });
};
export const updateKoodistoRyhma = async (
    koodistoRyhmaUri: string,
    koodistoRyhma: InsertKoodistoRyhma
): Promise<KoodistoRyhma | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.put<KoodistoRyhma>(
            `${API_INTERNAL_PATH}/koodistoryhma/${koodistoRyhmaUri}`,
            koodistoRyhma
        );
        return data;
    });
};
export const fetchKoodistoRyhma = async (koodistoRyhmaUri: string): Promise<KoodistoRyhma | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.get<KoodistoRyhma>(`${API_INTERNAL_PATH}/koodistoryhma/${koodistoRyhmaUri}`);
        return data;
    });
};
export const deleteKoodistoRyhma = async (koodistoRyhmaUri: string): Promise<KoodistoRyhma[] | undefined> => {
    return errorHandlingWrapper(async () => {
        await axios.delete(`${API_INTERNAL_PATH}/koodistoryhma/${koodistoRyhmaUri}`);
        return await fetchEmptyKoodistoRyhma();
    });
};
export const fetchEmptyKoodistoRyhma = async (): Promise<KoodistoRyhma[] | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.get<KoodistoRyhma[]>(`${API_INTERNAL_PATH}/koodistoryhma/empty`);
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
            `${API_INTERNAL_PATH}/koodisto/${koodistoUri}/${versio}`
        );
        if (apiPageKoodisto) {
            const organisaatioNimi = await fetchOrganisaatioNimi(apiPageKoodisto.organisaatioOid);
            return { ...mapApiPageKoodistoToPageKoodisto(apiPageKoodisto), organisaatioNimi };
        } else {
            return undefined;
        }
    });
};
