/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.Filter;

public interface FilterRepository extends AbstractTypeRepository<Filter, Long> {
  @Query("select f from Filter f " +
      "where f.servicedomain=?1 and f.anropsbehorighet.id=?2 and f.deleted=?3")
  Filter findUnique(String serviceDomain, long anropsbehorighetId, boolean deleted);
}
