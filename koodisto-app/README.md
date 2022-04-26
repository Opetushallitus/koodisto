# Koodisto-palvelun käyttöliittymä

## Sisältää sovellukset:

-   Käyttöliittymä
    -   ./
-   Mock virkailija
    -   ./mock-api
-   SpringBoot aws containeria varten
    -   ./server

## Paikalliskehitys

Npm install:

`npm run install:all`

Käynnistä dev modessa, sisältäen mock-api:

`npm run dev`

Jest testit:

`npm run test `

Cypress testit:

`npm run cypress:ci`

Container buildataan github actionilla, ja sonarcloud analyysi löytyy

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Opetushallitus_koodisto-app&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Opetushallitus_koodisto-app)
