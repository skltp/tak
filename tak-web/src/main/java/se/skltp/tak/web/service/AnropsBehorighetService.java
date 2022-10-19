package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Anropsbehorighet;

@Service
public class AnropsBehorighetService extends EntityServiceBase<Anropsbehorighet>{

  @Autowired
  AnropsBehorighetService(JpaRepository<Anropsbehorighet, Long> repository) {
    super(repository);
  }

  @Override
  public String getEntityName() {
    return "Anropsbehörighet";
  }

  @Override
  public Anropsbehorighet createEntity() {
    return new Anropsbehorighet();
  }
}
