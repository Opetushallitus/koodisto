import { ApiDate, Kieli, Metadata, Locale } from '../types';
import { format, parseISO } from 'date-fns';

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
    return parseISO(a);
};
export const parseUIDate = (a: Date): ApiDate | '' => {
    return a && (format(a, 'yyyy-MM-dd') as ApiDate);
};
const kieliSorter = (o: Metadata) => (o.kieli === 'FI' ? 1 : o.kieli === 'SV' ? 2 : 3);
export const fillMetadata = (apiMetadata: Metadata[]) => {
    const metadata = [...apiMetadata];
    (['FI', 'SV', 'EN'] as Kieli[]).forEach(
        (kieli) => metadata.find((a) => a.kieli === kieli) || metadata.push({ kieli, nimi: '' })
    );
    return metadata.sort((a, b) => kieliSorter(a) - kieliSorter(b));
};
export { downloadCsv } from './downloadCsv';
export { capitalize, debounce, uniqBy, uniqWith } from './helpers';
export { translateMetadata } from './translateMetadata';
