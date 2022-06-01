import { translateMetadata } from './index';
import { Kieli, Metadata } from '../types';

describe('translateMetadata', () => {
    const uppercaseSVLocale = 'SV' as Kieli;
    const uppercaseFiLocale = 'FI' as Kieli;
    const uppercaseEnLocale = 'EN' as Kieli;
    const FIMetadata = { kieli: uppercaseFiLocale };
    const SVMetadata = { kieli: uppercaseSVLocale };
    const ENMetadata = { kieli: uppercaseEnLocale };
    const metadatasAllLangs = [FIMetadata, SVMetadata, ENMetadata] as Metadata[];
    const metadatasFISV = [FIMetadata, SVMetadata] as Metadata[];
    const emptyMetadatasByError = [] as Metadata[];
    test.each([
        ['Should return localized metadata based on locale', metadatasAllLangs, uppercaseSVLocale, SVMetadata],
        ['Should default to FI locale if locale prop is not found', metadatasFISV, uppercaseEnLocale, FIMetadata],
        ['Should default to FI locale if non valid uppercase locale is given', metadatasFISV, 'en', FIMetadata],
        ['Should should return undefined if metadata is empty', emptyMetadatasByError, uppercaseFiLocale, undefined],
    ])('%s', (_, input, second, expected) => {
        expect(translateMetadata({ metadata: input, lang: second as Kieli })).toStrictEqual(expected);
    });
});
