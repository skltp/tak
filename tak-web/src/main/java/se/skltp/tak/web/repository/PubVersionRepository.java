package se.skltp.tak.web.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.PubVersion;

import java.util.List;

public interface PubVersionRepository extends JpaRepository<PubVersion, Long> {

  PubVersion findTopByOrderByIdDesc();

  List<PubVersion> findAllByOrderByIdDesc(Pageable pageable);
}
