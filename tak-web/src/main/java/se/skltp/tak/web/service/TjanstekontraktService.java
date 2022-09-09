package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.TjanstekontraktRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TjanstekontraktService implements EntityService<Tjanstekontrakt> {

    @Autowired
    TjanstekontraktService(TjanstekontraktRepository repository) {
        this.repository = repository;
    }

    TjanstekontraktRepository repository;

    public PagedEntityList<Tjanstekontrakt> getEntityList(int offset, int max) {
        List<Tjanstekontrakt> contents = repository.findAll().stream()
                .filter(f -> !f.isDeletedInPublishedVersion())
                .skip(offset)
                .limit(max)
                .collect(Collectors.toList());
        long total = repository.findAll().stream()
                .filter(f -> !f.isDeletedInPublishedVersion())
                .count();
        return new PagedEntityList<>(contents, (int) total, offset, max);
    }

    public Optional<Tjanstekontrakt> findById(long id) { return repository.findById(id); }

    public Tjanstekontrakt add(Tjanstekontrakt instance, String user) {
        setMetadata(instance, user);
        return repository.save(instance);
    }

    public Tjanstekontrakt update(Tjanstekontrakt instance, String user) {
        setMetadata(instance, user);
        return repository.save(instance);
    }

    public boolean delete(Long id, String user) {
        Optional<Tjanstekontrakt> opt = repository.findById(id);
        if (opt.isPresent()) {
            Tjanstekontrakt instance = opt.get();
            setMetadata(instance, user);
            instance.setDeleted(true);
            repository.save(instance);
            return true;
        }
        return false;
    }

    private void setMetadata(Tjanstekontrakt instance, String user) {
        instance.setUpdatedBy(user);
        instance.setUpdatedTime(new Date());
    }
}
