package se.skltp.tak.web.service;

import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.web.dto.PagedEntityList;

import java.util.Optional;

public interface EntityService<T extends AbstractVersionInfo> {
    PagedEntityList<T> getEntityList(int offset, int max);

    Optional<T> findById(long id);

    T add(T instance, String user);

    T update(T instance, String user);

    boolean delete(Long id, String user);
}
