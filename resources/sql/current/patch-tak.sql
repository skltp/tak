CREATE TABLE locktb (
  tabell varchar(100) NOT NULL,
  locked int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`tabell`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO locktb(`tabell`,`locked`)
VALUES('PubVersion',0);