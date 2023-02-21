package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.web.repository.TjanstekomponentRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class TjanstekomponentService extends EntityServiceBase<Tjanstekomponent> {

    @Autowired
    TjanstekomponentService(TjanstekomponentRepository repository) {
        super(repository);
    }

    @Override
    public String getEntityName() { return "Tjänstekomponent"; }

    @Override
    public Tjanstekomponent createEntity() {
        return new Tjanstekomponent();
    }

    @Override
    public Map<String, String> getListFilterFieldOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("hsaId", "HSA-id");
        options.put("beskrivning", "Beskrivning");
        return options;
    }

    @Override
    public String getFieldValue(String fieldName, Tjanstekomponent entity) {
        switch (fieldName) {
            case "hsaId":
                return entity.getHsaId();
            case "beskrivning":
                return entity.getBeskrivning();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    protected List<AbstractVersionInfo> getEntityDependencies(Tjanstekomponent entity) {
        List<AbstractVersionInfo> deps = new ArrayList<>();
        deps.addAll(entity.getAnropsAdresser());
        deps.addAll(entity.getAnropsbehorigheter());
        return deps;
    }

    @Override
    public long getId(Tjanstekomponent entity) {
        return entity == null ? 0 : entity.getId();
    }

    public Tjanstekomponent getTjanstekomponentByHSAId(String hsaId) {
        return ((TjanstekomponentRepository)repository).findFirstByHsaIdAndDeleted(hsaId, false);
    }

    public boolean hasDuplicate(Tjanstekomponent t) {
        if (t == null) return false;
        Tjanstekomponent match = ((TjanstekomponentRepository)repository)
                .findFirstByHsaIdAndDeleted(t.getHsaId(), t.getDeleted());
        return match != null && match.getId() != t.getId();
    }
}
