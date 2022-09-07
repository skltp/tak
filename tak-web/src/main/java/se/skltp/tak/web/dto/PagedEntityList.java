package se.skltp.tak.web.dto;

import java.util.List;

public class PagedEntityList<T> {
    private List<T> content;
    private int totalElements;
    private int offset;
    private int max;

    public PagedEntityList(List<T> content, int totalElements, int offset, int max) {
        this.content = content;
        this.totalElements = totalElements;
        this.offset = offset;
        this.max = max;
    }

    public int getTotalPages() {
        return totalElements / max;
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
}
