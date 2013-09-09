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

INSERT INTO `LogiskAdressat` (`id`, `beskrivning`, `hsaId`, `version`) VALUES
(1, 'Demo adressat tidbok, vardcentralen kusten, Karna', 'HSA-VKK123', 0),
(2, 'Demo adressat tidbok, vardcentralen kusten, Marstrand', 'HSA-VKM345', 0),
(3, 'Demo adressat tidbok, vardcentralen kusten, Ytterby', 'HSA-VKY567', 0),
(4, 'VP''s egna ping-tjanst', 'Ping', 0),
(5, 'Organisation: Inera', '5565594230', 0);

INSERT INTO `Tjanstekontrakt` (`id`, `beskrivning`, `namnrymd`, `version`) VALUES
(1, 'Tidbokning - GetSubjectOfCareSchedule', 'urn:riv:crm:scheduling:GetSubjectOfCareSchedule:1:rivtabp21', 0),
(2, 'Ping', 'urn:riv:itinfra:tp:Ping:1:rivtabp20', 0),
(3, 'Stödtjänst VP', 'urn:riv:itintegration:registry:GetLogicalAddresseesByServiceContract:1:rivtabp21', 0),
(4, 'Stödtjänst VP', 'urn:riv:itintegration:registry:GetSupportedServiceContracts:1:rivtabp21', 0),
(5, 'Engagemangsindex - Findcontent', 'urn:riv:itintegration:engagementindex:FindContent:1:rivtabp21', 0),
(6, 'Engagemangsindex - ProcessNotification', 'urn:riv:itintegration:engagementindex:ProcessNotification:1:rivtabp21', 0),
(7, 'Engagemangsindex - Update', 'urn:riv:itintegration:engagementindex:Update:1:rivtabp21', 0),
(8, 'Engagemangsindex - GetUpdates', 'urn:riv:itintegration:engagementindex:GetUpdates:1:rivtabp21', 0);

INSERT INTO `Tjanstekomponent` (`id`, `adress`, `beskrivning`, `hsaId`, `version`) VALUES
(1, 'http://33.33.33.33:8080/Schedulr-0.1/ws/GetSubjectOfCareSchedule/1', 'Demo tidbok', 'Schedulr', 0),
(4, '', 'tp test client', 'tp', 0),
(5, 'http://localhost:10000/test/Ping_Service', 'VP intern ping tjÃ¤nst', 'Ping-service', 0),
(6, 'http://localhost:8081/skltp-ei/update-service/v1', 'Producent: Engagemangsidex - Update', 'EI-Update', 0),
(7, 'http://localhost:8082/skltp-ei/find-content-service/v1', 'Producent: Engagemangsidex - FindContent', 'EI-FindContent', 0),
(8, 'http://localhost:8081/skltp-ei/notification-service/v1', 'Producent: Engagemangsidex - ProcessNotification', 'EI-ProcessNotification', 0),
(9, 'https://localhost:23001/vp/GetLogicalAddresseesByServiceContract/1/rivtabp21', 'VP-Cachad-GetLogicalAddresseesByServiceContract', 'VP-Cachad-GetLogicalAddresseesByServiceContract', 0),
(10, '', 'Inera som konsument, tex EI', '5565594230', 0),
(11, 'http://localhost:8083/GetAggregatedSubjectOfCareSchedule/service/v1', 'Producent: GetAggregatedSubjectOfCareSchedule', 'AGT-Tidbok', 0);

INSERT INTO `LogiskAdress` (`id`, `fromTidpunkt`, `tomTidpunkt`, `version`, `logiskAdressat_id`, `rivVersion_id`, `tjanstekontrakt_id`, `tjansteproducent_id`) VALUES
(1, '2013-05-24', '2113-05-24', 0, 1, 2, 1, 1),
(2, '2013-05-24', '2113-05-24', 0, 2, 2, 1, 1),
(3, '2013-05-24', '2113-05-24', 0, 3, 2, 1, 1),
(4, '2013-05-28', '2113-05-28', 0, 4, 1, 2, 5),
(5, '2013-08-24', '2113-08-24', 1, 5, 2, 3, 9),
(6, '2013-08-24', '2113-08-24', 0, 5, 2, 5, 7),
(7, '2013-08-24', '2113-08-24', 0, 5, 2, 6, 8),
(8, '2013-08-24', '2113-08-24', 0, 5, 2, 7, 6),
(9, '2013-08-24', '2113-08-24', 0, 5, 2, 1, 11);

INSERT INTO `Anropsbehorighet` (`id`, `fromTidpunkt`, `integrationsavtal`, `tomTidpunkt`, `version`, `logiskAdressat_id`, `tjanstekonsument_id`, `tjanstekontrakt_id`) VALUES
(1, '2013-05-24', 'I1', '2113-05-24', 0, 1, 4, 1),
(2, '2013-05-24', 'I1', '2113-05-24', 0, 2, 4, 1),
(3, '2013-05-24', 'I1', '2113-05-24', 0, 3, 4, 1),
(4, '2013-05-28', 'I2', '2113-05-28', 0, 4, 4, 2),
(5, '2013-08-24', 'EI', '2113-08-24', 0, 5, 10, 3),
(6, '2013-08-24', 'I3', '2113-08-24', 0, 5, 4, 1),
(7, '2013-08-24', 'EI', '2113-08-24', 0, 5, 10, 5),
(8, '2013-08-25', 'I4', '2113-08-25', 0, 5, 4, 5);


--
-- Constraints for table `Anropsbehorighet`
--
-- ALTER TABLE `Anropsbehorighet`
--  ADD CONSTRAINT `Anropsbehorighet_ibfk_1` FOREIGN KEY (`logiskAdressat_id`) REFERENCES `LogiskAdressat` (`id`),
--  ADD CONSTRAINT `Anropsbehorighet_ibfk_2` FOREIGN KEY (`tjanstekonsument_id`) REFERENCES `Tjanstekomponent` (`id`),
--  ADD CONSTRAINT `Anropsbehorighet_ibfk_3` FOREIGN KEY (`tjanstekontrakt_id`) REFERENCES `Tjanstekontrakt` (`id`);

--
-- Constraints for table `LogiskAdress`
--
-- ALTER TABLE `LogiskAdress`
--  ADD CONSTRAINT `LogiskAdress_ibfk_1` FOREIGN KEY (`logiskAdressat_id`) REFERENCES `LogiskAdressat` (`id`),
--  ADD CONSTRAINT `LogiskAdress_ibfk_2` FOREIGN KEY (`rivVersion_id`) REFERENCES `RivVersion` (`id`),
--  ADD CONSTRAINT `LogiskAdress_ibfk_3` FOREIGN KEY (`tjanstekontrakt_id`) REFERENCES `Tjanstekontrakt` (`id`),
--  ADD CONSTRAINT `LogiskAdress_ibfk_4` FOREIGN KEY (`tjansteproducent_id`) REFERENCES `Tjanstekomponent` (`id`);

