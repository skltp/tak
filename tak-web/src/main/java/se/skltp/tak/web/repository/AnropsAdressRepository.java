package se.skltp.tak.web.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.Tjanstekontrakt;

import java.util.List;

public interface AnropsAdressRepository extends AbstractTypeRepository<AnropsAdress, Long> {

  @Query("select aa from AnropsAdress aa " +
      "where aa.deleted=FALSE and aa.adress =:adress and aa.rivTaProfil.namn=:rivta " +
      "and aa.rivTaProfil.deleted=FALSE and aa.tjanstekomponent.hsaId=:komponent " +
      "and aa.tjanstekomponent.deleted=FALSE")
  List<AnropsAdress> findMatchingNonDeleted(@Param("rivta") String rivta, @Param("komponent") String komponent, @Param("adress") String adress);

  @Query("select aa from AnropsAdress aa " +
      "where aa.rivTaProfil.id=?1 and aa.tjanstekomponent.id=?2 and aa.adress=?3 and aa.deleted=?4")
  AnropsAdress findUnique(long rivtaId, long tjanstekomponentId, String adress, boolean deleted);

  @Query("SELECT aa FROM AnropsAdress aa " +
          "WHERE aa NOT IN (SELECT a FROM Vagval vv JOIN vv.anropsAdress a WHERE vv.deleted=FALSE) " +
          "AND aa.deleted=FALSE")
  List<AnropsAdress> findUnmatched(Sort by);

}
