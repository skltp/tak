package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.LogiskAdressRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            String sortBy,
            boolean sortDesc,
            String unmatchedBy) {
        Sort.Direction direction = sortDesc ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sortBy == null ? "id" : sortBy; // use id as default sort

        if (unmatchedBy.equals("Vagval")) {
            List<LogiskAdress> l = ((LogiskAdressRepository) repository).findUnmatchedByVagval(Sort.by(direction, sortField));
            List<LogiskAdress> contents = l.stream().skip(offset).limit(max).collect(Collectors.toList());
            return new PagedEntityList<>(contents, l.size(), offset, max, sortBy, sortDesc, "unmatchedByVagval");
        } else if (unmatchedBy.equals("Anropsbehorighet")) {
            List<LogiskAdress> l = ((LogiskAdressRepository) repository).findUnmatchedByAnropsbehorighet(Sort.by(direction, sortField));
            List<LogiskAdress> contents = l.stream().skip(offset).limit(max).collect(Collectors.toList());
            return new PagedEntityList<>(contents, l.size(), offset, max, sortBy, sortDesc, "unmatchedByAnropsbehorighet");
        }
        List<LogiskAdress> l = ((LogiskAdressRepository) repository).findUnmatched(Sort.by(direction, sortField));
        List<LogiskAdress> contents = l.stream().skip(offset).limit(max).collect(Collectors.toList());
        return new PagedEntityList<>(contents, l.size(), offset, max, sortBy, sortDesc, "unmatched");
    }
}
