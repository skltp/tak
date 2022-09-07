package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.repository.RivTaProfilRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RivTaProfilService {

    @Autowired
    RivTaProfilService(RivTaProfilRepository repository) {
        this.repository = repository;
    }

    RivTaProfilRepository repository;

    public List<RivTaProfil> findNotDeleted() {
        return repository.findByDeleted(false);
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

    private void setMetadata(RivTaProfil instance, String user) {
        instance.setUpdatedBy(user);
        instance.setUpdatedTime(new Date());
    }
}
