import assert from 'assert/strict';
import { describe, it } from 'node:test';

import { mapKoodiToCSV } from './mapKoodiToCsv';
import { Tila, Koodi } from '../types';
import { ApiPageKoodisto } from '../api/koodisto';

const cleanKoodi: Koodi = {
    versio: 0,
    paivittajaOid: '1.2.3.4.5',
    koodisto: { koodistoUri: 'koodistoUri', versio: 0, voimassaAlkuPvm: '2000-12-12' } as unknown as ApiPageKoodisto,
    sisaltyyKoodeihin: [],
    sisaltaaKoodit: [],
    rinnastuuKoodeihin: [],
    koodiArvo: '',
    koodiUri: '',
    metadata: [],
    paivitysPvm: new Date(),
    resourceUri: '',
    tila: 'LUONNOS' as Tila,
    koodiVersio: [],
    lockingVersion: 0,
    voimassaAlkuPvm: new Date('2022-01-01'),
    voimassaLoppuPvm: undefined,
};
describe('mapKoodiToCSV', () => {
    it('can map koodi with 0 metadata', () => {
        assert.deepStrictEqual(mapKoodiToCSV(cleanKoodi as unknown as Koodi), {
            koodistoUri: 'koodistoUri',
            koodiArvo: '',
            versio: 0,
            voimassaAlkuPvm: new Date('2022-01-01'),
            voimassaLoppuPvm: undefined,
            nimi_FI: '',
            nimi_SV: '',
            nimi_EN: '',
            kuvaus_FI: '',
            kuvaus_SV: '',
            kuvaus_EN: '',
            lyhytNimi_FI: '',
            lyhytNimi_SV: '',
            lyhytNimi_EN: '',
        });
    });
    it('can map koodi with 1 metadata', () => {
        assert.deepStrictEqual(
            mapKoodiToCSV({ ...cleanKoodi, metadata: [{ kieli: 'FI', nimi: 'bar' }] } as unknown as Koodi),
            {
                koodistoUri: 'koodistoUri',
                koodiArvo: '',
                versio: 0,
                voimassaAlkuPvm: new Date('2022-01-01'),
                voimassaLoppuPvm: undefined,
                nimi_FI: 'bar',
                nimi_SV: '',
                nimi_EN: '',
                kuvaus_FI: '',
                kuvaus_SV: '',
                kuvaus_EN: '',
                lyhytNimi_FI: '',
                lyhytNimi_SV: '',
                lyhytNimi_EN: '',
            }
        );
    });
});
