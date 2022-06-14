import { KoodistoRyhma, InsertKoodistoRyhma } from '../types';
import { errorHandlingWrapper } from './errorHandling';
import axios from 'axios';
import { API_INTERNAL_PATH } from '../context/constants';

export const fetchEmptyKoodistoRyhma = async (): Promise<KoodistoRyhma[] | undefined> => {
    return errorHandlingWrapper(async () => {
        const { data } = await axios.get<KoodistoRyhma[]>(`${API_INTERNAL_PATH}/koodistoryhma/empty/`);
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
        return fetchEmptyKoodistoRyhma();
    });
};
