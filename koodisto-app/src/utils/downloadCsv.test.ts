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
};
describe('downloadCsv', () => {
    describe('mapKoodiToCSV', () => {
        it('can map koodi with 0 metadata', () => {
            expect(mapKoodiToCSV(cleanKoodi)).toStrictEqual({
                koodiArvo: '',
                koodiUri: '',
                paivitysPvm: '',
                tila: '',
                versio: 0,
                voimassaAlkuPvm: '',
                voimassaLoppuPvm: '',
            });
        });
        it('can map koodi with 1 metadata', () => {
            expect(mapKoodiToCSV({ ...cleanKoodi, metadata: [{ kieli: 'FI', nimi: 'bar' }] })).toStrictEqual({
                koodiArvo: '',
                koodiUri: '',
                paivitysPvm: '',
                tila: '',
                versio: 0,
                voimassaAlkuPvm: '',
                voimassaLoppuPvm: '',
                NIMI_FI: 'bar',
            });
        });
    });
});
