package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.web.entity.TAKSettings;

public interface SettingsRepository extends JpaRepository<TAKSettings, Long> {
}