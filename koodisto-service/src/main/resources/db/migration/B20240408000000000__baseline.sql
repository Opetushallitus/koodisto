create sequence hibernate_sequence
    increment by 25;

alter sequence hibernate_sequence owner to app;

create table koodisto
(
    id              bigint       not null
        primary key,
    version         bigint       not null,
    koodistouri     varchar(256) not null
        unique,
    lukittu         boolean,
    omistaja        varchar(256),
    organisaatiooid varchar(256) not null,
    sitovuustaso    varchar(255)
);

alter table koodisto
    owner to app;

create table koodi
(
    id          bigint not null
        primary key,
    version     bigint not null,
    koodiuri    varchar(256),
    koodisto_id bigint not null
        constraint fk617f550f685885c
            references koodisto
);

alter table koodi
    owner to app;

create index koodi_koodisto_id_idx
    on koodi (koodisto_id);

create unique index koodi_koodiuri_idx
    on koodi (koodiuri);

create unique index koodisto_koodistouri_idx
    on koodisto (koodistouri);

create table koodistoryhma
(
    id               bigint not null
        primary key,
    version          bigint not null,
    koodistoryhmauri varchar(256)
        unique
);

alter table koodistoryhma
    owner to app;

create table koodistoryhma_koodisto
(
    koodistoryhma_id bigint not null
        constraint fkcc1cb2061a846418
            references koodistoryhma,
    koodisto_id      bigint not null
        constraint fkcc1cb206f685885c
            references koodisto
            on update cascade on delete cascade,
    primary key (koodistoryhma_id, koodisto_id)
);

alter table koodistoryhma_koodisto
    owner to app;

create index koodistoryhma_koodisto_koodistoryhma_id_idx
    on koodistoryhma_koodisto (koodistoryhma_id);

create table koodistoryhmametadata
(
    id               bigint       not null
        primary key,
    version          bigint       not null,
    kieli            varchar(256) not null,
    nimi             varchar(256) not null,
    koodistoryhma_id bigint       not null
        constraint fk59d388661a846418
            references koodistoryhma,
    constraint koodistoryhmametadata_kieli_key
        unique (kieli, koodistoryhma_id)
);

alter table koodistoryhmametadata
    owner to app;

create table koodistoversio
(
    id                  bigint                                            not null
        primary key,
    version             bigint                                            not null,
    paivityspvm         timestamp,
    tila                varchar(256) default 'LUONNOS'::character varying not null,
    versio              integer                                           not null,
    voimassaalkupvm     date                                              not null,
    voimassaloppupvm    date,
    koodisto_id         bigint                                            not null
        constraint fkb91e82d4f685885c
            references koodisto
            on update cascade on delete cascade,
    huomioitavakoodisto varchar(255),
    koodistonlahde      varchar(255),
    tarkentaakoodistoa  varchar(255),
    toimintaymparisto   varchar(255),
    luotu               timestamp                                         not null,
    paivittaja_oid      text,
    constraint koodistoversio_versio_key
        unique (versio, koodisto_id)
);

alter table koodistoversio
    owner to app;

create table koodistometadata
(
    id                  bigint       not null
        primary key,
    version             bigint       not null,
    kasite              varchar(256),
    kayttoohje          varchar(2048),
    kieli               varchar(256),
    kohdealue           varchar(256),
    kohdealueenosaalue  varchar(256),
    kuvaus              varchar(2048),
    nimi                varchar(256) not null,
    toimintaymparisto   varchar(256),
    koodistoversio_id   bigint       not null
        constraint fk4708282d8af3cb3c
            references koodistoversio
            on update cascade on delete cascade,
    huomioitavakoodisto varchar(256),
    koodistonlahde      varchar(256),
    sitovuustaso        varchar(256),
    tarkentaakoodistoa  varchar(256),
    constraint koodistometadata_kieli_key
        unique (kieli, koodistoversio_id)
);

alter table koodistometadata
    owner to app;

create index koodistometadata_koodistoversio_id_idx
    on koodistometadata (koodistoversio_id);

create table koodistonsuhde
(
    id                        bigint  default nextval('hibernate_sequence'::regclass) not null
        primary key,
    version                   bigint  default 0                                       not null,
    alakoodistoversio_id      bigint                                                  not null
        constraint fk77fc2ec958ded328
            references koodistoversio,
    suhteentyyppi             varchar(256)                                            not null,
    ylakoodistoversio_id      bigint                                                  not null
        constraint fk77fc2ec94e4f9547
            references koodistoversio,
    versio                    integer                                                 not null,
    ylakoodistostapassiivinen boolean default false                                   not null,
    alakoodistostapassiivinen boolean default false                                   not null,
    constraint koodistonsuhde_alakoodi_id_key
        unique (alakoodistoversio_id, ylakoodistoversio_id, suhteentyyppi)
);

alter table koodistonsuhde
    owner to app;

create index koodistonsuhde_alakoodistoversio_id_idx
    on koodistonsuhde (alakoodistoversio_id);

create index koodistonsuhde_ylakoodistoversio_id_idx
    on koodistonsuhde (ylakoodistoversio_id);

create index koodistoversio_koodisto_id_idx
    on koodistoversio (koodisto_id);

create index koodistoversio_versio_idx
    on koodistoversio (versio);

create table koodiversio
(
    id               bigint                                            not null
        primary key,
    version          bigint                                            not null,
    koodiarvo        varchar(256)                                      not null,
    paivityspvm      timestamp,
    tila             varchar(256) default 'LUONNOS'::character varying not null,
    versio           integer                                           not null,
    voimassaalkupvm  date                                              not null,
    voimassaloppupvm date,
    koodi_id         bigint                                            not null
        constraint fk500453c6b14d4f98
            references koodi
            on update cascade on delete cascade,
    luotu            timestamp                                         not null,
    paivittaja_oid   text,
    constraint koodiversio_versio_key
        unique (versio, koodi_id)
);

alter table koodiversio
    owner to app;

create table koodimetadata
(
    id                  bigint       not null
        primary key,
    version             bigint       not null,
    eisisallamerkitysta varchar(2048),
    huomioitavakoodi    varchar(256),
    kasite              varchar(256),
    kayttoohje          varchar(2048),
    kieli               varchar(256) not null,
    kuvaus              varchar(2048),
    lyhytnimi           varchar(256),
    nimi                varchar(512) not null,
    sisaltaakoodiston   varchar(2048),
    sisaltaamerkityksen varchar(2048),
    koodiversio_id      bigint       not null
        constraint fkbbbd849f32803578
            references koodiversio
            on update cascade on delete cascade,
    constraint koodimetadata_kieli_key
        unique (kieli, koodiversio_id)
);

alter table koodimetadata
    owner to app;

create index koodimetadata_koodiversio_id_idx
    on koodimetadata (koodiversio_id);

create table koodinsuhde
(
    id                     bigint  default nextval('hibernate_sequence'::regclass) not null
        primary key,
    version                bigint  default 0                                       not null,
    alakoodiversio_id      bigint                                                  not null
        constraint fk77fc2ec958ded32e
            references koodiversio,
    suhteentyyppi          varchar(256)                                            not null,
    ylakoodiversio_id      bigint                                                  not null
        constraint fk77fc2ec94e4f9546
            references koodiversio,
    versio                 integer                                                 not null,
    ylakoodistapassiivinen boolean default false                                   not null,
    alakoodistapassiivinen boolean default false                                   not null,
    constraint koodinsuhde_alakoodi_id_key
        unique (alakoodiversio_id, ylakoodiversio_id, suhteentyyppi)
);

alter table koodinsuhde
    owner to app;

create index koodinsuhde_alakoodiversio_id_idx
    on koodinsuhde (alakoodiversio_id);

create index koodinsuhde_ylakoodiversio_id_idx
    on koodinsuhde (ylakoodiversio_id);

create table koodistoversio_koodiversio
(
    koodistoversio_id bigint                                     not null
        constraint fk34dd96db8af3cb3c
            references koodistoversio
            on update cascade on delete cascade,
    koodiversio_id    bigint                                     not null
        constraint fk34dd96db32803578
            references koodiversio
            on update cascade on delete cascade,
    id                bigint                                     not null
        primary key,
    version           bigint                                     not null,
    koodiarvo         varchar(256) default ''::character varying not null,
    constraint uk_koodisto_koodiversio_02
        unique (koodistoversio_id, koodiarvo)
);

alter table koodistoversio_koodiversio
    owner to app;

create index koodistoversio_koodiversio_koodistoversio_id_idx
    on koodistoversio_koodiversio (koodistoversio_id);

create index koodistoversio_koodiversio_koodiversio_id_idx
    on koodistoversio_koodiversio (koodiversio_id);

create index koodiversio_koodi_id_idx
    on koodiversio (koodi_id);

create table scheduled_tasks
(
    task_name            text                     not null,
    task_instance        text                     not null,
    task_data            bytea,
    execution_time       timestamp with time zone not null,
    picked               boolean                  not null,
    picked_by            text,
    last_success         timestamp with time zone,
    last_failure         timestamp with time zone,
    consecutive_failures integer,
    last_heartbeat       timestamp with time zone,
    version              bigint                   not null,
    primary key (task_name, task_instance)
);

alter table scheduled_tasks
    owner to app;

create index execution_time_idx
    on scheduled_tasks (execution_time);

create index last_heartbeat_idx
    on scheduled_tasks (last_heartbeat);

create table schema_version
(
    installed_rank integer                 not null,
    version        varchar(50)             not null
        constraint schema_version_pk
            primary key,
    description    varchar(200)            not null,
    type           varchar(20)             not null,
    script         varchar(1000)           not null,
    checksum       integer,
    installed_by   varchar(100)            not null,
    installed_on   timestamp default now() not null,
    execution_time integer                 not null,
    success        boolean                 not null
);

alter table schema_version
    owner to app;

create index schema_version_ir_idx
    on schema_version (installed_rank);

create index schema_version_s_idx
    on schema_version (success);

create table spring_session
(
    primary_id            char(36) not null
        constraint spring_session_pk
            primary key,
    session_id            char(36) not null,
    creation_time         bigint   not null,
    last_access_time      bigint   not null,
    max_inactive_interval integer  not null,
    expiry_time           bigint   not null,
    principal_name        varchar(100)
);

alter table spring_session
    owner to app;

create unique index spring_session_ix1
    on spring_session (session_id);

create index spring_session_ix2
    on spring_session (expiry_time);

create index spring_session_ix3
    on spring_session (principal_name);

create table spring_session_attributes
(
    session_primary_id char(36)     not null
        constraint spring_session_attributes_fk
            references spring_session
            on delete cascade,
    attribute_name     varchar(200) not null,
    attribute_bytes    bytea        not null,
    constraint spring_session_attributes_pk
        primary key (session_primary_id, attribute_name)
);

alter table spring_session_attributes
    owner to app;

