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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;

