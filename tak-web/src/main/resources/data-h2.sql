--
-- Copyright (c) 2013 Center for eHalsa i samverkan (CeHis).
-- 					<http://cehis.se/>
--
-- This file is part of SKLTP.
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public
-- License as published by the Free Software Foundation; either
-- version 2.1 of the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
--

--
-- TAK is normally used with a MySql database
-- When TAK is built and run locally a H2 in-memory database is instead created
-- and populated with some test data
--
-- However the default behavior when it comes to strings is different in H2
-- and MySql or SQL server etc. When comparing values like "TEST", "test" and
-- "TeSt" they are considered as matching strings in MySql but not in H2.
-- Because we want the environment to match the real stuff as good as possible
-- this script is called to change the column types for the H2 database to
-- achieve the same behavior.
-- (The tables are created automatically from entity types so we need to apply
-- the changes after the tables have been created.)
--
-- There are also some constraints added that are present in the "real"
-- database that are not created automatically in the H2 database

ALTER TABLE ANROPSADRESS ALTER COLUMN PUBVERSION VARCHAR_IGNORECASE(255);
ALTER TABLE ANROPSADRESS ALTER COLUMN UPDATEDBY VARCHAR_IGNORECASE(255);
-- Note: Column ADRESS uses case-sensitive collation in MySQL, do not use IGNORECASE here

ALTER TABLE ANROPSBEHORIGHET ALTER COLUMN PUBVERSION VARCHAR_IGNORECASE(255);
ALTER TABLE ANROPSBEHORIGHET ALTER COLUMN UPDATEDBY VARCHAR_IGNORECASE(255);
ALTER TABLE ANROPSBEHORIGHET ALTER COLUMN INTEGRATIONSAVTAL VARCHAR_IGNORECASE(255);

ALTER TABLE ANVANDARE ALTER COLUMN ANVANDARNAMN VARCHAR_IGNORECASE(255);
ALTER TABLE ANVANDARE ALTER COLUMN LOSENORD_HASH VARCHAR_IGNORECASE(255);

ALTER TABLE FILTER ALTER COLUMN PUBVERSION VARCHAR_IGNORECASE(255);
ALTER TABLE FILTER ALTER COLUMN UPDATEDBY VARCHAR_IGNORECASE(255);
ALTER TABLE FILTER ALTER COLUMN SERVICEDOMAIN VARCHAR_IGNORECASE(255);

ALTER TABLE FILTERCATEGORIZATION ALTER COLUMN PUBVERSION VARCHAR_IGNORECASE(255);
ALTER TABLE FILTERCATEGORIZATION ALTER COLUMN UPDATEDBY VARCHAR_IGNORECASE(255);
ALTER TABLE FILTERCATEGORIZATION ALTER COLUMN CATEGORY VARCHAR_IGNORECASE(255);

ALTER TABLE LOCKTB ALTER COLUMN TABELL VARCHAR_IGNORECASE(255);

ALTER TABLE LOGISKADRESS ALTER COLUMN PUBVERSION VARCHAR_IGNORECASE(255);
ALTER TABLE LOGISKADRESS ALTER COLUMN UPDATEDBY VARCHAR_IGNORECASE(255);
ALTER TABLE LOGISKADRESS ALTER COLUMN BESKRIVNING VARCHAR_IGNORECASE(255);
ALTER TABLE LOGISKADRESS ALTER COLUMN HSAID VARCHAR_IGNORECASE(255);

ALTER TABLE PUBVERSION ALTER COLUMN KOMMENTAR VARCHAR_IGNORECASE(255);
ALTER TABLE PUBVERSION ALTER COLUMN UTFORARE VARCHAR_IGNORECASE(255);

ALTER TABLE RIVTAPROFIL ALTER COLUMN PUBVERSION VARCHAR_IGNORECASE(255);
ALTER TABLE RIVTAPROFIL ALTER COLUMN UPDATEDBY VARCHAR_IGNORECASE(255);
ALTER TABLE RIVTAPROFIL ALTER COLUMN BESKRIVNING VARCHAR_IGNORECASE(255);
ALTER TABLE RIVTAPROFIL ALTER COLUMN NAMN VARCHAR_IGNORECASE(255);

ALTER TABLE TAKSETTINGS ALTER COLUMN SETTINGNAME VARCHAR_IGNORECASE(255);

ALTER TABLE TJANSTEKOMPONENT ALTER COLUMN PUBVERSION VARCHAR_IGNORECASE(255);
ALTER TABLE TJANSTEKOMPONENT ALTER COLUMN UPDATEDBY VARCHAR_IGNORECASE(255);
ALTER TABLE TJANSTEKOMPONENT ALTER COLUMN BESKRIVNING VARCHAR_IGNORECASE(255);
ALTER TABLE TJANSTEKOMPONENT ALTER COLUMN HSAID VARCHAR_IGNORECASE(255);

ALTER TABLE TJANSTEKONTRAKT ALTER COLUMN PUBVERSION VARCHAR_IGNORECASE(255);
ALTER TABLE TJANSTEKONTRAKT ALTER COLUMN UPDATEDBY VARCHAR_IGNORECASE(255);
ALTER TABLE TJANSTEKONTRAKT ALTER COLUMN BESKRIVNING VARCHAR_IGNORECASE(255);
ALTER TABLE TJANSTEKONTRAKT ALTER COLUMN NAMNRYMD VARCHAR_IGNORECASE(255);

ALTER TABLE VAGVAL ALTER COLUMN PUBVERSION VARCHAR_IGNORECASE(255);
ALTER TABLE VAGVAL ALTER COLUMN UPDATEDBY VARCHAR_IGNORECASE(255);

ALTER TABLE RivTaProfil ADD CONSTRAINT uc_namn UNIQUE(namn, deleted);
ALTER TABLE Tjanstekomponent ADD CONSTRAINT uc_hsaid UNIQUE(hsaId, deleted);
ALTER TABLE Tjanstekontrakt ADD CONSTRAINT uc_namnrymd UNIQUE(namnrymd, deleted);
ALTER TABLE LogiskAdress ADD CONSTRAINT uc_hsaid_2 UNIQUE(hsaId, deleted);
ALTER TABLE Anropsbehorighet ADD CONSTRAINT uc_tjanstekonsument UNIQUE(tjanstekonsument_id, tjanstekontrakt_id, logiskAdress_id, fromTidpunkt, tomTidpunkt, deleted);
ALTER TABLE AnropsAdress ADD CONSTRAINT uc_tjanstekomponent_adress UNIQUE(tjanstekomponent_id, rivTaProfil_id, adress, deleted);
ALTER TABLE Vagval ADD CONSTRAINT uc_vagval_adress UNIQUE(anropsAdress_id, tjanstekontrakt_id, logiskAdress_id, fromTidpunkt, tomTidpunkt, deleted);
ALTER TABLE Filter ADD CONSTRAINT uc_servicedomain UNIQUE(anropsbehorighet_id, servicedomain, deleted);
ALTER TABLE Filtercategorization ADD CONSTRAINT uc_category UNIQUE(filter_id, category, deleted);


-- Normally this file is used to init the H2 test database
-- However it can also be used to populate a MySql test database that has been
-- coinfigured with the tp-admin-DDL script. In that case some data that was
-- added there needs to be removed to avoid conflict with data added in this
-- script.
DELETE FROM Anvandare;
DELETE FROM LockTb;
DELETE FROM TAKSettings;

-- user=admin password=skltp
-- user=skltp password=skltp
INSERT INTO `Anvandare` (`anvandarnamn`, `losenord_hash`, `administrator`, `version`) VALUES
('admin', '3e1a694fd3a41e113dfbd4bf108cdee44206d1b1', 1, 0),
('skltp', '3e1a694fd3a41e113dfbd4bf108cdee44206d1b1', 0, 0);

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

INSERT INTO Locktb(`tabell`,`locked`)
VALUES('PubVersion',0);

 INSERT INTO `TAKSettings` (`settingName`, `settingValue` ,`version`) VALUES
 ('alerter.mail.toAddress', 'mtuliakova@gmail.com,toAddress2@server.com', 0),
 ('alerter.mail.fromAddress', 'pubAlert@server.com', 0),
 ('alerter.mail.publicering.subject', 'RFC: C5-Tjänsteplattformen TAKning  ${date} ', 0),
 ('alerter.mail.publicering.text', '[Service: Ttjänste platformen] ${separator}[Classification: C5] ${separator}[Start: ${pubVersion.time}] ${separator}[End: ${pubVersion.time}] ${separator}[Confirmation: false]${separator}${separator}Publicerad version: ${pubVersion.id} ${separator} Format version: ${pubVersion.formatVersion} ${separator} Skapad den: ${pubVersion.time} ${separator} Utförare: ${pubVersion.utforare} ${separator} Kommentar:${pubVersion.kommentar} ${separator} ${separator} ${listOfChanges}', 0),
 ('alerter.mail.rollback.subject', 'RFC: C5-Tjänsteplattformen TAKning  ${date} ', 0),
 ('alerter.mail.rollback.text', '[Service: Ttjänste platformen] ${separator}[Classification: C5] ${separator}[Start: ${pubVersion.time}] ${separator}[End: ${pubVersion.time}] ${separator}[Confirmation: false]$ {separator}${separator}${separator} Rollback av version: ${pubVersion.id} ${separator} Skapad den:  ${pubVersion.time} ${separator} Utförare: ${pubVersion.utforare} ${separator} Kommentar:${pubVersion.kommentar}', 0),
 ('mail.alerter.ny.tjanstekontrakt.toAddress', 'mtuliakova@gmail.com', 0),
 ('mail.alerter.ny.tjanstekontrakt.subject', 'ny contract är skapad', 0),
 ('mail.alerter.ny.tjanstekontrakt.text', 'ny kontrakt namn ${contractName} ${date} ', 0);


-- SKLTP-637 - bättre om id > 9

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
('Organisation: XXXX', 'HSA-NYA-TEST-123', '2015-10-10', 'admin', NULL, FALSE, 0),
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

-- Insert many items to make sure that latest publicerad version (11) shows up first in the list!
-- This file is normally used with the H2 database. To use it to manually init
-- a MySql test database copy the file and replace "FILE_READ" below with
-- "LOAD_FILE". (And eventually update paths.)
INSERT INTO `PubVersion` (`formatversion`, `kommentar`, `time`, `utforare`, `data`, `version`, `storlek`) VALUES
('1', 'default version', '2015-05-24', 'admin', FILE_READ('../tak-core/src/test/resources/export.gzip'), 0, 2),
('1', 'ändrat beskrivning', '2015-07-04', 'admin', FILE_READ('../tak-core/src/test/resources/export.gzip'), 0, 2),
('1', 'borttagning', '2015-10-10', 'admin', FILE_READ('../tak-core/src/test/resources/export.gzip'), 0, 2);

-- If deploying on a MySQL database change FILE_READ to LOAD_FILE and place export.gzip so it's accesible from script file as shown below
-- INSERT INTO `PubVersion` (`id`, `formatversion`, `kommentar`, `time`, `utforare`, `data`, `version`, `storlek`) VALUES
-- (1, '1', 'default version', '2015-05-24', 'admin', LOAD_FILE('C:/tmp/export.gzip'), 0, 2),
-- (2, '1', 'ändrat beskrivning', '2015-07-04', 'admin', LOAD_FILE('C:/tmp/export.gzip'), 0, 2),
-- (3, '2', 'uppdaterad format', '2015-10-10', 'admin', LOAD_FILE('C:/tmp/export.gzip'), 0, 2);

--
-- Constraints for table `Anropsbehorighet`
--
-- ALTER TABLE `Anropsbehorighet`
--  ADD CONSTRAINT `Anropsbehorighet_ibfk_1` FOREIGN KEY (`logiskAdress_id`) REFERENCES `LogiskAdress` (`id`),
--  ADD CONSTRAINT `Anropsbehorighet_ibfk_2` FOREIGN KEY (`tjanstekonsument_id`) REFERENCES `Tjanstekomponent` (`id`),
--  ADD CONSTRAINT `Anropsbehorighet_ibfk_3` FOREIGN KEY (`tjanstekontrakt_id`) REFERENCES `Tjanstekontrakt` (`id`);

--
-- Constraints for table `Vagval`
--
-- ALTER TABLE `LogiskAdress`
--  ADD CONSTRAINT `Vagval_ibfk_1` FOREIGN KEY (`logiskAdress_id`) REFERENCES `LogiskAdress` (`id`),
--  ADD CONSTRAINT `Vagval_ibfk_2` FOREIGN KEY (`rivTaProfil_id`) REFERENCES `RivTaProfil` (`id`),
--  ADD CONSTRAINT `Vagval_ibfk_3` FOREIGN KEY (`tjanstekontrakt_id`) REFERENCES `Tjanstekontrakt` (`id`),
--  ADD CONSTRAINT `Vagval_ibfk_4` FOREIGN KEY (`anropsAdress_id`) REFERENCES `AnropsAdress` (`id`);

