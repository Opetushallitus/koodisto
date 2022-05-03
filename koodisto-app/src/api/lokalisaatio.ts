import { atom, Getter } from 'jotai';
import { API_LOKALISAATIO_PATH } from '../context/constants';
import { casMeLangAtom } from './kayttooikeus';
import axios from 'axios';

const urlAtom = atom(API_LOKALISAATIO_PATH);
type Lokalisaatio = { locale: 'fi' | 'sv' | 'en'; key: string; value: string };

export const lokalisaatioAtom = atom(async (get: Getter): Promise<Lokalisaatio[]> => {
    const locale = get(casMeLangAtom);
    const url = get(urlAtom);
    const { data } = await axios.get<Lokalisaatio[]>(`${url}?${new URLSearchParams({ category: 'koodisto', locale })}`);
    return data;
});

export const lokalisaatioMessagesAtom = atom((get: Getter): Record<string, string> => {
    return get(lokalisaatioAtom).reduce((p: Record<string, string>, c: Lokalisaatio) => {
        return { ...p, [c.key]: c.value };
    }, {});
});
