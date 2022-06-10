INSERT INTO koodistoryhma (id,koodistoryhmauri,version)
VALUES
    ('-99', 'dummy', '0'),
    ('-1', 'a', '0'),
    ('-2', 'b', '0'),
    ('-3', 'c', '0');

INSERT INTO koodistoryhmametadata (id,koodistoryhma_id,kieli,nimi,version)
VALUES
    ('-99', '-99', 'FI', 'Dummy', '0'),

    ('-1', '-1', 'FI', 'a1', '0'),
    ('-2', '-1', 'SV', 'a2', '0'),

    ('-3', '-2', 'FI', 'b1', '0'),
    ('-4', '-2', 'SV', 'b2', '0'),
    ('-5', '-2', 'EN', 'b3', '0'),

    ('-6', '-3', 'FI', 'c1', '0'),
    ('-7', '-3', 'SV', 'c2', '0'),
    ('-8', '-3', 'EN', 'c3', '0');

INSERT INTO koodisto (id,koodistouri,version,organisaatioOid)
VALUES
    ('-99', 'dummy', '0', '1.2.2004.6');

INSERT INTO koodi (id,koodisto_id,version,koodiuri)
VALUES
    ('-99', '-99', '0', 'dummy');

INSERT INTO koodiversio (id,version,paivityspvm,luotu,tila,versio,voimassaalkupvm,koodi_id,koodiarvo)
VALUES
    ('-99', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '1990-01-01', '-99', 'dummy');

INSERT INTO koodimetadata (id,kieli,nimi,version,koodiversio_id,lyhytnimi, kuvaus)
VALUES
    ('-99', 'FI', 'Dummy', '0', '-99','Dummy', 'Dummy');

INSERT INTO koodistoversio (id,version,paivityspvm,luotu,tila,versio,koodisto_id,voimassaalkupvm)
VALUES
    ('-99', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-99', '2012-11-20');

INSERT INTO koodistometadata (id,kieli,nimi,version,koodistoversio_id, kuvaus)
VALUES
    ('-99', 'FI', 'Dummy', '0', '-99', 'kuvaus');

INSERT INTO koodistoversio_koodiversio (id,version,koodistoversio_id,koodiversio_id)
VALUES
    ('-99', '0', '-99', '-99');

INSERT INTO koodistoryhma_koodisto (koodistoryhma_id,koodisto_id)
VALUES
    ('-99', '-99');