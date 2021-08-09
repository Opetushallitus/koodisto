--
-- KH-297: Tutkinnonosajärjestyskoodistojen ja suhteiden poisto koodistopalvelusta.
--

-- 1. Drop koodiversio-koodinsuhde foreign keys
ALTER TABLE koodinsuhde DROP CONSTRAINT fk77fc2ec94e4f9546;
ALTER TABLE koodinsuhde DROP CONSTRAINT fk77fc2ec958ded32e;

-- 2. Recreate with cascade
ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT fk77fc2ec94e4f9546 FOREIGN KEY (ylakoodiversio_id) REFERENCES koodiversio(id) ON DELETE CASCADE;

ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT fk77fc2ec958ded32e FOREIGN KEY (alakoodiversio_id) REFERENCES koodiversio(id) ON DELETE CASCADE;

-- 3. Drop obsoleted codes (should cascade to remove completely)
DELETE FROM koodi WHERE koodi.koodiuri = 'koulutustyyppi_12';

-- 4. Drop koodiversio-koodinsuhde foreign keys
ALTER TABLE koodinsuhde DROP CONSTRAINT fk77fc2ec94e4f9546;
ALTER TABLE koodinsuhde DROP CONSTRAINT fk77fc2ec958ded32e;

-- 5. Recreate in original form
ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT fk77fc2ec94e4f9546 FOREIGN KEY (ylakoodiversio_id) REFERENCES koodiversio(id);

ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT fk77fc2ec958ded32e FOREIGN KEY (alakoodiversio_id) REFERENCES koodiversio(id);
