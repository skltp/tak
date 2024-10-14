package se.skltp.tak.web.repository;


import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.From;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import se.skltp.tak.web.dto.FilterCondition;
import se.skltp.tak.web.dto.ListFilter;

@Component
public class QueryGeneratorImpl<T> implements QueryGenerator<T> {

  public Specification<T> generateUserFiltersSpecification(List<ListFilter> userFilters) {
    return (root, query, criteriaBuilder) -> criteriaBuilder.and(
       userFilters.stream().map(listFilter ->
               createPredicate(criteriaBuilder, root,
                   listFilter.getField(), listFilter.getText(),listFilter.getCondition()))
           .toArray(Predicate[]::new));
  }

  public Specification<T> generateDeletedSpecification(){
    return (root, query, criteriaBuilder) -> criteriaBuilder.and(
          isDeletedInPublishedVersionFilter().stream()
              .map(listFilter ->
                  createPredicate(criteriaBuilder, root,
                      listFilter.getField(), listFilter.getText(), listFilter.getCondition()))
              .toArray(Predicate[]::new));
  }

  private List<ListFilter> isDeletedInPublishedVersionFilter() {
    List<ListFilter> filters = new ArrayList<>();
    filters.add(isDeletedFilter(true));
    filters.add(hasPubVersionFilter(true));
    filters.add(hasModifiedByFilter(false));
    return filters;
  }

  private ListFilter isDeletedFilter(boolean deleted) {
    if (deleted) {
      return new ListFilter("deleted", FilterCondition.NOT_EXISTS);
    } else {
      return new ListFilter("deleted", FilterCondition.EQUALS, 0);
    }
  }

  private ListFilter hasPubVersionFilter(boolean published) {
    if (published) {
      return new ListFilter("pubVersion", FilterCondition.EXISTS);
    } else {
      return new ListFilter("pubVersion", FilterCondition.NOT_EXISTS);
    }
  }

  private ListFilter hasModifiedByFilter(boolean modified) {
    if (modified) {
      return new ListFilter("updatedBy", FilterCondition.EXISTS);
    } else {
      return new ListFilter("updatedBy", FilterCondition.NOT_EXISTS);
    }
  }

  private Predicate createPredicate(CriteriaBuilder cb, Root<T> root,
      String field, Object value, FilterCondition condition) {
    if (value instanceof Boolean booleanValue) {
      return handleBooleanPredicate(cb, getTargetField(root, field), booleanValue, condition);
    } else if (value instanceof Date dateValue) {
      return handleDatePredicate(cb, getTargetField(root, field), dateValue, condition);
    } else {
      return handleStringPredicate(cb, getTargetField(root, field), value, condition);
    }
  }

  private <F> Path<F> getTargetField(Root<T> root, String field) {
    Path<F> targetField;
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
    return targetField;
  }

  private Predicate handleBooleanPredicate(CriteriaBuilder cb, Path<Boolean> targetField, Boolean value,
      FilterCondition condition) {
    return switch (condition) {
      case EQUALS -> cb.equal(targetField, value);
      case NOT_EQUALS -> cb.notEqual(targetField, value);
      case EXISTS -> cb.isNotNull(targetField);
      case NOT_EXISTS -> cb.isNull(targetField);
      default -> throw new UnsupportedOperationException(
          "Condition not supported for Boolean type: " + condition);
    };
  }

  private Predicate handleDatePredicate(CriteriaBuilder cb, Path<Date> targetField, Date value,
      FilterCondition condition) {
    return switch (condition) {
      case FROM -> cb.greaterThanOrEqualTo(targetField, value);
      case TO -> cb.lessThanOrEqualTo(targetField, value);
      default -> throw new UnsupportedOperationException(
          "Condition not supported for Date type: " + condition);
    };
  }

  private Predicate handleStringPredicate(CriteriaBuilder cb, Path<String> targetField,
      Object value, FilterCondition condition) {
    return switch (condition) {
      case STARTS_WITH -> cb.like(targetField, value + "%");
      case CONTAINS -> cb.like(targetField, "%" + value + "%");
      case EQUALS -> cb.equal(targetField, value);
      case NOT_EQUALS -> cb.notEqual(targetField, value);
      case EXISTS -> cb.isNotNull(targetField);
      case NOT_EXISTS -> cb.isNull(targetField);
      default -> throw new UnsupportedOperationException(
          "Condition not supported for String type: " + condition);
    };
  }
}
