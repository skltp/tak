package se.skltp.tak.web.repository;


import java.util.ArrayList;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import se.skltp.tak.web.dto.FilterCondition;
import se.skltp.tak.web.dto.ListFilter;

@Component
public class QueryGeneratorImpl<T> implements QueryGenerator<T> {

  public Specification<T> generateCriteriaQuery(List<ListFilter> userFilters) {
    return (root, query, criteriaBuilder) -> {
      Predicate userPredicates = criteriaBuilder.and(
          userFilters.stream().map(listFilter ->
                  createPredicate(criteriaBuilder, root, listFilter.getField(), listFilter.getText(),
                      listFilter.getCondition()))
              .toArray(Predicate[]::new));

      Predicate isNOTDeletedInPublishedVersionFilter = criteriaBuilder.and(isDeletedInPublishedVersionFilter().stream()
          .map(listFilter -> createPredicate(criteriaBuilder, root, listFilter.getField(),
              listFilter.getText(), listFilter.getCondition())).toArray(Predicate[]::new));

      return criteriaBuilder.and(userPredicates, criteriaBuilder.not(isNOTDeletedInPublishedVersionFilter));
    };
  }

  private List<ListFilter> isDeletedInPublishedVersionFilter() {
    List<ListFilter> filters = new ArrayList<>();
    filters.add(isDeletedFilter(true));
    filters.add(hasPubVersionFilter(true));
    filters.add(hasModifiedByFilter(false));
    return filters;
  }

  private ListFilter isDeletedFilter(boolean deleted) {
    if (deleted){
      return new ListFilter("deleted", FilterCondition.NOT_EXISTS);
    } else {
      return new ListFilter("deleted", FilterCondition.EQUALS, 0);
    }
  }

  public ListFilter hasPubVersionFilter(boolean published) {
    if (published) {
      return new ListFilter("pubVersion", FilterCondition.EXISTS);
    } else {
      return new ListFilter("pubVersion",  FilterCondition.NOT_EXISTS);
    }
  }

  public ListFilter hasModifiedByFilter(boolean modified) {
    if (modified) {
      return new ListFilter("updatedBy", FilterCondition.EXISTS);
    } else {
      return new ListFilter("updatedBy", FilterCondition.NOT_EXISTS);
    }
  }

  private Predicate createPredicate(CriteriaBuilder cb, Root<T> root,
      String field, Object value, FilterCondition condition) {

    Path<String> targetField;
    if (field.contains(".")) {
      String[] fieldParts = field.split("\\.");
      Path<?> path = root;

      for (int i = 0; i < fieldParts.length - 1; i++) {
        path = ((From<?, ?>) path).join(fieldParts[i]);
      }
      targetField = path.get(fieldParts[fieldParts.length - 1]);
    } else {
      targetField = root.get(field);
    }

    if (value instanceof Boolean) {
      return handleBooleanPredicate(cb, targetField, (Boolean) value, condition);
    } else {
      return handleStringPredicate(cb, targetField, value, condition);
    }
  }

    private Predicate handleBooleanPredicate(CriteriaBuilder cb, Path<?> targetField, Boolean value, FilterCondition condition) {
      switch (condition) {
        case EQUALS:
          return cb.equal(targetField, value);
        case NOT_EQUALS:
          return cb.notEqual(targetField, value);
        case EXISTS:
          return cb.isNotNull(targetField);
        case NOT_EXISTS:
          return cb.isNull(targetField);
        default:
          throw new UnsupportedOperationException("Condition not supported for Boolean type: " + condition);
      }
    }

    private Predicate handleStringPredicate(CriteriaBuilder cb, Path<String> targetField, Object value, FilterCondition condition) {
      switch (condition) {
        case STARTS_WITH:
          return cb.like(targetField, value + "%");
        case CONTAINS:
          return cb.like(targetField, "%" + value + "%");
        case EQUALS:
          return cb.equal(targetField, value);
        case NOT_EQUALS:
          return cb.notEqual(targetField, value);
        case EXISTS:
          return cb.isNotNull(targetField);
        case NOT_EXISTS:
          return cb.isNull(targetField);
        default:
          return cb.like(targetField, value.toString());
      }
    }
}
