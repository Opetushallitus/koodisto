ALTER TABLE koodiversio ADD COLUMN luotu timestamp without time zone;
ALTER TABLE koodistoversio ADD COLUMN luotu timestamp without time zone;
UPDATE koodiversio SET luotu = '2013-01-01 00:00';
UPDATE koodistoversio SET luotu = '2013-01-01 00:00';
ALTER TABLE koodiversio ALTER COLUMN luotu SET NOT NULL;
ALTER TABLE koodistoversio ALTER COLUMN luotu SET NOT NULL;