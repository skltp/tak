package se.skltp.tak.web.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.Vagval;

import java.util.List;

public interface VagvalRepository extends AbstractTypeRepository<Vagval, Long> {
    @Query("from Vagval as vv where vv.deleted=FALSE and " +
            "vv.logiskAdress.hsaId=:la and vv.logiskAdress.deleted=FALSE and " +
            "vv.tjanstekontrakt.namnrymd=:kontrakt and vv.tjanstekontrakt.deleted=FALSE")
    List<Vagval> findMatchingNonDeleted(@Param("la") String logiskAdress, @Param("kontrakt") String tjanstekontrakt);

    @Query("from Vagval as vv where vv.deleted=FALSE and " +
            "vv.logiskAdress.id=:la and vv.tjanstekontrakt.id=:kontrakt")
    List<Vagval> findMatchingNonDeleted(@Param("la") long logiskAdressId, @Param("kontrakt") long tjanstekontraktId);

    @Query("SELECT vv FROM Vagval vv WHERE vv.deleted=FALSE " +
            "AND vv.tjanstekontrakt NOT IN (SELECT ab.tjanstekontrakt FROM Anropsbehorighet ab WHERE ab.deleted=FALSE)")
    List<Vagval> findUnmatched(Sort by);
}
