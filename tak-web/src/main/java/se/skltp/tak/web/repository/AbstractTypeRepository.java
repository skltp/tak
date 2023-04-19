package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean // To ensure Spring Boot doesn't try to auto-map this to the database.
public interface AbstractTypeRepository<T, ID> extends JpaRepository<T, ID> {

  List<T> findByDeletedFalse();

  List<T> findAllByPubVersion(String pubVersion);

  List<T> findAllByUpdatedByIsNotNull();

  List<T> findAllByUpdatedBy(String username);
}
