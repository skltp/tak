package se.skltp.tak.web.dto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class PagedEntityList<T> {
    private final List<T> content;
    private final long totalElements;
    private final int offset;
    private final int max;
    private final String sortBy;
    private final boolean sortDesc;
    private String preDefinedFilter = null;
    private List<ListFilter> filters = new ArrayList<>();
    private Map<String, String> filterFieldOptions  = new LinkedHashMap<>();

    public PagedEntityList(List<T> content, int totalElements, int offset, int max) {
        this(content, totalElements, offset, max, null, false,null);
    }

    public PagedEntityList(List<T> content, int totalElements, int offset, int max,
                           String sortBy, boolean sortDesc, String preDefinedFilter) {
        this.content = content;
        this.totalElements = totalElements;
        this.offset = offset;
        this.max = max;
        this.sortBy = sortBy;
        this.sortDesc = sortDesc;
        this.preDefinedFilter = preDefinedFilter;
    }

    public PagedEntityList(List<T> content, long totalElements, int offset, int max, String sortBy, boolean sortDesc,
                           List<ListFilter> filters, Map<String, String> filterFieldOptions) {
        this.content = content;
        this.totalElements = totalElements;
        this.offset = offset;
        this.max = max;
        this.filters = filters;
        this.filterFieldOptions = filterFieldOptions;
        this.sortBy = sortBy;
        this.sortDesc = sortDesc;
    }

    public long getTotalPages() {
        return totalElements / max + Long.signum(totalElements % max);
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
        return filters.stream().map(ListFilter::getCondition).map(FilterCondition::getCondition).collect(Collectors.joining(","));
    }

    public String getFilterTexts() {
        return filters.stream().map(ListFilter::getText).filter(Objects::nonNull).map(Object::toString).collect(Collectors.joining(","));
    }

    public String getSortBy() { return sortBy; }

    public boolean isSortDesc() { return sortDesc; }

    public String getPreDefinedFilter() { return preDefinedFilter; }
}
