import { atom, Getter } from 'jotai';
import { API_LOKALISAATIO_PATH } from '../context/constants';
import { casMeLangAtom } from './kayttooikeus';
import axios from 'axios';

const urlAtom = atom(API_LOKALISAATIO_PATH);
type Lokalisaatio = { locale: 'fi' | 'sv' | 'en'; key: string; value: string };

export const lokalisaatioAtom = atom(async (get: Getter): Promise<Lokalisaatio[]> => {
    const url = get(urlAtom);
    const { data } = await axios.get<Lokalisaatio[]>(`${url}?${new URLSearchParams({ category: 'koodisto' })}`);
    return data;
});
export const lokalisaatioMessagesAtom = atom((get: Getter): Record<string, string> => {
    const locale = get(casMeLangAtom);
    return get(lokalisaatioAtom)
        .filter((a: Lokalisaatio) => a.locale === locale)
        .reduce((p: Record<string, string>, c: Lokalisaatio) => {
            const here = {} as Record<string, string>;
            here[c.key] = c.value;
            return { ...p, ...here };
        }, {} as Record<string, string>);
});
