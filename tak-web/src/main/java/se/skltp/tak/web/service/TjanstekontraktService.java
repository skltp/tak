package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.TjanstekontraktRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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


    @Override
    protected List<AbstractVersionInfo> getEntityDependencies(Tjanstekontrakt entity) {
        List<AbstractVersionInfo> deps = new ArrayList<>();
        deps.addAll(entity.getVagval());
        deps.addAll(entity.getAnropsbehorigheter());
        return deps;
    }

    @Override
    public long getId(Tjanstekontrakt entity) {
        return entity == null ? 0 : entity.getId();
    }


    public Tjanstekontrakt getTjanstekontraktByNamnrymd(String namnrymd) {
        return ((TjanstekontraktRepository)repository).findFirstByNamnrymdAndDeleted(namnrymd, false);
    }

    public boolean hasDuplicate(Tjanstekontrakt tk) {
        if (tk == null) return false;
        Tjanstekontrakt match = ((TjanstekontraktRepository)repository)
                .findFirstByNamnrymdAndDeleted(tk.getNamnrymd(), tk.getDeleted());
        return match != null && match.getId() != tk.getId();
    }

    @Override
    public PagedEntityList<Tjanstekontrakt> getUnmatchedEntityList(
            Integer offset,
            Integer max,
            String sortBy,
            boolean sortDesc,
            String unmatchedBy) {
        Sort.Direction direction = sortDesc ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = sortBy == null ? "id" : sortBy; // use id as default sort

        if (unmatchedBy.equals("Vagval")) {
            List<Tjanstekontrakt> l = ((TjanstekontraktRepository) repository).findUnmatchedByVagval(Sort.by(direction, sortField));
            List<Tjanstekontrakt> contents = l.stream().skip(offset).limit(max).collect(Collectors.toList());
            return new PagedEntityList<>(contents, l.size(), offset, max, sortBy, sortDesc, "unmatchedByVagval");
        } else if (unmatchedBy.equals("Anropsbehorighet")) {
            List<Tjanstekontrakt> l = ((TjanstekontraktRepository) repository).findUnmatchedByAnropsbehorighet(Sort.by(direction, sortField));
            List<Tjanstekontrakt> contents = l.stream().skip(offset).limit(max).collect(Collectors.toList());
            return new PagedEntityList<>(contents, l.size(), offset, max, sortBy, sortDesc, "unmatchedByAnropsbehorighet");
        }
        List<Tjanstekontrakt> l = ((TjanstekontraktRepository) repository).findUnmatched(Sort.by(direction, sortField));
        List<Tjanstekontrakt> contents = l.stream().skip(offset).limit(max).collect(Collectors.toList());
        return new PagedEntityList<>(contents, l.size(), offset, max, sortBy, sortDesc, "unmatched");
    }
}
