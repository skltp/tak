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
DROP TABLE IF EXISTS Locktb;
DROP TABLE IF EXISTS TAKSettings;



CREATE TABLE `RivTaProfil` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `deleted` tinyint(1) DEFAULT '0',
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
  `deleted` tinyint(1) DEFAULT '0',
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
  `deleted` tinyint(1) DEFAULT '0',
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
  `deleted` tinyint(1) DEFAULT '0',
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
  `deleted` tinyint(1) DEFAULT '0',
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
  `deleted` tinyint(1) DEFAULT '0',
  `pubVersion` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `updatedTime` date DEFAULT NULL,
  `adress` varchar(255) DEFAULT NULL COLLATE utf8_bin,
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
  `deleted` tinyint(1) DEFAULT '0',
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
  `deleted` tinyint(1) DEFAULT '0',
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
  `deleted` tinyint(1) DEFAULT '0',
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

INSERT INTO `RivTaProfil` (`beskrivning`, `namn`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
('RIV TA BP 2.0-Published', 'RIVTABP20', NULL, NULL, 1, FALSE, 0),
('RIV TA BP 2.1-Published', 'RIVTABP21', NULL, NULL, 1, FALSE, 0),
('RIV TA BP 3.0-Published and updated', 'RIVTABP30', NULL, NULL, 1, FALSE, 0),
('RIV TA BP 4.0-Newly created', 'RIVTABP40', '2015-12-11', 'admin', NULL, FALSE, 0),
('RIV TA BP 5.0-Published and then deleted', 'RIVTABP50', NULL, NULL, 1, FALSE, 0),
('RIV TA BP 6.0-Newly created by user skltp but not published ', 'RIVTABP60', '2015-12-15', 'skltp', NULL, FALSE, 0),
('RIV TA BP 7.0-Newly created but not published', 'RIVTABP70', '2015-12-15', 'admin', NULL, FALSE, 0);
-- ('RIV TA BP 8.0-Created then deleted but not published', 'RIVTABP80', '2015-12-15', 'admin', NULL, TRUE, 0);


UPDATE `RivTaProfil` SET BESKRIVNING = 'RIV TA BP 3 Updated', UPDATEDTIME = '2015-12-11', UPDATEDBY = 'admin' WHERE NAMN = 'RIVTABP30';

UPDATE `RivTaProfil` SET UPDATEDTIME = '2015-12-11', UPDATEDBY = 'admin', DELETED=null WHERE NAMN = 'RIVTABP50';

INSERT INTO `Tjanstekontrakt` (`id`, `beskrivning`, `namnrymd`, `majorVersion`, `minorVersion`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
(10, 'Tidbokning - GetSubjectOfCareSchedule', 'urn:riv:crm:scheduling:GetSubjectOfCareScheduleResponder:1', '1', '0', NULL, NULL, 1, FALSE, 0),
(11, 'Ping', 'urn:riv:itinfra:tp:PingResponder:1', '1', '0', NULL, NULL, 1, FALSE, 0),
(12, 'Stödtjänst VP', 'urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContractResponder:1', '1', '0', NULL, NULL, 1, FALSE, 0),
(13, 'Stödtjänst VP', 'urn:riv:itintegration:registry:GetSupportedServiceContractsResponder:1', '1', '0', NULL, NULL, 1, FALSE, 0),
(14, 'Engagemangsindex - Findcontent', 'urn:riv:itintegration:engagementindex:FindContentResponder:1', '1', '0', NULL, NULL, 1, FALSE, 0),
(15, 'Engagemangsindex - ProcessNotification', 'urn:riv:itintegration:engagementindex:ProcessNotificationResponder:1','1', '0', NULL, NULL, 1, FALSE, 0),
(16, 'Engagemangsindex - Update', 'urn:riv:itintegration:engagementindex:UpdateResponder:1', '1', '0', NULL, NULL, 1, FALSE, 0),
(17, 'Stödtjänst VP ny - GetValidAddress', 'urn:riv:itintegration:registry:GetValidAffressResponder:1','1', '0', '2015-12-11', 'admin', NULL, FALSE, 0),
(18, 'Unpublished kontrakt created by skltp', 'urn:riv:itintegration:registry:UnpublishedContract:1','1', '0', '2015-12-11', 'skltp', NULL, FALSE, 0);

INSERT INTO `LogiskAdress` (`beskrivning`, `hsaId`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
('Demo adressat tidbok, vardcentralen kusten, Karna', 'HSA-VKK123', NULL, NULL, 1, FALSE, 0),
('Demo adressat tidbok, vardcentralen kusten, Marstrand', 'HSA-VKM345', NULL, NULL, 1, FALSE, 0),
('Demo adressat tidbok, vardcentralen kusten, Ytterby', 'HSA-VKY567', NULL, NULL, 1, FALSE, 0),
('VP''s egna ping-tjanst', 'PING', NULL, NULL, 1, FALSE, 0),
('Organisation: Inera', '5565594230', NULL, NULL, 1, FALSE, 0),
(NULL, 'HSA-NYA-TEST-123', '2015-10-10', 'admin', NULL, FALSE, 0),
('Organisation: Unpublished by skltp', 'HSA-UNPUBLISHED-USER-SKLTP', '2015-10-10', 'skltp', NULL, FALSE, 0);

INSERT INTO `Tjanstekomponent` (`beskrivning`, `hsaId`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
('Demo tidbok', 'SCHEDULR', NULL, NULL, 1, FALSE, 0),
('tp test client', 'TP', NULL, NULL, 1, FALSE, 0),
('VP intern ping tjänst', 'PING-HSAID', NULL, NULL, 1, FALSE, 0),
('Engagemangsidex', 'EI-HSAID', NULL, NULL, 1, FALSE, 0),
('VP-Cachad-GetLogicalAddresseesByServiceContract', 'VP-CACHAD-GETLOGICALADDRESSEESBYSERVICECONTRACT', NULL, NULL, 1, FALSE, 0),
('Inera som konsument, tex EI', '5565594230', NULL, NULL, 1, FALSE, 0),
('Producent: GetAggregatedSubjectOfCareSchedule', 'AGT-TIDBOK', NULL, NULL, 1, FALSE, 0),
('Nya producent: GetAggregatedSubjectOfCareSchedule', 'HSA-NYA-TEST-123', '2015-10-10', 'admin', NULL, FALSE, 0),
('Nya producent: UnpublishedByskltp', 'HSA-NYA-UNPUBLISHED-SKLTP', '2015-10-10', 'skltp', NULL, FALSE, 0);

INSERT INTO `AnropsAdress` (`adress`, `tjanstekomponent_id`, `rivTaProfil_id`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
('http://33.33.33.33:8080/Schedulr-0.1/ws/GetSubjectOfCareSchedule/1', 1, 2, NULL, NULL, 1, FALSE, 0),
('http://localhost:10000/test/Ping_Service', 2, 1, NULL, NULL, 1, FALSE, 0),
('http://localhost:8081/skltp-ei/update-service/v1', 4, 2, NULL, NULL, 1, FALSE, 0),
('http://localhost:8082/skltp-ei/find-content-service/v1', 4, 2, NULL, NULL, 1, FALSE, 0),
('http://localhost:8081/skltp-ei/notification-service/v1', 4, 2, NULL, NULL, 1, FALSE, 0),
('https://localhost:23001/vp/GetLogicalAddresseesByServiceContract/1/rivtabp21', 5, 2, NULL, NULL, 1, FALSE, 0),
('http://localhost:8083/GetAggregatedSubjectOfCareSchedule/service/v1', 7, 2, NULL, NULL, 1, FALSE, 0),
('http://localhost:8083/NyaServiceURL/service/v1', 4, 3, '2015-10-10', 'admin', NULL, FALSE, 0),
('http://unpublishedrivtaprofil:8083/NyaServiceURL/service/v1', 4, 1, '2015-10-10', 'admin', NULL, FALSE, 0),
('http://unpublishedrivtaprofil:8083/NyaServiceURL/service/v1', 4, 1, '2015-10-10', 'skltp', NULL, NULL, 0);

INSERT INTO `Vagval` (`fromTidpunkt`, `tomTidpunkt`, `logiskAdress_id`, `anropsAdress_id`, `tjanstekontrakt_id`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
('2013-05-24', '2113-05-24', 1, 1, 10, NULL, NULL, 1, FALSE, 0),
('2013-05-24', '2113-05-24', 2, 1, 10, NULL, NULL, 1, FALSE, 0),
('2013-05-24', '2113-05-24', 3, 1, 10, NULL, NULL, 1, FALSE, 0),
('2013-05-28', '2113-05-28', 4, 2, 11, '2015-10-10', 'admin', 1, NULL, 1),
('2013-08-24', '2113-08-24', 5, 6, 12, NULL, NULL, 1, FALSE, 0),
('2013-08-24', '2113-08-24', 5, 4, 13, NULL, NULL, 1, FALSE, 0),
('2013-08-24', '2113-08-24', 5, 5, 14, NULL, NULL, 1, FALSE, 0),
('2013-08-24', '2113-08-24', 5, 3, 15, NULL, NULL, 1, FALSE, 0),
('2013-08-24', '2113-08-24', 5, 7, 16, NULL, NULL, 1, FALSE, 0),
('2013-08-24', '2113-08-24', 5, 7, 10, '2015-10-10', 'admin', NULL, FALSE, 0),
('2013-08-24', '2014-08-24', 5, 7, 11, '2015-10-10', 'admin', 1, FALSE, 0);


INSERT INTO `Anropsbehorighet` (`fromTidpunkt`, `integrationsavtal`, `tomTidpunkt`, `logiskAdress_id`, `tjanstekonsument_id`, `tjanstekontrakt_id`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
('2013-05-24', 'I1', '2113-05-24', 1, 2, 10, NULL, NULL, 1, FALSE, 0),
('2013-05-24', 'I1', '2113-05-24', 2, 2, 10, NULL, NULL, 1, FALSE, 0),
('2013-05-24', 'I1', '2113-05-24', 3, 2, 10, NULL, NULL, 1, FALSE, 0),
('2013-05-28', 'I2', '2113-05-28', 4, 2, 11, NULL, NULL, 1, FALSE, 0),
('2013-08-24', 'EI', '2113-08-24', 5, 2, 12, NULL, NULL, 1, FALSE, 0),
('2013-08-24', 'I3', '2113-08-24', 5, 2, 11, NULL, NULL, 1, FALSE, 0),
('2013-08-24', 'EI', '2113-08-24', 5, 2, 13, NULL, NULL, 1, FALSE, 0),
('2013-08-25', 'I4', '2113-08-25', 5, 2, 14, NULL, NULL, 1, FALSE, 0),
('2013-08-25', 'Nya_I', '2113-08-25', 6, 8, 17, '2015-10-10', 'admin', NULL, FALSE, 0);

INSERT INTO `Filter` (`servicedomain`, `anropsbehorighet_id`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
('urn:riv:itintegration:registry:GetItems', 5, NULL, NULL, 1, false, 0),
('urn:riv:itintegration:registry:GetItems', 7, NULL, NULL, 1, false, 0),
('urn:riv:itintegration:registry:GetItems', 8, NULL, NULL, 1, false, 0),
('urn:riv:itintegration:registry:GetMoreItems', 8, NULL, NULL, 1, false, 0),
('urn:riv:itintegration:registry:GetItems', 9, '2015-05-24', 'admin', NULL, false, 0);

INSERT INTO `Filtercategorization` (`category`, `filter_id`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
('Category c1', 2, NULL, NULL, 3, NULL, 0),
('Category c1', 2, NULL, NULL, 1, false, 0),
('Category c2', 3, NULL, NULL, 1, false, 0),
('Category c2', 4, '2015-05-24', 'admin', NULL, false, 0);

INSERT INTO `PubVersion` (`id`, `data`, `formatVersion`, `kommentar`, `time`, `utforare`, `version`, `storlek`) VALUES
(1, 0x1f8b0800000000000000ed5add6ee23818bdef5358dc2e69fea1cd1ded74bba8db1934b048a3d56a651213521227b20d33dd51df669f615f605e6c9d8416489338240c0b9d9590dad88e7dcef97efcd9f0f50c80d6342401646344a817e216b0404b6db5e38ee5eb26e639d102cf59d2a629caa5a4e892aa8c14c54a3e3f29aaa2a443178c4f0c094a86de41df4769fb3c0c0284192449c7fa29e974208371fb57fec01f89b7643022e1d4f3e3d6df57edbcc773e206b5fdfc8c619002fdd81f8f7a57032d4591f44d109df399b087dde72160d4035703a09d6f0c8b16936dc649c7533bbba856bca85a6d5155b828f8231dd1620f105386e6216604a6babf56a1b385883c0649736b41b0c521581ef330432e818c2f6321ec4217c5a27bd8415facdf222e3afa886814f267629570b8495f85d8a5c9bb4002e9dbeb5702f810920d5f5a5b28f0f0568fb2b3f0aa52ced3268145ed1972163e476cdd22365c4c1e90cd3e4cafb9230ed3ae6a54479e3309e7f123275934d38168ab62f34e09b458640d38de4af4e2810742afede29c04b91e65e43136deafa1ebd9d0ef390e41942244af1e87882c3d1b5d27d160b34a5487ecdb3f0e7bf8f6378f23301e1c88b55e97f57011452161c8c970a5474cd668927f7ee67f6292bca16e128aa7b0d3290ec4d86cc27840429b7bf4fb907953eee1f198bacc73a6fa2e0abcec473e0f4a3a8749480ab6e41985fd54965f863d697c77a76a7a31b9772808413a2f648025e9b70d969038368a23c04718cc177c2bc46d700709864d36ee2cb47bdd30f703ed1e121ec9103bbbc3d30be17d323bddfdc0fbc4182293c7ddc11939e006fdf7b7c5b0c6030a908b2188788b94d631bbaf6be6ac6b9a1dd3bc3434bda4c6fb405c883d9a0620e86344c40ef3bae80a7850c649a5aaa3af0a032230566aa3fd38308b8a17631160886f04b6ef6da5c6261e191b5de26ed97f57667a10e73f8213db83d57eb41fa7bbe98b56df4c933c4beec7e7c603e91ada33e848952a93326d6a4e54157da766c42431026818007ed8a08b78bb6a73f7f9026efabb83e8e680e8dd8ea451c6f35fd5a524741671dab2e2a2bbe7babc34827131545e7e8b021a621246b4d2aeb51ed49a315e52cbb2ae9f3f7fac0be542919fa35c52ce55f933958b8e0772d939efa587203fc95274e645747df64dfbb6cfbf2ff15f90a3d455f793307fbc66e987dc11672165163fc12b8a1c270e393e26fcb9f2c83d7351055c340117bd12176e2f55a6739f4512f2e445725895684a485e1eda3e868093519593b6e634e5a5a0b42a7e8f979959c35a78a3a83d5e669d2266748b1a4fbd8a2a2f23b952d6975539813989b443f335057cbb552da9cbe20c2eff6766ed6ed3ccec1413340b89e7ce90a8f4db38e651c801f8e956ba81794ac26054f9d634c59b7d4355f9704dd28ca237ea49969ee57a2f6614a6e4bcfef5bda4aa54de7ede946c22576b2a9bfe3665d3bfb36c864836ed2465331aca26aa114d816c9ba7811392cd6c289ba81ced88bc4dff216513795bf77f6fcb934d17c87621f236e387942d53abbf14754be82ea1e81be423d6297b7db159a8652fe4738bb85a455a8e20aa524eef1805d95ff9f54604c92dbc6a15562714325a8920b92555ad92e98404e99408929b876b1543272488d14010d17edd3d4541cc0682886ecd2e4e5110bd8120a26bb5cb5314a4db40904e41ad36f57c8688a0565b5d1b3a6100bd9481f887357d8602ba1f05b6af0745a6d58e137657005b3f4ed81795ab933ab0ef43820e053de3f236e490f8e8bfd25ff19407c06af06302e97af500ecc6b7e8ebe813edf1da1161d14558b4fd61d1b3463c8bff7f3afb17e9cc55e04b2d0000, 1, 'default version', '2015-05-24', 'admin', 0, 2),
(2, 0x1f8b0800000000000000ed5add6ee23818bdef5358dc2e69fea1cd1ded74bba8db1934b048a3d56a651213521227b20d33dd51df669f615f605e6c9d8416489338240c0b9d9590dad88e7dcef97efcd9f0f50c80d6342401646344a817e216b0404b6db5e38ee5eb26e639d102cf59d2a629caa5a4e892aa8c14c54a3e3f29aaa2a443178c4f0c094a86de41df4769fb3c0c0284192449c7fa29e974208371fb57fec01f89b7643022e1d4f3e3d6df57edbcc773e206b5fdfc8c619002fdd81f8f7a57032d4591f44d109df399b087dde72160d4035703a09d6f0c8b16936dc649c7533bbba856bca85a6d5155b828f8231dd1620f105386e6216604a6babf56a1b385883c0649736b41b0c521581ef330432e818c2f6321ec4217c5a27bd8415facdf222e3afa886814f267629570b8495f85d8a5c9bb4002e9dbeb5702f810920d5f5a5b28f0f0568fb2b3f0aa52ced3268145ed1972163e476cdd22365c4c1e90cd3e4cafb9230ed3ae6a54479e3309e7f123275934d38168ab62f34e09b458640d38de4af4e2810742afede29c04b91e65e43136deafa1ebd9d0ef390e41942244af1e87882c3d1b5d27d160b34a5487ecdb3f0e7bf8f6378f23301e1c88b55e97f57011452161c8c970a5474cd668927f7ee67f6292bca16e128aa7b0d3290ec4d86cc27840429b7bf4fb907953eee1f198bacc73a6fa2e0abcec473e0f4a3a8749480ab6e41985fd54965f863d697c77a76a7a31b9772808413a2f648025e9b70d969038368a23c04718cc177c2bc46d700709864d36ee2cb47bdd30f703ed1e121ec9103bbbc3d30be17d323bddfdc0fbc4182293c7ddc11939e006fdf7b7c5b0c6030a908b2188788b94d631bbaf6be6ac6b9a1dd3bc3434bda4c6fb405c883d9a0620e86344c40ef3bae80a7850c649a5aaa3af0a032230566aa3fd38308b8a17631160886f04b6ef6da5c6261e191b5de26ed97f57667a10e73f8213db83d57eb41fa7bbe98b56df4c933c4beec7e7c603e91ada33e848952a93326d6a4e54157da766c42431026818007ed8a08b78bb6a73f7f9026efabb83e8e680e8dd8ea451c6f35fd5a524741671dab2e2a2bbe7babc34827131545e7e8b021a621246b4d2aeb51ed49a315e52cbb2ae9f3f7fac0be542919fa35c52ce55f933958b8e0772d939efa587203fc95274e645747df64dfbb6cfbf2ff15f90a3d455f793307fbc66e987dc11672165163fc12b8a1c270e393e26fcb9f2c83d7351055c340117bd12176e2f55a6739f4512f2e445725895684a485e1eda3e868093519593b6e634e5a5a0b42a7e8f979959c35a78a3a83d5e669d2266748b1a4fbd8a2a2f23b952d6975539813989b443f335057cbb552da9cbe20c2eff6766ed6ed3ccec1413340b89e7ce90a8f4db38e651c801f8e956ba81794ac26054f9d634c59b7d4355f9704dd28ca237ea49969ee57a2f6614a6e4bcfef5bda4aa54de7ede946c22576b2a9bfe3665d3bfb36c864836ed2465331aca26aa114d816c9ba7811392cd6c289ba81ced88bc4dff216513795bf77f6fcb934d17c87621f236e387942d53abbf14754be82ea1e81be423d6297b7db159a8652fe4738bb85a455a8e20aa524eef1805d95ff9f54604c92dbc6a15562714325a8920b92555ad92e98404e99408929b876b1543272488d14010d17edd3d4541cc0682886ecd2e4e5110bd8120a26bb5cb5314a4db40904e41ad36f57c8688a0565b5d1b3a6100bd9481f887357d8602ba1f05b6af0745a6d58e137657005b3f4ed81795ab933ab0ef43820e053de3f236e490f8e8bfd25ff19407c06af06302e97af500ecc6b7e8ebe813edf1da1161d14558b4fd61d1b3463c8bff7f3afb17e9cc55e04b2d0000, 1, 'ändrat beskrivning', '2015-07-04', 'admin', 0, 2),
(3, 0x1f8b0800000000000000ed5add6ee23818bdef5358dc2e69fea1cd1ded74bba8db1934b048a3d56a651213521227b20d33dd51df669f615f605e6c9d8416489338240c0b9d9590dad88e7dcef97efcd9f0f50c80d6342401646344a817e216b0404b6db5e38ee5eb26e639d102cf59d2a629caa5a4e892aa8c14c54a3e3f29aaa2a443178c4f0c094a86de41df4769fb3c0c0284192449c7fa29e974208371fb57fec01f89b7643022e1d4f3e3d6df57edbcc773e206b5fdfc8c619002fdd81f8f7a57032d4591f44d109df399b087dde72160d4035703a09d6f0c8b16936dc649c7533bbba856bca85a6d5155b828f8231dd1620f105386e6216604a6babf56a1b385883c0649736b41b0c521581ef330432e818c2f6321ec4217c5a27bd8415facdf222e3afa886814f267629570b8495f85d8a5c9bb4002e9dbeb5702f810920d5f5a5b28f0f0568fb2b3f0aa52ced3268145ed1972163e476cdd22365c4c1e90cd3e4cafb9230ed3ae6a54479e3309e7f123275934d38168ab62f34e09b458640d38de4af4e2810742afede29c04b91e65e43136deafa1ebd9d0ef390e41942244af1e87882c3d1b5d27d160b34a5487ecdb3f0e7bf8f6378f23301e1c88b55e97f57011452161c8c970a5474cd668927f7ee67f6292bca16e128aa7b0d3290ec4d86cc27840429b7bf4fb907953eee1f198bacc73a6fa2e0abcec473e0f4a3a8749480ab6e41985fd54965f863d697c77a76a7a31b9772808413a2f648025e9b70d969038368a23c04718cc177c2bc46d700709864d36ee2cb47bdd30f703ed1e121ec9103bbbc3d30be17d323bddfdc0fbc4182293c7ddc11939e006fdf7b7c5b0c6030a908b2188788b94d631bbaf6be6ac6b9a1dd3bc3434bda4c6fb405c883d9a0620e86344c40ef3bae80a7850c649a5aaa3af0a032230566aa3fd38308b8a17631160886f04b6ef6da5c6261e191b5de26ed97f57667a10e73f8213db83d57eb41fa7bbe98b56df4c933c4beec7e7c603e91ada33e848952a93326d6a4e54157da766c42431026818007ed8a08b78bb6a73f7f9026efabb83e8e680e8dd8ea451c6f35fd5a524741671dab2e2a2bbe7babc34827131545e7e8b021a621246b4d2aeb51ed49a315e52cbb2ae9f3f7fac0be542919fa35c52ce55f933958b8e0772d939efa587203fc95274e645747df64dfbb6cfbf2ff15f90a3d455f793307fbc66e987dc11672165163fc12b8a1c270e393e26fcb9f2c83d7351055c340117bd12176e2f55a6739f4512f2e445725895684a485e1eda3e868093519593b6e634e5a5a0b42a7e8f979959c35a78a3a83d5e669d2266748b1a4fbd8a2a2f23b952d6975539813989b443f335057cbb552da9cbe20c2eff6766ed6ed3ccec1413340b89e7ce90a8f4db38e651c801f8e956ba81794ac26054f9d634c59b7d4355f9704dd28ca237ea49969ee57a2f6614a6e4bcfef5bda4aa54de7ede946c22576b2a9bfe3665d3bfb36c864836ed2465331aca26aa114d816c9ba7811392cd6c289ba81ced88bc4dff216513795bf77f6fcb934d17c87621f236e387942d53abbf14754be82ea1e81be423d6297b7db159a8652fe4738bb85a455a8e20aa524eef1805d95ff9f54604c92dbc6a15562714325a8920b92555ad92e98404e99408929b876b1543272488d14010d17edd3d4541cc0682886ecd2e4e5110bd8120a26bb5cb5314a4db40904e41ad36f57c8688a0565b5d1b3a6100bd9481f887357d8602ba1f05b6af0745a6d58e137657005b3f4ed81795ab933ab0ef43820e053de3f236e490f8e8bfd25ff19407c06af06302e97af500ecc6b7e8ebe813edf1da1161d14558b4fd61d1b3463c8bff7f3afb17e9cc55e04b2d0000, 2, 'uppdaterad format', '2015-10-10', 'admin', 0, 2);

