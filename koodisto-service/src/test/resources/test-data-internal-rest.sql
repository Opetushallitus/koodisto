INSERT INTO koodisto (id, koodistouri, version, organisaatioOid)
VALUES ('-1', 'dummy', '0', '1.2.2004.6'),
       ('-2', 'one', '1', '1.2.2004.6'),
       ('-3', 'get', '1', '1.2.2004.6'),
       ('-4', 'two', '1', '1.2.2004.6');

INSERT INTO koodistoversio (id, version, paivityspvm, luotu, tila, versio, koodisto_id, voimassaalkupvm)
VALUES ('-1', '0', '2012-03-22 13:23:41.414000', '2013-01-01', 'HYVAKSYTTY', '1', '-1', '2012-11-20'),
       ('-2', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '-2', '2012-11-20'),
       ('-3', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'HYVAKSYTTY', '1', '-3', '2012-11-20'),
       ('-4', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '-4', '2012-11-20');

INSERT INTO koodistoryhma (id, koodistoryhmauri, version)
VALUES ('-1', 'general', '0'),
       ('-2', 'dummy', '0');

INSERT INTO koodistoryhmametadata (id, version, kieli, nimi, koodistoryhma_id)
VALUES (-1, 0, 'FI', 'general', -1),
       (-2, 0, 'FI', 'dummy', -2);

INSERT INTO koodistoryhma_koodisto (koodistoryhma_id, koodisto_id)
VALUES ('-1', '-1'),
       ('-1', '-2'),
       ('-1', '-3'),
       ('-1', '-4');

INSERT INTO koodistometadata (id, kieli, nimi, version, koodistoversio_id, kuvaus)
VALUES ('-1', 'FI', 'Dummy', '0', '-1', 'kuvaus'),
       ('-2', 'FI', 'One', '1', '-2', 'kuvaus'),
       ('-3', 'FI', 'get', '1', '-3', 'get'),
       ('-4', 'FI', 'Twot', '1', '-4', 'Two kuvaus');


INSERT INTO koodi (id, koodisto_id, version, koodiuri)
VALUES ('-1', '-3', '1', 'get_1'),
       ('-2', '-4', '1', 'two_1');

INSERT INTO koodiversio (id, version, paivityspvm, luotu, tila, versio, voimassaalkupvm, koodi_id, koodiarvo)
VALUES ('-1', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'HYVAKSYTTY', '1', '1990-01-01', '-1', '1'),
       ('-2', '1', '2012-03-22 13:23:41.414000', '2013-01-01', 'LUONNOS', '1', '1990-01-01', '-2', '1');

INSERT INTO koodimetadata (id, kieli, nimi, version, koodiversio_id, lyhytnimi, kuvaus)
VALUES ('-1', 'FI', 'get1', '1', '-1', 'Get 1', 'Get 1'),
       ('-2', 'FI', 'two1', '1', '-2', 'Two 1', 'Two 1');

INSERT INTO koodistoversio_koodiversio (id, version, koodistoversio_id, koodiversio_id)
VALUES ('-3', '1', '-3', '-1'),
       ('-4', '1', '-4', '-2');