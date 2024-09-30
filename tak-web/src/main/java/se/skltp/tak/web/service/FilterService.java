package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.Filter;
import se.skltp.tak.web.repository.QueryGenerator;
import se.skltp.tak.web.repository.FilterRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class FilterService extends EntityServiceBase<Filter> {

  @Autowired
  FilterService(FilterRepository repository, QueryGenerator<Filter> queryGenerator) {
    super(repository, queryGenerator);
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
    options.put("servicedomain", "Tjänstedomän");
    return options;
  }

  @Override
  protected List<AbstractVersionInfo> getEntityDependencies(Filter entity) {
    List<AbstractVersionInfo> deps = new ArrayList<>();
    deps.addAll(entity.getCategorization());
    return deps;
  }

  @Override
  public long getId(Filter entity) {
      return entity == null ? 0 : entity.getId();
    }

  public boolean hasDuplicate(Filter f) {
    if (f==null) return false;
    if (f.getAnropsbehorighet() == null) throw new IllegalArgumentException("Filter must have Anropsbehorighet");
    Filter match = ((FilterRepository) repository)
            .findUnique(f.getServicedomain(), f.getAnropsbehorighet().getId(), f.getDeleted());

    return match != null && match.getId() != f.getId();
  }
}
