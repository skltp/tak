package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se.skltp.tak.core.entity.Filter;

public interface FilterRepository extends JpaRepository<Filter, Long> {
}
