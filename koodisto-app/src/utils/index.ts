import { ApiDate, Kieli, Metadata, Locale } from '../types';
import moment from 'moment';
import { sortBy } from 'lodash';

export const translateMultiLocaleText = ({
    multiLocaleText,
    locale,
    defaultValue,
}: {
    multiLocaleText?: Record<Locale, string>;
    locale: Locale;
    defaultValue: string;
}): string => {
    return multiLocaleText?.[locale] || defaultValue;
};

export const metadataToMultiLocaleText = (metadata: Metadata[], field: keyof Metadata): Record<Locale, string> => ({
    fi: metadata.find((b) => b.kieli === 'FI')?.[field] || '',
    sv: metadata.find((b) => b.kieli === 'SV')?.[field] || '',
    en: metadata.find((b) => b.kieli === 'EN')?.[field] || '',
});

export const parseApiDate = (a: ApiDate): Date => {
    return !!a && moment(a).toDate();
};
export const parseUIDate = (a: Date): ApiDate | '' => {
    return a && (moment(a).format('YYYY-MM-DD') as ApiDate);
};
const kieliSorter = (o: Metadata) => (o.kieli === 'FI' ? 1 : o.kieli === 'SV' ? 2 : 3);
export const fillMetadata = (apiMetadata: Metadata[]) => {
    const metadata = [...apiMetadata];
    (['FI', 'SV', 'EN'] as Kieli[]).forEach(
        (kieli) => metadata.find((a) => a.kieli === kieli) || metadata.push({ kieli, nimi: '' })
    );
    return sortBy(metadata, kieliSorter);
};
export { downloadCsv } from './downloadCsv';
export { translateMetadata } from './translateMetadata';
