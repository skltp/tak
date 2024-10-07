package se.skltp.tak.web.service;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.AbstractTypeRepository;
import se.skltp.tak.web.repository.QueryGenerator;

@Service
public abstract class EntityServiceBase<T extends AbstractVersionInfo> implements EntityService<T> {

    static final Logger log = LoggerFactory.getLogger(EntityServiceBase.class);
    protected JpaRepository<T, Long> repository;
    protected QueryGenerator<T> queryGenerator;

    EntityServiceBase(JpaRepository<T, Long> repository, QueryGenerator<T> queryGenerator) {
        this.repository = repository;
        this.queryGenerator = queryGenerator;
    }

    public abstract Map<String, String> getListFilterFieldOptions();

    public String getFieldValue(String fieldName, T entity) {
        try {
            Field field = entity.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return String.valueOf(field.get(entity));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalArgumentException("Invalid field name: " + fieldName, e);
        }
    }

    protected abstract List<AbstractVersionInfo> getEntityDependencies(T entity);

    private Pageable createPageDescription(int offset, int max, String sortBy, boolean sortDesc){
        Sort.Direction direction = sortDesc ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sortBy == null ? "id" : sortBy; // id should always be present, use as default sort
        return PageRequest.of(offset/max, max, Sort.by(direction, sortField));
    }

    public PagedEntityList<T> getEntityList(int offset, int max, List<ListFilter> userFilters,
        String sortBy, boolean sortDesc) {

        Specification<T> query = queryGenerator.generateCriteriaQuery(userFilters);
        Page<T> contents = ((AbstractTypeRepository)repository).findAll
            (query, createPageDescription(offset, max, sortBy, sortDesc) );

        return new PagedEntityList<>(contents.getContent(), contents.getTotalElements(), offset, max,
            sortBy, sortDesc, userFilters, getListFilterFieldOptions());
    }

    public PagedEntityList<T> getUnmatchedEntityList(Integer offset, Integer max, String sortBy, boolean sortDesc) {
        Sort.Direction direction = sortDesc ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sortBy == null ? "id" : sortBy; // id should always be present, use as default sort

        AbstractTypeRepository<T, Long> repo = (AbstractTypeRepository<T, Long>) repository;
        List<T> all = repo.findUnmatched(Sort.by(direction, sortField));
        List<T> contents = all.stream().skip(offset).limit(max).collect(Collectors.toList());

        return new PagedEntityList<>(contents, all.size(), offset, max, sortBy, sortDesc, "unmatched");
    }

    @Override
    public PagedEntityList<T> getUnmatchedEntityList(Integer offset, Integer max, String sortBy, boolean sortDesc, String unmatchedBy) {
        return null;
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
        T created = repository.save(instance);
        log.info("{} med id {} [{}] skapad av {}", getEntityName(), getFieldValue("id", created), created, user);
        return created;
    }

    public T update(T instance, String user) {
        setMetadata(instance, user);
        T updated = repository.save(instance);
        log.info("{} med id {} [{}] uppdaterad av {}", getEntityName(), getFieldValue("id", updated), updated, user);
        return updated;
    }

    public T rollback(T instance, String user) {
        setMetadata(instance, user);
        instance.setPubVersion(null);
        T rolledBack = repository.save(instance);
        log.info("{} med id {} [{}] back av {}", getEntityName(), getFieldValue("id", rolledBack), rolledBack, user);
        return rolledBack;
    }

    /**
     * Simple straight-through save.
     */
    public T save(T instance) {
        return repository.save(instance);
    }

    /**
     * Delete from database if not published, otherwise just set deleted flag.
     * Returns true on success, false if delete constraints are not met.
     */
    public boolean delete(Long id, String user) {
        Optional<T> opt = repository.findById(id);
        if (!opt.isPresent()) throw new IllegalArgumentException(String.format("%s med id %d hittades ej", getEntityName(), id));
        T instance = opt.get();
        if (!isUserAllowedToDelete(instance, user)) return false;
        if (instance.isPublished()) {
            setMetadata(instance, user);
            // null is used to allow only one deleted=false and many deleted posts
            instance.setDeleted(null);
            repository.save(instance);
            log.info("{} med id{} [{}] markerad borttagen av {}", getEntityName(), getFieldValue("id", instance), instance, user);
        }
        else {
            repository.delete(instance);
            log.info("{} med id{} [{}] borttagen av {}", getEntityName(), getFieldValue("id", instance), instance, user);
        }
        return true;
    }

    public boolean isUserAllowedToDelete(T instance, String user) {
        List<AbstractVersionInfo> deps = getEntityDependencies(instance);
        if (deps == null) return true;
        // Anything depending on this instance must be marked deleted,
        // and if not yet published it must be deleted by the same user
        for (AbstractVersionInfo d : deps) {
            if (!d.isDeletedInPublishedVersionOrByUser(user)) return false;
        }
        return true;
    }

    protected void setMetadata(T instance, String user) {
        instance.setUpdatedBy(user);
        instance.setUpdatedTime(new Date());
    }


}
