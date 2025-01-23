import { atom, Getter } from 'jotai';
import { API_STATUS_PATH } from '../context/constants';
import axios from 'axios';

const urlAtom = atom(API_STATUS_PATH);

export const statusAtom = atom(async (get: Getter) => {
    const url = get(urlAtom);
    await axios.get<never>(`${url}`);
});
