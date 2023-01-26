package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.repository.RivTaProfilRepository;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class RivTaProfilService extends EntityServiceBase<RivTaProfil> {

    @Autowired
    RivTaProfilService(RivTaProfilRepository repository) {
        super(repository);
    }

    @Override
    public String getEntityName() { return "RIV-TA-profil"; }

    @Override
    public RivTaProfil createEntity() {
        return new RivTaProfil();
    }

    @Override
    public Map<String, String> getListFilterFieldOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("namn", "Namn");
        options.put("beskrivning", "Beskrivning");
        return options;
    }

    @Override
    public String getFieldValue(String fieldName, RivTaProfil entity) {
        switch (fieldName) {
            case "namn":
                return entity.getNamn();
            case "beskrivning":
                return entity.getBeskrivning();
            default:
                throw new IllegalArgumentException();
        }
    }

    public RivTaProfil getRivTaProfilByNamn(String namn) {
        return ((RivTaProfilRepository)repository).findFirstByNamnAndDeleted(namn, false);
    }
}
