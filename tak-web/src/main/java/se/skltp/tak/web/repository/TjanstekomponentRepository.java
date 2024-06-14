package se.skltp.tak.web.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;

import java.util.List;

public interface TjanstekomponentRepository extends AbstractTypeRepository<Tjanstekomponent, Long> {
  Tjanstekomponent findFirstByHsaIdAndDeleted(String hsaId, boolean deleted);

  @Query("SELECT COUNT(t) > 0 FROM Tjanstekomponent t WHERE t.hsaId = :hsaId AND t.deleted = false")
  boolean existsByHsaId(@Param("hsaId") String hsaId);

  @Query("SELECT tk FROM Tjanstekomponent tk " +
          "WHERE tk.deleted=FALSE " +
          "AND   tk NOT IN (SELECT k FROM AnropsAdress aa JOIN aa.tjanstekomponent k WHERE aa.deleted=FALSE)" )
  List<Tjanstekomponent> findUnmatched(Sort by);


}