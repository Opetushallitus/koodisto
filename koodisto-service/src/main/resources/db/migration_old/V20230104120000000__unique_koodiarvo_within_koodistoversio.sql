--
-- KH-402: Koodiarvo needs to be unique within koodistoversio
--

-- Add extra column to ensure uniqueness
ALTER TABLE koodistoversio_koodiversio ADD COLUMN koodiarvo VARCHAR(256) NOT NULL DEFAULT '';

-- Populate with sane default
UPDATE koodistoversio_koodiversio SET koodiarvo = kv.koodiarvo FROM koodiversio kv WHERE kv.id = koodiversio_id;

-- "mask" existing duplicates e.g. keep one intact, concatenate rest with some rubbish
WITH input AS (
UPDATE koodistoversio_koodiversio set koodiarvo = koodiarvo || '-' || id::VARCHAR
WHERE (koodistoversio_id, koodiarvo) IN (
    SELECT koodistoversio_id, koodiarvo
    FROM koodistoversio_koodiversio
    GROUP BY koodistoversio_id, koodiarvo
    HAVING COUNT(id) > 1)
AND id NOT IN (
    SELECT MIN(id)
    FROM koodistoversio_koodiversio
    GROUP BY koodistoversio_id, koodiarvo
    HAVING COUNT(id) > 1) RETURNING *
) SELECT COUNT(id) AS replaced_non_unique_values FROM input;

-- Add constraint to ensure uniqueness
ALTER TABLE koodistoversio_koodiversio ADD CONSTRAINT UK_koodisto_koodiVersio_02 UNIQUE (koodistoversio_id, koodiarvo);
