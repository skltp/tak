#4 nya kolumner i AnropsAdress
ALTER TABLE AnropsAdress ADD `updatedTime` varchar(255) DEFAULT NULL;
ALTER TABLE AnropsAdress ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE AnropsAdress ADD `pubVersion` date DEFAULT NULL;
ALTER TABLE AnropsAdress ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Anropsbehorighet
ALTER TABLE Anropsbehorighet ADD `updatedTime` varchar(255) DEFAULT NULL;
ALTER TABLE Anropsbehorighet ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Anropsbehorighet ADD `pubVersion` date DEFAULT NULL;
ALTER TABLE Anropsbehorighet ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Filter
ALTER TABLE Filter ADD `updatedTime` varchar(255) DEFAULT NULL;
ALTER TABLE Filter ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Filter ADD `pubVersion` date DEFAULT NULL;
ALTER TABLE Filter ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Filtercategorization
ALTER TABLE Filtercategorization ADD `updatedTime` varchar(255) DEFAULT NULL;
ALTER TABLE Filtercategorization ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Filtercategorization ADD `pubVersion` date DEFAULT NULL;
ALTER TABLE Filtercategorization ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Logiskadress
ALTER TABLE Logiskadress ADD `updatedTime` varchar(255) DEFAULT NULL;
ALTER TABLE Logiskadress ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Logiskadress ADD `pubVersion` date DEFAULT NULL;
ALTER TABLE Logiskadress ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Rivtaprofil
ALTER TABLE Rivtaprofil ADD `updatedTime` varchar(255) DEFAULT NULL;
ALTER TABLE Rivtaprofil ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Rivtaprofil ADD `pubVersion` date DEFAULT NULL;
ALTER TABLE Rivtaprofil ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Tjanstekomponent
ALTER TABLE Tjanstekomponent ADD `updatedTime` varchar(255) DEFAULT NULL;
ALTER TABLE Tjanstekomponent ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Tjanstekomponent ADD `pubVersion` date DEFAULT NULL;
ALTER TABLE Tjanstekomponent ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Tjanstekontrakt
ALTER TABLE Tjanstekontrakt ADD `updatedTime` varchar(255) DEFAULT NULL;
ALTER TABLE Tjanstekontrakt ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Tjanstekontrakt ADD `pubVersion` date DEFAULT NULL;
ALTER TABLE Tjanstekontrakt ADD `deleted` bool DEFAULT FALSE;

#4 nya kolumner i Vagval
ALTER TABLE Vagval ADD `updatedTime` varchar(255) DEFAULT NULL;
ALTER TABLE Vagval ADD `updatedBy` varchar(255) DEFAULT NULL;
ALTER TABLE Vagval ADD `pubVersion` date DEFAULT NULL;
ALTER TABLE Vagval ADD `deleted` bool DEFAULT FALSE;
