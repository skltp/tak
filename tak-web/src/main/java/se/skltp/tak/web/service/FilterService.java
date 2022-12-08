package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.web.repository.AnropsAdressRepository;
import se.skltp.tak.web.repository.FilterRepository;

import java.util.List;

@Service
public class FilterService extends EntityServiceBase<Filter>{

  @Autowired
  FilterService(FilterRepository repository) {
    super(repository);
  }

  @Override
  public String getEntityName() {
    return "Filter";
  }

  @Override
  public Filter createEntity() {
    return new Filter();
  }

}
