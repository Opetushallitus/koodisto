import type { CSVUpsertKoodi, MapToApiObject, Koodi } from '../types';
import { errorHandlingWrapper } from './errorHandling';
import axios from 'axios';
import { API_INTERNAL_PATH } from '../context/constants';
import { ApiPageKoodisto } from './koodisto';
import { parseApiDate } from '../utils';

export type ApiKoodi = MapToApiObject<Koodi> & {
    koodisto: ApiPageKoodisto;
};

export const batchUpsertKoodi = async (koodistoUri: string, koodi: CSVUpsertKoodi[]): Promise<string | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.post<ApiPageKoodisto>(`${API_INTERNAL_PATH}/koodi/${koodistoUri}`, koodi);
        return data.koodistoUri;
    });
};
export const mapApiKoodi = (koodi: ApiKoodi): Koodi => {
    return {
        ...koodi,
        paivitysPvm: parseApiDate(koodi.paivitysPvm),
        voimassaAlkuPvm: parseApiDate(koodi.voimassaAlkuPvm),
        voimassaLoppuPvm: koodi.voimassaLoppuPvm && parseApiDate(koodi.voimassaLoppuPvm),
    };
};
export const fetchKoodistoKoodis = async (
    koodistoUri: string,
    koodistoVersio: number
): Promise<Koodi[] | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data: pageData } = await axios.get<ApiKoodi[]>(
            `${API_INTERNAL_PATH}/koodi/koodisto/${koodistoUri}/${koodistoVersio}`
        );
        return pageData.map(mapApiKoodi);
    });
};
export const fetchPageKoodi = async (koodiUri: string, versio: number): Promise<Koodi | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data: pageData } = await axios.get<ApiKoodi>(`${API_INTERNAL_PATH}/koodi/${koodiUri}/${versio}`);
        return mapApiKoodi(pageData);
    });
};
