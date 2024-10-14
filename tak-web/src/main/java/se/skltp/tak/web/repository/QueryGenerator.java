package se.skltp.tak.web.repository;

import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import se.skltp.tak.web.dto.ListFilter;

public interface QueryGenerator<T> {
  Specification<T> generateUserFiltersSpecification(List<ListFilter> userFilters);

  Specification<T> generateDeletedSpecification();
}
