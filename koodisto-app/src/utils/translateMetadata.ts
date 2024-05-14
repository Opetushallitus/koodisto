import { Kieli, Metadata } from '../types';

export const translateMetadata = ({
    metadata = [{ kieli: 'FI', nimi: 'N/A', kuvaus: 'N/A' }],
    lang,
}: {
    metadata: Metadata[];
    lang: Kieli;
}): Metadata | undefined => metadata.find((a) => a.kieli === lang) || metadata.find((a) => a.kieli === 'FI');
