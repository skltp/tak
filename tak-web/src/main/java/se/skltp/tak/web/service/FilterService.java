package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.web.repository.FilterRepository;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class FilterService extends EntityServiceBase<Filter> {

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

  @Override
  public Map<String, String> getListFilterFieldOptions() {
    Map<String, String> options = new LinkedHashMap<>();
    options.put("serviceDomain", "Tjänstedomän");
    options.put("anropsbehörighet", "Anropsbehörighet");
    return options;
  }

  @Override
  public String getFieldValue(String fieldName, Filter entity) {
    switch (fieldName) {
      case "serviceDomain":
        return entity.getServicedomain();
      case "anropsbehörighet":
        return entity.getAnropsbehorighet().toString();
      default:
        throw new IllegalArgumentException();
    }
  }

  public boolean hasDuplicate(Filter f) {
    if (f==null) return false;
    Filter match = ((FilterRepository) repository)
            .findUnique(f.getServicedomain(), f.getAnropsbehorighet().getId(), f.getDeleted());

    return match != null && match.getId() != f.getId();
  }
}
