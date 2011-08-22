DROP TABLE IF EXISTS Anvandare;
DROP TABLE IF EXISTS LogiskAdress;
DROP TABLE IF EXISTS Anropsbehorighet;
DROP TABLE IF EXISTS LogiskAdressat;
DROP TABLE IF EXISTS Tjanstekontrakt;
DROP TABLE IF EXISTS Tjanstekomponent;
DROP TABLE IF EXISTS RivVersion;


CREATE TABLE `rivversion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `beskrivning` varchar(255) DEFAULT NULL,
  `namn` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) TYPE=INNODB; 

CREATE TABLE `tjanstekomponent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `adress` varchar(255) DEFAULT NULL,
  `beskrivning` varchar(255) DEFAULT NULL,
  `hsaId` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) TYPE=INNODB; 

CREATE TABLE `tjanstekontrakt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `beskrivning` varchar(255) DEFAULT NULL,
  `namnrymd` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) TYPE=INNODB; 

CREATE TABLE `logiskadressat` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `beskrivning` varchar(255) DEFAULT NULL,
  `hsaId` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) TYPE=INNODB; 

CREATE TABLE `anropsbehorighet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fromTidpunkt` date DEFAULT NULL,
  `integrationsavtal` varchar(255) DEFAULT NULL,
  `tomTidpunkt` date DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `logiskAdressat_id` bigint(20) NOT NULL,
  `tjanstekonsument_id` bigint(20) NOT NULL,
  `tjanstekontrakt_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK1144C39E31F3452` (`tjanstekontrakt_id`),
  KEY `FK1144C39E388AE8DD` (`tjanstekonsument_id`),
  KEY `FK1144C39EA69F7BA2` (`logiskAdressat_id`),
  FOREIGN KEY (logiskAdressat_id) REFERENCES logiskadressat (id),
  FOREIGN KEY (tjanstekonsument_id) REFERENCES tjanstekomponent (id),
  FOREIGN KEY (tjanstekontrakt_id) REFERENCES tjanstekontrakt (id)
) TYPE=INNODB;

CREATE TABLE `logiskadress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fromTidpunkt` date DEFAULT NULL,
  `tomTidpunkt` date DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `logiskAdressat_id` bigint(20) NOT NULL,
  `rivVersion_id` bigint(20) NOT NULL,
  `tjanstekontrakt_id` bigint(20) NOT NULL,
  `tjansteproducent_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK2C881BB350F9DB81` (`tjansteproducent_id`),
  KEY `FK2C881BB3E6234A82` (`rivVersion_id`),
  KEY `FK2C881BB331F3452` (`tjanstekontrakt_id`),
  KEY `FK2C881BB3A69F7BA2` (`logiskAdressat_id`),
  FOREIGN KEY (logiskAdressat_id) REFERENCES logiskadressat (id),
  FOREIGN KEY (rivVersion_id) REFERENCES rivversion (id),
  FOREIGN KEY (tjanstekontrakt_id) REFERENCES tjanstekontrakt (id),
  FOREIGN KEY (tjansteproducent_id) REFERENCES tjanstekomponent (id)
) TYPE=INNODB;

CREATE TABLE `Anvandare` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `anvandarnamn` varchar(255) NOT NULL,
  `losenord_hash` varchar(255) NOT NULL,
  `administrator` boolean DEFAULT FALSE,
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) TYPE=INNODB; 
