package se.skltp.tak.web.repository;

import se.skltp.tak.core.entity.Tjanstekontrakt;

public interface TjanstekontraktRepository extends AbstractTypeRepository<Tjanstekontrakt, Long> {
  Tjanstekontrakt findFirstByNamnrymdAndDeleted(String namnrymd, boolean deleted);
}