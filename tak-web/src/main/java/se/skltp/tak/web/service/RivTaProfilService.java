package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.repository.RivTaProfilRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RivTaProfilService {

    @Autowired
    RivTaProfilRepository repository;

    public List<RivTaProfil> findAll() {
        return repository.findAll();
    }

    public Optional<RivTaProfil> findById(long id) { return repository.findById(id); }
}
