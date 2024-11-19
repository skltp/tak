package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.web.repository.QueryGenerator;
import se.skltp.tak.web.repository.RivTaProfilRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RivTaProfilService extends EntityServiceBase<RivTaProfil> {

    @Autowired
    RivTaProfilService(RivTaProfilRepository repository, QueryGenerator<RivTaProfil> queryGenerator) {
        super(repository, queryGenerator);
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
    protected List<AbstractVersionInfo> getEntityDependencies(RivTaProfil entity) {
        List<AbstractVersionInfo> deps = new ArrayList<>();
        deps.addAll(entity.getAnropsAdresser());
        return deps;
    }

    @Override
    public long getId(RivTaProfil entity) {
        return entity == null ? 0 : entity.getId();
    }

    public RivTaProfil getRivTaProfilByNamn(String namn) {
        return ((RivTaProfilRepository)repository).findFirstByNamnAndDeleted(namn, false);
    }

    public boolean hasDuplicate(RivTaProfil r) {
        if (r == null) return false;
        RivTaProfil match = ((RivTaProfilRepository)repository)
                .findFirstByNamnAndDeleted(r.getNamn(), r.getDeleted());
        return match != null && match.getId() != r.getId();
    }
}
