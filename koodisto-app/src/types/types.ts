export type KoodistoRyhma = {
    id: number;
    koodistoRyhmaUri: string;
    koodistos: Koodisto[];
    metadata: Metadata[];
};
export type Koodisto = {
    koodistoUri: string;
    organisaatioOid: string;
    metadata: Metadata[];
};
export type Metadata = {
    kieli: Kieli;
    nimi: string;
};
export type Kieli = 'EN' | 'FI' | 'SV';
