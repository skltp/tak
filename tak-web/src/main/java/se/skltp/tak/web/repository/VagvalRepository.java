package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.skltp.tak.core.entity.Vagval;

import java.util.List;

public interface VagvalRepository extends JpaRepository<Vagval, Long> {

    @Query("from Vagval as vv where vv.deleted=FALSE and " +
            "vv.logiskAdress.hsaId=:la and vv.logiskAdress.deleted=FALSE and " +
            "vv.tjanstekontrakt.namnrymd=:kontrakt and vv.tjanstekontrakt.deleted=FALSE")
    List<Vagval> findMatchingNonDeleted(@Param("la") String logiskAdress, @Param("kontrakt") String tjanstekontrakt);
}
