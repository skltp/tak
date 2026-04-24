/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.skltp.tak.core.entity.LogiskAdress;

import java.util.List;

public interface LogiskAdressRepository extends AbstractTypeRepository<LogiskAdress, Long> {
  LogiskAdress findFirstByHsaIdAndDeleted(String hsaId, boolean deleted);

  @Query("SELECT COUNT(la) > 0 FROM LogiskAdress la WHERE la.hsaId = :hsaId AND la.deleted = false")
  boolean existsByHsaId(@Param("hsaId") String hsaId);

  @Query("SELECT la FROM LogiskAdress la " +
          "WHERE la NOT IN (SELECT l FROM Vagval vv JOIN vv.logiskAdress l WHERE vv.deleted=FALSE) " +
          "AND   la NOT IN (SELECT l FROM Anropsbehorighet aa JOIN aa.logiskAdress l WHERE aa.deleted=FALSE) " +
          "AND la.deleted=FALSE")
  List<LogiskAdress> findUnmatched(Sort by);
  @Query("SELECT la FROM LogiskAdress la " +
          "WHERE la NOT IN (SELECT l FROM Vagval vv JOIN vv.logiskAdress l WHERE vv.deleted=FALSE) " +
          "AND la.deleted=FALSE")
  List<LogiskAdress> findUnmatchedByVagval(Sort by);
  @Query("SELECT la FROM LogiskAdress la " +
          "WHERE la NOT IN (SELECT l FROM Anropsbehorighet aa JOIN aa.logiskAdress l WHERE aa.deleted=FALSE) " +
          "AND la.deleted=FALSE")
  List<LogiskAdress> findUnmatchedByAnropsbehorighet(Sort by);


}
