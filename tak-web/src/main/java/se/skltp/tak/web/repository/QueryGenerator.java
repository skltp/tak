/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.repository;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.skltp.tak.web.dto.ListFilter;

public interface QueryGenerator<T> {
  Specification<T> generateFiltersSpecification(List<ListFilter> userFilters);

  List<ListFilter> getDeletedInPublishedVersionFilters();
}
