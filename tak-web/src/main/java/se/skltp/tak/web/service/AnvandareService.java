package se.skltp.tak.web.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.entity.Anvandare;
import se.skltp.tak.web.repository.AnvandareRepository;

import java.util.List;
import java.util.Optional;

@Service
public class AnvandareService {

    private final AnvandareRepository repository;

    PasswordEncoder passwordEncoder;


    @Autowired
    public AnvandareService(AnvandareRepository repository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public PagedEntityList<Anvandare> getEntityList(Integer offset, Integer max) {
        List<Anvandare> contents = repository.findAll().stream()
                .skip(offset)
                .limit(max).toList();
        long total = repository.count();
        return new PagedEntityList<>(contents, (int) total, offset, max);
    }

    public Optional<Anvandare> findById(Long id) {
        return repository.findById(id);
    }

    public Anvandare add(Anvandare instance) {
        if (instance == null || StringUtils.isBlank(instance.getLosenord())) {
            throw new IllegalArgumentException("Lösenord saknas");
        }
        instance.setLosenordHash(passwordEncoder.encode(instance.getLosenord()));

        return repository.save(instance);
    }

    public Anvandare update(Anvandare instance) {
        if (instance == null) throw new IllegalArgumentException("Användare saknas");

        Anvandare current = repository.findById(instance.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Användare med id %d saknas", instance.getId())));

        current.setAnvandarnamn(instance.getAnvandarnamn());
        current.setAdministrator(instance.getAdministrator());
        if (StringUtils.isNoneBlank((instance.getLosenord()))) {
            current.setLosenordHash(passwordEncoder.encode(instance.getLosenord()));
        }

        return repository.save(current);
    }

    // Ta bort en användare baserat på ID och version
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
