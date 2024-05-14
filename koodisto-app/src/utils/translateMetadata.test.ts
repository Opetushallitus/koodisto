import assert from 'assert/strict';
import { describe, it } from 'node:test';

import { translateMetadata } from './translateMetadata';
import { Kieli, Metadata } from '../types';

describe('translateMetadata', () => {
    const uppercaseSVLocale = 'SV';
    const uppercaseFiLocale = 'FI';
    const uppercaseEnLocale = 'EN';
    const FIMetadata = { kieli: uppercaseFiLocale };
    const SVMetadata = { kieli: uppercaseSVLocale };
    const ENMetadata = { kieli: uppercaseEnLocale };
    const metadatasAllLangs = [FIMetadata, SVMetadata, ENMetadata] as Metadata[];
    const metadatasFISV = [FIMetadata, SVMetadata] as Metadata[];
    const emptyMetadatasByError = [] as Metadata[];

    it('Should return localized metadata based on locale', () => {
        assert.deepStrictEqual(translateMetadata({ metadata: metadatasAllLangs, lang: uppercaseSVLocale }), SVMetadata);
    });
    it('Should default to FI locale if locale prop is not found', () => {
        assert.deepStrictEqual(translateMetadata({ metadata: metadatasFISV, lang: uppercaseEnLocale }), FIMetadata);
    });
    it('Should default to FI locale if non valid uppercase locale is given', () => {
        assert.deepStrictEqual(translateMetadata({ metadata: metadatasFISV, lang: 'en' as Kieli }), FIMetadata);
    });
    it('Should should return undefined if metadata is empty', () => {
        assert.deepStrictEqual(
            translateMetadata({ metadata: emptyMetadatasByError, lang: uppercaseFiLocale }),
            undefined
        );
    });
});
