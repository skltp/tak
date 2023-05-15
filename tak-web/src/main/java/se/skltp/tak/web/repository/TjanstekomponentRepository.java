package se.skltp.tak.web.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;

import java.util.List;

public interface TjanstekomponentRepository extends AbstractTypeRepository<Tjanstekomponent, Long> {
  Tjanstekomponent findFirstByHsaIdAndDeleted(String hsaId, boolean deleted);

  @Query("SELECT tk FROM Tjanstekomponent tk " +
          "WHERE tk.deleted=FALSE " +
          "AND   tk NOT IN (SELECT k FROM AnropsAdress aa JOIN aa.tjanstekomponent k WHERE aa.deleted=FALSE)" )
  List<Tjanstekomponent> findUnmatched(Sort by);
}