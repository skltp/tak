#4 nya kolumner i AnropsAdress
ALTER TABLE AnropsAdress ADD `updatedTime` date DEFAULT NULL;
ALTER TABLE AnropsAdress ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE AnropsAdress ADD `pubVersion` varchar(255) DEFAULT NULL;
ALTER TABLE AnropsAdress ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Anropsbehorighet
ALTER TABLE Anropsbehorighet ADD `updatedTime` varchar(255) DEFAULT NULL;
ALTER TABLE Anropsbehorighet ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Anropsbehorighet ADD `pubVersion` varchar(255) DEFAULT NULL;
ALTER TABLE Anropsbehorighet ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Filter
ALTER TABLE Filter ADD `updatedTime` date DEFAULT NULL;
ALTER TABLE Filter ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Filter ADD `pubVersion` varchar(255) DEFAULT NULL;
ALTER TABLE Filter ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Filtercategorization
ALTER TABLE Filtercategorization ADD `updatedTime` date DEFAULT NULL;
ALTER TABLE Filtercategorization ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Filtercategorization ADD `pubVersion` varchar(255) DEFAULT NULL;
ALTER TABLE Filtercategorization ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Logiskadress
ALTER TABLE LogiskAdress ADD `updatedTime` date DEFAULT NULL;
ALTER TABLE LogiskAdress ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE LogiskAdress ADD `pubVersion` varchar(255) DEFAULT NULL;
ALTER TABLE LogiskAdress ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Rivtaprofil
ALTER TABLE RivTaProfil ADD `updatedTime` date DEFAULT NULL;
ALTER TABLE RivTaProfil ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE RivTaProfil ADD `pubVersion` varchar(255) DEFAULT NULL;
ALTER TABLE RivTaProfil ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Tjanstekomponent
ALTER TABLE Tjanstekomponent ADD `updatedTime` date DEFAULT NULL;
ALTER TABLE Tjanstekomponent ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Tjanstekomponent ADD `pubVersion` varchar(255) DEFAULT NULL;
ALTER TABLE Tjanstekomponent ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Tjanstekontrakt
ALTER TABLE Tjanstekontrakt ADD `updatedTime` date DEFAULT NULL;
ALTER TABLE Tjanstekontrakt ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Tjanstekontrakt ADD `pubVersion` varchar(255) DEFAULT NULL;
ALTER TABLE Tjanstekontrakt ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Vagval
ALTER TABLE Vagval ADD `updatedTime` date DEFAULT NULL;
ALTER TABLE Vagval ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Vagval ADD `pubVersion` varchar(255) DEFAULT NULL;
ALTER TABLE Vagval ADD `deleted` bool DEFAULT FALSE;

# Set PubVersion to 0 as initial value
UPDATE AnropsAdress SET PubVersion = 0;
UPDATE Anropsbehorighet SET PubVersion = 0;
UPDATE Filter SET PubVersion = 0;
UPDATE Filtercategorization SET PubVersion = 0;
UPDATE LogiskAdress SET PubVersion = 0;
UPDATE RivTaProfil SET PubVersion = 0;
UPDATE Tjanstekomponent SET PubVersion = 0;
UPDATE Tjanstekontrakt SET PubVersion = 0;
UPDATE Vagval SET PubVersion = 0;

DROP TABLE IF EXISTS PubVersion;

CREATE TABLE `PubVersion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `data` longblob,
  `formatVersion` bigint(20) NOT NULL,
  `kommentar` varchar(255) DEFAULT NULL,
  `time` date DEFAULT NULL,
  `utforare` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `storlek` bigint(20) DEFAULT 0,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

# Remove old Unique constraints and update it with 'deleted' column
ALTER TABLE `AnropsAdress` DROP INDEX `UC_TJANSTEKOMPONENT_ADRESS`;
ALTER TABLE `AnropsAdress` ADD CONSTRAINT `UC_TJANSTEKOMPONENT_ADRESS` UNIQUE (`tjanstekomponent_id`,`rivTaProfil_id`,`adress`,`deleted`);

ALTER TABLE `Anropsbehorighet` DROP INDEX `UC_TJANSTEKONSUMENT`;
ALTER TABLE `Anropsbehorighet` ADD CONSTRAINT `UC_TJANSTEKONSUMENT` UNIQUE (`tjanstekonsument_id`,`tjanstekontrakt_id`,`logiskAdress_id`,`fromTidpunkt`,`tomTidpunkt`,`deleted`);
  
ALTER TABLE `Filter` DROP INDEX `UC_SERVICEDOMAIN`;
ALTER TABLE `Filter` ADD CONSTRAINT `UC_SERVICEDOMAIN` UNIQUE (`anropsbehorighet_id`,`servicedomain`,`deleted`);

ALTER TABLE `Filtercategorization` DROP INDEX `UC_CATEGORY`;
ALTER TABLE `Filtercategorization` ADD CONSTRAINT `UC_CATEGORY` UNIQUE (`filter_id`,`category`,`deleted`);

ALTER TABLE `LogiskAdress` DROP INDEX `UC_HSAID`;
ALTER TABLE `LogiskAdress` ADD CONSTRAINT `UC_HSAID` UNIQUE (`hsaId`,`deleted`);

ALTER TABLE `RivTaProfil` DROP INDEX `UC_NAMN`;
ALTER TABLE `RivTaProfil` ADD CONSTRAINT `UC_NAMN` UNIQUE (`namn`,`deleted`);

ALTER TABLE `Tjanstekomponent` DROP INDEX `UC_HSAID`;
ALTER TABLE `Tjanstekomponent` ADD CONSTRAINT `UC_HSAID` UNIQUE (`hsaId`,`deleted`);

ALTER TABLE `Tjanstekontrakt` DROP INDEX `UC_NAMNRYMD`;
ALTER TABLE `Tjanstekontrakt` ADD CONSTRAINT `UC_NAMNRYMD` UNIQUE (`namnrymd`,`deleted`);

ALTER TABLE `Vagval` DROP INDEX `UC_VAGVAL_ADRESS`;
ALTER TABLE `Vagval` ADD CONSTRAINT `UC_VAGVAL_ADRESS` UNIQUE (`anropsAdress_id`,`tjanstekontrakt_id`,`logiskAdress_id`,`fromTidpunkt`,`tomTidpunkt`,`deleted`);

# Remove old index och update it with 'deleted' column
DROP INDEX anropsbehorighet_distinct_idx ON Anropsbehorighet;

CREATE INDEX anropsbehorighet_distinct_idx ON Anropsbehorighet (
  `fromTidpunkt`,
  `integrationsavtal`,
  `tomTidpunkt`,
  `version`,
  `logiskAdress_id`,
  `tjanstekonsument_id`,
  `tjanstekontrakt_id`,
  `deleted`);
