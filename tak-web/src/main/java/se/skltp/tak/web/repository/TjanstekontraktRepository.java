package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.web.dto.PagedEntityList;

import java.util.List;

public interface TjanstekontraktRepository extends AbstractTypeRepository<Tjanstekontrakt, Long> {
  Tjanstekontrakt findFirstByNamnrymdAndDeleted(String namnrymd, boolean deleted);

  @Query("SELECT tk FROM Tjanstekontrakt tk " +
          "WHERE tk NOT IN (SELECT k FROM Vagval vv JOIN vv.tjanstekontrakt k WHERE vv.deleted=FALSE) " +
          "AND   tk NOT IN (SELECT k FROM Anropsbehorighet aa JOIN aa.tjanstekontrakt k WHERE aa.deleted=FALSE) " +
          "AND tk.deleted=FALSE")
  List<Tjanstekontrakt> findUnmatched();
  @Query("SELECT tk FROM Tjanstekontrakt tk " +
          "WHERE tk NOT IN (SELECT k FROM Vagval vv JOIN vv.tjanstekontrakt k WHERE vv.deleted=FALSE) " +
          "AND tk.deleted=FALSE")
  List<Tjanstekontrakt> findUnmatchedByVagval();
  @Query("SELECT tk FROM Tjanstekontrakt tk " +
          "WHERE tk NOT IN (SELECT k FROM Anropsbehorighet aa JOIN aa.tjanstekontrakt k WHERE aa.deleted=FALSE) " +
          "AND tk.deleted=FALSE")
  List<Tjanstekontrakt> findUnmatchedByAnropsbehorighet();
}
