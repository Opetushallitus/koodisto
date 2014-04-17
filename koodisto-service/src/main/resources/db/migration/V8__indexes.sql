CREATE INDEX koodimetadata_koodiversio_id_idx ON koodimetadata USING btree (koodiversio_id);

CREATE UNIQUE INDEX koodisto_koodistouri_idx ON koodisto USING btree (koodistouri COLLATE pg_catalog."default");

CREATE INDEX koodistometadata_koodistoversio_id_idx ON koodistometadata USING btree (koodistoversio_id);

CREATE INDEX koodistoryhma_koodisto_koodistoryhma_id_idx ON koodistoryhma_koodisto USING btree (koodistoryhma_id);

CREATE INDEX koodistoversio_koodisto_id_idx ON koodistoversio USING btree (koodisto_id);
CREATE INDEX koodistoversio_versio_idx ON koodistoversio USING btree (versio);

CREATE INDEX koodistoversio_koodiversio_koodistoversio_id_idx ON koodistoversio_koodiversio USING btree (koodistoversio_id);
CREATE INDEX koodistoversio_koodiversio_koodiversio_id_idx ON koodistoversio_koodiversio USING btree (koodiversio_id);

CREATE INDEX koodiversio_koodi_id_idx ON koodiversio USING btree (koodi_id);