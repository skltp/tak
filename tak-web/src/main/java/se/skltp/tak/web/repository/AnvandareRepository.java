package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.web.entity.Anvandare;

public interface AnvandareRepository extends JpaRepository<Anvandare, Long> {
    Anvandare findFirstByAnvandarnamn(String anvandarnamn);
}