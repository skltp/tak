# Scriptet kontrollerar att varje tabell populerats med korrekt data efter
# migrering till datamodellen för TAK 1.4.2.
# Eventuella skillnader rapporteras av scriptet.
#
# 1: I de fall exakt samma data kopieras över, säkerställ att id är samma
# 2: I de fall ny data skapats, säkerställ att korrekta id'n skapats från befintlig data

# Anvandare

SELECT 'Anvandare', id, anvandarnamn
FROM
 (
   SELECT tak.id, tak.anvandarnamn
   FROM tak.anvandare tak
   UNION ALL
   SELECT takv2.id, takv2.anvandarnamn
   FROM takv2.Anvandare takv2
)  t
GROUP BY id, anvandarnamn
HAVING COUNT(*) = 1
ORDER BY id;


# Tjanstekontrakt
SELECT 'Tjanstekontrakt', id, namnrymd
FROM
 (
   SELECT tak.id, tak.namnrymd
   FROM tak.Tjanstekontrakt tak
   UNION ALL
   SELECT takv2.id, takv2.namnrymd
   FROM takv2.Tjanstekontrakt takv2
)  t
GROUP BY id, namnrymd
HAVING COUNT(*) = 1
ORDER BY id;

# RivTaProfil
SELECT 'RivTaProfil', id, namn
FROM
 (
   SELECT tak.id, tak.namn
   FROM tak.RivVersion tak
   UNION ALL
   SELECT takv2.id, takv2.namn
   FROM takv2.RivTaProfil takv2
)  t
GROUP BY id, namn
HAVING COUNT(*) = 1
ORDER BY id;

# Tjanstekomponent
SELECT 'Tjanstekomponent', id, hsaId
FROM
 (
   SELECT tak.id, tak.hsaId
   FROM tak.Tjanstekomponent tak
   UNION ALL
   SELECT takv2.id, takv2.hsaId
   FROM takv2.Tjanstekomponent takv2
)  t
GROUP BY id, hsaId
HAVING COUNT(*) = 1
ORDER BY id;

# LogiskAdress
SELECT 'LogiskAdress', id, hsaId
FROM
 (
   SELECT tak.id, tak.hsaId
   FROM tak.LogiskAdressat tak
   UNION ALL
   SELECT takv2.id, takv2.hsaId
   FROM takv2.LogiskAdress takv2
)  t
GROUP BY id, hsaId
HAVING COUNT(*) = 1
ORDER BY id;

# AnropsAdress
SELECT 'AnropsAdress för Vagval', id, adress, rivTaProfil_id
FROM
 (
   SELECT la.id, tk.adress, la.rivVersion_id as rivTaProfil_id
	  FROM tak.LogiskAdress la, tak.Tjanstekomponent tk
	  where la.tjansteproducent_id = tk.id
   UNION ALL
   SELECT takv2.id, takv2.adress, takv2.rivTaProfil_id
   FROM takv2.AnropsAdress takv2
)  t
GROUP BY id, adress
HAVING COUNT(*) = 1
ORDER BY id;

SELECT 'Korrekt RIV TA Profil', id, rivTaProfil_id
FROM
 (
   SELECT la.id, la.rivVersion_id as rivTaProfil_id
	  FROM tak.LogiskAdress la, tak.Tjanstekomponent tk
	  where la.tjansteproducent_id = tk.id
   UNION ALL
   SELECT takv2.id, takv2.rivTaProfil_id
   FROM takv2.AnropsAdress takv2
)  t
GROUP BY id, rivTaProfil_id
HAVING COUNT(*) = 1
ORDER BY id;

# Anropsbehorighet
SELECT 'Anropsbehorighet konsument', id, tjanstekonsument_id
FROM
 (
   SELECT tak.id, tak.tjanstekonsument_id
   FROM tak.Anropsbehorighet tak
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
   FROM tak.Anropsbehorighet tak
   UNION ALL
   SELECT takv2.id, takv2.logiskAdress_id
   FROM takv2.Anropsbehorighet takv2
)  t
GROUP BY id, logiskAdress_id
HAVING COUNT(*) = 1
ORDER BY id;

# Vagval
SELECT 'Vagval korrekt logisk adress', id, logiskAdress_id
FROM
 (
   SELECT tak.id, tak.logiskAdressat_id as logiskAdress_id
   FROM tak.LogiskAdress tak
   UNION ALL
   SELECT takv2.id, takv2.logiskAdress_id
   FROM takv2.Vagval takv2
)  t
GROUP BY id, logiskAdress_id
HAVING COUNT(*) = 1
ORDER BY id;

SELECT 'Vagval korrekt tjänstekontrakt', id, tjanstekontrakt_id
FROM
 (
   SELECT tak.id, tak.tjanstekontrakt_id
   FROM tak.LogiskAdress tak
   UNION ALL
   SELECT takv2.id, takv2.tjanstekontrakt_id
   FROM takv2.Vagval takv2
)  t
GROUP BY id, tjanstekontrakt_id
HAVING COUNT(*) = 1
ORDER BY id;
