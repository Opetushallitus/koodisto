import { ApiDate, Kieli, Metadata } from '../types';
import moment from 'moment';

export const translateMetadata = ({
    metadata = [{ kieli: 'FI', nimi: 'N/A', kuvaus: 'N/A' }],
    lang,
}: {
    metadata: Metadata[];
    lang: Kieli;
}): Metadata | undefined => metadata.find((a) => a.kieli === lang) || metadata.find((a) => a.kieli === 'FI');

export const parseApiDate = (a: ApiDate): Date => {
    return !!a && moment(a).toDate();
};
export { downloadCsv } from './downloadCsv';
