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

Kontainer buildataan github actionilla, ja sonarcloud analyysi löytyy

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/summary/new_code?id=Opetushallitus_koodisto-app)
