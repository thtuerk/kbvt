-- MySQL dump 9.09
--
-- Host: localhost    Database: db24566
---------------------------------------------------------
-- Server version	4.0.15-Max

--
-- Table structure for table `ausleihe`
--
DROP DATABASE IF EXISTS koebDB;
create database koebDB;
use koebDB;

set foreign_key_checks=0;

DROP TABLE IF EXISTS ausleihe;
CREATE TABLE ausleihe (
  ID int(11) NOT NULL auto_increment,
  MediumID int(11) default NULL,
  BenutzerID int(11) default NULL,
  RueckgabeDatum date default NULL,
  MitarbeiterIDRueckgabe int(11) default NULL,
  Bemerkungen text,
  PRIMARY KEY  (ID),
  KEY medium (MediumID),
  KEY benutzer (BenutzerID),
  KEY mitarbeiter (MitarbeiterIDRueckgabe),
  CONSTRAINT `0_3973` FOREIGN KEY (`MediumID`) REFERENCES `medium` (`ID`),
  CONSTRAINT `0_3975` FOREIGN KEY (`BenutzerID`) REFERENCES `benutzer` (`ID`),
  CONSTRAINT `0_3977` FOREIGN KEY (`MitarbeiterIDRueckgabe`) REFERENCES `mitarbeiter` (`ID`)
) TYPE=InnoDB;

--
-- Table structure for table `ausleihzeitraum`
--

DROP TABLE IF EXISTS ausleihzeitraum;
CREATE TABLE ausleihzeitraum (
  AusleiheID int(11) default NULL,
  MitarbeiterID int(11) default NULL,
  Beginn date default NULL,
  Ende date default NULL,
  TaetigungsDatum date default NULL,
  KEY ausleihe (AusleiheID),
  KEY mitarbeiter (MitarbeiterID),
  CONSTRAINT `0_3980` FOREIGN KEY (`MitarbeiterID`) REFERENCES `mitarbeiter` (`ID`),
  CONSTRAINT `0_3982` FOREIGN KEY (`AusleiheID`) REFERENCES `ausleihe` (`ID`)
) TYPE=InnoDB;

--
-- Table structure for table `benutzer`
--

DROP TABLE IF EXISTS benutzer;
CREATE TABLE benutzer (
  ID int(11) NOT NULL auto_increment,
  Vorname varchar(30) default NULL,
  Nachname varchar(40) default NULL,
  Adresse varchar(60) default NULL,
  Ort varchar(30) default NULL,
  Geburtsdatum date default NULL,
  Klasse varchar(5) default NULL,
  Tel varchar(30) default NULL,
  Fax varchar(30) default NULL,
  eMail varchar(40) default NULL,
  Benutzername varchar(20) default NULL,
  Passwort blob,
  Anmeldedatum date default NULL,
  Bemerkungen text,
  VIP tinyint(1) NOT NULL default '0',
  PRIMARY KEY  (ID),
  UNIQUE KEY Benutzername (Benutzername)
) TYPE=InnoDB;

--
-- Table structure for table `benutzer_besucht_veranstaltung`
--

DROP TABLE IF EXISTS benutzer_besucht_veranstaltung;
CREATE TABLE benutzer_besucht_veranstaltung (
  ID int(11) NOT NULL auto_increment,
  BenutzerID int(11) NOT NULL default '0',
  VeranstaltungID int(11) NOT NULL default '0',
  Anmeldenr int(11) default NULL,
  Bemerkungen text,
  PRIMARY KEY  (ID),
  KEY benutzer_index (BenutzerID),
  KEY veranstaltungs_index (VeranstaltungID),
  KEY veranstaltung (VeranstaltungID),
  KEY benutzer (BenutzerID),
  CONSTRAINT `0_3927` FOREIGN KEY (`BenutzerID`) REFERENCES `benutzer` (`ID`),
  CONSTRAINT `0_3929` FOREIGN KEY (`VeranstaltungID`) REFERENCES `veranstaltung` (`ID`)
) TYPE=InnoDB;

--
-- Table structure for table `client`
--

DROP TABLE IF EXISTS client;
CREATE TABLE client (
  ID int(11) NOT NULL auto_increment,
  Name varchar(50) default NULL,
  IP varchar(15) default NULL,
  PRIMARY KEY  (ID),
  KEY ip_index (IP)
) TYPE=InnoDB;

--
-- Table structure for table `einstellung`
--

DROP TABLE IF EXISTS einstellung;
CREATE TABLE einstellung (
  ID int(11) NOT NULL auto_increment,
  ClientID int(11) default NULL,
  MitarbeiterID int(11) default NULL,
  Name longblob NOT NULL,
  Wert longblob,
  PRIMARY KEY  (ID),
  KEY client (ClientID),
  KEY mitarbeiter (MitarbeiterID),
  CONSTRAINT `0_3950` FOREIGN KEY (`ClientID`) REFERENCES `client` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `0_3953` FOREIGN KEY (`MitarbeiterID`) REFERENCES `mitarbeiter` (`ID`) ON DELETE CASCADE
) TYPE=InnoDB;

--
-- Table structure for table `internet_zugang`
--

DROP TABLE IF EXISTS internet_zugang;
CREATE TABLE internet_zugang (
  ID int(11) NOT NULL auto_increment,
  ClientID int(11) default NULL,
  BenutzerID int(11) default NULL,
  MitarbeiterID int(11) default NULL,
  Von datetime default NULL,
  Bis datetime default NULL,
  PRIMARY KEY  (ID),
  KEY benutzer_index (BenutzerID),
  KEY client_index (ClientID),
  KEY mitarbeiter_index (MitarbeiterID),
  KEY benutzer (BenutzerID),
  KEY client (ClientID),
  KEY mitarbeiter (MitarbeiterID),
  CONSTRAINT `0_3901` FOREIGN KEY (`BenutzerID`) REFERENCES `benutzer` (`ID`),
  CONSTRAINT `0_3904` FOREIGN KEY (`ClientID`) REFERENCES `client` (`ID`),
  CONSTRAINT `0_3907` FOREIGN KEY (`MitarbeiterID`) REFERENCES `mitarbeiter` (`ID`)
) TYPE=InnoDB;

--
-- Table structure for table `medienlisten`
--

DROP TABLE IF EXISTS medienlisten;
CREATE TABLE medienlisten (
  ListenNr int(11) NOT NULL auto_increment,
  Kriterien text,
  Titel varchar(255) default NULL,
  PRIMARY KEY  (ListenNr)
) TYPE=MyISAM;

--
-- Table structure for table `medientyp`
--

DROP TABLE IF EXISTS medientyp;
CREATE TABLE medientyp (
  Name varchar(40) NOT NULL default '',
  Plural varchar(40) default NULL,
  ID int(11) NOT NULL auto_increment,
  PRIMARY KEY  (ID)
) TYPE=InnoDB;

INSERT INTO medientyp VALUES ('Buch','Bücher',1),('CD','CDs',2),('CD-ROM','CD-ROMs',3),('MC','MCs',4),('Spiel','Spiele',5),('Video','Videos',6),('Zeitschrift','Zeitschriften',7),('DVD','DVDs',8);

--
-- Table structure for table `medium`
--

DROP TABLE IF EXISTS medium;
CREATE TABLE medium (
  Nr varchar(20) NOT NULL default '',
  Titel varchar(255) default NULL,
  Autor varchar(255) default NULL,
  Beschreibung text,
  eingestellt_seit date default NULL,
  aus_Bestand_entfernt date default NULL,
  Medienanzahl int(11) NOT NULL default '1',
  ID int(11) NOT NULL auto_increment,
  medientypID int(11) default NULL,
  ISBN varchar(10) default NULL,
  PRIMARY KEY  (ID),
  KEY nr_index (Nr),
  KEY titel_index (Titel),
  KEY autor_index (Autor),
  KEY medientyp (medientypID),
  KEY isbn (ISBN),
  KEY aus_Bestand_entfernt (aus_Bestand_entfernt),
  CONSTRAINT `0_3943` FOREIGN KEY (`medientypID`) REFERENCES `medientyp` (`ID`)
) TYPE=InnoDB;

--
-- Table structure for table `medium_ean`
--

DROP TABLE IF EXISTS medium_ean;
CREATE TABLE medium_ean (
  mediumID int(11) default NULL,
  EAN varchar(13) NOT NULL default '',
  PRIMARY KEY  (EAN),
  KEY medium (mediumID),
  CONSTRAINT `0_3938` FOREIGN KEY (`mediumID`) REFERENCES `medium` (`ID`) ON DELETE CASCADE
) TYPE=InnoDB;

--
-- Table structure for table `medium_gehoert_zu_systematik`
--

DROP TABLE IF EXISTS medium_gehoert_zu_systematik;
CREATE TABLE medium_gehoert_zu_systematik (
  MediumID int(11) NOT NULL default '0',
  SystematikID int(11) NOT NULL default '0',
  PRIMARY KEY  (MediumID,SystematikID),
  KEY systematik (SystematikID),
  KEY medium (MediumID),
  CONSTRAINT `0_3968` FOREIGN KEY (`MediumID`) REFERENCES `medium` (`ID`) ON DELETE CASCADE,
  CONSTRAINT `0_3970` FOREIGN KEY (`SystematikID`) REFERENCES `systematik` (`ID`)
) TYPE=InnoDB;

--
-- Table structure for table `mitarbeiter`
--

DROP TABLE IF EXISTS mitarbeiter;
CREATE TABLE mitarbeiter (
  ID int(11) NOT NULL auto_increment,
  BenutzerID int(11) default NULL,
  Benutzername varchar(20) default NULL,
  Passwort blob,
  Berechtigungen int(11) default '0',
  PRIMARY KEY  (ID),
  UNIQUE KEY Benutzernr (BenutzerID),
  UNIQUE KEY Benutzername (Benutzername),
  CONSTRAINT `0_3890` FOREIGN KEY (`BenutzerID`) REFERENCES `benutzer` (`ID`) ON DELETE CASCADE
) TYPE=InnoDB;

--
-- Table structure for table `systematik`
--

DROP TABLE IF EXISTS systematik;
CREATE TABLE systematik (
  Name varchar(50) default NULL,
  Beschreibung text,
  ID int(11) NOT NULL auto_increment,
  SpezialisiertID int(11) default NULL,
  PRIMARY KEY  (ID),
  KEY spezialisiertKey (SpezialisiertID),
  CONSTRAINT `0_3960` FOREIGN KEY (`SpezialisiertID`) REFERENCES `systematik` (`ID`)
) TYPE=InnoDB;

--
-- Table structure for table `systematik_spezialisiert`
--

DROP TABLE IF EXISTS systematik_spezialisiert;
CREATE TABLE systematik_spezialisiert (
  SystematikID int(11) NOT NULL default '0',
  SpezialisiertID int(11) NOT NULL default '0',
  PRIMARY KEY  (SystematikID,SpezialisiertID),
  KEY systematik (SystematikID),
  KEY spezialisiert (SpezialisiertID),
  CONSTRAINT `0_3963` FOREIGN KEY (`SpezialisiertID`) REFERENCES `systematik` (`ID`),
  CONSTRAINT `0_3965` FOREIGN KEY (`SystematikID`) REFERENCES `systematik` (`ID`)
) TYPE=InnoDB;

--
-- Table structure for table `termin`
--

DROP TABLE IF EXISTS termin;
CREATE TABLE termin (
  ID int(11) NOT NULL auto_increment,
  VeranstaltungID int(11) default NULL,
  Beginn datetime default NULL,
  Ende datetime default NULL,
  PRIMARY KEY  (ID),
  KEY veranstaltung_index (VeranstaltungID),
  KEY veranstaltung (VeranstaltungID),
  CONSTRAINT `0_3920` FOREIGN KEY (`VeranstaltungID`) REFERENCES `veranstaltung` (`ID`)
) TYPE=InnoDB;

--
-- Table structure for table `veranstaltung`
--

DROP TABLE IF EXISTS veranstaltung;
CREATE TABLE veranstaltung (
  ID int(11) NOT NULL auto_increment,
  Titel varchar(80) default NULL,
  kurzTitel varchar(20) default NULL,
  Ansprechpartner varchar(80) default NULL,
  Beschreibung text,
  Kosten decimal(5,2) default NULL,
  Bezugsgruppe varchar(80) default NULL,
  Waehrung char(3) default 'EUR',
  anmeldung_erforderlich tinyint(4) default '1',
  maximaleTeilnehmeranzahl int(11) default '0',
  VeranstaltungsgruppeID int(11) default NULL,
  PRIMARY KEY  (ID),
  KEY veranstaltungsgruppe (VeranstaltungsgruppeID),
  CONSTRAINT `0_3915` FOREIGN KEY (`VeranstaltungsgruppeID`) REFERENCES `veranstaltungsgruppe` (`ID`)
) TYPE=InnoDB;

--
-- Table structure for table `veranstaltungsgruppe`
--

DROP TABLE IF EXISTS veranstaltungsgruppe;
CREATE TABLE veranstaltungsgruppe (
  Name varchar(80) NOT NULL default '',
  kurzName varchar(30) default NULL,
  Beschreibung text,
  homeDir varchar(255) default NULL,
  ID int(11) NOT NULL auto_increment,
  PRIMARY KEY  (ID)
) TYPE=InnoDB;

set foreign_key_checks=1;
