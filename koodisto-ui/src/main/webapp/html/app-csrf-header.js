export class CsrfHeaderInterceptor {
    // TODO fix this
    constructor($cookies) {
        "ngInject";
        this.$cookies = $cookies;

        this.request = (config) => {
            config.headers['clientSubSystemCode'] = "koodisto.koodisto-ui.frontend";

            var csrfToken = this.$cookies['CSRF'];
            if (csrfToken) {
                config.headers['CSRF'] = csrfToken;
                console.debug("CSRF header '%s' set", csrfToken);
            }

            return config;
        };
    }
}
