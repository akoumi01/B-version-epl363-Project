-- phpMyAdmin SQL Dump
-- version 3.5.1
-- http://www.phpmyadmin.net
--
-- Φιλοξενητής: localhost
-- Χρόνος δημιουργίας: 28 Μάη 2015 στις 12:24:30
-- Έκδοση διακομιστή: 5.5.24-log
-- Έκδοση PHP: 5.3.13

SET SQL_MODE="NO_AUTO_VALUE_ON_ZERO";
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- Βάση: `collaboration_platform`
--

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `documents`
--

CREATE TABLE IF NOT EXISTS `documents` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
  `groupId` int(11) NOT NULL,
  `lastEdit` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `type` enum('File','Topic') DEFAULT NULL,
  `path` varchar(500) NOT NULL,
  `author` int(11) NOT NULL,
  `fileData` longblob NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`),
  KEY `groupId` (`groupId`),
  KEY `groupId_2` (`groupId`),
  KEY `path_2` (`path`),
  KEY `path_3` (`path`),
  KEY `author` (`author`),
  KEY `author_2` (`author`),
  KEY `author_3` (`author`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=27 ;

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `room`
--

CREATE TABLE IF NOT EXISTS `room` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(80) NOT NULL,
  `dateCreated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `path` varchar(100) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `path` (`path`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=117 ;

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `roomusers`
--

CREATE TABLE IF NOT EXISTS `roomusers` (
  `groupId` int(11) NOT NULL,
  `userId` int(11) NOT NULL,
  `userState` enum('ADMIN','MEMBER') NOT NULL,
  KEY `groupId` (`groupId`),
  KEY `userId` (`userId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- --------------------------------------------------------

--
-- Δομή πίνακα για τον πίνακα `users`
--

CREATE TABLE IF NOT EXISTS `users` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `nickName` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(8) NOT NULL,
  `lastLogIn` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `nickName` (`nickName`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1 AUTO_INCREMENT=3 ;

--
-- Περιορισμοί για άχρηστους πίνακες
--

--
-- Περιορισμοί για πίνακα `documents`
--
ALTER TABLE `documents`
  ADD CONSTRAINT `documents_ibfk_1` FOREIGN KEY (`groupId`) REFERENCES `room` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `documents_ibfk_2` FOREIGN KEY (`author`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Περιορισμοί για πίνακα `roomusers`
--
ALTER TABLE `roomusers`
  ADD CONSTRAINT `roomusers_ibfk_1` FOREIGN KEY (`groupId`) REFERENCES `room` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `roomusers_ibfk_2` FOREIGN KEY (`userId`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
