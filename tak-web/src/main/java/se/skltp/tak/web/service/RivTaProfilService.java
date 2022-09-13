package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.RivTaProfilRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RivTaProfilService implements EntityService<RivTaProfil> {

    @Autowired
    RivTaProfilService(RivTaProfilRepository repository) {
        this.repository = repository;
    }

    RivTaProfilRepository repository;

    public PagedEntityList<RivTaProfil> getEntityList(int offset, int max) {
        List<RivTaProfil> contents = repository.findAll().stream()
                .filter(f -> !f.isDeletedInPublishedVersion())
                .skip(offset)
                .limit(max)
                .collect(Collectors.toList());
        long total = repository.findAll().stream()
                .filter(f -> !f.isDeletedInPublishedVersion())
                .count();
        return new PagedEntityList<>(contents, (int) total, offset, max);
    }

    public Optional<RivTaProfil> findById(long id) { return repository.findById(id); }

    public RivTaProfil add(RivTaProfil instance, String user) {
        setMetadata(instance, user);
        return repository.save(instance);
    }

    public RivTaProfil update(RivTaProfil instance, String user) {
        setMetadata(instance, user);
        return repository.save(instance);
    }

    public boolean delete(Long id, String user) {
        Optional<RivTaProfil> opt = repository.findById(id);
        if (opt.isPresent()) {
            RivTaProfil instance = opt.get();
            setMetadata(instance, user);
            instance.setDeleted(true);
            repository.save(instance);
            return true;
        }
        return false;
    }

    public String getEntityName() {
        return "RIV-TA-profil";
    }

    private void setMetadata(RivTaProfil instance, String user) {
        instance.setUpdatedBy(user);
        instance.setUpdatedTime(new Date());
    }
}
