package se.skltp.tak.web.dto;

public enum FilterCondition {
  STARTS_WITH("begins"),
  CONTAINS("contains"),
  NOT_EQUALS("not_equals"),
  EQUALS("equals"),
  EXISTS("exists"),
  NOT_EXISTS("not_exists");;

  private final String condition;

  FilterCondition(String condition) {
    this.condition = condition;
  }

  public String getCondition() {
    return condition;
  }

  public static FilterCondition fromCondition(String condition) {
    for (FilterCondition filterCondition : FilterCondition.values()) {
      if (filterCondition.getCondition().equalsIgnoreCase(condition)) {
        return filterCondition;
      }
    }
    throw new IllegalArgumentException("No " + condition);
  }
}
