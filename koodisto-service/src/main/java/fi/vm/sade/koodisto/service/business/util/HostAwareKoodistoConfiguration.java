/*
 * Copyright (c) 2013 The Finnish National Board of Education - Opetushallitus
 *
 * This program is free software: Licensed under the EUPL, Version 1.1 or - as
 * soon as they will be approved by the European Commission - subsequent versions
 * of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at: http://www.osor.eu/eupl/
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * European Union Public Licence for more details.
 */
package fi.vm.sade.koodisto.service.business.util;

import fi.vm.sade.koodisto.common.configuration.KoodistoConfiguration;
import java.net.MalformedURLException;
import java.text.MessageFormat;
import org.apache.commons.configuration.ConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author markus
 */
@Component("hostAwareKoodistoConfiguration")
public class HostAwareKoodistoConfiguration extends KoodistoConfiguration {
    @Value("${host.virkailija:}")
    private String host;

    static final String protocol = "https://";
    static final private String koodistoUriFormat = "/koodisto-service/rest/codes/{0}";
    //static final private String koodiUriFormat = koodistoUriFormat + "/koodi/{1}";
    static final private String koodiUriFormat = "/koodisto-service/rest/codeelement/{0}";

    public HostAwareKoodistoConfiguration() throws ConfigurationException, MalformedURLException {
        super();
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getBaseUri() {
        if (!host.isEmpty()) {
            return protocol + host;
        } else {
            return super.getBaseUri();
        }
    }

    @Override
    public String getKoodistoResourceUri(String koodistoUri) {
        if (!host.isEmpty()) {
            return MessageFormat.format(getBaseUri() + koodistoUriFormat, koodistoUri);
        } else {
            return MessageFormat.format(configurationProperties.getString("koodistoUri"), koodistoUri);
        }
    }

    @Override
    public String getKoodiResourceUri(String koodistoUri, String koodiUri) {
        if (!host.isEmpty()) {
            return MessageFormat.format(getBaseUri() + koodiUriFormat, koodistoUri, koodiUri);
        } else {
            return MessageFormat.format(configurationProperties.getString("koodiUri"), koodiUri);
        }
    }

}
