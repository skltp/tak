package se.skltp.tak.web.repository;

import se.skltp.tak.core.entity.RivTaProfil;

public interface RivTaProfilRepository extends AbstractTypeRepository<RivTaProfil, Long> {
  RivTaProfil findFirstByNamnAndDeleted(String namn, boolean deleted);
}