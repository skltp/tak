package se.skltp.tak.web.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.PubVersion;

import java.util.Date;
import java.util.List;

public interface PubVersionRepository extends JpaRepository<PubVersion, Long> {

    PubVersion findTopByOrderByIdDesc();

    List<PubVersion> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT DISTINCT p.utforare FROM PubVersion p ORDER BY p.utforare ASC")
    List<String> findAllUniqueUtforare();

    List<PubVersion> findAllByTimeBetween(Pageable pageable, Date startDate, Date endDate);

    List<PubVersion> findAllByTimeBetweenAndUtforare(Pageable pageable, Date startDate, Date endDate, String utforare);
}
