package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.Anropsbehorighet;

import java.util.List;

public interface AnropsBehorighetRepository extends JpaRepository<Anropsbehorighet, Long> {
    @Query("select ab from Anropsbehorighet ab where ab.deleted=FALSE and " +
            "ab.logiskAdress.hsaId=:logisk and ab.logiskAdress.deleted=FALSE and " +
            "ab.tjanstekontrakt.namnrymd=:kontrakt and ab.tjanstekontrakt.deleted=FALSE and " +
            "ab.tjanstekonsument.hsaId=:konsument and ab.tjanstekonsument.deleted=FALSE")
    List<Anropsbehorighet> getAnropsbehorighet(String logisk, String konsument, String kontrakt);
}
