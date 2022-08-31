import { CsvKoodiObject, Kieli, MessageFormatter, CSVUpsertKoodi } from '../../types';
import { ColumnDef } from '@tanstack/react-table';

export const getHeaders = (data?: CsvKoodiObject[]): (keyof CsvKoodiObject)[] =>
    (data?.[0] ? Object.keys(data[0]) : []) as (keyof CsvKoodiObject)[];

export const mapHeadersToColumns = ({
    headers,
    formatMessage,
}: {
    headers?: (keyof CsvKoodiObject)[];
    formatMessage: MessageFormatter;
}): ColumnDef<CsvKoodiObject>[] =>
    headers?.map((key): ColumnDef<CsvKoodiObject> => {
        if (key === 'newRow')
            return {
                header: formatMessage({
                    id: 'CSV_UPLOAD_ROW_TITLE',
                    defaultMessage: 'Toimenpide',
                }),
                id: 'new',
                enableColumnFilter: false,
                accessorFn: (originalRow) => {
                    return originalRow.newRow
                        ? formatMessage({
                              id: 'CSV_UPLOAD_NEW_ROW',
                              defaultMessage: 'Koodi lisätään',
                          })
                        : formatMessage({
                              id: 'CSV_UPLOAD_OLD_ROW',
                              defaultMessage: 'Koodi päivitetään',
                          });
                },
            };
        else
            return {
                header: key,
                id: key,
                enableColumnFilter: false,
                accessorKey: key,
            };
    }) || [];

export const validData = (data: CsvKoodiObject[] | undefined): boolean => {
    return !!data && data.length > 0;
};
const mapCsvRowToMetadata = (csvRow: CsvKoodiObject, kieli: Kieli) => {
    return csvRow[`nimi_${kieli}`]
        ? [
              {
                  kieli,
                  kuvaus: csvRow[`kuvaus_${kieli}`] || undefined,
                  lyhytNimi: csvRow[`lyhytNimi_${kieli}`] || undefined,
                  nimi: csvRow[`nimi_${kieli}`],
              },
          ]
        : [];
};
export const mapCsvToKoodi = (csvRow: CsvKoodiObject): CSVUpsertKoodi => {
    return {
        koodiArvo: csvRow.koodiArvo,
        versio: !csvRow.versio || isNaN(+csvRow.versio) ? undefined : +csvRow.versio,
        voimassaAlkuPvm: csvRow.voimassaAlkuPvm || undefined,
        voimassaLoppuPvm: csvRow.voimassaLoppuPvm || undefined,
        metadata: [
            ...mapCsvRowToMetadata(csvRow, 'FI'),
            ...mapCsvRowToMetadata(csvRow, 'SV'),
            ...mapCsvRowToMetadata(csvRow, 'EN'),
        ],
    };
};
