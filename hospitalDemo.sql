-- MySQL dump 10.14  Distrib 5.5.57-MariaDB, for Linux (x86_64)
--
-- Host: classdb2.csc.ncsu.edu    Database: ashamas
-- ------------------------------------------------------
-- Server version	5.5.60-MariaDB

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
-- Table structure for table `billing`
--

DROP TABLE IF EXISTS `billing`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `billing` (
  `bill_id` int(11) NOT NULL AUTO_INCREMENT,
  `mr_id` int(11) NOT NULL,
  `payment_type` varchar(45) DEFAULT NULL,
  `total_cost` double NOT NULL,
  `payment_status` int(11) NOT NULL,
  `card` varchar(19) DEFAULT NULL,
  PRIMARY KEY (`bill_id`,`mr_id`),
  KEY `mrid_idx` (`mr_id`),
  CONSTRAINT `mrid` FOREIGN KEY (`mr_id`) REFERENCES `medical_records` (`mr_id`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `billing`
--

LOCK TABLES `billing` WRITE;
/*!40000 ALTER TABLE `billing` DISABLE KEYS */;
INSERT INTO `billing` VALUES (11,33,'Credit Card',1000,0,'4044-9876-1234-9123');
/*!40000 ALTER TABLE `billing` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `doctor`
--

DROP TABLE IF EXISTS `doctor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `doctor` (
  `doc_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `gender` varchar(1) DEFAULT NULL,
  `phone` bigint(10) NOT NULL,
  `dept` varchar(45) DEFAULT NULL,
  `professional_title` varchar(45) DEFAULT NULL,
  `address` longtext,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`doc_id`),
  UNIQUE KEY `name` (`name`,`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `doctor`
--

LOCK TABLES `doctor` WRITE;
/*!40000 ALTER TABLE `doctor` DISABLE KEYS */;
INSERT INTO `doctor` VALUES (11,'Mary',40,'F',654,'Neurology','senior','90 ABC St , Raleigh NC 27',1),(12,'Emma',55,'F',546,'Oncological Surgery','Senior surgeon','49 ABC St , Raleigh NC 27',1),(13,'Peter',52,'M',724,'Oncological Surgery','Anesthetist','475 RG St , Raleigh NC 27',1);
/*!40000 ALTER TABLE `doctor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medical_records`
--

DROP TABLE IF EXISTS `medical_records`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `medical_records` (
  `mr_id` int(11) NOT NULL AUTO_INCREMENT,
  `diagnosis` longtext,
  `prescription` longtext,
  `checkin_date` date NOT NULL,
  `checkout_date` date DEFAULT NULL,
  `patient_id` int(11) NOT NULL,
  `ward_id` int(11) DEFAULT NULL,
  `doc_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`mr_id`),
  KEY `patient_id_idx` (`patient_id`),
  KEY `doc_id_idx` (`doc_id`),
  KEY `ward_id` (`ward_id`),
  CONSTRAINT `doc_id` FOREIGN KEY (`doc_id`) REFERENCES `doctor` (`doc_id`),
  CONSTRAINT `patient_id` FOREIGN KEY (`patient_id`) REFERENCES `patient` (`patient_id`),
  CONSTRAINT `ward_id` FOREIGN KEY (`ward_id`) REFERENCES `ward` (`ward_id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medical_records`
--

LOCK TABLES `medical_records` WRITE;
/*!40000 ALTER TABLE `medical_records` DISABLE KEYS */;
INSERT INTO `medical_records` VALUES (30,'Hospitalization','nervine','2019-03-01',NULL,1001,21,11),(31,'Hospitalization','nervine','2019-03-10',NULL,1002,22,11),(32,'Hospitalization','nervine','2019-03-15',NULL,1003,21,11),(33,'Surgeon, Hospitalization','analgesic','2019-03-17','2019-03-21',1004,23,12);
/*!40000 ALTER TABLE `medical_records` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `nurse`
--

DROP TABLE IF EXISTS `nurse`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `nurse` (
  `nurse_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `gender` varchar(1) DEFAULT NULL,
  `phone` bigint(10) NOT NULL,
  `dept` varchar(45) DEFAULT NULL,
  `professional_title` varchar(45) DEFAULT NULL,
  `address` longtext,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`nurse_id`),
  UNIQUE KEY `name` (`name`,`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `nurse`
--

LOCK TABLES `nurse` WRITE;
/*!40000 ALTER TABLE `nurse` DISABLE KEYS */;
INSERT INTO `nurse` VALUES (11,'Carol',55,'F',911,'ER',NULL,'351 MH St , Greensboro NC 27',1),(12,'Olivia',27,'F',799,'Neurology',NULL,'325 PD St , Raleigh NC 27',1);
/*!40000 ALTER TABLE `nurse` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `operator`
--

DROP TABLE IF EXISTS `operator`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `operator` (
  `oper_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `gender` varchar(1) DEFAULT NULL,
  `phone` bigint(10) NOT NULL,
  `department` varchar(45) DEFAULT NULL,
  `job_title` varchar(45) DEFAULT NULL,
  `address` longtext,
  PRIMARY KEY (`oper_id`),
  UNIQUE KEY `name` (`name`,`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `operator`
--

LOCK TABLES `operator` WRITE;
/*!40000 ALTER TABLE `operator` DISABLE KEYS */;
INSERT INTO `operator` VALUES (10,'John',45,'M',564,'Office','Billing staff','798 XYZ St , Rochester NY 54'),(11,'Ava',55,'F',777,'Office','Front Desk Staff','425 RG St , Raleigh NC 27');
/*!40000 ALTER TABLE `operator` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `patient`
--

DROP TABLE IF EXISTS `patient`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `patient` (
  `patient_id` int(11) NOT NULL AUTO_INCREMENT,
  `ssn` bigint(10) DEFAULT NULL,
  `name` varchar(45) NOT NULL,
  `phone` bigint(10) NOT NULL,
  `age` int(11) DEFAULT NULL,
  `gender` varchar(1) DEFAULT NULL,
  `address` longtext,
  `current_status` int(11) NOT NULL,
  PRIMARY KEY (`patient_id`),
  UNIQUE KEY `name` (`name`,`phone`)
) ENGINE=InnoDB AUTO_INCREMENT=1005 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `patient`
--

LOCK TABLES `patient` WRITE;
/*!40000 ALTER TABLE `patient` DISABLE KEYS */;
INSERT INTO `patient` VALUES (1001,11234,'David',9191233324,39,'M','69 ABC St , Raleigh NC 27730',0),(1002,21234,'Sarah',9195633478,48,'F','81 DEF St , Cary NC 27519',0),(1003,31234,'Joseph',9199572199,32,'M','31 OPG St , Cary NC 27519',0),(1004,41234,'Lucy',9198387123,34,'F','10 TBC St , Raleigh NC 27730',1);
/*!40000 ALTER TABLE `patient` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `treatment`
--

DROP TABLE IF EXISTS `treatment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treatment` (
  `tr_id` int(11) NOT NULL AUTO_INCREMENT,
  `mr_id` int(11) NOT NULL,
  `doc_id` int(11) NOT NULL,
  `treatment_type` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`tr_id`,`mr_id`),
  KEY `doc_id_idx` (`doc_id`),
  KEY `mr_id_idx` (`mr_id`),
  KEY `treatment_type_idx` (`treatment_type`),
  CONSTRAINT `docid` FOREIGN KEY (`doc_id`) REFERENCES `doctor` (`doc_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `mr_id` FOREIGN KEY (`mr_id`) REFERENCES `medical_records` (`mr_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `treatment_type` FOREIGN KEY (`treatment_type`) REFERENCES `treatment_cost` (`treatment_type`) ON DELETE SET NULL ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `treatment`
--

LOCK TABLES `treatment` WRITE;
/*!40000 ALTER TABLE `treatment` DISABLE KEYS */;
INSERT INTO `treatment` VALUES (29,30,11,'Eye Test'),(30,31,13,'Skin test'),(31,32,12,'PT test'),(32,33,13,'Normal Consultation');
/*!40000 ALTER TABLE `treatment` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `treatment_cost`
--

DROP TABLE IF EXISTS `treatment_cost`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `treatment_cost` (
  `treatment_type` varchar(45) NOT NULL,
  `cost` double NOT NULL,
  PRIMARY KEY (`treatment_type`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `treatment_cost`
--

LOCK TABLES `treatment_cost` WRITE;
/*!40000 ALTER TABLE `treatment_cost` DISABLE KEYS */;
INSERT INTO `treatment_cost` VALUES ('Blood test',8769.26),('Bone Density test',2362.32),('CAT scan',1453.43),('CT scan',3489.8),('Eye test',2478.76),('MRI',5737.87),('Normal Consultation',500),('PT test',984.98),('RBC count test',7453.36),('Skin test',2853.41),('X-ray',530.57);
/*!40000 ALTER TABLE `treatment_cost` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ward`
--

DROP TABLE IF EXISTS `ward`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ward` (
  `ward_id` int(11) NOT NULL AUTO_INCREMENT,
  `total_capacity` int(11) NOT NULL,
  `current_availability` int(11) NOT NULL,
  `ward_type` int(11) NOT NULL,
  `nurse_id` int(11),
  PRIMARY KEY (`ward_id`),
  KEY `nurse_id_idx` (`nurse_id`),
  KEY `ward_type_idx` (`ward_type`),
  CONSTRAINT `nurse_id` FOREIGN KEY (`nurse_id`) REFERENCES `nurse` (`nurse_id`),
  CONSTRAINT `ward_type` FOREIGN KEY (`ward_type`) REFERENCES `ward_charges` (`ward_type`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ward`
--

LOCK TABLES `ward` WRITE;
/*!40000 ALTER TABLE `ward` DISABLE KEYS */;
INSERT INTO `ward` VALUES (21,4,4,4,11),(22,4,4,4,11),(23,2,2,3,12),(24,2,2,2,12);
/*!40000 ALTER TABLE `ward` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ward_charges`
--

DROP TABLE IF EXISTS `ward_charges`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ward_charges` (
  `ward_type` int(11) NOT NULL AUTO_INCREMENT,
  `charges` double NOT NULL,
  PRIMARY KEY (`ward_type`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ward_charges`
--

LOCK TABLES `ward_charges` WRITE;
/*!40000 ALTER TABLE `ward_charges` DISABLE KEYS */;
INSERT INTO `ward_charges` VALUES (1,50),(2,50),(3,100),(4,100);
/*!40000 ALTER TABLE `ward_charges` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;


-- Dump completed on 2019-04-12 18:58:25
