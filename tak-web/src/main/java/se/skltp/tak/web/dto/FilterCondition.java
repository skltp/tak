/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.dto;

public enum FilterCondition {
  STARTS_WITH("begins"),
  CONTAINS("contains"),
  NOT_EQUALS("not_equals"),
  EQUALS("equals"),

  EXISTS("exists"),
  NOT_EXISTS("not_exists"),

  FROM("from"),
  TO("to");

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
