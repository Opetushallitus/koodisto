import { MessageDescriptor } from '@formatjs/intl/src/types';
import { FormatXMLElementFn, PrimitiveType } from 'intl-messageformat';
import { Options as IntlMessageFormatOptions } from 'intl-messageformat/src/core';

export type Kieli = 'EN' | 'FI' | 'SV';
export type ApiDate = `${number}${number}${number}${number}-${number}${number}-${number}${number}`;

export type Metadata = {
    kieli: Kieli;
    nimi: string;
    kuvaus?: string;
    lyhytnimi?: string;
    kayttoohje?: string;
    kasite?: string;
    sisaltaamerkityksen?: string;
    eisisallamerkitysta?: string;
    huomioitavakoodi?: string;
    sisaltaakoodiston?: string;
    kohdealue?: string;
    sitovuustaso?: string;
    kohdealueenOsaAlue?: string;
    toimintaymparisto?: string;
    tarkentaaKoodistoa?: string;
    huomioitavaKoodisto?: string;
    koodistonLahde?: string;
};

export type Koodi = UpsertKoodi & {
    versio: number;
    version: number;
    koodiUri: string;
    paivitysPvm: string;
    resourceUri: string;
    tila: string;
    koodisto?: { koodistoUri: string };
};
export type UpsertKoodi = {
    koodiArvo: string;
    versio?: number;
    voimassaAlkuPvm?: string;
    voimassaLoppuPvm?: string;
    metadata: Metadata[];
};
export type CsvKoodiObject = {
    koodistoUri: string;
    koodiArvo: string;
    versio: string;
    voimassaAlkuPvm?: string;
    voimassaLoppuPvm?: string;
    nimi_FI: string;
    nimi_SV: string;
    nimi_EN: string;
    lyhytNimi_FI: string;
    lyhytNimi_SV: string;
    lyhytNimi_EN: string;
    kuvaus_FI: string;
    kuvaus_SV: string;
    kuvaus_EN: string;
    newRow: boolean;
};

export type MessageFormatter = (
    descriptor: MessageDescriptor,
    values?: Record<string, PrimitiveType | FormatXMLElementFn<string, string>>,
    opts?: IntlMessageFormatOptions
) => string;
