# Koodisto-palvelun käyttöliittymä

## Sisältää sovellukset:

-   Käyttöliittymä
    -   ./
-   Mock virkailija
    -   ./mock-api
-   SpringBoot aws containeria varten
    -   ./server

## Paikalliskehitys

Huom, lokaalikehitys vaatii paikallisen [backendin](https://github.com/Opetushallitus/koodisto).

Käynnistä dev modessa, sisältäen mock-api:

`./start-local-env.sh`

Nodejs testit:

`npm run test `

Cypress testit:

`npm run cypress:ci`

Container buildataan github actionilla, ja sonarcloud analyysi löytyy

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Opetushallitus_koodisto-app&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Opetushallitus_koodisto-app)
