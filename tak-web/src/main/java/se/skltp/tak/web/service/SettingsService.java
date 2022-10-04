package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.entity.Anvandare;
import se.skltp.tak.web.entity.TAKSettings;
import se.skltp.tak.web.repository.AnvandareRepository;
import se.skltp.tak.web.repository.SettingsRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SettingsService {

    SettingsRepository repository;

    @Autowired
    public SettingsService(SettingsRepository repository) {
        this.repository = repository;
    }

    public PagedEntityList<TAKSettings> getEntityList(Integer offset, Integer max) {
        List<TAKSettings> contents = repository.findAll().stream()
                .skip(offset)
                .limit(max)
                .collect(Collectors.toList());
        long total = repository.count();
        return new PagedEntityList<>(contents, (int) total, offset, max);
    }

    public Optional<TAKSettings> findById(Long id) {
        return repository.findById(id);
    }

    public TAKSettings add(TAKSettings instance) {
        return repository.save(instance);
    }

    public TAKSettings update(TAKSettings instance) {
        return repository.save(instance);
    }

    public boolean delete(Long id, Long version) {
        Optional<TAKSettings> opt = repository.findById(id);
        if (opt.isPresent()) {
            TAKSettings instance = opt.get();
            if (instance.getVersion() != version) return false;
            repository.delete(instance);
            return true;
        }
        return false;
    }
}
