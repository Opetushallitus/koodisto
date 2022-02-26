ALTER TABLE koodinsuhde ADD COLUMN versio integer;
UPDATE koodinsuhde SET versio = 1;
ALTER TABLE koodinsuhde ALTER COLUMN versio SET NOT NULL;