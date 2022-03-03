import { HEALTH_CHECK_URI } from '../context/constants';
import { atom } from 'jotai';
import axios from 'axios';

const urlAtom = atom(HEALTH_CHECK_URI);
export const healthCheckAtom = atom(async (get) => {
    const response = await axios.get<string>(get(urlAtom));
    return response.data;
});
