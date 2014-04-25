CREATE INDEX koodistonsuhde_ylakoodistoversio_id_idx ON koodistonsuhde USING btree (ylakoodistoversio_id);
CREATE INDEX koodistonsuhde_alakoodistoversio_id_idx ON koodistonsuhde USING btree (alakoodistoversio_id);

CREATE INDEX koodinsuhde_ylakoodiversio_id_idx ON koodinsuhde USING btree (ylakoodiversio_id);
CREATE INDEX koodinsuhde_alakoodiversio_id_idx ON koodinsuhde USING btree (alakoodiversio_id);