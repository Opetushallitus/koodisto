import Papa from 'papaparse';

import { fetchKoodiListByKoodisto } from '../api/koodi';
import { info } from '../components/Notification/Notification';
import { mapKoodiToCSV } from './mapKoodiToCsv';

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

export const downloadCsv = async ({
    koodistoUri,
    koodistoVersio,
}: {
    koodistoUri: string;
    koodistoVersio?: number;
}) => {
    const data = await fetchKoodiListByKoodisto({ koodistoUri, koodistoVersio });
    if (!data) return;
    data.sort((a, b) => a.koodiUri.localeCompare(b.koodiUri));
    const csvData = data.map(mapKoodiToCSV);
    const csv = Papa.unparse(csvData, { quotes: true, quoteChar: '"', delimiter: '\t', newline: '\r\n' });
    const blob = convertCsvToExcelAcceptedBlob(csv);
    pushBlobToUser({ fileName: `${koodistoUri}.csv`, blob });
    info({ title: 'CSV_DOWNLOAD_NOTIFICATION_TITLE', message: 'CSV_DOWNLOAD_NOTIFICATION_SUCCESS' });
};
