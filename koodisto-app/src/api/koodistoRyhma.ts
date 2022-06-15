import { KoodistoRyhma, InsertKoodistoRyhma } from '../types';
import { errorHandlingWrapper } from './errorHandling';
import axios from 'axios';
import { API_INTERNAL_PATH } from '../context/constants';
import { atom, Getter } from 'jotai';
import { casMeLocaleAtom } from './kayttooikeus';

const urlAtom = atom<string>(`${API_INTERNAL_PATH}/koodistoryhma`);

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

export const koodistoRyhmaListAtom = atom<Promise<KoodistoRyhma[]>>(async (get: Getter) => {
    const { data } = await axios.get<KoodistoRyhma[]>(get(urlAtom));
    return data;
});
export const koodistoRyhmaOptionsAtom = atom<{ label: string; value: string }[]>((get: Getter) => {
    const locale = get(casMeLocaleAtom);
    const data = get(koodistoRyhmaListAtom);
    return data.map((a) => {
        return {
            label: a.nimi[locale] || a.nimi.fi,
            value: a.koodistoRyhmaUri,
        };
    });
});
