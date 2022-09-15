package se.skltp.tak.web.service;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.web.dto.PagedEntityList;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public abstract class EntityServiceBase<T extends AbstractVersionInfo> implements EntityService<T> {

    protected JpaRepository<T, Long> repository;

    EntityServiceBase(JpaRepository<T, Long> repository) {
        this.repository = repository;
    }

    public abstract String getEntityName();

    public PagedEntityList<T> getEntityList(int offset, int max) {
        List<T> contents = repository.findAll().stream()
                .filter(f -> !f.isDeletedInPublishedVersion())
                .skip(offset)
                .limit(max)
                .collect(Collectors.toList());
        long total = repository.findAll().stream()
                .filter(f -> !f.isDeletedInPublishedVersion())
                .count();
        return new PagedEntityList<T>(contents, (int) total, offset, max);
    }

    public Optional<T> findById(long id) { return repository.findById(id); }

    public T add(T instance, String user) {
        setMetadata(instance, user);
        return repository.save(instance);
    }

    public T update(T instance, String user) {
        setMetadata(instance, user);
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
}
