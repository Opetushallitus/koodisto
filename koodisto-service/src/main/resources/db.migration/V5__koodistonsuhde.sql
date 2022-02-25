CREATE TABLE koodistonsuhde (
    id bigint DEFAULT nextval('hibernate_sequence'::regclass) NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    alakoodistoversio_id bigint NOT NULL,
    suhteentyyppi character varying(256) NOT NULL,
    ylakoodistoversio_id bigint NOT NULL
);


ALTER TABLE public.koodistonsuhde OWNER TO oph;


--
-- Name: koodistonsuhde_alakoodi_id_key; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace:
--

ALTER TABLE ONLY koodistonsuhde
    ADD CONSTRAINT koodistonsuhde_alakoodi_id_key UNIQUE (alakoodistoversio_id, ylakoodistoversio_id, suhteentyyppi);


--
-- Name: koodistonsuhde_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace:
--

ALTER TABLE ONLY koodistonsuhde
    ADD CONSTRAINT koodistonsuhde_pkey PRIMARY KEY (id);

--



--
-- Name: fk77fc2ec94e4f9547; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodistonsuhde
    ADD CONSTRAINT fk77fc2ec94e4f9547 FOREIGN KEY (ylakoodistoversio_id) REFERENCES koodistoversio(id);


--
-- Name: fk77fc2ec958ded328; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodistonsuhde
    ADD CONSTRAINT fk77fc2ec958ded328 FOREIGN KEY (alakoodistoversio_id) REFERENCES koodistoversio(id);

