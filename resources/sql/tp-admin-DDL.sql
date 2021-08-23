# Skapa nya tabeller i vald databas

DROP TABLE IF EXISTS Filtercategorization;
DROP TABLE IF EXISTS Filter;
DROP TABLE IF EXISTS Anvandare;
DROP TABLE IF EXISTS Vagval;
DROP TABLE IF EXISTS Anropsbehorighet;
DROP TABLE IF EXISTS LogiskAdress;
DROP TABLE IF EXISTS Tjanstekontrakt;
DROP TABLE IF EXISTS AnropsAdress;
DROP TABLE IF EXISTS Tjanstekomponent;
DROP TABLE IF EXISTS RivTaProfil;
DROP TABLE IF EXISTS PubVersion;


CREATE TABLE `RivTaProfil` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `beskrivning` varchar(255) DEFAULT NULL,
  `namn` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_NAMN` (`namn`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Tjanstekomponent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `beskrivning` varchar(255) DEFAULT NULL,
  `hsaId` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_HSAID` (`hsaId`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Tjanstekontrakt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `beskrivning` varchar(255) DEFAULT NULL,
  `namnrymd` varchar(255) DEFAULT NULL,
  `majorVersion` bigint(20) DEFAULT 0,
  `minorVersion` bigint(20) DEFAULT 0,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_NAMNRYMD` (`namnrymd`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `LogiskAdress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `beskrivning` varchar(255) DEFAULT NULL,
  `hsaId` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_HSAID` (`hsaId`,`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Anropsbehorighet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `fromTidpunkt` date DEFAULT NULL,
  `integrationsavtal` varchar(255) DEFAULT NULL,
  `tomTidpunkt` date DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `logiskAdress_id` bigint(20) NOT NULL,
  `tjanstekonsument_id` bigint(20) NOT NULL,
  `tjanstekontrakt_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_TJANSTEKONSUMENT` (`tjanstekonsument_id`,`tjanstekontrakt_id`,`logiskAdress_id`,`fromTidpunkt`,`tomTidpunkt`,`deleted`),
  KEY `FK1144C39E31F3452` (`tjanstekontrakt_id`),
  KEY `FK1144C39E388AE8DD` (`tjanstekonsument_id`),
  KEY `FK1144C39EA69F7BA2` (`logiskAdress_id`),
  FOREIGN KEY (logiskAdress_id) REFERENCES LogiskAdress (id),
  FOREIGN KEY (tjanstekonsument_id) REFERENCES Tjanstekomponent (id),
  FOREIGN KEY (tjanstekontrakt_id) REFERENCES Tjanstekontrakt (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `AnropsAdress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `adress` varchar(255) DEFAULT NULL,
  `rivTaProfil_id` bigint(20) NOT NULL,
  `tjanstekomponent_id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_TJANSTEKOMPONENT_ADRESS` (`tjanstekomponent_id`,`rivTaProfil_id`,`adress`,`deleted`),
  KEY `FK9144C39E31F3452` (`tjanstekomponent_id`),
  KEY `FK9144C39E388AE8DD` (`rivTaProfil_id`),
  FOREIGN KEY (tjanstekomponent_id) REFERENCES Tjanstekomponent (id),
  FOREIGN KEY (rivTaProfil_id) REFERENCES RivTaProfil (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


CREATE TABLE `Vagval` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `fromTidpunkt` date DEFAULT NULL,
  `tomTidpunkt` date DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `logiskAdress_id` bigint(20) NOT NULL,
  `tjanstekontrakt_id` bigint(20) NOT NULL,
  `anropsAdress_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_VAGVAL_ADRESS` (`anropsAdress_id`,`tjanstekontrakt_id`,`logiskAdress_id`,`fromTidpunkt`,`tomTidpunkt`,`deleted`),
  KEY `FK2C881BB350F9DB81` (`anropsAdress_id`),
  KEY `FK2C881BB3E6234A82` (`tjanstekontrakt_id`),
  KEY `FK2C881BB331F3452` (`logiskAdress_id`),
  FOREIGN KEY (logiskAdress_id) REFERENCES LogiskAdress (id),
  FOREIGN KEY (anropsAdress_id) REFERENCES AnropsAdress (id),
  FOREIGN KEY (tjanstekontrakt_id) REFERENCES Tjanstekontrakt (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Anvandare` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `anvandarnamn` varchar(255) NOT NULL,
  `losenord_hash` varchar(255) NOT NULL,
  `administrator` boolean DEFAULT FALSE,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- user=admin password=skltp 
-- user=skltp password=skltp 
INSERT INTO `Anvandare` (`id`, `anvandarnamn`, `losenord_hash`, `administrator`, `version`) VALUES 
(1, 'admin', '3e1a694fd3a41e113dfbd4bf108cdee44206d1b1', 1, 0), 
(2, 'skltp', '3e1a694fd3a41e113dfbd4bf108cdee44206d1b1', 0, 0); 

CREATE TABLE `Filter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `servicedomain` varchar(255) NOT NULL,
  `version` bigint(20) NOT NULL,
  `anropsbehorighet_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_SERVICEDOMAIN` (`anropsbehorighet_id`,`servicedomain`,`deleted`),
  KEY `FK7D6DB798BC716E82` (`anropsbehorighet_id`),
  FOREIGN KEY (anropsbehorighet_id) REFERENCES Anropsbehorighet (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

CREATE TABLE `Filtercategorization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `category` varchar(255) NOT NULL,
  `version` bigint(20) NOT NULL,
  `filter_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_CATEGORY` (`filter_id`,`category`,`deleted`),
  KEY `FK7EB5D6C12046FE42` (`filter_id`),
  FOREIGN KEY (filter_id) REFERENCES Filter (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

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

CREATE INDEX anropsbehorighet_distinct_idx ON Anropsbehorighet (
  `fromTidpunkt`,
  `integrationsavtal`,
  `tomTidpunkt`,
  `version`,
  `logiskAdress_id`,
  `tjanstekonsument_id`,
  `tjanstekontrakt_id`,
  `deleted`);
  
CREATE TABLE Locktb (
  tabell varchar(100) NOT NULL,
  locked int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`tabell`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO Locktb(`tabell`,`locked`)
VALUES('PubVersion',0);

CREATE TABLE TAKSettings (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `settingName` varchar(255) DEFAULT NULL,
  `settingValue` TEXT DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE (`settingName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `TAKSettings` (`id`, `settingName`, `settingValue` ,`version`) VALUES
  (1, 'alerter.mail.toAddress', 'toAddress@server.com,toAddress2@server.com', 0),
  (2, 'alerter.mail.fromAddress', 'fromAddress@server.com', 0),
  (3, 'alerter.mail.publicering.subject', 'C5-Tjänsteplattformen TAKning  ${date} ', 0),
  (4, 'alerter.mail.publicering.text', '${separator} Publicerad version: ${pubVersion.id} ${separator} Format version: ${pubVersion.formatVersion} ${separator} Skapad den: ${pubVersion.time} ${separator} Utförare: ${pubVersion.utforare} ${separator} Kommentar: ${pubVersion.kommentar} ${separator}  ${separator} ${listOfChanges}', 0),
  (5, 'alerter.mail.rollback.subject', 'C5-Tjänsteplattformen TAKning  ${date} ', 0),
  (6, 'alerter.mail.rollback.text', '${separator} Rollback av version: ${pubVersion.id} ${separator} Skapad den:  ${pubVersion.time} ${separator} Utförare: ${pubVersion.utforare} ${separator} Kommentar:${pubVersion.kommentar}', 0);

