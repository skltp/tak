package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.web.dto.ListFilter;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.LogiskAdressRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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

    @Override
    protected List<AbstractVersionInfo> getEntityDependencies(LogiskAdress entity) {
        List<AbstractVersionInfo> deps = new ArrayList<>();
        deps.addAll(entity.getVagval());
        deps.addAll(entity.getAnropsbehorigheter());
        return deps;
    }

    @Override
    public long getId(LogiskAdress entity) {
        return entity == null ? 0 : entity.getId();
    }

    public LogiskAdress getLogiskAdressByHSAId(String hsaId) {
        return ((LogiskAdressRepository)repository).findFirstByHsaIdAndDeleted(hsaId, false);
    }

    public boolean hasDuplicate(LogiskAdress la) {
        if (la == null) return false;
        LogiskAdress match = ((LogiskAdressRepository)repository)
                .findFirstByHsaIdAndDeleted(la.getHsaId(), la.getDeleted());
        return match != null && match.getId() != la.getId();
    }

    @Override
    public PagedEntityList<LogiskAdress> getUnmatchedEntityList(
            Integer offset,
            Integer max,
            List<ListFilter> filters,
            String sortBy,
            boolean sortDesc,
            String unmatchedBy) {
        if (unmatchedBy.equals("Vagval")) {
            List<LogiskAdress> l = ((LogiskAdressRepository) repository).findUnmatchedByVagval();
            return new PagedEntityList<>(l, l.size(), offset, max, "Diskrepanser");
        } else if (unmatchedBy.equals("Anropsbehorighet")) {
            List<LogiskAdress> l = ((LogiskAdressRepository) repository).findUnmatchedByAnropsbehorighet();
            return new PagedEntityList<>(l, l.size(), offset, max, "Diskrepanser");
        }
        List<LogiskAdress> l = ((LogiskAdressRepository) repository).findUnmatched();
        return new PagedEntityList<>(l, l.size(), offset, max, "Diskrepanser");
    }
}
