import { API_BASE_PATH } from '../context/constants';
import { atom } from 'jotai';
import axios from 'axios';
import { Kieli } from '../types/types';

export type Koodisto = {
    koodistoUri: string;
    latestKoodistoVersio: {
        metadata: Metadata[];
        versio: number;
        voimassaAlkuPvm: string;
        voimassaLoppuPvm: string;
    };
    organisaatioOid: string;
};
type Metadata = {
    kieli: Kieli;
    nimi: string;
};
type KoodistoRyhma = {
    id: number;
    koodistoRyhmaUri: string;
    koodistos: Koodisto[];
    metadata: Metadata[];
};
const urlAtom = atom(`${API_BASE_PATH}/codes`);
export const koodistoRyhmaAtom = atom(async (get) => {
    const response = await axios.get<KoodistoRyhma[]>(get(urlAtom));
    return response.data;
});
const distinctTypeFilter = (a: Koodisto, i: number, array: Koodisto[]): boolean =>
    array.findIndex((b) => b.koodistoUri === a.koodistoUri) === i;

export const koodistoAtom = atom((get) => {
    return get(koodistoRyhmaAtom)
        .flatMap((a) => a.koodistos)
        .filter(distinctTypeFilter);
});
