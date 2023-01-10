package se.skltp.tak.web.dto;

import java.util.List;
import java.util.Map;

public class PagedEntityList<T> {
    private List<T> content;
    private int totalElements;
    private int offset;
    private int max;
    private List<ListFilter> filters;
    private Map<String, String> filterFieldOptions;

    public PagedEntityList(List<T> content, int totalElements, int offset, int max) {
        this.content = content;
        this.totalElements = totalElements;
        this.offset = offset;
        this.max = max;
    }

    public PagedEntityList(List<T> content, int totalElements, int offset, int max,
                           List<ListFilter> filters, Map<String, String> filterFieldOptions) {
        this.content = content;
        this.totalElements = totalElements;
        this.offset = offset;
        this.max = max;
        this.filters = filters;
        this.filterFieldOptions = filterFieldOptions;
    }

    public int getTotalPages() {
        return totalElements / max + 1;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public int getOffset() {
        return offset;
    }

    public int getMax() {
        return max;
    }

    public int getNumber() {
        return offset/max + 1;
    }

    public int getSize() {
        return content.size();
    }

    public List getContent() {
        return content;
    }

    public boolean isFirst() {
        return offset == 0;
    }

    public boolean isLast() {
        return false;
    }

    public List<ListFilter> getFilters() { return filters; }

    public void setFilters(List<ListFilter> filters) { this.filters = filters; }

    public Map<String, String> getFilterFieldOptions() {
        return filterFieldOptions;
    }

    public void setFilterFieldOptions(Map<String, String> filterFieldOptions) {
        this.filterFieldOptions = filterFieldOptions;
    }
}
