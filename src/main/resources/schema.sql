CREATE database spicejetcheckin;
USE DATABASE spicejetcheckin;
CREATE TABLE `twitter_pnr` (
`id` int(11) NOT NULL AUTO_INCREMENT,  
`pnr` varchar(6) NOT NULL,
  `userid` varchar(45) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `created_date` datetime DEFAULT NULL,
  `modified_date` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB;