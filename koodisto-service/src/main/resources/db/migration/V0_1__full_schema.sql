--
-- PostgreSQL database dump
--

SET statement_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = off;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET escape_string_warning = off;

SET search_path = public, pg_catalog;


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: oph
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;



--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: oph
--

SELECT pg_catalog.setval('hibernate_sequence', 1, true);


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: koodi; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodi (
    id bigint NOT NULL,
    version bigint NOT NULL,
    koodiuri character varying(256),
    koodisto_id bigint NOT NULL
);


--
-- Name: koodimetadata; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodimetadata (
    id bigint NOT NULL,
    version bigint NOT NULL,
    eisisallamerkitysta character varying(2048),
    huomioitavakoodi character varying(256),
    kasite character varying(256),
    kayttoohje character varying(256),
    kieli character varying(256) NOT NULL,
    kuvaus character varying(2048) NOT NULL,
    lyhytnimi character varying(256) NOT NULL,
    nimi character varying(256) NOT NULL,
    sisaltaakoodiston character varying(2048),
    sisaltaamerkityksen character varying(2048),
    koodiversio_id bigint NOT NULL
);


--
-- Name: koodinsuhde; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodinsuhde (
    id bigint DEFAULT nextval('hibernate_sequence'::regclass) NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    alakoodiversio_id bigint NOT NULL,
    suhteentyyppi character varying(256) NOT NULL,
    ylakoodiversio_id bigint NOT NULL
);


--
-- Name: koodisto; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodisto (
    id bigint NOT NULL,
    version bigint NOT NULL,
    koodistouri character varying(256) NOT NULL,
    lukittu boolean,
    omistaja character varying(256),
    organisaatiooid character varying(256) NOT NULL,
    sitovuustaso character varying(255)
);


--
-- Name: koodistometadata; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodistometadata (
    id bigint NOT NULL,
    version bigint NOT NULL,
    kasite character varying(256),
    kayttoohje character varying(2048),
    kieli character varying(256),
    kohdealue character varying(256),
    kohdealueenosaalue character varying(256),
    kuvaus character varying(2048) NOT NULL,
    nimi character varying(256) NOT NULL,
    toimintaymparisto character varying(256),
    koodistoversio_id bigint NOT NULL,
    huomioitavakoodisto character varying(256),
    koodistonlahde character varying(256),
    sitovuustaso character varying(256),
    tarkentaakoodistoa character varying(256)
);


--
-- Name: koodistoryhma; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodistoryhma (
    id bigint NOT NULL,
    version bigint NOT NULL,
    koodistoryhmauri character varying(256)
);


--
-- Name: koodistoryhma_koodisto; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodistoryhma_koodisto (
    koodistoryhma_id bigint NOT NULL,
    koodisto_id bigint NOT NULL
);


--
-- Name: koodistoryhmametadata; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodistoryhmametadata (
    id bigint NOT NULL,
    version bigint NOT NULL,
    kieli character varying(256) NOT NULL,
    nimi character varying(256) NOT NULL,
    koodistoryhma_id bigint NOT NULL
);


--
-- Name: koodistoversio; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodistoversio (
    id bigint NOT NULL,
    version bigint NOT NULL,
    paivityspvm timestamp without time zone,
    tila character varying(256) DEFAULT 'LUONNOS'::character varying NOT NULL,
    versio integer NOT NULL,
    voimassaalkupvm date NOT NULL,
    voimassaloppupvm date,
    koodisto_id bigint NOT NULL,
    huomioitavakoodisto character varying(255),
    koodistonlahde character varying(255),
    tarkentaakoodistoa character varying(255),
    toimintaymparisto character varying(255)
);

--
-- Name: koodistoversio_koodiversio; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodistoversio_koodiversio (
    koodistoversio_id bigint NOT NULL,
    koodiversio_id bigint NOT NULL,
    id bigint NOT NULL,
    version bigint NOT NULL
);

--
-- Name: koodiversio; Type: TABLE; Schema: public; Owner: oph; Tablespace: 
--

CREATE TABLE koodiversio (
    id bigint NOT NULL,
    version bigint NOT NULL,
    koodiarvo character varying(256) NOT NULL,
    paivityspvm timestamp without time zone,
    tila character varying(256) DEFAULT 'LUONNOS'::character varying NOT NULL,
    versio integer NOT NULL,
    voimassaalkupvm date NOT NULL,
    voimassaloppupvm date,
    koodi_id bigint NOT NULL
);

--
-- Name: koodi_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodi
    ADD CONSTRAINT koodi_pkey PRIMARY KEY (id);


--
-- Name: koodimetadata_kieli_key; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodimetadata
    ADD CONSTRAINT koodimetadata_kieli_key UNIQUE (kieli, koodiversio_id);


--
-- Name: koodimetadata_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodimetadata
    ADD CONSTRAINT koodimetadata_pkey PRIMARY KEY (id);


--
-- Name: koodinsuhde_alakoodi_id_key; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT koodinsuhde_alakoodi_id_key UNIQUE (alakoodiversio_id, ylakoodiversio_id, suhteentyyppi);


--
-- Name: koodinsuhde_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT koodinsuhde_pkey PRIMARY KEY (id);


--
-- Name: koodisto_koodistouri_key; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodisto
    ADD CONSTRAINT koodisto_koodistouri_key UNIQUE (koodistouri);


--
-- Name: koodisto_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodisto
    ADD CONSTRAINT koodisto_pkey PRIMARY KEY (id);


--
-- Name: koodistometadata_kieli_key; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodistometadata
    ADD CONSTRAINT koodistometadata_kieli_key UNIQUE (kieli, koodistoversio_id);


--
-- Name: koodistometadata_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodistometadata
    ADD CONSTRAINT koodistometadata_pkey PRIMARY KEY (id);


--
-- Name: koodistoryhma_koodisto_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodistoryhma_koodisto
    ADD CONSTRAINT koodistoryhma_koodisto_pkey PRIMARY KEY (koodistoryhma_id, koodisto_id);


--
-- Name: koodistoryhma_koodistoryhmauri_key; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodistoryhma
    ADD CONSTRAINT koodistoryhma_koodistoryhmauri_key UNIQUE (koodistoryhmauri);


--
-- Name: koodistoryhma_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodistoryhma
    ADD CONSTRAINT koodistoryhma_pkey PRIMARY KEY (id);


--
-- Name: koodistoryhmametadata_kieli_key; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodistoryhmametadata
    ADD CONSTRAINT koodistoryhmametadata_kieli_key UNIQUE (kieli, koodistoryhma_id);


--
-- Name: koodistoryhmametadata_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodistoryhmametadata
    ADD CONSTRAINT koodistoryhmametadata_pkey PRIMARY KEY (id);


--
-- Name: koodistoversio_koodiversio_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodistoversio_koodiversio
    ADD CONSTRAINT koodistoversio_koodiversio_pkey PRIMARY KEY (id);


--
-- Name: koodistoversio_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodistoversio
    ADD CONSTRAINT koodistoversio_pkey PRIMARY KEY (id);


--
-- Name: koodistoversio_versio_key; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodistoversio
    ADD CONSTRAINT koodistoversio_versio_key UNIQUE (versio, koodisto_id);


--
-- Name: koodiversio_pkey; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodiversio
    ADD CONSTRAINT koodiversio_pkey PRIMARY KEY (id);


--
-- Name: koodiversio_versio_key; Type: CONSTRAINT; Schema: public; Owner: oph; Tablespace: 
--

ALTER TABLE ONLY koodiversio
    ADD CONSTRAINT koodiversio_versio_key UNIQUE (versio, koodi_id);


--
-- Name: fk34dd96db32803578; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodistoversio_koodiversio
    ADD CONSTRAINT fk34dd96db32803578 FOREIGN KEY (koodiversio_id) REFERENCES koodiversio(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: fk34dd96db8af3cb3c; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodistoversio_koodiversio
    ADD CONSTRAINT fk34dd96db8af3cb3c FOREIGN KEY (koodistoversio_id) REFERENCES koodistoversio(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: fk4708282d8af3cb3c; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodistometadata
    ADD CONSTRAINT fk4708282d8af3cb3c FOREIGN KEY (koodistoversio_id) REFERENCES koodistoversio(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: fk500453c6b14d4f98; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodiversio
    ADD CONSTRAINT fk500453c6b14d4f98 FOREIGN KEY (koodi_id) REFERENCES koodi(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: fk59d388661a846418; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodistoryhmametadata
    ADD CONSTRAINT fk59d388661a846418 FOREIGN KEY (koodistoryhma_id) REFERENCES koodistoryhma(id);


--
-- Name: fk617f550f685885c; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodi
    ADD CONSTRAINT fk617f550f685885c FOREIGN KEY (koodisto_id) REFERENCES koodisto(id);


--
-- Name: fk77fc2ec94e4f9546; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT fk77fc2ec94e4f9546 FOREIGN KEY (ylakoodiversio_id) REFERENCES koodiversio(id);


--
-- Name: fk77fc2ec958ded32e; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodinsuhde
    ADD CONSTRAINT fk77fc2ec958ded32e FOREIGN KEY (alakoodiversio_id) REFERENCES koodiversio(id);


--
-- Name: fkb91e82d4f685885c; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodistoversio
    ADD CONSTRAINT fkb91e82d4f685885c FOREIGN KEY (koodisto_id) REFERENCES koodisto(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: fkbbbd849f32803578; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodimetadata
    ADD CONSTRAINT fkbbbd849f32803578 FOREIGN KEY (koodiversio_id) REFERENCES koodiversio(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: fkcc1cb2061a846418; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodistoryhma_koodisto
    ADD CONSTRAINT fkcc1cb2061a846418 FOREIGN KEY (koodistoryhma_id) REFERENCES koodistoryhma(id);


--
-- Name: fkcc1cb206f685885c; Type: FK CONSTRAINT; Schema: public; Owner: oph
--

ALTER TABLE ONLY koodistoryhma_koodisto
    ADD CONSTRAINT fkcc1cb206f685885c FOREIGN KEY (koodisto_id) REFERENCES koodisto(id) ON UPDATE CASCADE ON DELETE CASCADE;

--
-- PostgreSQL database dump complete
--

