INSERT INTO koodisto (id,koodistouri,version,organisaatioOid)
VALUES
    ('-99', 'dummy', '0', '1.2.2004.6'),
    ('-1', 'eisuhteitaviela1', '0', '1.2.2004.6'),
    ('-2', 'eisuhteitaviela2', '0', '1.2.2004.6'),
    ('-3', 'eisuhteitaviela3', '0', '1.2.2004.6'),
    ('-4', 'eisuhteitaviela4', '0', '1.2.2004.6'),
    ('-5', 'sisaltyysuhde1', '0', '1.2.2004.6'),
    ('-6', 'sisaltyysuhde2', '0', '1.2.2004.6'),
    ('-7', 'moniaversioita', '0', '1.2.2004.6'),
    ('-8', 'vainyksiversio', '0', '1.2.2004.6'),
    ('-9', 'csvfileuploaduri', '0', '1.2.2004.6'),
    ('-10', 'xmlfileuploaduri', '0', '1.2.2004.6'),
    ('-11', 'xlsfileuploaduri', '0', '1.2.2004.6'),
    ('-12', 'updatekoodisto', '0', '1.2.2004.6'),
    ('-13', 'filedownloaduri', '0', '1.2.2004.6'),
    ('-14', 'deletethisuri', '0', '1.2.2004.6'),
    ('-15', 'koodistoryhmatestikoodisto', '0', '1.2.2004.6'),
    ('-16', 'newkoodistoforgrouptesting', '0', '1.2.2004.6'),
    ('-17', 'paljonmuutoksia', '0', '1.2.2004.6');

INSERT INTO koodi (id,koodisto_id,version,koodiuri)
VALUES
    ('-99', '-99', '0', 'dummy'),
    ('-1', '-13', '0', 'downloadtestikoodi'),
    ('-2', '-17', '0', 'v'),
    ('-3', '-17', '0', 'poistettukoodi'),
    ('-4', '-17', '0', 'lisattykoodi');

INSERT INTO koodiversio (id,version,paivityspvm,luotu,tila,versio,voimassaalkupvm,koodi_id,koodiarvo)
VALUES
    ('-99', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '1990-01-01', '-99', 'dummy'),
    ('-1', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '1990-01-01', '-1', 'csvtestikoodi'),
    ('-2', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '1990-01-01', '-2', 'muutettukoodi'),
    ('-3', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '2', '1990-01-01', '-2', 'muutettukoodi'),
    ('-4', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '3', '1990-01-01', '-2', 'muutettukoodi'),
    ('-5', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '1990-01-01', '-3', 'poistettukoodi'),
    ('-6', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '1990-01-01', '-4', 'lisattykoodi');


INSERT INTO koodimetadata (id,kieli,nimi,version,koodiversio_id,lyhytnimi, kuvaus)
VALUES
    ('-99', 'FI', 'Dummy', '0', '-99','Dummy', 'Dummy'),
    ('-1', 'FI', 'Download testi', '0', '-1', 'csv', 'Description of downloaded code'),
    ('-2', 'FI', 'Muutettu koodi', '0', '-2', 'csv', 'Muutettu koodi'),
    ('-3', 'FI', 'Muutettu koodi', '0', '-3', 'csv', 'Tätä koodia muutettiin versiossa 2'),
    ('-4', 'FI', 'Koodi muutettu', '0', '-4', 'csv', 'Tätä koodia muutettiin versiossa 2 ja 3'),
    ('-5', 'FI', 'Poistettu koodi', '0', '-5', 'csv', 'Lisätty versiossa kaksi ja poistettu versiossa 3'),
    ('-6', 'FI', 'Lisätty koodi', '0', '-6', 'csv', 'Lisätty versioon kolme');

INSERT INTO koodinsuhde (id,alakoodiversio_id,ylakoodiversio_id,suhteentyyppi,version,versio,ylakoodistapassiivinen,alakoodistapassiivinen)
VALUES
    ('-99', '-99', '-99', 'SISALTYY', '0', '1', 'false', 'false');

INSERT INTO koodistoversio (id,version,paivityspvm,luotu,tila,versio,koodisto_id,voimassaalkupvm)
VALUES
    ('-99', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-99', '2012-11-20'),
    ('-1', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-1', '2012-11-20'),
    ('-2', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-2', '2012-11-20'),
    ('-3', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-3', '2012-11-20'),
    ('-4', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-4', '2012-11-20'),
    ('-5', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-5', '2012-11-20'),
    ('-6', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-6', '2012-11-20'),
    ('-7', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-7', '2012-11-20'),
    ('-8', '0', '2012-03-22', '2013-06-01', 'HYVAKSYTTY', '2', '-7', '2012-11-20'),
    ('-9', '0', '2012-03-22', '2014-01-01', 'HYVAKSYTTY', '3', '-7', '2012-11-20'),
    ('-10', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-8', '2012-11-20'),
    ('-11', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-9', '2012-11-20'),
    ('-12', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-10', '2012-11-20'),
    ('-13', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-11', '2012-11-20'),
    ('-14', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-12', '2012-11-20'),
    ('-15', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-13', '2012-11-20'),
    ('-16', '0', '2012-03-22', '2013-01-01', 'PASSIIVINEN', '1', '-14', '2012-11-20'),
    ('-17', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-15', '2012-11-20'),
    ('-18', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-16', '2012-11-20'),
    ('-19', '0', '2012-03-22', '2013-01-01', 'HYVAKSYTTY', '1', '-17', '2012-11-20'),
    ('-20', '0', '2012-03-22', '2014-01-01', 'HYVAKSYTTY', '2', '-17', '2012-11-20'),
    ('-21', '0', '2012-03-22', '2014-08-20', 'LUONNOS', '3', '-17', '2013-11-20');

INSERT INTO koodistometadata (id,kieli,nimi,version,koodistoversio_id, kuvaus)
VALUES
    ('-99', 'FI', 'Dummy', '0', '-99', 'kuvaus'),
    ('-1', 'FI', '1 sisaltaa 2 -testi', '0', '-1', '1 sisaltaa 2 -testi'),
    ('-2', 'FI', '2 sisältyy 1 -testi', '0', '-2', '2 sisältyy 1 -testi'),
    ('-3', 'FI', '3 rinnastuu 4 -testi', '0', '-3', '3 rinnastuu 4 -testi'),
    ('-4', 'FI', '4 rinnastuu 3 -testi', '0', '-4', '4 rinnastuu 3 -testi'),
    ('-5', 'FI', '5 sisältää 6', '0', '-5', '5 sisältää 6'),
    ('-6', 'FI', '6 sisältyy 5', '0', '-6', '6 sisältyy 5'),
    ('-7', 'FI', '7n versio1', '0', '-7', 'Versio1'),
    ('-8', 'FI', '7n versio2', '0', '-8', 'Versio2'),
    ('-9', 'FI', '7n versio3', '0', '-9', 'Versio3'),
    ('-10', 'FI', 'Listattava koodisto', '0', '-10', 'Täytettä koodistoryhmä 3:een'),
    ('-11', 'FI', 'CSV upload testi', '0', '-11', 'Tänne ladataan csv_example.csv'),
    ('-12', 'FI', 'XML upload testi', '0', '-12', 'Tänne ladataan jhs_xml_example.xml'),
    ('-13', 'FI', 'XLS upload testi', '0', '-13', 'Tänne ladataan excel_example.xml'),
    ('-14', 'FI', 'Update testi', '0', '-14', 'Tämä päivitetään uudella nimellä'),
    ('-15', 'FI', 'CSV download testi', '0', '-15', 'Tämä ladataan csv-download testissä.'),
    ('-16', 'FI', 'Delete testi', '0', '-16', 'Tämä tuhotaan.'),
    ('-17', 'FI', 'Update koodistoryhmä testi', '0', '-17', 'Tämä testaa täyden koodistoryhmän päivittämistä'),
    ('-18', 'FI', 'Koodistoryhmään lisättävä koodi', '0', '-18', 'Lisätään koodiryhmään urin ja organisaation perusteella'),
    ('-19', 'FI', 'Paljon muutoksia', '0', '-19', 'Tässä on paljon muutoksia'),
    ('-20', 'SV', 'SV', '0', '-19', 'SV'),
    ('-21', 'FI', 'Paljon muutoksia', '0', '-20', 'Tässä on paljon muutoksia'),
    ('-22', 'FI', 'Paljon muutettu', '0', '-21', 'Muutettu vähän kaikkea'),
    ('-23', 'EN', 'Plenty of changes', '0', '-21', 'A lot of changes in this codes');

INSERT INTO koodistoversio_koodiversio (id,version,koodistoversio_id,koodiversio_id,koodiarvo)
VALUES
    ('-99', '0', '-99', '-99', 'dummy'),
    ('-1', '0', '-15', '-1', 'csvtestikoodi'),
    ('-2', '0', '-19', '-2', 'muutettukoodi'),
    ('-3', '0', '-20', '-3', 'muutettukoodi'),
    ('-4', '0', '-21', '-4', 'muutettukoodi'),
    ('-5', '0', '-20', '-5', 'poistettukoodi'),
    ('-6', '0', '-21', '-6', 'lisattykoodi');

INSERT INTO koodistoryhma (id,koodistoryhmauri,version)
VALUES
    ('-99', 'dummy', '0'),
    ('-1', 'relaatioidenlisaaminen', '0'),
    ('-2', 'koodistojenlisaaminenkoodistoryhmaan', '0'),
    ('-3', 'koodistoryhmantuhoaminen', '0'),
    ('-4', 'koodistoryhmanpaivittaminen', '0'),
    ('-5', 'koodistoryhmanpaivittaminen2', '0'),
    ('-6', 'montametadataa', '0');

INSERT INTO koodistoryhma_koodisto (koodistoryhma_id,koodisto_id)
VALUES
    ('-99', '-99'),
    ('-1', '-1'),
    ('-1', '-2'),
    ('-1', '-3'),
    ('-1', '-4'),
    ('-99', '-12'),
    ('-1', '-17'),
    ('-5', '-15');

INSERT INTO koodistoryhmametadata (id,koodistoryhma_id,kieli,nimi,version)
VALUES
    ('-99', '-99', 'FI', 'Dummy', '0'),
    ('-1', '-1', 'FI', 'Relaatioiden lisääminen', '0'),
    ('-2', '-2', 'FI', 'Koodistojen lisääminen koodistoryhmään', '0'),
    ('-3', '-3', 'FI', 'Tyhjän koodistoryhmän tuhoaminen', '0'),
    ('-4', '-4', 'FI', 'Tyhjän koodistoryhmän päivittäminen', '0'),
    ('-5', '-5', 'FI', 'Koodistoryhmän päivittäminen', '0'),
    ('-6', '-6', 'FI', 'Koodistoryhmä jolla monta metadataa', '0'),
    ('-7', '-6', 'SV', 'Koodistoryhmä jolla monta metadataa', '0'),
    ('-8', '-6', 'EN', 'Koodistoryhmä jolla monta metadataa', '0');

INSERT INTO koodistonsuhde (id,alakoodistoversio_id,ylakoodistoversio_id,versio,suhteentyyppi,version,ylakoodistostapassiivinen,alakoodistostapassiivinen)
VALUES
    ('-1', '-6', '-5', '1', 'SISALTYY', '0', 'false', 'false'),
    ('-2', '-16', '-19', '1', 'SISALTYY', '0', 'true', 'true'),
    ('-3', '-17', '-20', '1', 'RINNASTEINEN', '0', 'false', 'false'),
    ('-4', '-17', '-21', '2', 'RINNASTEINEN', '0', 'false', 'false'),
    ('-5', '-16', '-21', '1', 'RINNASTEINEN', '0', 'false', 'true');
