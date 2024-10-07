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
