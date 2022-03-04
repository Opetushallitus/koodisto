import axios from 'axios';
import { atom } from 'jotai';

type CASMeApi = {
    uid: string;
    oid: string;
    firstName: string;
    lastName: string;
    groups: string[];
    roles: string;
    lang: string;
};

const urlAtom = atom((get) => '/kayttooikeus-service/');
export const casMeAtom = atom(async (get) => {
    const { data } = await axios.get<CASMeApi>(`${get(urlAtom)}cas/me`);
    return data;
});
export const casMeLangAtom = atom((get) => {
    return get(casMeAtom).lang;
});
