package se.skltp.tak.web.service;

import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;

import java.util.List;
import java.util.Optional;

public interface EntityService<T extends AbstractVersionInfo> {
    PagedEntityList<T> getEntityList(int offset, int max, List<ListFilter> filters, String sortBy, boolean sortDesc);

    Optional<T> findById(long id);

    T add(T instance, String user);

    T update(T instance, String user);

    boolean delete(Long id, String user);

    String getEntityName();

    T createEntity();

    long getId(T entity);

    boolean isUserAllowedToDelete(T instance, String user);

    PagedEntityList<T> getUnmatchedEntityList(Integer offset, Integer max, List<ListFilter> filters, String sortBy, boolean sortDesc);

    PagedEntityList<T> getUnmatchedEntityList(Integer offset, Integer max, List<ListFilter> filters, String sortBy, boolean sortDesc, String unmatchedBy);
}
