package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.Filter;

import java.util.List;

public interface FilterRepository extends JpaRepository<Filter, Long> {
  List<Filter> findByDeletedFalse();

  @Query("select f from Filter f " +
          "where f.servicedomain=?1 and f.anropsbehorighet.id=?2 and f.deleted=?3")
  Filter findUnique(String serviceDomain, long anropsbehorighetId, boolean deleted);
}
