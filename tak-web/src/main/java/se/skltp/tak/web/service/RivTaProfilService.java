package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.repository.RivTaProfilRepository;

@Service
public class RivTaProfilService extends EntityServiceBase<RivTaProfil> {

    @Autowired
    RivTaProfilService(RivTaProfilRepository repository) {
        super(repository);
    }

    @Override
    public String getEntityName() { return "RIV-TA-profil"; }
}
