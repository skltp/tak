package se.skltp.tak.web.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import se.skltp.tak.core.entity.Anropsbehorighet;

import java.util.List;

@NoRepositoryBean // To ensure Spring Boot doesn't try to auto-map this to the database.
public interface AbstractTypeRepository<T, ID> extends JpaRepository<T, ID> {

  List<T> findByDeletedFalse();

  List<T> findAllByPubVersion(String pubVersion);

  List<T> findAllByUpdatedByIsNotNull();

  List<T> findAllByUpdatedBy(String username);

  @Query("SELECT 1 FROM Anvandare WHERE 1=0")
  List<T> findUnmatched(Sort by);

}
