package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.entity.Anvandare;
import se.skltp.tak.web.repository.AnvandareRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AnvandareService {

    AnvandareRepository repository;

    @Autowired
    public AnvandareService(AnvandareRepository repository) {
        this.repository = repository;
    }

    public Anvandare getAnvandareByUsername(String username) {
        return repository.findFirstByAnvandarnamn(username);
    }

    public PagedEntityList<Anvandare> getEntityList(Integer offset, Integer max) {
        List<Anvandare> contents = repository.findAll().stream()
                .skip(offset)
                .limit(max)
                .collect(Collectors.toList());
        long total = repository.count();
        return new PagedEntityList<>(contents, (int) total, offset, max);
    }

    public Optional<Anvandare> findById(Long id) {
        return repository.findById(id);
    }

    public Anvandare add(Anvandare instance) {
        return repository.save(instance);
    }

    public Anvandare update(Anvandare instance) {
        return repository.save(instance);
    }

    public boolean delete(Long id, Long version) {
        Optional<Anvandare> opt = repository.findById(id);
        if (opt.isPresent()) {
            Anvandare instance = opt.get();
            if (instance.getVersion() != version) return false;
            repository.delete(instance);
            return true;
        }
        return false;
    }
}
