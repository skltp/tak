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

INSERT INTO `LogiskAdress` (`id`, `beskrivning`, `hsaId`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
(1, 'Demo adressat tidbok, vardcentralen kusten, Karna', 'HSA-VKK123', NULL, NULL, 1, FALSE, 0),
(2, 'Demo adressat tidbok, vardcentralen kusten, Marstrand', 'HSA-VKM345', NULL, NULL, 1, FALSE, 0),
(3, 'Demo adressat tidbok, vardcentralen kusten, Ytterby', 'HSA-VKY567', NULL, NULL, 1, FALSE, 0),
(4, 'VP''s egna ping-tjanst', 'PING', NULL, NULL, 1, FALSE, 0),
(5, 'Organisation: Inera', '5565594230', NULL, NULL, 1, FALSE, 0),
(6, 'Organisation: XXXX', 'HSA-NYA-Test-123', '2015-10-10', 'admin', NULL, FALSE, 0);

-- SKLTP-637 - bättre om id > 9

INSERT INTO `Tjanstekontrakt` (`id`, `beskrivning`, `namnrymd`, `majorVersion`, `minorVersion`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
(11, 'Tidbokning - GetSubjectOfCareSchedule', 'urn:riv:crm:scheduling:GetSubjectOfCareSchedule:1:rivtabp21', '1', '0', NULL, NULL, 1, FALSE, 0),
(12, 'Ping', 'urn:riv:itinfra:tp:Ping:1:rivtabp20', '1', '0', NULL, NULL, 1, FALSE, 0),
(23, 'Stödtjänst VP', 'urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContract:1:rivtabp21', '1', '0', NULL, NULL, 1, FALSE, 0),
(34, 'Stödtjänst VP', 'urn:riv:itintegration:registry:GetSupportedServiceContracts:1:rivtabp21', '1', '0', NULL, NULL, 1, FALSE, 0),
(45, 'Engagemangsindex - Findcontent', 'urn:riv:itintegration:engagementindex:FindContent:1:rivtabp21', '1', '0', NULL, NULL, 1, FALSE, 0),
(56, 'Engagemangsindex - ProcessNotification', 'urn:riv:itintegration:engagementindex:ProcessNotification:1:rivtabp21','1', '0', NULL, NULL, 1, FALSE, 0),
(67, 'Engagemangsindex - Update', 'urn:riv:itintegration:engagementindex:Update:1:rivtabp21', '1', '0', NULL, NULL, 1, FALSE, 0),
(78, 'Engagemangsindex - GetUpdates', 'urn:riv:itintegration:engagementindex:GetUpdates:1:rivtabp21','1', '0', NULL, NULL, 1, FALSE, 0),
(89, 'Nya stödtjänst VP - GetValidAddress', 'nya_urn:riv:itintegration:stodtjanst:GetUpdates:1:rivtabp21','1', '0', '2015-10-10', 'admin', NULL, FALSE, 0);

INSERT INTO `Tjanstekomponent` (`id`, `beskrivning`, `hsaId`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
(1, 'Demo tidbok', 'Schedulr', NULL, NULL, 1, FALSE, 0),
(2, 'tp test client', 'tp', NULL, NULL, 1, FALSE, 0),
(3, 'VP intern ping tjänst', 'PING-HSAID', NULL, NULL, 1, FALSE, 0),
(4, 'Engagemangsidex', 'EI-HSAID', NULL, NULL, 1, FALSE, 0),
(5, 'VP-Cachad-GetLogicalAddresseesByServiceContract', 'VP-Cachad-GetLogicalAddresseesByServiceContract', NULL, NULL, 1, FALSE, 0),
(6, 'Inera som konsument, tex EI', '5565594230', NULL, NULL, 1, FALSE, 0),
(7, 'Producent: GetAggregatedSubjectOfCareSchedule', 'AGT-Tidbok', NULL, NULL, 1, FALSE, 0),
(8, 'Nya producent: GetAggregatedSubjectOfCareSchedule', 'HSA-NYA-Test-123', '2015-10-10', 'admin', NULL, FALSE, 0);

INSERT INTO `AnropsAdress` (`id`, `adress`, `tjanstekomponent_id`, `rivTaProfil_id`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
(1, 'http://33.33.33.33:8080/Schedulr-0.1/ws/GetSubjectOfCareSchedule/1', 1, 2, NULL, NULL, 1, FALSE, 0),
(2, 'http://localhost:10000/test/Ping_Service', 2, 1, NULL, NULL, 1, FALSE, 0),
(3, 'http://localhost:8081/skltp-ei/update-service/v1', 4, 2, NULL, NULL, 1, FALSE, 0),
(4, 'http://localhost:8082/skltp-ei/find-content-service/v1', 4, 2, NULL, NULL, 1, FALSE, 0),
(5, 'http://localhost:8081/skltp-ei/notification-service/v1', 4, 2, NULL, NULL, 1, FALSE, 0),
(6, 'https://localhost:23001/vp/GetLogicalAddresseesByServiceContract/1/rivtabp21', 5, 2, NULL, NULL, 1, FALSE, 0),
(7, 'http://localhost:8083/GetAggregatedSubjectOfCareSchedule/service/v1', 7, 2, NULL, NULL, 1, FALSE, 0),
(8, 'http://localhost:8083/NyaServiceURL/service/v1', 8, 3, '2015-10-10', 'admin', NULL, FALSE, 0);

INSERT INTO `Vagval` (`id`, `fromTidpunkt`, `tomTidpunkt`, `logiskAdress_id`, `anropsAdress_id`, `tjanstekontrakt_id`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
(1, '2013-05-24', '2113-05-24', 1, 1, 11, NULL, NULL, 1, FALSE, 0),
(2, '2013-05-24', '2113-05-24', 2, 1, 11, NULL, NULL, 1, FALSE, 0),
(3, '2013-05-24', '2113-05-24', 3, 1, 11, NULL, NULL, 1, FALSE, 0),
(4, '2013-05-28', '2113-05-28', 4, 2, 12, NULL, NULL, 1, FALSE, 0),
(5, '2013-08-24', '2113-08-24', 5, 6, 23, NULL, NULL, 1, FALSE, 0),
(6, '2013-08-24', '2113-08-24', 5, 4, 45, NULL, NULL, 1, FALSE, 0),
(7, '2013-08-24', '2113-08-24', 5, 5, 56, NULL, NULL, 1, FALSE, 0),
(8, '2013-08-24', '2113-08-24', 5, 3, 67, NULL, NULL, 1, FALSE, 0),
(9, '2013-08-24', '2113-08-24', 5, 7, 11, NULL, NULL, 1, FALSE, 0),
(10, '2013-08-24', '2113-08-24', 6, 8, 89, '2015-10-10', 'admin', NULL, FALSE, 0);

INSERT INTO `Anropsbehorighet` (`id`, `fromTidpunkt`, `integrationsavtal`, `tomTidpunkt`, `logiskAdress_id`, `tjanstekonsument_id`, `tjanstekontrakt_id`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
(1, '2013-05-24', 'I1', '2113-05-24', 1, 2, 11, NULL, NULL, 1, FALSE, 0),
(2, '2013-05-24', 'I1', '2113-05-24', 2, 2, 11, NULL, NULL, 1, FALSE, 0),
(3, '2013-05-24', 'I1', '2113-05-24', 3, 2, 11, NULL, NULL, 1, FALSE, 0),
(4, '2013-05-28', 'I2', '2113-05-28', 4, 2, 12, NULL, NULL, 1, FALSE, 0),
(5, '2013-08-24', 'EI', '2113-08-24', 5, 2, 23, NULL, NULL, 1, FALSE, 0),
(6, '2013-08-24', 'I3', '2113-08-24', 5, 2, 11, NULL, NULL, 1, FALSE, 0),
(7, '2013-08-24', 'EI', '2113-08-24', 5, 2, 45, NULL, NULL, 1, FALSE, 0),
(8, '2013-08-25', 'I4', '2113-08-25', 5, 2, 45, NULL, NULL, 1, FALSE, 0),
(9, '2013-08-25', 'Nya_I', '2113-08-25', 6, 8, 89, '2015-10-10', 'admin', NULL, FALSE, 0);

INSERT INTO `Filter` (`id`, `servicedomain`, `anropsbehorighet_id`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
(1, 'urn:riv:itintegration:registry:GetItems', 8, NULL, NULL, 1, false, 0),
(2, 'urn:riv:itintegration:registry:GetItems', 7, '2015-05-24', 'admin', NULL, false, 0);

INSERT INTO `Filtercategorization` (`id`, `category`, `filter_id`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
(1, 'Category c1', 1, NULL, NULL, 1, false, 0),
(2, 'Category c2', 2, '2015-05-24', 'admin', NULL, false, 0);

INSERT INTO `PubVersion` (`id`, `formatversion`, `kommentar`, `time`, `utforare`, `data`, `version`) VALUES
(1, '1', 'default version', '2015-05-24', 'admin', FILE_READ('../tak-core/src/test/resources/export.gzip'), 0),
(2, '1', 'ändrat beskrivning', '2015-07-04', 'admin', FILE_READ('../tak-core/src/test/resources/export.gzip'), 0),
(3, '2', 'uppdaterad format', '2015-10-10', 'admin', FILE_READ('../tak-core/src/test/resources/export.gzip'), 0);

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



