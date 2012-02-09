INSERT INTO `RivVersion` VALUES 
(3, 'RIV_TA_v1', 'RIV_TA_v1', 1);

INSERT INTO `Tjanstekomponent` VALUES 
(4,'https://localhost:19000/vardgivare-b/tjanst1','tjansteproducent tjanst1','tjanst1-impl-VardgivareB-TEST',1),
(5,'','tjanstekonsument tjanst1','VardgivareA-TEST',1),
(6,'https://localhost:19000/vardgivare-c/riv13606RequestEHRExtract','tjansteproducent riv13606','riv13606-impl-VardgivareC-IVK',1),
(7,'','tjanstekonsument riv13606','VardgivareA-IVK',1);

INSERT INTO `Tjanstekontrakt` VALUES 
(8,'tjanst1','urn:skl:tjanst1:0.1',1),
(9,'riv13606','urn:riv:ehr:ehrexchange:EhrExtraction:1:rivtabp20',1);

INSERT INTO `LogiskAdressat` VALUES 
(1, 'För användning i tp-test-nationell-tjanst', 'VardgivareB-TEST', 1),
(2, 'För användning i tp-journalinfo-apoteket-test', 'VardgivareC-IVK', 1);

INSERT INTO `Anropsbehorighet` VALUES 
(11,'2001-01-01','','2099-01-01',1,1,5,8),
(12,'2001-01-01','','2099-01-01',1,2,7,9);

INSERT INTO `LogiskAdress` VALUES 
(10,'2001-01-01','2099-01-01',1,1,3,8,4),
(11,'2001-01-01','2099-01-01',1,2,3,9,6);

INSERT INTO `Anvandare` VALUES 
(1, 'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', true, 1);
