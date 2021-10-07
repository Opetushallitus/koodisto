--
-- KH-297: Tutkinnonosajärjestyskoodistojen ja suhteiden poisto koodistopalvelusta.
--

-- 1. Drop foreign keys with no cascade rule
ALTER TABLE koodi DROP CONSTRAINT fk617f550f685885c;
ALTER TABLE koodistonsuhde DROP CONSTRAINT fk77fc2ec94e4f9547;
ALTER TABLE koodistonsuhde DROP CONSTRAINT fk77fc2ec958ded328;
ALTER TABLE koodinsuhde DROP CONSTRAINT fk77fc2ec94e4f9546;
ALTER TABLE koodinsuhde DROP CONSTRAINT fk77fc2ec958ded32e;

-- 2. Recreate aforementioned foreign keys with cascade
ALTER TABLE ONLY koodi
    ADD CONSTRAINT fk617f550f685885c FOREIGN KEY (koodisto_id) REFERENCES koodisto(id) ON DELETE CASCADE;

ALTER TABLE ONLY koodistonsuhde
    ADD CONSTRAINT fk77fc2ec94e4f9547 FOREIGN KEY (ylakoodistoversio_id) REFERENCES koodistoversio(id) ON DELETE CASCADE;

ALTER TABLE ONLY koodistonsuhde
    ADD CONSTRAINT fk77fc2ec958ded328 FOREIGN KEY (alakoodistoversio_id) REFERENCES koodistoversio(id) ON DELETE CASCADE;

ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT fk77fc2ec94e4f9546 FOREIGN KEY (ylakoodiversio_id) REFERENCES koodiversio(id) ON DELETE CASCADE;

ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT fk77fc2ec958ded32e FOREIGN KEY (alakoodiversio_id) REFERENCES koodiversio(id) ON DELETE CASCADE;

-- 3. Drop obsoleted codecs (should cascade to remove completely)
DELETE FROM koodisto WHERE id IN
    (SELECT DISTINCT koodisto_id FROM koodistoversio WHERE id IN
        (SELECT koodistoversio_id FROM koodistometadata WHERE
            lower(nimi) LIKE '% at järjestys' OR
            lower(nimi) LIKE '% eat järjestys' OR
            lower(nimi) like '% pt järjestys'));

-- 4. Drop foreign keys modified in step 2
ALTER TABLE koodi DROP CONSTRAINT fk617f550f685885c;
ALTER TABLE koodistonsuhde DROP CONSTRAINT fk77fc2ec94e4f9547;
ALTER TABLE koodistonsuhde DROP CONSTRAINT fk77fc2ec958ded328;
ALTER TABLE koodinsuhde DROP CONSTRAINT fk77fc2ec94e4f9546;
ALTER TABLE koodinsuhde DROP CONSTRAINT fk77fc2ec958ded32e;

-- 5. Recreate foreign keys in original form
ALTER TABLE ONLY koodi
    ADD CONSTRAINT fk617f550f685885c FOREIGN KEY (koodisto_id) REFERENCES koodisto(id);

ALTER TABLE ONLY koodistonsuhde
    ADD CONSTRAINT fk77fc2ec94e4f9547 FOREIGN KEY (ylakoodistoversio_id) REFERENCES koodistoversio(id);

ALTER TABLE ONLY koodistonsuhde
    ADD CONSTRAINT fk77fc2ec958ded328 FOREIGN KEY (alakoodistoversio_id) REFERENCES koodistoversio(id);

ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT fk77fc2ec94e4f9546 FOREIGN KEY (ylakoodiversio_id) REFERENCES koodiversio(id);

ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT fk77fc2ec958ded32e FOREIGN KEY (alakoodiversio_id) REFERENCES koodiversio(id);
