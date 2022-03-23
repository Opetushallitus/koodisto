import Papa from 'papaparse';
import { Koodi, Metadata } from '../types/types';
import { fetchKoodisto } from '../api/koodisto';

export const mapKoodiToCSV = (koodi: Koodi) => {
    const reducedMetdata = koodi.metadata.reduce((p, a) => {
        const languageKeyedMetadata: { [key: string]: string | undefined } = {};
        const keys = Object.keys(a) as (keyof Metadata)[];
        keys.filter((k) => k !== 'kieli').forEach(
            (k) => (languageKeyedMetadata[`${k.toUpperCase()}_${a.kieli}`] = a[k])
        );
        return { ...p, ...languageKeyedMetadata };
    }, {});
    return {
        versio: koodi.versio,
        koodiUri: koodi.koodiUri,
        koodiArvo: koodi.koodiArvo,
        paivitysPvm: koodi.paivitysPvm,
        voimassaAlkuPvm: koodi.voimassaAlkuPvm,
        voimassaLoppuPvm: koodi.voimassaLoppuPvm,
        tila: koodi.tila,
        ...reducedMetdata,
    };
};
const pushBlobToUser = ({ fileName, blob }: { fileName: string; blob: Blob }) => {
    const link = document.createElement('a');
    if (link.download !== undefined) {
        const url = URL.createObjectURL(blob);
        link.setAttribute('href', url);
        link.setAttribute('download', fileName);
        link.style.visibility = 'hidden';
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }
};

const convertCsvToExcelAcceptedBlob = (csv: string) => {
    const csvWithBom = `${decodeURIComponent('%EF%BB%BF')}${csv}`;
    const charCodeArray = Array.from(csvWithBom).map((k) => k.charCodeAt(0));
    const csvArray = new Uint16Array(charCodeArray);
    return new Blob([csvArray], { type: 'text/csv;charset=UTF-16LE;' });
};

const downloadCsv = async (koodistoUri: string) => {
    const response = await fetchKoodisto(koodistoUri);
    if (!response) return;
    const { data } = response;
    data.sort((a, b) => a.koodiUri.localeCompare(b.koodiUri));
    const csvData = data.map(mapKoodiToCSV);
    const csv = Papa.unparse(csvData, { quotes: true, quoteChar: '"', delimiter: '\t', newline: '\r\n' });
    const blob = convertCsvToExcelAcceptedBlob(csv);
    pushBlobToUser({ fileName: `${koodistoUri}.csv`, blob });
};
export default downloadCsv;
