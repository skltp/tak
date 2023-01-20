package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.RivTaProfil;

import java.util.List;

public interface RivTaProfilRepository extends JpaRepository<RivTaProfil, Long> {
    RivTaProfil findFirstByNamnAndDeleted(String namn, boolean deleted);

  List<RivTaProfil> findByDeletedFalse();

  List<RivTaProfil> findAllByPubVersion(String pubVersion);
}