package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.core.entity.Filtercategorization;
import se.skltp.tak.web.repository.FilterCategorizationRepository;
import se.skltp.tak.web.repository.FilterRepository;

@Service
public class FilterCategorizationService extends EntityServiceBase<Filtercategorization>{

  @Autowired
  FilterCategorizationService(FilterCategorizationRepository repository) {
    super(repository);
  }

  @Override
  public String getEntityName() {
    return "Filterkategorisering";
  }

  @Override
  public Filtercategorization createEntity() {
    return new Filtercategorization();
  }

}
