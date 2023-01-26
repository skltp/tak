package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.core.entity.Filtercategorization;
import se.skltp.tak.web.repository.FilterCategorizationRepository;
import se.skltp.tak.web.repository.FilterRepository;

import java.util.LinkedHashMap;
import java.util.Map;

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

  @Override
  public Map<String, String> getListFilterFieldOptions() {
    Map<String, String> options = new LinkedHashMap<>();
    options.put("category", "Kategori");
    options.put("filter", "Filter");
    return options;
  }

  @Override
  public String getFieldValue(String fieldName, Filtercategorization entity) {
    switch (fieldName) {
      case "category":
        return entity.getCategory();
      case "filter":
        return entity.getFilter().getServicedomain();
      default:
        throw new IllegalArgumentException();
    }
  }

  public boolean hasDuplicate(Filtercategorization fc) {
    if (fc==null) return false;
    Filtercategorization match = ((FilterCategorizationRepository) repository)
            .findUnique(fc.getCategory(), fc.getFilter().getId(), fc.getDeleted());

    return match != null && match.getId() != fc.getId();
  }
}
