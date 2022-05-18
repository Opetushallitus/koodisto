import { mapCsvToKoodi, validData } from './uploadCsv';

describe('CSVUploader', () => {
    it('can verify undefined data', () => {
        expect(validData(undefined)).toBe(false);
    });
    it('can verify empty data', () => {
        expect(validData([])).toBe(false);
    });
    it('can map csv to koodi', () => {
        expect(
            mapCsvToKoodi({
                koodistoUri: 'foo',
                koodiArvo: '0',
                versio: '',
                voimassaAlkuPvm: '',
                voimassaLoppuPvm: '',
                nimi_EN: 'FOO_ZERO',
                nimi_FI: '',
                nimi_SV: '',
                lyhytNimi_EN: '',
                lyhytNimi_FI: '',
                lyhytNimi_SV: '',
                kuvaus_EN: '',
                kuvaus_FI: '',
                kuvaus_SV: '',
                newRow: false,
            })
        ).toStrictEqual({
            koodiArvo: '0',
            versio: undefined,
            voimassaAlkuPvm: undefined,
            voimassaLoppuPvm: undefined,
            metadata: [{ kieli: 'EN', nimi: 'FOO_ZERO', lyhytNimi: undefined, kuvaus: undefined }],
        });
        expect(
            mapCsvToKoodi({
                koodistoUri: 'foo-start',
                koodiArvo: '0',
                versio: 'qwe',
                voimassaAlkuPvm: '2022-01-01',
                voimassaLoppuPvm: '',
                nimi_EN: 'FOO_ZERO',
                nimi_FI: '',
                nimi_SV: '',
                lyhytNimi_EN: '',
                lyhytNimi_FI: '',
                lyhytNimi_SV: '',
                kuvaus_EN: '',
                kuvaus_FI: '',
                kuvaus_SV: '',
                newRow: false,
            })
        ).toStrictEqual({
            koodiArvo: '0',
            versio: undefined,
            voimassaAlkuPvm: '2022-01-01',
            voimassaLoppuPvm: undefined,
            metadata: [{ kieli: 'EN', nimi: 'FOO_ZERO', lyhytNimi: undefined, kuvaus: undefined }],
        });
        expect(
            mapCsvToKoodi({
                koodistoUri: 'foo-start',
                koodiArvo: '0',
                versio: '6',
                voimassaAlkuPvm: '2022-01-01',
                voimassaLoppuPvm: '2033-01-01',
                nimi_EN: 'FOO_ZERO',
                nimi_FI: 'Yks',
                nimi_SV: 'Ett',
                lyhytNimi_EN: '',
                lyhytNimi_FI: 'fi',
                lyhytNimi_SV: '',
                kuvaus_EN: '',
                kuvaus_FI: '',
                kuvaus_SV: 'SV',
                newRow: false,
            })
        ).toStrictEqual({
            koodiArvo: '0',
            versio: 6,
            voimassaAlkuPvm: '2022-01-01',
            voimassaLoppuPvm: '2033-01-01',
            metadata: [
                { kieli: 'FI', nimi: 'Yks', lyhytNimi: 'fi', kuvaus: undefined },
                { kieli: 'SV', nimi: 'Ett', lyhytNimi: undefined, kuvaus: 'SV' },
                { kieli: 'EN', nimi: 'FOO_ZERO', lyhytNimi: undefined, kuvaus: undefined },
            ],
        });
    });
});
