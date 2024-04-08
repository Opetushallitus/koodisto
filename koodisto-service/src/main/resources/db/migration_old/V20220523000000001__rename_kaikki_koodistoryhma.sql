UPDATE koodistoryhma
SET koodistoryhmauri = 'muutkoodistot'
WHERE id = 0;

UPDATE koodistoryhmametadata
SET nimi = 'Muut koodistot'
WHERE koodistoryhma_id = 0
  AND kieli = 'FI';