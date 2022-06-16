import type { UpsertKoodi, PageKoodi, ApiDate } from '../types';
import { errorHandlingWrapper } from './errorHandling';
import axios from 'axios';
import { API_INTERNAL_PATH } from '../context/constants';
import { ApiPageKoodisto } from './koodisto';
import { parseApiDate } from '../utils';

type MapDateToApiDate<PropType> = PropType extends Date ? ApiDate : PropType;

type MapToApiObject<T> = {
    [PropertyKey in keyof T]: MapDateToApiDate<T[PropertyKey]>;
};

type ApiPageKoodi = {
    koodi: MapToApiObject<PageKoodi['koodi']>;
    koodisto: PageKoodi['koodisto'];
};

export const batchUpsertKoodi = async (koodistoUri: string, koodi: UpsertKoodi[]): Promise<string | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.post<ApiPageKoodisto>(`${API_INTERNAL_PATH}/koodi/${koodistoUri}`, koodi);
        return data.koodistoUri;
    });
};

export const fetchPageKoodi = async (koodiUri: string, versio: number): Promise<PageKoodi | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data: pageData } = await axios.get<ApiPageKoodi>(`${API_INTERNAL_PATH}/koodi/${koodiUri}/${versio}`);
        return {
            koodi: {
                ...pageData.koodi,
                paivitysPvm: parseApiDate(pageData.koodi.paivitysPvm),
                voimassaAlkuPvm: parseApiDate(pageData.koodi.voimassaAlkuPvm),
                voimassaLoppuPvm: pageData.koodi.voimassaLoppuPvm && parseApiDate(pageData.koodi.voimassaLoppuPvm),
            },
            koodisto: pageData.koodisto,
        };
    });
};
