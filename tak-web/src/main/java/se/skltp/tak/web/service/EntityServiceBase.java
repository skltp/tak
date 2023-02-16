package se.skltp.tak.web.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.AbstractTypeRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public abstract class EntityServiceBase<T extends AbstractVersionInfo> implements EntityService<T> {

    protected JpaRepository<T, Long> repository;

    EntityServiceBase(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    public abstract String getEntityName();

    public abstract T createEntity();

    public abstract Map<String, String> getListFilterFieldOptions();

    public abstract String getFieldValue(String fieldName, T entity);

    public PagedEntityList<T> getEntityList(int offset, int max, List<ListFilter> filters) {
        List<T> contents = repository.findAll().stream()
                .filter(f -> !f.isDeletedInPublishedVersion())
                .filter(f -> matchesListFilters(f, filters))
                .skip(offset)
                .limit(max)
                .collect(Collectors.toList());
        long total = repository.findAll().stream()
                .filter(f -> !f.isDeletedInPublishedVersion())
                .filter(f -> matchesListFilters(f, filters))
                .count();
        return new PagedEntityList<T>(contents, (int) total, offset, max, filters, getListFilterFieldOptions());
    }

    public Optional<T> findById(long id) { return repository.findById(id); }

    public List<T> findAllByPubVersion(Long id) {
        return ((AbstractTypeRepository<T, Long>) repository).findAllByPubVersion(id.toString());
    }

    public List<T> findAllNotDeleted() {
        return ((AbstractTypeRepository<T, Long>) repository).findByDeletedFalse();
    }

    public List<T> findAllByUpdatedByIsNotNull() {
        return ((AbstractTypeRepository<T, Long>) repository).findAllByUpdatedByIsNotNull();
    }

    public List<T> findAllByUpdatedBy(String username) {
        return ((AbstractTypeRepository<T, Long>) repository).findAllByUpdatedBy(username);
    }

    public T add(T instance, String user) {
        setMetadata(instance, user);
        return repository.save(instance);
    }

    public T update(T instance, String user) {
        setMetadata(instance, user);
        return repository.save(instance);
    }

    /**
     * Simple straight-through save.
     */
    public T save(T instance) {
        return repository.save(instance);
    }

    public boolean delete(Long id, String user) {
        Optional<T> opt = repository.findById(id);
        if (opt.isPresent()) {
            T instance = opt.get();
            setMetadata(instance, user);
            instance.setDeleted(true);
            repository.save(instance);
            return true;
        }
        return false;
    }

    protected void setMetadata(T instance, String user) {
        instance.setUpdatedBy(user);
        instance.setUpdatedTime(new Date());
    }

    private boolean matchesListFilters(T entity, List<ListFilter> filters) {
        if (filters == null) return true;

        for (ListFilter filter: filters) {
            String fieldValue = getFieldValue(filter.getField(), entity);
            switch (filter.getCondition()) {
                case "contains":
                    if (!fieldValue.toLowerCase().contains(filter.getText().toLowerCase())) return false;
                    break;
                case "begins":
                    if (!fieldValue.toLowerCase().startsWith(filter.getText().toLowerCase())) return false;
                    break;
                case "equals":
                    if (!fieldValue.equalsIgnoreCase(filter.getText())) return false;
            }
        }
        return true;
    }
}
