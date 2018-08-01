// Käytössä vain suomenkieliset "käännökset"
import angular from "angular";
import jQuery from 'jquery';
import {urls} from 'oph-urls-js';

export default angular.module('localization', [])
    .filter('i18n', [ function() {
        jQuery.i18n.properties({
            name : 'messages',
            path : urls.url('koodisto-service.i18n'),
            mode : 'map',
            language : 'fi_FI',
            callback : function() {
            }
        });

        return function(text) {
            return jQuery.i18n.prop(text);
        };
    } ]);