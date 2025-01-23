import axios from 'axios';
import { Atom, atom, Getter } from 'jotai';
import { Kieli, Locale } from '../types';

const urlAtom = atom<string>('/kayttooikeus-service');

type CASMeApi = {
    uid: string;
    oid: string;
    firstName: string;
    lastName: string;
    groups: string[];
    roles: string;
    lang: Locale;
};

const casMeAtom = atom<Promise<CASMeApi>>(async (get: Getter) => {
    const { data } = await axios.get<CASMeApi | string>(`${get(urlAtom)}/cas/me`);
    if (typeof data === 'string') {
        const retry = await axios.get<CASMeApi>(`${get(urlAtom)}/cas/me`);
        return retry.data;
    }
    return data;
});
export const casMeLocaleAtom: Atom<Locale> = atom((get) => {
    const casMe = get(casMeAtom);
    return casMe.lang;
});
export const casMeLangAtom: Atom<Kieli> = atom((get) => {
    const lang = get(casMeLocaleAtom);
    return lang.toUpperCase() as Kieli;
});
