package se.skltp.tak.web.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PagedEntityList<T> {
    private final List<T> content;
    private final int totalElements;
    private final int offset;
    private final int max;
    private final String description;
    private final List<ListFilter> filters;
    private final Map<String, String> filterFieldOptions;
    private final String sortBy;
    private final boolean sortDesc;

    public PagedEntityList(List<T> content, int totalElements, int offset, int max) {
        this(content, totalElements, offset, max, "Lista");
    }

    public PagedEntityList(List<T> content, int totalElements, int offset, int max, String description) {
        this(content, totalElements, offset, max, description,
             new ArrayList<>(), new LinkedHashMap<>(), null, false);
    }

    public PagedEntityList(List<T> content, int totalElements, int offset, int max, String description,
                           List<ListFilter> filters, Map<String, String> filterFieldOptions,
                           String sortBy, boolean sortDesc) {
        this.content = content;
        this.totalElements = totalElements;
        this.offset = offset;
        this.max = max;
        this.filters = filters;
        this.filterFieldOptions = filterFieldOptions;
        this.sortBy = sortBy;
        this.sortDesc = sortDesc;
        this.description = description;
    }

    public int getTotalPages() {
        return totalElements / max + Integer.signum(totalElements % max);
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

    public Map<String, String> getFilterFieldOptions() {
        return filterFieldOptions;
    }

    public String getFilterFields() {
        return filters.stream().map(ListFilter::getField).collect(Collectors.joining(","));
    }

    public String getFilterConditions() {
        return filters.stream().map(ListFilter::getCondition).collect(Collectors.joining(","));
    }

    public String getFilterTexts() {
        return filters.stream().map(ListFilter::getText).collect(Collectors.joining(","));
    }

    public String getSortBy() { return sortBy; }

    public boolean isSortDesc() { return sortDesc; }

    public String getDescription() { return description; }
}
