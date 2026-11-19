create table AnropsAdress
(
    id                  bigint identity not null,
    deleted             bit,
    pubVersion          varchar(255),
    updatedBy           varchar(255),
    updatedTime         datetime2(6),
    adress              varchar(255),
    version             bigint not null,
    rivTaProfil_id      bigint not null,
    tjanstekomponent_id bigint not null,
    primary key (id)
);
create table Anropsbehorighet
(
    id                  bigint identity not null,
    deleted             bit,
    pubVersion          varchar(255),
    updatedBy           varchar(255),
    updatedTime         datetime2(6),
    fromTidpunkt        date,
    integrationsavtal   varchar(255),
    tomTidpunkt         date,
    version             bigint not null,
    logiskAdress_id     bigint not null,
    tjanstekonsument_id bigint not null,
    tjanstekontrakt_id  bigint not null,
    primary key (id)
);
create table Anvandare
(
    id            bigint identity not null,
    administrator bit,
    anvandarnamn  varchar(255) not null,
    losenord_hash varchar(255),
    version       bigint       not null,
    primary key (id)
);
create table Filter
(
    id                  bigint identity not null,
    deleted             bit,
    pubVersion          varchar(255),
    updatedBy           varchar(255),
    updatedTime         datetime2(6),
    servicedomain       varchar(255),
    version             bigint not null,
    anropsbehorighet_id bigint not null,
    primary key (id)
);
create table Filtercategorization
(
    id          bigint identity not null,
    deleted     bit,
    pubVersion  varchar(255),
    updatedBy   varchar(255),
    updatedTime datetime2(6),
    category    varchar(255),
    version     bigint not null,
    filter_id   bigint not null,
    primary key (id)
);
create table Locktb
(
    tabell varchar(255) not null,
    locked int,
    primary key (tabell)
);
create table LogiskAdress
(
    id          bigint identity not null,
    deleted     bit,
    pubVersion  varchar(255),
    updatedBy   varchar(255),
    updatedTime datetime2(6),
    beskrivning varchar(255),
    hsaId       varchar(255),
    version     bigint not null,
    primary key (id)
);
create table PubVersion
(
    id            bigint identity not null,
    data          varbinary(max),
    formatVersion bigint not null,
    kommentar     varchar(255),
    storlek       bigint not null,
    time          datetime2(6),
    utforare      varchar(255),
    version       bigint not null,
    primary key (id)
);
create table RivTaProfil
(
    id          bigint identity not null,
    deleted     bit,
    pubVersion  varchar(255),
    updatedBy   varchar(255),
    updatedTime datetime2(6),
    beskrivning varchar(255),
    namn        varchar(255),
    version     bigint not null,
    primary key (id)
);
create table TAKSettings
(
    id           bigint identity not null,
    settingName  varchar(255) not null,
    settingValue varchar(max) not null,
    version      bigint       not null,
    primary key (id)
);
create table Tjanstekomponent
(
    id          bigint identity not null,
    deleted     bit,
    pubVersion  varchar(255),
    updatedBy   varchar(255),
    updatedTime datetime2(6),
    beskrivning varchar(255),
    hsaId       varchar(255),
    version     bigint not null,
    primary key (id)
);
create table Tjanstekontrakt
(
    id           bigint identity not null,
    deleted      bit,
    pubVersion   varchar(255),
    updatedBy    varchar(255),
    updatedTime  datetime2(6),
    beskrivning  varchar(255),
    majorVersion bigint not null,
    minorVersion bigint not null,
    namnrymd     varchar(255),
    version      bigint not null,
    primary key (id)
);
create table Vagval
(
    id                 bigint identity not null,
    deleted            bit,
    pubVersion         varchar(255),
    updatedBy          varchar(255),
    updatedTime        datetime2(6),
    fromTidpunkt       date,
    tomTidpunkt        date,
    version            bigint not null,
    anropsAdress_id    bigint not null,
    logiskAdress_id    bigint not null,
    tjanstekontrakt_id bigint not null,
    primary key (id)
);
alter table Anvandare
    add constraint UKo6prihj4imh6d40bu0pqnyhe1 unique (anvandarnamn);
alter table TAKSettings
    add constraint UK7e3wfnkd8ns6asax3nfxh3c1o unique (settingName);
alter table AnropsAdress
    add constraint FKkjoc0te5hv32trad7rnyj95h6 foreign key (rivTaProfil_id) references RivTaProfil;
alter table AnropsAdress
    add constraint FKgv6borcp70omgihq48fthhamh foreign key (tjanstekomponent_id) references Tjanstekomponent;
alter table Anropsbehorighet
    add constraint FKprhjvdww7w9yuql7x6804vmc2 foreign key (logiskAdress_id) references LogiskAdress;
alter table Anropsbehorighet
    add constraint FKfi6xpovy5phvre06mer2f2h9q foreign key (tjanstekonsument_id) references Tjanstekomponent;
alter table Anropsbehorighet
    add constraint FKkaamxv2h20d3jiicwwrnofntr foreign key (tjanstekontrakt_id) references Tjanstekontrakt;
alter table Filter
    add constraint FK6fymvlsugsijaf1b1chttjcfu foreign key (anropsbehorighet_id) references Anropsbehorighet;
alter table Filtercategorization
    add constraint FKnd08lnxiycqs947ngofil2ent foreign key (filter_id) references Filter;
alter table Vagval
    add constraint FK3jhxnd1a85x7n6eq70m5dot95 foreign key (anropsAdress_id) references AnropsAdress;
alter table Vagval
    add constraint FKnv6ybpvlgdwyrleerywf4r8m6 foreign key (logiskAdress_id) references LogiskAdress;
alter table Vagval
    add constraint FKb4ts74h8entt0r6o1hdmdaiis foreign key (tjanstekontrakt_id) references Tjanstekontrakt;
