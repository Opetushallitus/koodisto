import { mapKoodiToCSV } from './downloadCsv';

const cleanKoodi = {
    koodiArvo: '',
    koodiUri: '',
    metadata: [],
    paivitysPvm: '',
    resourceUri: '',
    tila: '',
    versio: 0,
    version: 0,
    voimassaAlkuPvm: '',
    voimassaLoppuPvm: '',
    koodisto: {
        koodistoUri: 'koodistoUri',
    },
};
describe('downloadCsv', () => {
    describe('mapKoodiToCSV', () => {
        it('can map koodi with 0 metadata', () => {
            expect(mapKoodiToCSV(cleanKoodi)).toStrictEqual({
                koodistoUri: 'koodistoUri',
                koodiArvo: '',
                versio: 0,
                voimassaAlkuPvm: '',
                voimassaLoppuPvm: '',
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
            expect(mapKoodiToCSV({ ...cleanKoodi, metadata: [{ kieli: 'FI', nimi: 'bar' }] })).toStrictEqual({
                koodistoUri: 'koodistoUri',
                koodiArvo: '',
                versio: 0,
                voimassaAlkuPvm: '',
                voimassaLoppuPvm: '',
                nimi_FI: 'bar',
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
    });
});
