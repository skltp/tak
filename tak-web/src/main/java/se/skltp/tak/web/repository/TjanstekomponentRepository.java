package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.Tjanstekomponent;

import java.util.List;

public interface TjanstekomponentRepository extends JpaRepository<Tjanstekomponent, Long> {
    Tjanstekomponent findFirstByHsaIdAndDeleted(String hsaId, boolean deleted);

  List<Tjanstekomponent> findByDeletedFalse();
}