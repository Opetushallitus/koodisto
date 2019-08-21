// Käytössä vain suomenkieliset "käännökset"
import angular from 'angular';
import {urls} from 'oph-urls-js';
import jQuery from 'jquery';

export default angular.module('localization', [])
    .filter('i18n', [ () => {
        jQuery.ajaxSetup({
          headers: {
            'Caller-Id': '1.2.246.562.10.00000000001.koodisto.koodisto-ui'
          }
        });

        jQuery.i18n.properties({
            name : 'messages',
            path : urls.url('koodisto-service.i18n'),
            mode : 'map',
            language : 'fi_FI'
        });

        return (text) => {
            return jQuery.i18n.prop(text);
        };
    } ]);
