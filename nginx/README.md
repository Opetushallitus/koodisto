# Koodisto UI:n lokaalidevaus

Perustuu nginx välityspalvelimeen. Ajatus on että osa resursseista haetaan 
lokaalista tiedostojärjestelmästä ja loput halutusta pilviympäristöstä.

## Vaatimukset

* docker
* docker-compose

## Resurssit

Välityspalvelinta ajetaan portissa **8080**

## Ohjeet

1. Käynnistä kehityspalvelin `npm start`
2. Käynnistä välityspalvelin: `docker-compose up`
3. Autentikoidu navigoimalle selaimella: http://localhost:8080/cas -> pitäisi päätyä virkailijan työpöydälle.
4. Navigoi organisaatiopalveluun joko menun kautta tai suoraan: http://localhost:8080/koodisto-ui

## Ongelmatilanteet

### Haluan käyttää eri kehitysympäristöä

1. Muokkaa [nginx.conf](nginx.conf) tiedostoa ja vaihda kehitysympäristö haluttuun.
2. Tyhjennä evästeet
3. Katso ohjeet yllä
