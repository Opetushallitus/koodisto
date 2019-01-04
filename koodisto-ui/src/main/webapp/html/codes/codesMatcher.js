
export class CodesMatcher {
    constructor() {
        "ngInject";
    }

    nameOrTunnusMatchesSearch(data, filter) {
        const matchesLanguage = (data, language, searchString) => {
            let found = false;
            data.latestKoodistoVersio.metadata.forEach((metadata) => {
                found = found || metadata.kieli === language && metadata.nimi.replace(/ /g, '').toLowerCase().indexOf(searchString) > -1;
            });
            return found;
        };
        if (!filter) {
            return true;
        }
        if (filter.length < 2) {
            return false;
        }
        const searchString = filter.replace(/ /g, '').toLowerCase();
        return data.koodistoUri.indexOf(searchString) > -1 || matchesLanguage(data, "FI", searchString) || matchesLanguage(data, "EN", searchString)
            || matchesLanguage(data, "SV", searchString);
    }
}
