# Koodisto-UI

## Lokaali ajaminen ympäristöä vasten
### Konfigurointi
Hae valmis koodiston konfiguraatiotiedosto ympäristön instanssilta tai täytä projektin mukana tuleva `koodisto.properties.template`-tiedosto, poista .template-pääte ja kopioi se `oph-configuration`-kansioon kotihakemistossasi

Luo oph-configuration kansioosi `koodisto-ui.properties`-tiedosto ja aseta sinne koodiston vaatimat url-propertyt

    organisaatio-service.byOid=https://virkailija.untuvaopintopolku.fi/organisaatio-service/rest/organisaatio/:oid
    organisaatio-service.hae=https://virkailija.untuvaopintopolku.fi/organisaatio-service/rest/organisaatio/hae
    organisaatio-service.parentoids=https://virkailija.untuvaopintopolku.fi/organisaatio-service/rest/organisaatio/$1/parentoids
    koodisto-service.base=http://localhost:8081/koodisto-service/rest/
    koodisto-service.codesgroup=http://localhost:8081/koodisto-service/rest/codesgroup
    cas.myroles=http://localhost:8081/cas/myroles
    koodisto-service.i18n=http://localhost:8081/koodisto-ui/i18n/
Nämä pätevät tätä kirjoittaessa joten tarkista uusimmat propertyt `UrlConfiguration.java`-tiedostosta

Koodisto-ui tarvitsee omasta koodisto-ui taustapalvelimestaan tiedostoja joten tätä on ajettava erillisen webpack palvelimen lisäksi portissa 8081. Webpack palvelin on konfiguroitu ohjaamaan `localhost:8081/configurations/*` => `localhost:3000/configurations/*` 

### Ajaminen
Projektin juuressa `mvn clean install`

/servers `mvn jetty:run`

Projektin juuressa `npm start`

### Selain
UI:ssa tehdään kyselyitä eri domaineihin joten selaimesta pitää poistaa käytöstä jotain suojausominaisuuksia

Sulje kaikki chrome instanssit ennen seuraavan ajamista.

    Linux $ google-chrome --disable-web-security

    Windows chrome.exe --user-data-dir="C:/Chrome dev session" --disable-web-security

    OSX $ open -a Google\ Chrome --args --disable-web-security --user-data-dir

Tämän jälkeen avaa sessio haluttuihin palveluihin kirjautumalla käytettävään testiympäristöön.

**Huom. ei turvallinen selailuun kehityksen aikana! Käytä tällöin rinnalla eri selainta.**
