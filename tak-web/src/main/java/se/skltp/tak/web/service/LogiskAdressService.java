package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.web.repository.LogiskAdressRepository;
import se.skltp.tak.web.repository.TjanstekomponentRepository;

import java.util.List;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class LogiskAdressService extends EntityServiceBase<LogiskAdress>{

    @Autowired
    LogiskAdressService(LogiskAdressRepository repository) {
        super(repository);
    }

    @Override
    public String getEntityName() {
      return "Logisk Adress";
    }

    @Override
    public LogiskAdress createEntity() {
      return new LogiskAdress();
    }

    @Override
    public Map<String, String> getListFilterFieldOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("hsaId", "HSA-id");
        options.put("beskrivning", "Beskrivning");
        return options;
    }

    @Override
    public String getFieldValue(String fieldName, LogiskAdress entity) {
        switch (fieldName) {
            case "hsaId":
                return entity.getHsaId();
            case "beskrivning":
                return entity.getBeskrivning();
            default:
                throw new IllegalArgumentException();
        }
    }

    public LogiskAdress getLogiskAdressByHSAId(String hsaId) {
        return ((LogiskAdressRepository)repository).findFirstByHsaIdAndDeleted(hsaId, false);
    }

    public List<LogiskAdress> findAllNotDeleted() {
        return ((LogiskAdressRepository)repository).findByDeletedFalse();
    }
}
