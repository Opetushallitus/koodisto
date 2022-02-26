ALTER TABLE koodistonsuhde ADD COLUMN versio integer;
UPDATE koodistonsuhde SET versio = 1;
ALTER TABLE koodistonsuhde ALTER COLUMN versio SET NOT NULL;