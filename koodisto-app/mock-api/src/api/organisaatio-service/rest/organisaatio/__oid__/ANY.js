module.exports = function (request, response) {
    response.json({
        status: 'AKTIIVINEN',
        version: 0,
        oid: '1.2.246.562.10.00000000001',
        alkuPvm: '1970-01-01',
        nimi: { fi: 'Opetushallitus', sv: 'Utbildningsstyrelsen', en: 'Finnish National Agency for Education' },
        kieletUris: [],
        kuvaus2: {},
        maaUri: 'maatjavaltiot1_fin',
        parentOidPath: '',
        tyypit: ['Muu organisaatio'],
        vuosiluokat: [],
        ryhmatyypit: [],
        kayttoryhmat: [],
        yhteystietoArvos: [],
        kayntiosoite: {},
        yhteystiedot: [],
        nimet: [
            {
                nimi: { fi: 'Opetushallitus', sv: 'Utbildningsstyrelsen', en: 'Finnish National Agency for Education' },
                alkuPvm: '1970-01-01',
                version: 0,
            },
        ],
        postiosoite: {},
    });
};
