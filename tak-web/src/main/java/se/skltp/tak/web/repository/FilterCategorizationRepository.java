/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.Filtercategorization;

public interface FilterCategorizationRepository extends AbstractTypeRepository<Filtercategorization, Long> {
  @Query("select fc from Filtercategorization fc " +
      "where fc.category=?1 and fc.filter.id=?2 and fc.deleted=?3")
  Filtercategorization findUnique(String category, long filterId, Boolean deleted);
}
