/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.PubVersion;

import java.util.Date;
import java.util.List;

public interface PubVersionRepository extends JpaRepository<PubVersion, Long>,
    JpaSpecificationExecutor<PubVersion> {

    PubVersion findTopByOrderByIdDesc();

    List<PubVersion> findAllByOrderByIdDesc(Pageable pageable);

    @Query("SELECT DISTINCT p.utforare FROM PubVersion p ORDER BY p.utforare ASC")
    List<String> findAllUniqueUtforare();

    List<PubVersion> findAllByTimeBetween(Pageable pageable, Date startDate, Date endDate);

    List<PubVersion> findAllByTimeBetweenAndUtforare(Pageable pageable, Date startDate, Date endDate, String utforare);
}
