package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.Filter;

public interface FilterRepository extends AbstractTypeRepository<Filter, Long> {
  @Query("select f from Filter f " +
      "where f.servicedomain=?1 and f.anropsbehorighet.id=?2 and f.deleted=?3")
  Filter findUnique(String serviceDomain, long anropsbehorighetId, boolean deleted);
}
