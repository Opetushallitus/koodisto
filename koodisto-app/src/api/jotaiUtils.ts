import { Getter, atom } from 'jotai';

export const asyncAtomWithReset = <T>(fn: (get: Getter) => T) => {
    const refreshCounter = atom(0);

    return atom(
        (get) => {
            get(refreshCounter);
            return fn(get);
        },
        (_, set) => set(refreshCounter, (i) => i + 1)
    );
};
