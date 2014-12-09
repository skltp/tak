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

INSERT INTO `LogiskAdress` (`id`, `beskrivning`, `hsaId`, `version`) VALUES
(1, 'Demo adressat tidbok, vardcentralen kusten, Karna', 'HSA-VKK123', 0),
(2, 'Demo adressat tidbok, vardcentralen kusten, Marstrand', 'HSA-VKM345', 0),
(3, 'Demo adressat tidbok, vardcentralen kusten, Ytterby', 'HSA-VKY567', 0),
(4, 'VP''s egna ping-tjanst', 'Ping', 0),
(5, 'Organisation: Inera', '5565594230', 0);

INSERT INTO `Tjanstekontrakt` (`id`, `beskrivning`, `namnrymd`, `majorVersion`, `minorVersion`, `version`) VALUES
(1, 'Tidbokning - GetSubjectOfCareSchedule', 'urn:riv:crm:scheduling:GetSubjectOfCareSchedule:1:rivtabp21', '1', '0', 0),
(2, 'Ping', 'urn:riv:itinfra:tp:Ping:1:rivtabp20', '1', '0', 0),
(3, 'Stödtjänst VP', 'urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContract:1:rivtabp21', '1', '0',  0),
(4, 'Stödtjänst VP', 'urn:riv:itintegration:registry:GetSupportedServiceContracts:1:rivtabp21', '1', '0',  0),
(5, 'Engagemangsindex - Findcontent', 'urn:riv:itintegration:engagementindex:FindContent:1:rivtabp21', '1', '0', 0),
(6, 'Engagemangsindex - ProcessNotification', 'urn:riv:itintegration:engagementindex:ProcessNotification:1:rivtabp21','1', '0', 0),
(7, 'Engagemangsindex - Update', 'urn:riv:itintegration:engagementindex:Update:1:rivtabp21', '1', '0', 0),
(8, 'Engagemangsindex - GetUpdates', 'urn:riv:itintegration:engagementindex:GetUpdates:1:rivtabp21','1', '0', 0);

INSERT INTO `Tjanstekomponent` (`id`, `beskrivning`, `hsaId`, `version`) VALUES
(1, 'Demo tidbok', 'Schedulr', 0),
(2, 'tp test client', 'tp', 0),
(3, 'VP intern ping tjÃ¤nst', 'PING-HSAID', 0),
(4, 'Engagemangsidex', 'EI-HSAID', 0),
(5, 'VP-Cachad-GetLogicalAddresseesByServiceContract', 'VP-Cachad-GetLogicalAddresseesByServiceContract', 0),
(6, 'Inera som konsument, tex EI', '5565594230', 0),
(7, 'Producent: GetAggregatedSubjectOfCareSchedule', 'AGT-Tidbok', 0);

INSERT INTO `AnropsAdress` (`id`, `version`, `adress`, `tjanstekomponent_id`, `rivTaProfil_id`) VALUES
(1, 0, 'http://33.33.33.33:8080/Schedulr-0.1/ws/GetSubjectOfCareSchedule/1', 1, 2),
(2, 0, 'http://localhost:10000/test/Ping_Service', 2, 1),
(3, 0, 'http://localhost:8081/skltp-ei/update-service/v1', 4, 2),
(4, 0, 'http://localhost:8082/skltp-ei/find-content-service/v1', 4, 2),
(5, 0, 'http://localhost:8081/skltp-ei/notification-service/v1', 4, 2),
(6, 0, 'https://localhost:23001/vp/GetLogicalAddresseesByServiceContract/1/rivtabp21', 5, 2),
(7, 0, 'http://localhost:8083/GetAggregatedSubjectOfCareSchedule/service/v1', 7, 2);

INSERT INTO `Vagval` (`id`, `fromTidpunkt`, `tomTidpunkt`, `version`, `logiskAdress_id`, `anropsAdress_id`, `tjanstekontrakt_id`) VALUES
(1, '2013-05-24', '2113-05-24', 0, 1, 1, 1),
(2, '2013-05-24', '2113-05-24', 0, 2, 1, 1),
(3, '2013-05-24', '2113-05-24', 0, 3, 1, 1),
(4, '2013-05-28', '2113-05-28', 0, 4, 2, 2),
(5, '2013-08-24', '2113-08-24', 1, 5, 6, 3),
(6, '2013-08-24', '2113-08-24', 0, 5, 4, 5),
(7, '2013-08-24', '2113-08-24', 0, 5, 5, 6),
(8, '2013-08-24', '2113-08-24', 0, 5, 3, 7),
(9, '2013-08-24', '2113-08-24', 0, 5, 7, 1);

INSERT INTO `Anropsbehorighet` (`id`, `fromTidpunkt`, `integrationsavtal`, `tomTidpunkt`, `version`, `logiskAdress_id`, `tjanstekonsument_id`, `tjanstekontrakt_id`) VALUES
(1, '2013-05-24', 'I1', '2113-05-24', 0, 1, 2, 1),
(2, '2013-05-24', 'I1', '2113-05-24', 0, 2, 2, 1),
(3, '2013-05-24', 'I1', '2113-05-24', 0, 3, 2, 1),
(4, '2013-05-28', 'I2', '2113-05-28', 0, 4, 2, 2),
(5, '2013-08-24', 'EI', '2113-08-24', 0, 5, 2, 3),
(6, '2013-08-24', 'I3', '2113-08-24', 0, 5, 2, 1),
(7, '2013-08-24', 'EI', '2113-08-24', 0, 5, 2, 5),
(8, '2013-08-25', 'I4', '2113-08-25', 0, 5, 2, 5);

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



