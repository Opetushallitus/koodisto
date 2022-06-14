import { UpsertKoodi } from '../types';
import { errorHandlingWrapper } from './errorHandling';
import axios from 'axios';
import { API_INTERNAL_PATH } from '../context/constants';
import { ApiPageKoodisto } from './koodisto';

export const batchUpsertKoodi = async (koodistoUri: string, koodi: UpsertKoodi[]): Promise<string | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.post<ApiPageKoodisto>(`${API_INTERNAL_PATH}/koodi/${koodistoUri}`, koodi);
        return data.koodistoUri;
    });
};
