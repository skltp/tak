/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.dto;

public class ListFilter {
    private String field;
    private FilterCondition condition;
    private Object text;

    public ListFilter(String field, FilterCondition condition, Object value) {
        this.field = field;
        this.condition = condition;
        this.text = value;
    }

    public ListFilter(String field, FilterCondition condition) {
        this.field = field;
        this.condition = condition;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public FilterCondition getCondition() {
        return condition;
    }

    public void setCondition(FilterCondition condition) {
        this.condition = condition;
    }

    public Object getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
