package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.LogiskAdress;

import java.util.List;

public interface LogiskAdressRepository extends AbstractTypeRepository<LogiskAdress, Long> {
  LogiskAdress findFirstByHsaIdAndDeleted(String hsaId, boolean deleted);

  @Query("SELECT la FROM LogiskAdress la " +
          "WHERE la NOT IN (SELECT l FROM Vagval vv JOIN vv.logiskAdress l WHERE vv.deleted=FALSE) " +
          "AND   la NOT IN (SELECT l FROM Anropsbehorighet aa JOIN aa.logiskAdress l WHERE aa.deleted=FALSE) " +
          "AND la.deleted=FALSE")
  List<LogiskAdress> findUnmatched();
  @Query("SELECT la FROM LogiskAdress la " +
          "WHERE la NOT IN (SELECT l FROM Vagval vv JOIN vv.logiskAdress l WHERE vv.deleted=FALSE) " +
          "AND la.deleted=FALSE")
  List<LogiskAdress> findUnmatchedByVagval();
  @Query("SELECT la FROM LogiskAdress la " +
          "WHERE la NOT IN (SELECT l FROM Anropsbehorighet aa JOIN aa.logiskAdress l WHERE aa.deleted=FALSE) " +
          "AND la.deleted=FALSE")
  List<LogiskAdress> findUnmatchedByAnropsbehorighet();


}
