import angular from 'angular';

const app = angular.module('koodisto');

app.factory("CodesMatcher", function() {
    return {
        nameOrTunnusMatchesSearch : function(data, filter) {
            function matchesLanguage(data, language, searchString) {
                var found = false;
                data.latestKoodistoVersio.metadata.forEach(function(metadata) {
                    found = found || metadata.kieli == language && metadata.nimi.replace(/ /g, '').toLowerCase().indexOf(searchString) > -1;
                });
                return found;
            }
            if (!filter) {
                return true;
            }
            if (filter.length < 2) {
                return false;
            }
            var searchString = filter.replace(/ /g, '').toLowerCase();
            return data.koodistoUri.indexOf(searchString) > -1 || matchesLanguage(data, "FI", searchString) || matchesLanguage(data, "EN", searchString)
                    || matchesLanguage(data, "SV", searchString);
        }
    };

});