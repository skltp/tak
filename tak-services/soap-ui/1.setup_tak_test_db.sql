CREATE DATABASE takTestDB;
CREATE USER 'taktestuser'@'localhost' IDENTIFIED BY 'taktest';
GRANT ALL PRIVILEGES ON *.* TO 'taktestuser'@'localhost';
FLUSH PRIVILEGES;