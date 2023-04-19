package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.web.entity.Locktb;
import se.skltp.tak.web.entity.TAKSettings;

public interface LockRepository extends JpaRepository<Locktb, Long> {
    Locktb findFirstById(String id);
}