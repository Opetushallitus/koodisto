import { ApiDate, Kieli, Metadata, Locale } from '../types';
import moment from 'moment';

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
export const translateMetadata = ({
    metadata = [{ kieli: 'FI', nimi: 'N/A', kuvaus: 'N/A' }],
    lang,
}: {
    metadata: Metadata[];
    lang: Kieli;
}): Metadata | undefined => metadata.find((a) => a.kieli === lang) || metadata.find((a) => a.kieli === 'FI');

export const metadataToMultiLocaleText = (metadata: Metadata[], field: keyof Metadata): Record<Locale, string> => ({
    fi: metadata.find((b) => b.kieli === 'FI')?.[field] || '',
    sv: metadata.find((b) => b.kieli === 'SV')?.[field] || '',
    en: metadata.find((b) => b.kieli === 'EN')?.[field] || '',
});

export const parseApiDate = (a: ApiDate): Date => {
    return !!a && moment(a).toDate();
};
export const parseUIDate = (a: Date): ApiDate => {
    return !!a && (moment(a).format('YYYY-MM-DD') as ApiDate);
};
export { downloadCsv } from './downloadCsv';
