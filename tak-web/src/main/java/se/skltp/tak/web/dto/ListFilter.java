package se.skltp.tak.web.dto;

public class ListFilter {
    private String field;
    private String condition;
    private String text;

    public ListFilter(String field, String condition, String text) {
        this.field = field;
        this.condition = condition;
        this.text = text;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
