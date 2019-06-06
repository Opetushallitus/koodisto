export class CsrfHeaderInterceptor {
    constructor($cookies) {
        "ngInject";
        this.$cookies = $cookies;

        this.request = (config) => {
            config.headers['Caller-Id'] = "1.2.246.562.10.00000000001.koodisto.koodisto-ui.frontend";

            const csrfToken = this.$cookies['CSRF'];
            if (csrfToken) {
                config.headers['CSRF'] = csrfToken;
                console.debug("CSRF header '%s' set", csrfToken);
            }

            return config;
        };
    }
}
