package se.skltp.tak.web.repository;

import se.skltp.tak.core.entity.Tjanstekomponent;

public interface TjanstekomponentRepository extends AbstractTypeRepository<Tjanstekomponent, Long> {
  Tjanstekomponent findFirstByHsaIdAndDeleted(String hsaId, boolean deleted);
}