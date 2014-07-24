package fi.vm.sade.koodisto.service.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public abstract class CorsFilter {
    
    private static final String CORSFILTER_MODE_PARAM = "${common.corsfilter.mode:PRODUCTION}";

    static final String DEFAULT_DOMAIN_FOR_ALLOW_ORIGIN = "https://virkailija.opintopolku.fi";
    
    protected CorsFilterMode mode;
    
    @Value("${common.corsfilter.allowed-domains:}")
    protected String allowedDomains;

    @Value(CORSFILTER_MODE_PARAM)
    void setMode(String mode) {
        this.mode = StringUtils.isNotBlank(mode) && !mode.equalsIgnoreCase(CORSFILTER_MODE_PARAM) ? CorsFilterMode.valueOf(mode) : CorsFilterMode.PRODUCTION;
    }
    
}
