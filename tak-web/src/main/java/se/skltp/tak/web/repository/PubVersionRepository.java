package se.skltp.tak.web.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se.skltp.tak.core.entity.PubVersion;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public interface PubVersionRepository extends JpaRepository<PubVersion, Long> {

    PubVersion findTopByOrderByIdDesc();

    List<PubVersion> findAllByOrderByIdDesc(Pageable pageable);

    @Query(value = "SELECT * FROM pubversion p WHERE p.time BETWEEN :startDate AND :endDate " +
            "AND (:utforare = 'all' OR p.utforare = :utforare)", nativeQuery = true)
    List<PubVersion> findByDateBetweenAndByUtforare(LocalDate startDate, LocalDate endDate, String utforare);

    @Query(value = "SELECT DISTINCT utforare FROM pubversion", nativeQuery = true)
    List<String> findAllUniqueUtforare();

}


