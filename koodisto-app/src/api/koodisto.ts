import { API_BASE_PATH, API_INTERNAL_PATH } from '../context/constants';
import { atom, Getter } from 'jotai';
import type { MapToApiObject, BaseKoodisto, Koodi } from '../types';
import {
    ApiDate,
    Kieli,
    ListKoodisto,
    Metadata,
    PageKoodisto,
    KoodistoRelation,
    OrganisaatioNimi,
    Locale,
    Tila,
} from '../types';
import { casMeLangAtom } from './kayttooikeus';
import { parseApiDate, translateMetadata, parseUIDate, translateMultiLocaleText } from '../utils';
import { errorHandlingWrapper } from './errorHandling';
import axios, { AxiosResponse } from 'axios';
import { fetchOrganisaatioNimi } from './organisaatio';
import { ApiKoodi, mapApiKoodi } from './koodi';

const urlAtom = atom<string>(`${API_INTERNAL_PATH}/koodisto`);

type ApiRyhmaMetadata = {
    id: number;
    uri: string;
    kieli: Kieli;
    nimi: string;
};

type ApiBaseKoodisto = MapToApiObject<BaseKoodisto>;
export type ApiPageKoodisto = ApiBaseKoodisto & {
    lockingVersion: number;
    koodistoRyhmaUri: string;
    resourceUri: string;
    omistaja: string;
    organisaatioOid: string;
    koodistoRyhmaMetadata: Metadata[];
    paivitysPvm: ApiDate;
    paivittajaOid: string;
    tila: Tila;
    koodistoVersio: number[];
    sisaltyyKoodistoihin: KoodistoRelation[];
    sisaltaaKoodistot: KoodistoRelation[];
    rinnastuuKoodistoihin: KoodistoRelation[];
    metadata: Metadata[];
};
type ApiListKoodisto = ApiBaseKoodisto & {
    metadata: Metadata[];
    koodistoRyhmaMetadata: ApiRyhmaMetadata[];
    koodiCount: number;
};

type CreateKoodistoDataType = {
    voimassaAlkuPvm: ApiDate;
    voimassaLoppuPvm?: ApiDate;
    omistaja: string;
    organisaatioOid: string;
    metadataList: Metadata[];
};
type ApiKoodistoRelation = {
    codesUri: string;
    codesVersion: number;
    passive: boolean;
};
type UpdateKoodistoDataType = Omit<CreateKoodistoDataType, 'metadataList'> & {
    tila: Tila;
    codesGroupUri: string;
    koodistoUri: string;
    versio: number;
    version: number;
    metadata: Metadata[];
    includesCodes: ApiKoodistoRelation[];
    levelsWithCodes: ApiKoodistoRelation[];
    withinCodes: ApiKoodistoRelation[];
};

const apiKoodistoListToKoodistoList = (a: ApiListKoodisto, lang: Kieli): ListKoodisto => {
    const nimi = translateMetadata({ metadata: a.metadata, lang })?.nimi;
    const kuvaus = translateMetadata({ metadata: a.metadata, lang })?.kuvaus;
    const ryhmaNimi = translateMetadata({ metadata: a.koodistoRyhmaMetadata, lang })?.nimi;
    return {
        ryhmaUri: a.koodistoRyhmaMetadata?.[0]?.uri || undefined,
        koodistoUri: a.koodistoUri,
        versio: a.versio,
        voimassaAlkuPvm: a.voimassaAlkuPvm && parseApiDate(a.voimassaAlkuPvm),
        voimassaLoppuPvm: a.voimassaLoppuPvm && parseApiDate(a.voimassaLoppuPvm),
        nimi,
        kuvaus,
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
}): Promise<Koodi[] | undefined> =>
    errorHandlingWrapper(async () => {
        const { data } = await axios.get<ApiKoodi[]>(`${API_BASE_PATH}/json/${koodistoUri}/koodi`, {
            params: koodistoVersio !== undefined ? { koodistoVersio } : {},
        });
        return data.map((api) => mapApiKoodi({ api }));
    });

export const updateKoodisto = async ({
    koodisto,
    lang,
}: {
    koodisto: PageKoodisto;
    lang: Kieli;
}): Promise<PageKoodisto | undefined> =>
    upsertKoodisto<UpdateKoodistoDataType>({
        koodisto,
        lang,
        mapper: mapPageKoodistoToUpdatePageKoodisto,
        path: `${API_INTERNAL_PATH}/koodisto`,
        axiosFunc: axios.put,
    });
export const createKoodisto = async ({
    koodisto,
    lang,
}: {
    koodisto: PageKoodisto;
    lang: Kieli;
}): Promise<PageKoodisto | undefined> =>
    upsertKoodisto<CreateKoodistoDataType>({
        koodisto,
        lang,
        mapper: mapPageKoodistoToCreatePageKoodisto,
        path: `${API_INTERNAL_PATH}/koodisto/${koodisto.koodistoRyhmaUri.value}`,
        axiosFunc: axios.post,
    });

const upsertKoodisto = async <X>({
    koodisto,
    lang,
    mapper,
    path,
    axiosFunc,
}: {
    koodisto: PageKoodisto;
    lang: Kieli;
    mapper: (a: PageKoodisto) => X;
    path: string;
    axiosFunc: <T, R = AxiosResponse<T>>(url: string, data?: X) => Promise<R>;
}): Promise<PageKoodisto | undefined> =>
    errorHandlingWrapper(async () => {
        const { data: apiKoodisto } = await axiosFunc(path, mapper(koodisto));
        return (
            apiKoodisto && {
                ...mapApiPageKoodistoToPageKoodisto({
                    api: apiKoodisto,
                    lang,
                    organisaatioNimi: await fetchOrganisaatioNimi(apiKoodisto.organisaatioOid),
                }),
            }
        );
    });

const mapApiPageKoodistoToPageKoodisto = ({
    api,
    lang,
    organisaatioNimi,
}: {
    api: ApiPageKoodisto;
    lang: Kieli;
    organisaatioNimi?: OrganisaatioNimi;
}): PageKoodisto => {
    const metadata = [...api.metadata];
    (['FI', 'SV', 'EN'] as Kieli[]).forEach(
        (kieli) => metadata.find((a) => a.kieli === kieli) || api.metadata.push({ ...metadata[0], kieli })
    );
    return {
        ...api,
        organisaatioNimi,
        koodistoRyhmaUri: {
            label: translateMetadata({ metadata: api.koodistoRyhmaMetadata, lang })?.nimi || api.koodistoRyhmaUri,
            value: api.koodistoRyhmaUri,
        },
        organisaatioOid: {
            label: `${translateMultiLocaleText({
                multiLocaleText: organisaatioNimi,
                locale: lang.toLowerCase() as Locale,
                defaultValue: api.organisaatioOid,
            })} ${api.organisaatioOid}`,
            value: api.organisaatioOid,
        },
        voimassaAlkuPvm: api.voimassaAlkuPvm && parseApiDate(api.voimassaAlkuPvm),
        voimassaLoppuPvm: api.voimassaLoppuPvm && parseApiDate(api.voimassaLoppuPvm),
        paivitysPvm: parseApiDate(api.paivitysPvm),
    };
};

const mapPageKoodistoToCreatePageKoodisto = (koodisto: PageKoodisto): CreateKoodistoDataType => ({
    omistaja: koodisto.omistaja,
    metadataList: koodisto.metadata,
    organisaatioOid: koodisto.organisaatioOid.value,
    voimassaAlkuPvm: koodisto.voimassaAlkuPvm && parseUIDate(koodisto.voimassaAlkuPvm),
    voimassaLoppuPvm: koodisto.voimassaLoppuPvm && parseUIDate(koodisto.voimassaLoppuPvm),
});

const toApiRelation = (relation: KoodistoRelation): ApiKoodistoRelation => ({
    codesUri: relation.koodistoUri,
    codesVersion: relation.koodistoVersio,
    passive: !!relation.passive,
});

const mapPageKoodistoToUpdatePageKoodisto = (koodisto: PageKoodisto): UpdateKoodistoDataType => ({
    includesCodes: koodisto.sisaltaaKoodistot.map(toApiRelation),
    levelsWithCodes: koodisto.rinnastuuKoodistoihin.map(toApiRelation),
    withinCodes: koodisto.sisaltyyKoodistoihin.map(toApiRelation),
    version: koodisto.lockingVersion,
    tila: koodisto.tila,
    versio: koodisto.versio,
    codesGroupUri: koodisto.koodistoRyhmaUri.value,
    koodistoUri: koodisto.koodistoUri,
    metadata: koodisto.metadata,
    ...mapPageKoodistoToCreatePageKoodisto(koodisto),
});

const pageKoodistoAccessor = async <X>({
    koodistoUri,
    versio,
    lang,
    axiosFunc,
}: {
    koodistoUri: string;
    versio?: number;
    lang: Kieli;
    axiosFunc: <T, R = AxiosResponse<T>>(url: string, data?: X) => Promise<R>;
}): Promise<PageKoodisto | undefined> =>
    errorHandlingWrapper(async () => {
        const { data: apiPageKoodisto } = await axiosFunc<ApiPageKoodisto>(
            [API_INTERNAL_PATH, 'koodisto', koodistoUri, ...(versio ? [versio] : [])].join('/')
        );
        if (apiPageKoodisto) {
            const organisaatioNimi = await fetchOrganisaatioNimi(apiPageKoodisto.organisaatioOid);
            return { ...mapApiPageKoodistoToPageKoodisto({ api: apiPageKoodisto, lang, organisaatioNimi }) };
        } else {
            return undefined;
        }
    });

export const fetchPageKoodisto = async ({
    koodistoUri,
    versio,
    lang,
}: {
    koodistoUri: string;
    versio?: number;
    lang: Kieli;
}): Promise<PageKoodisto | undefined> => pageKoodistoAccessor({ koodistoUri, versio, lang, axiosFunc: axios.get });

export const createKoodistoVersion = async (
    koodistoUri: string,
    versio: number,
    lang: Kieli
): Promise<PageKoodisto | undefined> => pageKoodistoAccessor({ koodistoUri, versio, lang, axiosFunc: axios.post });

export const deleteKoodisto = async (koodisto: PageKoodisto): Promise<boolean | undefined> =>
    errorHandlingWrapper(async () => {
        const { status } = await axios.delete(
            `${API_INTERNAL_PATH}/koodisto/${koodisto.koodistoUri}/${koodisto.versio}`
        );
        return status === 204;
    });
