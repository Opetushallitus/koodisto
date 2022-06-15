import { MessageDescriptor } from '@formatjs/intl/src/types';
import { FormatXMLElementFn, PrimitiveType } from 'intl-messageformat';
import { Options as IntlMessageFormatOptions } from 'intl-messageformat/src/core';

export type Kieli = 'EN' | 'FI' | 'SV';
export type Locale = Lowercase<Kieli>;
export type ApiDate = `${number}${number}${number}${number}-${number}${number}-${number}${number}`;

export type KoodiMetadata = Metadata & {
    lyhytnimi?: string;
};
export type Metadata = {
    kieli: Kieli;
    nimi: string;
    kuvaus?: string;
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

export type InsertKoodistoRyhma = {
    nimi: {
        fi: string;
        sv: string;
        en: string;
    };
};
export type KoodistoRyhma = InsertKoodistoRyhma & {
    koodistoRyhmaUri: string;
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

export type SelectOptionType = {
    value: string;
    label: string;
};
export type OrganisaatioNimi = {
    fi: string;
    sv: string;
    en: string;
};
type BaseKoodisto = {
    koodistoUri: string;
    versio: number;
    voimassaAlkuPvm: Date;
    voimassaLoppuPvm?: Date;
};
export type ListKoodisto = BaseKoodisto & {
    nimi?: string;
    ryhmaNimi?: string;
    ryhmaUri?: string;
    koodiCount: number;
};
export type KoodistoRelation = {
    koodistoUri: string;
    koodistoVersio: number;
    passive: boolean;
    nimi: {
        fi: string;
        sv: string;
        en: string;
    };
    kuvaus: {
        fi: string;
        sv: string;
        en: string;
    };
};
export type PageKoodisto = BaseKoodisto & {
    koodistoRyhmaUri: SelectOption;
    resourceUri: string;
    omistaja: string | null;
    organisaatioOid: string;
    organisaatioNimi?: OrganisaatioNimi;
    lukittu: boolean | null;
    koodistoRyhmaMetadata: Metadata[];
    paivitysPvm: ApiDate;
    paivittajaOid: string;
    tila: string;
    metadata: Metadata[];
    koodiVersio: number[];
    sisaltyyKoodistoihin: KoodistoRelation[];
    sisaltaaKoodistot: KoodistoRelation[];
    rinnastuuKoodistoihin: KoodistoRelation[];
    koodiList: Koodi[];
};

export type SelectOption = { label: string; value: string };
