/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;

import java.util.List;
import java.util.Optional;

public interface EntityService<T extends AbstractVersionInfo> {
    PagedEntityList<T> getEntityList(int offset, int max, List<ListFilter> filters, String sortBy, boolean sortDesc, boolean isDeleted);

    Optional<T> findById(long id);

    T add(T instance, String user);

    T update(T instance, String user);

    boolean delete(Long id, String user);

    String getEntityName();

    T createEntity();

    long getId(T entity);

    boolean isUserAllowedToDelete(T instance, String user);

    PagedEntityList<T> getUnmatchedEntityList(Integer offset, Integer max, String sortBy, boolean sortDesc);

    PagedEntityList<T> getUnmatchedEntityList(Integer offset, Integer max, String sortBy, boolean sortDesc, String unmatchedBy);
}
