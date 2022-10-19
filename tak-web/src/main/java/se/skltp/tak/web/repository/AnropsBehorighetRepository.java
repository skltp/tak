package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Anropsbehorighet;

public interface AnropsBehorighetRepository extends JpaRepository<Anropsbehorighet, Long> {
}
