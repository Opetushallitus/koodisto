INSERT INTO koodisto (id, koodistouri, version, organisaatioOid)
VALUES ('-1', 'one', '0', '1.2.2004.6'),
       ('-2', 'two', '1', '1.2.2004.6'),
       ('-3', 'three', '1', '1.2.2004.6'),
       ('-4', 'four', '1', '1.2.2004.6');

INSERT INTO koodistoversio (id, version, paivityspvm, luotu, tila, versio, koodisto_id, voimassaalkupvm)
VALUES ('-1', '0', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '-1', '2012-11-20'),
       ('-2', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '-2', '2012-11-20'),
       ('-3', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '-3', '2012-11-20'),
       ('-4', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '-4', '2012-11-20');


INSERT INTO koodistoryhma (id, koodistoryhmauri, version)
VALUES ('-1', 'general', '0');

INSERT INTO koodistoryhmametadata (id, version, kieli, nimi, koodistoryhma_id)
VALUES (-1, 0, 'FI', 'general', -1);

INSERT INTO koodistoryhma_koodisto (koodistoryhma_id, koodisto_id)
VALUES ('-1', '-1'),
       ('-1', '-2'),
       ('-1', '-3'),
       ('-1', '-4');

INSERT INTO koodistometadata (id, kieli, nimi, version, koodistoversio_id, kuvaus)
VALUES ('-1', 'FI', 'One', '1', '-1', 'kuvaus'),
       ('-2', 'FI', 'Two', '1', '-2', 'kuvaus'),
       ('-3', 'FI', 'Three', '1', '-3', 'kuvaus'),
       ('-4', 'FI', 'Four', '1', '-4', 'Two kuvaus');


INSERT INTO koodistonSuhde (id, versio, version, ylakoodistoversio_id, alakoodistoversio_id, suhteentyyppi, alakoodistostapassiivinen, ylakoodistostapassiivinen)
VALUES (-1, 1, 1, -1, -2, 'RINNASTEINEN',false,false),
       (-2, 1, 1, -1, -3, 'SISALTYY',false,false),
       (-3, 1, 1, -4, -1, 'SISALTYY',false,false);

INSERT INTO koodi (id, koodisto_id, version, koodiuri)
VALUES ('-1', '-1', '1', 'one_1'),
       ('-2', '-2', '1', 'two_1'),
       ('-3', '-3', '1', 'three_1'),
       ('-4', '-4', '1', 'four_1');

INSERT INTO koodiversio (id, version, paivityspvm, luotu, tila, versio, voimassaalkupvm, koodi_id, koodiarvo)
VALUES ('-1', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '1990-01-01', '-1', '1'),
       ('-2', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '1990-01-01', '-2', '1'),
       ('-3', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '1990-01-01', '-3', '1'),
       ('-4', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '1990-01-01', '-4', '1');

INSERT INTO koodimetadata (id, kieli, nimi, version, koodiversio_id, lyhytnimi, kuvaus)
VALUES ('-1', 'FI', '1', '1', '-1', '1', '1'),
       ('-2', 'FI', '2', '1', '-2', '2', '2'),
       ('-3', 'FI', '3', '1', '-3', '3', '3'),
       ('-4', 'FI', '4', '1', '-4', '4', '4');

INSERT INTO koodistoversio_koodiversio (id, version, koodistoversio_id, koodiversio_id)
VALUES ('-1', '1', '-1', '-1'),
       ('-2', '1', '-2', '-2'),
       ('-3', '1', '-3', '-3'),
       ('-4', '1', '-4', '-4');
