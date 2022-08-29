import type { CSVUpsertKoodi, MapToApiObject, Koodi, KoodiMetadata, Tila, KoodiRelation, KoodiList } from '../types';
import { ApiDate } from '../types';
import { errorHandlingWrapper } from './errorHandling';
import axios, { AxiosResponse } from 'axios';
import { API_INTERNAL_PATH, API_BASE_PATH } from '../context/constants';
import { ApiPageKoodisto } from './koodisto';
import { parseApiDate, parseUIDate } from '../utils';

type ApiKoodi = MapToApiObject<Koodi>;
type ApiKoodiList = MapToApiObject<KoodiList>;

type CreateKoodiDataType = {
    koodiArvo: string;
    voimassaAlkuPvm: ApiDate;
    voimassaLoppuPvm?: ApiDate;
    metadata: KoodiMetadata[];
};
type UpdateKoodiDataType = CreateKoodiDataType & {
    koodiUri: string;
    tila: Tila;
    versio: number;
    lockingVersion: number;
    sisaltyyKoodeihin: KoodiRelation[];
    sisaltaaKoodit: KoodiRelation[];
    rinnastuuKoodeihin: KoodiRelation[];
};
export const batchUpsertKoodi = async (koodistoUri: string, koodi: CSVUpsertKoodi[]): Promise<string | undefined> =>
    errorHandlingWrapper(async () => {
        const { data } = await axios.post<ApiPageKoodisto>(`${API_INTERNAL_PATH}/koodi/${koodistoUri}`, koodi);
        return data.koodistoUri;
    });

export const mapApiKoodiList = (
    koodistoUri: string,
    koodistoVersio: number,
    { api }: { api: ApiKoodiList }
): KoodiList => ({
    ...api,
    koodistoUri,
    koodistoVersio,
    paivitysPvm: parseApiDate(api.paivitysPvm),
    voimassaAlkuPvm: parseApiDate(api.voimassaAlkuPvm),
    voimassaLoppuPvm: api.voimassaLoppuPvm && parseApiDate(api.voimassaLoppuPvm),
});
export const mapApiKoodi = ({ api }: { api: ApiKoodi }): Koodi => ({
    ...api,
    paivitysPvm: parseApiDate(api.paivitysPvm),
    voimassaAlkuPvm: parseApiDate(api.voimassaAlkuPvm),
    voimassaLoppuPvm: api.voimassaLoppuPvm && parseApiDate(api.voimassaLoppuPvm),
});

export const fetchKoodistoKoodis = async (
    koodistoUri: string,
    koodistoVersio: number,
    controller?: AbortController
): Promise<KoodiList[] | undefined> =>
    errorHandlingWrapper(async () => {
        const { data: pageData } = await axios.get<ApiKoodiList[]>(
            `${API_INTERNAL_PATH}/koodi/koodisto/${koodistoUri}/${koodistoVersio}`,
            { signal: controller?.signal }
        );
        return pageData.map((api) => mapApiKoodiList(koodistoUri, koodistoVersio, { api }));
    });

const pageKoodiAccessor = async <X>(
    koodiUri: string,
    versio: number,
    axiosFunc: <T, R = AxiosResponse<T>>(url: string, data?: X) => Promise<R>
): Promise<Koodi | undefined> =>
    errorHandlingWrapper(async () => {
        const { data: pageData } = await axiosFunc<ApiKoodi>(`${API_INTERNAL_PATH}/koodi/${koodiUri}/${versio}`);
        return mapApiKoodi({ api: pageData });
    });

export const fetchPageKoodi = async (koodiUri: string, versio: number): Promise<Koodi | undefined> =>
    pageKoodiAccessor(koodiUri, versio, axios.get);

export const updateKoodi = async (koodi: Koodi): Promise<Koodi | undefined> =>
    upsertKoodi<UpdateKoodiDataType>({
        koodi,
        mapper: mapKoodiToUpdateKoodi,
        path: `${API_INTERNAL_PATH}/koodi`,
        axiosFunc: axios.put,
    });

export const createKoodi = async (koodi: Koodi): Promise<Koodi | undefined> =>
    upsertKoodi<CreateKoodiDataType>({
        koodi,
        mapper: mapKoodiToCreateKoodi,
        path: `${API_INTERNAL_PATH}/koodi/${koodi.koodisto.koodistoUri}`,
        axiosFunc: axios.post,
    });
export const deleteKoodi = async (koodi: Koodi): Promise<boolean | undefined> =>
    errorHandlingWrapper(async () => {
        const { status } = await axios.delete(`${API_INTERNAL_PATH}/koodi/${koodi.koodiUri}/${koodi.versio}`);
        return status === 204;
    });

const upsertKoodi = async <X>({
    koodi,
    mapper,
    path,
    axiosFunc,
}: {
    koodi: Koodi;
    mapper: (a: Koodi) => X;
    path: string;
    axiosFunc: <T, R = AxiosResponse<T>>(url: string, data?: X) => Promise<R>;
}): Promise<Koodi | undefined> =>
    errorHandlingWrapper(async () => {
        const { data: apiKoodi } = await axiosFunc(path, mapper(koodi));
        return (
            apiKoodi && {
                ...mapApiKoodi({
                    api: apiKoodi,
                }),
            }
        );
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

const mapKoodiToUpdateKoodi = (koodi: Koodi): UpdateKoodiDataType => ({
    ...mapKoodiToCreateKoodi(koodi),
    koodiUri: koodi.koodiUri,
    lockingVersion: koodi.lockingVersion,
    tila: koodi.tila,
    versio: koodi.versio,
    sisaltaaKoodit: koodi.sisaltaaKoodit,
    sisaltyyKoodeihin: koodi.sisaltyyKoodeihin,
    rinnastuuKoodeihin: koodi.rinnastuuKoodeihin,
});

const mapKoodiToCreateKoodi = (koodi: Koodi): CreateKoodiDataType => ({
    koodiArvo: koodi.koodiArvo,
    metadata: koodi.metadata,
    voimassaAlkuPvm: parseUIDate(koodi.voimassaAlkuPvm),
    voimassaLoppuPvm: koodi.voimassaLoppuPvm && parseUIDate(koodi.voimassaLoppuPvm),
});
