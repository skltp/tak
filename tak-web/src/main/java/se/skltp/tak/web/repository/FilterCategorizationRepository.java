package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.Filtercategorization;

public interface FilterCategorizationRepository extends JpaRepository<Filtercategorization, Long> {
    @Query("select fc from Filtercategorization fc " +
            "where fc.category=?1 and fc.filter.id=?2 and fc.deleted=?3")
    Filtercategorization findUnique(String category, long filterId, Boolean deleted);
}
