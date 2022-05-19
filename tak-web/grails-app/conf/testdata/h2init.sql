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
-- "TeSt" they areconsidered as matching strings in MySql but not in H2.
-- Because we want theenvironment to match the real stuff as good as possible
-- this script iscalled to change the column types for the H2 database to
-- achieve the samebehavior.
-- (The tables are created automatically from entity types so we need to apply
-- the changes after the tables have been created.)
--
-- There are also some constraints added that are present in the "real"
-- database that are not created automatically in the H2 database

ALTER TABLE ANROPSADRESS ALTER COLUMN PUBVERSION VARCHAR_IGNORECASE(255);
ALTER TABLE ANROPSADRESS ALTER COLUMN UPDATEDBY VARCHAR_IGNORECASE(255);
ALTER TABLE ANROPSADRESS ALTER COLUMN ADRESS VARCHAR_IGNORECASE(255);

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
