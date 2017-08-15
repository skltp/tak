CREATE TABLE locktb (
  tabell varchar(100) NOT NULL,
  locked int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`tabell`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO locktb(`tabell`,`locked`)
VALUES('PubVersion',0);

CREATE TABLE TAKSETTINGS (
   `id` bigint(20) NOT NULL AUTO_INCREMENT,
   `version` bigint(20) NOT NULL,
   `settingName` varchar(255) DEFAULT NULL,
   `settingValue` varchar(255) DEFAULT NULL,
    PRIMARY KEY (`id`),
    UNIQUE (`settingName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `TAKSETTINGS` (`id`, `settingName`, `settingValue` ,`version`) VALUES
  (1, 'alerter.mail.toAddress', 'toAddress@server.com,toAddress2@server.com', 0),
  (2, 'alerter.mail.fromAddress', 'fromAddress@server.com', 0),
  (3, 'alerter.mail.publicering.subject', 'C5-Tjänsteplattformen TAKning  ${date} ', 0),
  (4, 'alerter.mail.publicering.text', '${separator} Publicerad version: ${pubVersion.id} ${separator} Format version: ${pubVersion.formatVersion} ${separator} Skapad den: ${pubVersion.time} ${separator} Utförare: ${pubVersion.utforare} ${separator} Kommentar:${pubVersion.kommentar} ${separator}  ${separator} ${listOfChanges}', 0),
  (5, 'alerter.mail.rollback.subject', 'C5-Tjänsteplattformen TAKning  ${date} ', 0),
  (6, 'alerter.mail.rollback.text', '${separator} Rollback av version: ${pubVersion.id} ${separator} Skapad den:  ${pubVersion.time} ${separator} Utförare: ${pubVersion.utforare} ${separator} Kommentar:${pubVersion.kommentar} ${separator}${separator} ${listOfChanges}', 0);

