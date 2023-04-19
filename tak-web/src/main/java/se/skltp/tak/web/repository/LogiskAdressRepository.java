package se.skltp.tak.web.repository;

import se.skltp.tak.core.entity.LogiskAdress;

public interface LogiskAdressRepository extends AbstractTypeRepository<LogiskAdress, Long> {
  LogiskAdress findFirstByHsaIdAndDeleted(String hsaId, boolean deleted);
}