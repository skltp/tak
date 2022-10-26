package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.LogiskAdress;

public interface LogiskAdressRepository extends JpaRepository<LogiskAdress, Long> {
    LogiskAdress findFirstByHsaIdAndDeleted(String hsaId, boolean deleted);
}