module.exports = function (request, response) {
    response.json({
        nimi: { fi: 'Opetushallitus', sv: 'Utbildningsstyrelsen', en: 'Finnish National Agency for Education' },
    });
};
