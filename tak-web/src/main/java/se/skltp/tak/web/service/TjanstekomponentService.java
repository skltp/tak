package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.web.repository.TjanstekomponentRepository;

import java.util.LinkedHashMap;
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

    public Tjanstekomponent getTjanstekomponentByHSAId(String hsaId) {
        return ((TjanstekomponentRepository)repository).findFirstByHsaIdAndDeleted(hsaId, false);
    }
}
