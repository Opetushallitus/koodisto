import { API_BASE_PATH } from '../context/constants';
import { atom } from 'jotai';
import axios from 'axios';
import { KoodistoRyhma } from '../types/types';

const urlAtom = atom(`${API_BASE_PATH}/codes`);
export const codesAtom = atom(async (get) => {
    const response = await axios.get<KoodistoRyhma[]>(get(urlAtom));
    return response.data;
});
