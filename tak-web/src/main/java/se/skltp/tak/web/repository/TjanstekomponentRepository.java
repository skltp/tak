package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.Tjanstekomponent;

public interface TjanstekomponentRepository extends JpaRepository<Tjanstekomponent, Long> {
}