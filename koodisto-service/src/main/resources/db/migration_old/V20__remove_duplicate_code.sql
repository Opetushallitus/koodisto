--
-- KH-303 - "tutkinonosat" codec has duplicate code values which makes
-- adminstration tricky. Remove seemingly redundant code.
--
DELETE FROM koodi
WHERE koodiuri = 'tutkinnonosat_300300-1'
AND koodisto_id = (SELECT id FROM koodisto WHERE koodistouri = 'tutkinnonosat');
