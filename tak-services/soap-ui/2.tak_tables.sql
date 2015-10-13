-- MySQL dump 10.13  Distrib 5.6.19, for Win64 (x86_64)
--
-- Host: localhost    Database: takTestDB
-- ------------------------------------------------------
-- Server version	5.6.19

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `anropsadress`
--

DROP TABLE IF EXISTS `anropsadress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `anropsadress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `adress` varchar(255) DEFAULT NULL,
  `rivTaProfil_id` bigint(20) NOT NULL,
  `tjanstekomponent_id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `updatedTime` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `pubVersion` date DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_TJANSTEKOMPONENT_ADRESS` (`tjanstekomponent_id`,`rivTaProfil_id`,`adress`),
  KEY `FK9144C39E31F3452` (`tjanstekomponent_id`),
  KEY `FK9144C39E388AE8DD` (`rivTaProfil_id`),
  CONSTRAINT `AnropsAdress_ibfk_1` FOREIGN KEY (`tjanstekomponent_id`) REFERENCES `tjanstekomponent` (`id`),
  CONSTRAINT `AnropsAdress_ibfk_2` FOREIGN KEY (`rivTaProfil_id`) REFERENCES `rivtaprofil` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1576 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `anropsadress`
--

LOCK TABLES `anropsadress` WRITE;
/*!40000 ALTER TABLE `anropsadress` DISABLE KEYS */;
/*!40000 ALTER TABLE `anropsadress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `anropsbehorighet`
--

DROP TABLE IF EXISTS `anropsbehorighet`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `anropsbehorighet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fromTidpunkt` date DEFAULT NULL,
  `integrationsavtal` varchar(255) DEFAULT NULL,
  `tomTidpunkt` date DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `logiskAdress_id` bigint(20) NOT NULL,
  `tjanstekonsument_id` bigint(20) NOT NULL,
  `tjanstekontrakt_id` bigint(20) NOT NULL,
  `updatedTime` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `pubVersion` date DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_TJANSTEKONSUMENT` (`tjanstekonsument_id`,`tjanstekontrakt_id`,`logiskAdress_id`,`fromTidpunkt`,`tomTidpunkt`),
  KEY `FK1144C39E31F3452` (`tjanstekontrakt_id`),
  KEY `FK1144C39E388AE8DD` (`tjanstekonsument_id`),
  KEY `FK1144C39EA69F7BA2` (`logiskAdress_id`),
  KEY `anropsbehorighet_distinct_idx` (`fromTidpunkt`,`integrationsavtal`,`tomTidpunkt`,`version`,`logiskAdress_id`,`tjanstekonsument_id`,`tjanstekontrakt_id`),
  CONSTRAINT `Anropsbehorighet_ibfk_1` FOREIGN KEY (`logiskAdress_id`) REFERENCES `logiskadress` (`id`),
  CONSTRAINT `Anropsbehorighet_ibfk_2` FOREIGN KEY (`tjanstekonsument_id`) REFERENCES `tjanstekomponent` (`id`),
  CONSTRAINT `Anropsbehorighet_ibfk_3` FOREIGN KEY (`tjanstekontrakt_id`) REFERENCES `tjanstekontrakt` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30679 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `anropsbehorighet`
--

LOCK TABLES `anropsbehorighet` WRITE;
/*!40000 ALTER TABLE `anropsbehorighet` DISABLE KEYS */;
/*!40000 ALTER TABLE `anropsbehorighet` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `anvandare`
--

DROP TABLE IF EXISTS `anvandare`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `anvandare` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `anvandarnamn` varchar(255) NOT NULL,
  `losenord_hash` varchar(255) NOT NULL,
  `administrator` tinyint(1) DEFAULT '0',
  `version` bigint(20) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `filter`
--

DROP TABLE IF EXISTS `filter`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `filter` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `servicedomain` varchar(255) NOT NULL,
  `version` bigint(20) NOT NULL,
  `anropsbehorighet_id` bigint(20) NOT NULL,
  `updatedTime` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `pubVersion` date DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_SERVICEDOMAIN` (`anropsbehorighet_id`,`servicedomain`),
  KEY `FK7D6DB798BC716E82` (`anropsbehorighet_id`),
  CONSTRAINT `Filter_ibfk_1` FOREIGN KEY (`anropsbehorighet_id`) REFERENCES `anropsbehorighet` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `filter`
--

LOCK TABLES `filter` WRITE;
/*!40000 ALTER TABLE `filter` DISABLE KEYS */;
/*!40000 ALTER TABLE `filter` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `filtercategorization`
--

DROP TABLE IF EXISTS `filtercategorization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `filtercategorization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `category` varchar(255) NOT NULL,
  `version` bigint(20) NOT NULL,
  `filter_id` bigint(20) NOT NULL,
  `updatedTime` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `pubVersion` date DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_CATEGORY` (`filter_id`,`category`),
  KEY `FK7EB5D6C12046FE42` (`filter_id`),
  CONSTRAINT `Filtercategorization_ibfk_1` FOREIGN KEY (`filter_id`) REFERENCES `filter` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `filtercategorization`
--

LOCK TABLES `filtercategorization` WRITE;
/*!40000 ALTER TABLE `filtercategorization` DISABLE KEYS */;
/*!40000 ALTER TABLE `filtercategorization` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `logiskadress`
--

DROP TABLE IF EXISTS `logiskadress`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `logiskadress` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `beskrivning` varchar(255) DEFAULT NULL,
  `hsaId` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `updatedTime` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `pubVersion` date DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_HSAID` (`hsaId`)
) ENGINE=InnoDB AUTO_INCREMENT=4062 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `logiskadress`
--

LOCK TABLES `logiskadress` WRITE;
/*!40000 ALTER TABLE `logiskadress` DISABLE KEYS */;
/*!40000 ALTER TABLE `logiskadress` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rivtaprofil`
--

DROP TABLE IF EXISTS `rivtaprofil`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rivtaprofil` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `beskrivning` varchar(255) DEFAULT NULL,
  `namn` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `updatedTime` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `pubVersion` date DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_NAMN` (`namn`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rivtaprofil`
--

LOCK TABLES `rivtaprofil` WRITE;
/*!40000 ALTER TABLE `rivtaprofil` DISABLE KEYS */;
/*!40000 ALTER TABLE `rivtaprofil` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tjanstekomponent`
--

DROP TABLE IF EXISTS `tjanstekomponent`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tjanstekomponent` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `beskrivning` varchar(255) DEFAULT NULL,
  `hsaId` varchar(255) DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `updatedTime` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `pubVersion` date DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_HSAID` (`hsaId`)
) ENGINE=InnoDB AUTO_INCREMENT=976 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tjanstekomponent`
--

LOCK TABLES `tjanstekomponent` WRITE;
/*!40000 ALTER TABLE `tjanstekomponent` DISABLE KEYS */;
/*!40000 ALTER TABLE `tjanstekomponent` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tjanstekontrakt`
--

DROP TABLE IF EXISTS `tjanstekontrakt`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tjanstekontrakt` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `beskrivning` varchar(255) DEFAULT NULL,
  `namnrymd` varchar(255) DEFAULT NULL,
  `majorVersion` bigint(20) DEFAULT '0',
  `minorVersion` bigint(20) DEFAULT '0',
  `version` bigint(20) NOT NULL,
  `updatedTime` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `pubVersion` date DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_NAMNRYMD` (`namnrymd`)
) ENGINE=InnoDB AUTO_INCREMENT=230 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tjanstekontrakt`
--

LOCK TABLES `tjanstekontrakt` WRITE;
/*!40000 ALTER TABLE `tjanstekontrakt` DISABLE KEYS */;
/*!40000 ALTER TABLE `tjanstekontrakt` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vagval`
--

DROP TABLE IF EXISTS `vagval`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `vagval` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fromTidpunkt` date DEFAULT NULL,
  `tomTidpunkt` date DEFAULT NULL,
  `version` bigint(20) NOT NULL,
  `logiskAdress_id` bigint(20) NOT NULL,
  `tjanstekontrakt_id` bigint(20) NOT NULL,
  `anropsAdress_id` bigint(20) NOT NULL,
  `updatedTime` varchar(255) DEFAULT NULL,
  `updatedBy` varchar(255) DEFAULT NULL,
  `pubVersion` date DEFAULT NULL,
  `deleted` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `UC_VAGVAL_ADRESS` (`anropsAdress_id`,`tjanstekontrakt_id`,`logiskAdress_id`,`fromTidpunkt`,`tomTidpunkt`),
  KEY `FK2C881BB350F9DB81` (`anropsAdress_id`),
  KEY `FK2C881BB3E6234A82` (`tjanstekontrakt_id`),
  KEY `FK2C881BB331F3452` (`logiskAdress_id`),
  CONSTRAINT `Vagval_ibfk_1` FOREIGN KEY (`logiskAdress_id`) REFERENCES `logiskadress` (`id`),
  CONSTRAINT `Vagval_ibfk_2` FOREIGN KEY (`anropsAdress_id`) REFERENCES `anropsadress` (`id`),
  CONSTRAINT `Vagval_ibfk_3` FOREIGN KEY (`tjanstekontrakt_id`) REFERENCES `tjanstekontrakt` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22336 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--

LOCK TABLES `vagval` WRITE;
/*!40000 ALTER TABLE `vagval` DISABLE KEYS */;
/*!40000 ALTER TABLE `vagval` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2015-10-13 13:54:22
