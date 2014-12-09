# Scriptet kontrollerar att varje tabell populerats med korrekt data efter
# migrering till datamodellen för TAK 1.4.2.

#####################
# Anvandare
#####################

SELECT 'Anvandare', id, anvandarnamn
FROM
 (
   SELECT tak.id, tak.anvandarnamn
   FROM tp_admin.anvandare tak
   UNION ALL
   SELECT takv2.id, takv2.anvandarnamn
   FROM takv2.Anvandare takv2
)  t
GROUP BY id, anvandarnamn
HAVING COUNT(*) = 1
ORDER BY id;

#####################
# Tjanstekontrakt
#####################
SELECT 'Tjanstekontrakt', id, namnrymd
FROM
 (
   SELECT tak.id, tak.namnrymd
   FROM tp_admin.Tjanstekontrakt tak
   UNION ALL
   SELECT takv2.id, takv2.namnrymd
   FROM takv2.Tjanstekontrakt takv2
)  t
GROUP BY id, namnrymd
HAVING COUNT(*) = 1
ORDER BY id;

#####################
# RivTaProfil
#####################
SELECT 'RivTaProfil', id, namn
FROM
 (
   SELECT tak.id, tak.namn
   FROM tp_admin.RivVersion tak
   UNION ALL
   SELECT takv2.id, takv2.namn
   FROM takv2.RivTaProfil takv2
)  t
GROUP BY id, namn
HAVING COUNT(*) = 1
ORDER BY id;

#####################
# Tjanstekomponent
#####################
SELECT 'Tjanstekomponent', id, hsaId
FROM
 (
   SELECT tak.id, tak.hsaId
   FROM tp_admin.Tjanstekomponent tak
   UNION ALL
   SELECT takv2.id, takv2.hsaId
   FROM takv2.Tjanstekomponent takv2
)  t
GROUP BY id, hsaId
HAVING COUNT(*) = 1
ORDER BY id;

#####################
# LogiskAdress
#####################
SELECT 'LogiskAdress', id, hsaId
FROM
 (
   SELECT tak.id, tak.hsaId
   FROM tp_admin.LogiskAdressat tak
   UNION ALL
   SELECT takv2.id, takv2.hsaId
   FROM takv2.LogiskAdress takv2
)  t
GROUP BY id, hsaId
HAVING COUNT(*) = 1
ORDER BY id;

#####################
# Filter
#####################
SELECT 'Filter', id, anropsbehorighet_id
FROM
(
  SELECT tak.id, tak.anropsbehorighet_id
  FROM tp_admin.Filter tak
  UNION ALL
  SELECT takv2.id, takv2.anropsbehorighet_id
  FROM takv2.Filter takv2
)  t
GROUP BY id, anropsbehorighet_id
HAVING COUNT(*) = 1
ORDER BY id;

#####################
# Filtercategorization
#####################
SELECT 'Filtercategorization', id, filter_id
FROM
(
  SELECT tak.id, tak.filter_id
  FROM tp_admin.Filtercategorization tak
  UNION ALL
  SELECT takv2.id, takv2.filter_id
  FROM takv2.Filtercategorization takv2
)  t
GROUP BY id, filter_id
HAVING COUNT(*) = 1
ORDER BY id;

#####################
# Anropsbehorighet
#####################
SELECT 'Anropsbehorighet konsument', id, tjanstekonsument_id
FROM
 (
   SELECT tak.id, tak.tjanstekonsument_id
   FROM tp_admin.Anropsbehorighet tak
   UNION ALL
   SELECT takv2.id, takv2.tjanstekonsument_id
   FROM takv2.Anropsbehorighet takv2
)  t
GROUP BY id, tjanstekonsument_id
HAVING COUNT(*) = 1
ORDER BY id;

SELECT 'Anropsbehorighet logisk adress', id, logiskAdress_id
FROM
 (
   SELECT tak.id, tak.logiskAdressat_id as logiskAdress_id
   FROM tp_admin.Anropsbehorighet tak
   UNION ALL
   SELECT takv2.id, takv2.logiskAdress_id
   FROM takv2.Anropsbehorighet takv2
)  t
GROUP BY id, logiskAdress_id
HAVING COUNT(*) = 1
ORDER BY id;

#####################
# Vagval
# För varje vägval (logisk adress, tjanstekontrakt, rivta profil) skall det finnas en
# matchande adress i Vagval
#####################
SELECT 'Vagval, logisk adress, tjänstekontrakt, adress', logiskAdress_id, tjanstekontrakt_id, rivTaProfil_id,adress
FROM
(
  SELECT la.logiskAdressat_id as logiskAdress_id, la.tjanstekontrakt_id, la.rivVersion_id as rivTaProfil_id, tk.adress
  FROM tp_admin.LogiskAdress la, tp_admin.Tjanstekomponent tk
  WHERE la.tjansteproducent_id = tk.id
  UNION ALL
  SELECT vagval.logiskAdress_id, vagval.tjanstekontrakt_id, aa.rivTaProfil_id, aa.adress
  FROM takv2.Vagval vagval, takv2.AnropsAdress aa
  WHERE vagval.anropsAdress_id = aa.id
)  t
GROUP BY logiskAdress_id, tjanstekontrakt_id, rivTaProfil_id, adress
HAVING COUNT(*) = 1
ORDER BY logiskAdress_id;

#####################
# AnropsAdress
# Varje tjänsteproducent måste ha en anropsadress med
# korrekt rivta profil. I tidigare databasen låg denna
# informationen redundant, därav distinct.
#####################
SELECT 'AnropsAdress och RIV TA profil för Vagval', id, adress, rivTaProfil_id
FROM
(
  SELECT DISTINCT tk.id, tk.adress, la.rivVersion_id as rivTaProfil_id
  FROM tp_admin.LogiskAdress la, tp_admin.Tjanstekomponent tk
  where la.tjansteproducent_id = tk.id
  UNION ALL
  SELECT takv2.tjanstekomponent_id, takv2.adress, takv2.rivTaProfil_id
  FROM takv2.AnropsAdress takv2
)  t
GROUP BY id, adress, rivTaProfil_id
HAVING COUNT(*) = 1
ORDER BY id;
