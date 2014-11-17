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
(1, 'admin', 'fa9beb99e4029ad5a6615399e7bbae21356086b3', 1, 0),
(2, 'skltp', '3e1a694fd3a41e113dfbd4bf108cdee44206d1b1', 1, 0);

INSERT INTO `RivTaProfil` (`id`, `beskrivning`, `namn`, `version`) VALUES
(1, 'RIV TA BP 2.0', 'RIVTABP20', 0),
(2, 'RIV TA BP 2.1', 'RIVTABP21', 0);