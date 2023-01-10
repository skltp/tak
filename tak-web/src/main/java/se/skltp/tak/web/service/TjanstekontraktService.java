package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.web.repository.TjanstekontraktRepository;

import java.util.LinkedHashMap;
import java.util.List;
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
        options.put("minorVersion", "Minor version");
        options.put("majorVersion", "Major version");
        return options;
    }

    public List<Tjanstekontrakt> findAllNotDeleted() {
        return ((TjanstekontraktRepository)repository).findByDeletedFalse();
    }

    public Tjanstekontrakt getTjanstekontraktByNamnrymd(String namnrymd) {
        return ((TjanstekontraktRepository)repository).findFirstByNamnrymd(namnrymd);
    }
}
