#############################################################
# Migrera data
#############################################################

# Namnbyte på tabell anvandare till Anvandare, se Jira SKLTP-322
INSERT INTO takv2.Anvandare(id, version, anvandarnamn, losenord_hash, administrator) \
SELECT id, version, anvandarnamn, losenord_hash, administrator \
FROM tak.anvandare;

# Namnbyte på tabell RivVersion till RivTaProfil
INSERT INTO takv2.RivTaProfil(id, version, namn, beskrivning) \
SELECT id, version, namn, beskrivning \
FROM tak.RivVersion;

#Konvention utgår från att version finns på 11 positionen från slutet:
#urn:riv:crm:scheduling:GetSubjectOfCareSchedule:1:rivtabp21
#Ger 1 som i version 1
#
# Not! Funkar inte på ett kontrakt, urn:riv13606:v1.1:RIV13606REQUEST_EHR_EXTRACT

INSERT INTO takv2.Tjanstekontrakt(id, version, namnrymd, beskrivning, majorVersion) \
SELECT id, version, namnrymd, beskrivning, substring(namnrymd,-11, 1) as majorVersion \
FROM tak.Tjanstekontrakt;

INSERT INTO takv2.Tjanstekomponent(id, version, hsaId, beskrivning) \
SELECT id, version, hsaId, beskrivning \
FROM tak.Tjanstekomponent;

# Namnbyte på tabell LogiskAdressat till LogiskAdress
INSERT INTO takv2.LogiskAdress(id, version, hsaId, beskrivning) \
SELECT id, version, hsaId, beskrivning \
FROM tak.LogiskAdressat;

#Problem om det finns tjänsteproducenter som exponerar samma adress i kombination med rivta profil
#Droppa constraint tills det är uppdaerat i TAK
ALTER TABLE `takv2`.`AnropsAdress` \
DROP INDEX `UC_TJANSTEKOMPONENT_ADRESS` ;

# Konventionen är att Anropsadress id är samma som LogiskAdress (Vagval) id för att senare koppla Anropsadress
# till rätt Vagval.
INSERT INTO takv2.AnropsAdress(id, version, adress, rivTaProfil_id, tjanstekomponent_id) \
SELECT la.id, la.version, tk.adress, la.rivVersion_id as rivTaProfil_id, tk.id as tjanstekomponent_id \
FROM tak.LogiskAdress la, tak.Tjanstekomponent tk \
where la.tjansteproducent_id = tk.id;

INSERT INTO takv2.Anropsbehorighet(id, version, fromTidpunkt, tomTidpunkt, integrationsavtal, logiskAdress_id, tjanstekonsument_id, tjanstekontrakt_id) \
SELECT ab.id, ab.version, ab.fromTidpunkt, ab.tomTidpunkt, ab.integrationsavtal, ab.logiskAdressat_id as logiskAdress_id, ab.tjanstekonsument_id, ab.tjanstekontrakt_id \
FROM tak.Anropsbehorighet ab \
where ab.tjanstekonsument_id;

# Namnbyte på tabell LogiskAdress till Vagval
# Konventionen är att Anropsadress_id är samma som logiskAdress_id som användes för att skapa AnropsAdress
INSERT INTO takv2.Vagval(id, version, fromTidpunkt, tomTidpunkt, logiskAdress_id, tjanstekontrakt_id, anropsAdress_id) \
SELECT la.id, la.version, la.fromTidpunkt, la.tomTidpunkt, la.logiskAdressat_id as logiskAdress_id, \
	la.tjanstekontrakt_id, la.id as anropsAdress_id \
FROM tak.LogiskAdress la, tak.Tjanstekomponent tk \
where la.tjansteproducent_id = tk.id;
