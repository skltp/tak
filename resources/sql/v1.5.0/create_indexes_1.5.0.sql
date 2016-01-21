use takv2;

DROP INDEX anropsbehorighet_distinct_idx ON Anropsbehorighet;

# index to support JPQL query:
#   Select Distinct a from Anropsbehorighet a left join fetch a.filter
# from: AnropsbehorighetDao.getAllAnropsbehorighetAndFilter()
CREATE INDEX anropsbehorighet_distinct_idx ON Anropsbehorighet (
  fromTidpunkt,
  integrationsavtal,
  tomTidpunkt,
  version,
  logiskAdress_id,
  tjanstekonsument_id,
  tjanstekontrakt_id);
