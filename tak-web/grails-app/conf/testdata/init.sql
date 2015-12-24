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

-- user=admin password=changeme
-- user=skltp password=skltp
INSERT INTO `Anvandare` (`id`, `anvandarnamn`, `losenord_hash`, `administrator`, `version`) VALUES
(1, 'admin', '3e1a694fd3a41e113dfbd4bf108cdee44206d1b1', 1, 0),
(2, 'skltp', '3e1a694fd3a41e113dfbd4bf108cdee44206d1b1', 0, 0);

-- 4.  -- Newly created
-- 6. -- Another user than admin
-- 8. -- Added not published but deleted

--Updated
--Deleted
INSERT INTO `RivTaProfil` (`id`, `beskrivning`, `namn`, `updatedTime`, `updatedBy`, `pubVersion`, `deleted`, `version`) VALUES
(1, 'RIV TA BP 2.0-Published', 'RIVTABP20', NULL, NULL, 1, FALSE, 0),
(2, 'RIV TA BP 2.1-Published', 'RIVTABP21', NULL, NULL, 1, FALSE, 0),
(3, 'RIV TA BP 3.0-Published and updated', 'RIVTABP30', NULL, NULL, 1, FALSE, 0),
(4, 'RIV TA BP 4.0-Newly created', 'RIVTABP40', '2015-12-11', 'admin', NULL, FALSE, 0), 
(5, 'RIV TA BP 5.0-Published and then deleted', 'RIVTABP50', NULL, NULL, 1, FALSE, 0),
(6, 'RIV TA BP 6.0-Newly created by user skltp but not published ', 'RIVTABP60', '2015-12-15', 'skltp', NULL, FALSE, 0), 
(7, 'RIV TA BP 7.0-Newly created but not published', 'RIVTABP70', '2015-12-15', 'admin', NULL, FALSE, 0);
--(8, 'RIV TA BP 8.0-Created then deleted but not published', 'RIVTABP80', '2015-12-15', 'admin', NULL, TRUE, 0);

UPDATE `RivTaProfil` SET BESKRIVNING = 'RIV TA BP 3 Updated', UPDATEDTIME = '2015-12-11', UPDATEDBY = 'admin' WHERE NAMN = 'RIVTABP30';  

UPDATE `RivTaProfil` SET UPDATEDTIME = '2015-12-11', UPDATEDBY = 'admin', DELETED=TRUE WHERE NAMN = 'RIVTABP50';