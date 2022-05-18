import axios from 'axios';
import { atom, Getter } from 'jotai';
import { Kieli } from '../types/types';

type CASMeApi = {
    uid: string;
    oid: string;
    firstName: string;
    lastName: string;
    groups: string[];
    roles: string;
    lang: 'fi' | 'sv' | 'en';
};

const urlAtom = atom<string>('/kayttooikeus-service/cas/me');
export const casMeAtom = atom<Promise<CASMeApi>>(async (get: Getter) => {
    const { data } = await axios.get<CASMeApi | string>(get(urlAtom));
    if (typeof data === 'string') {
        const retry = await axios.get<CASMeApi>(get(urlAtom));
        return retry.data;
    }
    return data;
});
export const casMeLocaleAtom = atom((get) => {
    const casMe = get(casMeAtom);
    return casMe.lang;
});
export const casMeLangAtom = atom((get) => {
    const lang = get(casMeLocaleAtom);
    return lang.toUpperCase() as Kieli;
});
