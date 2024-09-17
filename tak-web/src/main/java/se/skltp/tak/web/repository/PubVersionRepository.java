package se.skltp.tak.web.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.skltp.tak.core.entity.PubVersion;

import java.util.Date;
import java.util.List;

public interface PubVersionRepository extends JpaRepository<PubVersion, Long> {

  PubVersion findTopByOrderByIdDesc();

  List<PubVersion> findAllByOrderByIdDesc(Pageable pageable);

/*  List<PubVersion> findByCreatedDateBetween(Date startDate, Date endDate);*/

  @Query(value = "SELECT * FROM pubversion p WHERE p.time BETWEEN :startDate AND :endDate", nativeQuery = true)
  List<PubVersion> findByCreatedDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
