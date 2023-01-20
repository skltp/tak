package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.List;

@NoRepositoryBean
public interface AbstractTypeRepository<T, ID> extends JpaRepository<T, ID> {

  /*
  TODO: Abstract TAK Core Type repos?
  The idea of this interface is to create a
  shared set of functions that are common between
  many of the implementations of JpaRepo for use with
  TAK-types. Such as populating drop-down-lists with
  non-deleted entities, or finding all items referred
  to by a PubVersion (Published Version) of TAK.
  I have yet to try to implement this, to see if it works
  or breaks the repos/services.
   */

  List<T> findByDeletedFalse();

  List<T> findAllByPubVersion(String pubVersion);
}
