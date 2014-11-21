#############################################################
# Migrera data
#############################################################

#####################
# Anvandare
#####################

# Namnbyte på tabell anvandare till Anvandare, se Jira SKLTP-322
INSERT INTO takv2.Anvandare(id, version, anvandarnamn, losenord_hash, administrator)
SELECT id, version, anvandarnamn, losenord_hash, administrator
FROM tp_admin.anvandare;

#####################
# RivTaProfil
#####################

# Namnbyte på tabell RivVersion till RivTaProfil
INSERT INTO takv2.RivTaProfil(id, version, namn, beskrivning)
SELECT id, version, namn, beskrivning
FROM tp_admin.RivVersion;


#####################
# Tjanstekontrakt
#####################

#Konvention utgår från att version finns på 11 positionen från slutet:
#urn:riv:crm:scheduling:GetSubjectOfCareSchedule:1:rivtabp21
#Ger 1 som i version 1
#
# Not! Funkar inte på ett kontrakt, urn:riv13606:v1.1:RIV13606REQUEST_EHR_EXTRACT

INSERT INTO takv2.Tjanstekontrakt(id, version, namnrymd, beskrivning, majorVersion)
SELECT id, version, namnrymd, beskrivning, substring(namnrymd,-11, 1) as majorVersion
FROM tp_admin.Tjanstekontrakt;

#####################
# Tjanstekomponent
#####################

INSERT INTO takv2.Tjanstekomponent(id, version, hsaId, beskrivning)
SELECT id, version, hsaId, beskrivning
FROM tp_admin.Tjanstekomponent;

#####################
# LogiskAdress
#####################

# Namnbyte på tabell LogiskAdressat till LogiskAdress
INSERT INTO takv2.LogiskAdress(id, version, hsaId, beskrivning)
SELECT id, version, hsaId, beskrivning
FROM tp_admin.LogiskAdressat;

#####################
# AnropsAdress
#####################

# Identifiera dubletter där en och samma producent exponerar samma tjänst med olika riv profiler
# SELECT la.id
# FROM tp_admin.LogiskAdress la, tp_admin.LogiskAdress la2
# where la.tjansteproducent_id = la2.tjansteproducent_id
# AND la.rivVersion_id <> la2.rivVersion_id;

#Problem om det finns tjänsteproducenter som exponerar samma adress i kombination med rivta profil
#Droppa constraint tills det är uppdaerat i TAK
#ALTER TABLE `takv2`.`AnropsAdress` \
#DROP INDEX `UC_TJANSTEKOMPONENT_ADRESS` ;

INSERT INTO takv2.AnropsAdress(id, version, adress, rivTaProfil_id, tjanstekomponent_id)
SELECT DISTINCT tk.id, 0, tk.adress, la.rivVersion_id as rivTaProfil_id, tk.id as tjanstekomponent_id
FROM tp_admin.LogiskAdress la, tp_admin.Tjanstekomponent tk
where la.tjansteproducent_id = tk.id;

#######################
# Anropsbehorighet
#######################

INSERT INTO takv2.Anropsbehorighet(id, version, fromTidpunkt, tomTidpunkt, integrationsavtal, logiskAdress_id, tjanstekonsument_id, tjanstekontrakt_id)
SELECT ab.id, ab.version, ab.fromTidpunkt, ab.tomTidpunkt, ab.integrationsavtal, ab.logiskAdressat_id as logiskAdress_id, ab.tjanstekonsument_id, ab.tjanstekontrakt_id
FROM tp_admin.Anropsbehorighet ab
where ab.tjanstekonsument_id;

#######################
# Vagval
#######################

# Namnbyte på tabell LogiskAdress till Vagval
INSERT INTO takv2.Vagval(version,fromTidpunkt, tomTidpunkt, logiskAdress_id, tjanstekontrakt_id, anropsAdress_id)
SELECT 0, vagval.fromTidpunkt, vagval.tomTidpunkt, vagval.logiskAdressat_id as logiskAdress_id,
vagval.tjanstekontrakt_id, aa.id as anropsAdress_id
FROM tp_admin.Tjanstekomponent tk
join tp_admin.LogiskAdress vagval
  ON vagval.tjansteproducent_id = tk.id
join takv2.AnropsAdress aa
  ON tk.adress = aa.adress and aa.rivTaProfil_id = vagval.rivVersion_id;

#######################
# Filter
#######################

INSERT INTO takv2.Filter(id, version, servicedomain, anropsbehorighet_id)
SELECT id, version, servicedomain, anropsbehorighet_id
FROM tp_admin.Filter;

#######################
# FilterCategory
#######################

INSERT INTO takv2.Filtercategorization(id, version, category, filter_id)
SELECT id, version, category, filter_id
FROM tp_admin.Filtercategorization;
