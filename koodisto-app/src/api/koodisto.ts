import { API_BASE_PATH, API_INTERNAL_PATH } from '../context/constants';
import { atom, Getter } from 'jotai';
import { ApiDate, Kieli, Koodi, ListKoodisto, Metadata, PageKoodisto, UpsertKoodi, KoodistoRelation } from '../types';
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
export type ApiPageKoodisto = ApiBaseKoodisto & {
    koodistoRyhmaUri: string;
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
export const updateKoodisto = async (koodi: PageKoodisto): Promise<PageKoodisto | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data: apiPageKoodisto } = await axios.put<ApiPageKoodisto>(`${API_INTERNAL_PATH}/koodisto`, koodi);
        if (apiPageKoodisto) {
            const organisaatioNimi = await fetchOrganisaatioNimi(apiPageKoodisto.organisaatioOid);
            return { ...mapApiPageKoodistoToPageKoodisto(apiPageKoodisto), organisaatioNimi };
        } else {
            return undefined;
        }
    });
};

const mapApiPageKoodistoToPageKoodisto = (api: ApiPageKoodisto): PageKoodisto => {
    const metadata = [...api.metadata];
    (['FI', 'SV', 'EN'] as Kieli[]).forEach(
        (kieli) => metadata.find((a) => a.kieli === kieli) || api.metadata.push({ ...metadata[0], kieli })
    );
    return {
        ...api,
        voimassaAlkuPvm: api.voimassaAlkuPvm && parseApiDate(api.voimassaAlkuPvm),
        voimassaLoppuPvm: api.voimassaLoppuPvm && parseApiDate(api.voimassaLoppuPvm),
    };
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
