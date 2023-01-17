package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.PubVersion;

public interface PublicationVersionRepository extends JpaRepository<PubVersion, Long> {
}
