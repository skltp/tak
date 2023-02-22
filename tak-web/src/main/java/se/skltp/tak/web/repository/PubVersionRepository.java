package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.PubVersion;

public interface PubVersionRepository extends JpaRepository<PubVersion, Long> {

  PubVersion findTopByOrderByIdDesc();
}
