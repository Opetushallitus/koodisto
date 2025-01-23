import { Kieli, Metadata, Koodi } from '../types';

export const mapKoodiToCSV = (koodi: Koodi) => {
    const allLang: Kieli[] = ['FI', 'SV', 'EN'];
    const metadataKeys = ['nimi', 'lyhytNimi', 'kuvaus'] as (keyof Metadata)[];
    const reducedMetadata = allLang.reduce((p, language) => {
        const languageKeyedMetadata: { [key: string]: string | undefined } = {};
        const languageMetadata = {
            nimi: '',
            lyhytNimi: '',
            kuvaus: '',
            ...koodi.metadata.find((a) => a.kieli === language),
        };
        metadataKeys.forEach((k) => (languageKeyedMetadata[`${k}_${language}`] = languageMetadata[k]));
        return { ...p, ...languageKeyedMetadata };
    }, {});
    return {
        koodistoUri: koodi.koodisto?.koodistoUri,
        koodiArvo: koodi.koodiArvo,
        versio: koodi.versio,
        voimassaAlkuPvm: koodi.voimassaAlkuPvm,
        voimassaLoppuPvm: koodi.voimassaLoppuPvm,
        ...reducedMetadata,
    };
};
