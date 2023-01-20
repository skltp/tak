package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.skltp.tak.core.entity.Anropsbehorighet;

import java.util.List;

public interface AnropsBehorighetRepository extends JpaRepository<Anropsbehorighet, Long> {
    @Query("select ab from Anropsbehorighet ab where ab.deleted=FALSE and " +
            "ab.logiskAdress.hsaId=:la and ab.logiskAdress.deleted=FALSE and " +
            "ab.tjanstekontrakt.namnrymd=:kontrakt and ab.tjanstekontrakt.deleted=FALSE and " +
            "ab.tjanstekonsument.hsaId=:konsument and ab.tjanstekonsument.deleted=FALSE")
    List<Anropsbehorighet> findMatchingNonDeleted(@Param("la") String logiskAdress, @Param("konsument") String tjanstekonsument, @Param("kontrakt") String tjanstekonstrakt);

    @Query("select ab from Anropsbehorighet ab where ab.deleted=FALSE and " +
            "ab.logiskAdress.id=:la and ab.logiskAdress.deleted=FALSE and " +
            "ab.tjanstekontrakt.id=:kontrakt and ab.tjanstekontrakt.deleted=FALSE and " +
            "ab.tjanstekonsument.id=:konsument and ab.tjanstekonsument.deleted=FALSE")
    Anropsbehorighet findFirstNonDeletedByIds(@Param("la") long logiskAdress, @Param("konsument") long tjanstekonsument, @Param("kontrakt") long tjanstekonstrakt);

    @Query("select ab from Anropsbehorighet ab where ab.deleted=FALSE and " +
            "ab.logiskAdress.id=:la and ab.logiskAdress.deleted=FALSE and " +
            "ab.tjanstekontrakt.id=:kontrakt and ab.tjanstekontrakt.deleted=FALSE and " +
            "ab.tjanstekonsument.id=:konsument and ab.tjanstekonsument.deleted=FALSE")
    List<Anropsbehorighet> findMatchingNonDeleted(@Param("la") long logiskAdress, @Param("konsument") long tjanstekonsument, @Param("kontrakt") long tjanstekonstrakt);

}
