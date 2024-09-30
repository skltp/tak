package se.skltp.tak.web.repository;

import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean // To ensure Spring Boot doesn't try to auto-map this to the database.
public interface AbstractTypeRepository<T, ID> extends JpaSpecificationExecutor<T>,
    JpaRepository<T, ID> {

  List<T> findByDeletedFalse();

  List<T> findAllByPubVersion(String pubVersion);

  List<T> findAllByUpdatedByIsNotNull();

  List<T> findAllByUpdatedBy(String username);

  @Query("SELECT 1 FROM Anvandare WHERE 1=0")
  List<T> findUnmatched(Sort by);

}
