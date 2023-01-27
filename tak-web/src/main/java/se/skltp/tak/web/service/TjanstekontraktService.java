package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.web.repository.TjanstekontraktRepository;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TjanstekontraktService extends EntityServiceBase<Tjanstekontrakt> {

    @Autowired
    TjanstekontraktService(TjanstekontraktRepository repository) {
        super(repository);
    }

    @Override
    public String getEntityName() {
        return "Tjänstekontrakt";
    }

    @Override
    public Tjanstekontrakt createEntity() {
        return new Tjanstekontrakt();
    }

    public Map<String, String> getListFilterFieldOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("namnrymd", "Namnrymd");
        options.put("majorVersion", "Major version");
        options.put("minorVersion", "Minor version");
        options.put("beskrivning", "Beskrivning");
        return options;
    }

    public String getFieldValue(String fieldName, Tjanstekontrakt entity) {
        switch (fieldName) {
            case "namnrymd":
                return entity.getNamnrymd();
            case "majorVersion":
                return String.valueOf(entity.getMajorVersion());
            case "minorVersion":
                return String.valueOf(entity.getMinorVersion());
            case "beskrivning":
                return entity.getBeskrivning();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public long getId(Tjanstekontrakt entity) {
        return entity == null ? 0 : entity.getId();
    }

    public Tjanstekontrakt getTjanstekontraktByNamnrymd(String namnrymd) {
        return ((TjanstekontraktRepository)repository).findFirstByNamnrymd(namnrymd);
    }

}
