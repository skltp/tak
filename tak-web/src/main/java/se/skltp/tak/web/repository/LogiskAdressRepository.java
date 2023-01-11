package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.LogiskAdress;

import java.util.List;

public interface LogiskAdressRepository extends JpaRepository<LogiskAdress, Long> {
    LogiskAdress findFirstByHsaIdAndDeleted(String hsaId, boolean deleted);

  List<LogiskAdress> findByDeletedFalse();
}