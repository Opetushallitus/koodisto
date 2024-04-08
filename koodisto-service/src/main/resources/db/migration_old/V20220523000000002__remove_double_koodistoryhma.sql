DELETE
FROM koodistoryhma_koodisto
WHERE koodisto_id IN
      (SELECT koodisto_id
       FROM koodistoryhma_koodisto
       GROUP BY koodisto_id
       HAVING count(1) > 1)
  AND koodistoryhma_id = 0;
