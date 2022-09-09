package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.Tjanstekontrakt;

public interface TjanstekontraktRepository extends JpaRepository<Tjanstekontrakt, Long> {
}