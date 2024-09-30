package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.web.repository.AnropsBehorighetRepository;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import se.skltp.tak.web.repository.QueryGenerator;

@Service
public class AnropsBehorighetService extends EntityServiceBase<Anropsbehorighet> {

    @Autowired
    AnropsBehorighetService(AnropsBehorighetRepository repository, QueryGenerator<Anropsbehorighet> queryGenerator) {
        super(repository, queryGenerator);
    }

    @Override
    public String getEntityName() {
        return "Anropsbehörighet";
    }

    @Override
    public Anropsbehorighet createEntity() {
        // Set sensible default dates
        Anropsbehorighet ab = new Anropsbehorighet();
        LocalDate today = LocalDate.now();
        LocalDate muchLater = today.plusYears(100);
        ab.setFromTidpunkt(Date.valueOf(today));
        ab.setTomTidpunkt(Date.valueOf(muchLater));
        return ab;
    }

    @Override
    public Map<String, String> getListFilterFieldOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("integrationsavtal", "Integrationsavtal");
        options.put("tjanstekonsument.hsaId", "Tjänstekonsument");
        options.put("tjanstekontrakt.namnrymd", "Tjänstekontrakt");
        options.put("logiskAdress.hsaId", "Logisk adress");
        return options;
    }

    @Override
    public String getFieldValue(String fieldName, Anropsbehorighet entity) {
        switch (fieldName) {
            case "id":
                return String.valueOf(entity.getId());
            case "integrationsavtal":
                return entity.getIntegrationsavtal();
            case "tjanstekonsument":
                return entity.getTjanstekonsument().getHsaId();
            case "tjanstekontrakt":
                return entity.getTjanstekontrakt().getNamnrymd();
            case "logiskAdress":
                return entity.getLogiskAdress().getHsaId();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    protected List<AbstractVersionInfo> getEntityDependencies(Anropsbehorighet entity) {
        List<AbstractVersionInfo> deps = new ArrayList<>();
        deps.addAll(entity.getFilter());
        return deps;
    }

    @Override
    public long getId(Anropsbehorighet entity) {
        return entity == null ? 0 : entity.getId();
    }

    public List<Anropsbehorighet> getAnropsbehorighet(String logiskAdress, String tjanstekonsument,
                                                      String tjanstekontrakt, Date fromDate, Date toDate) {
        List<Anropsbehorighet> anropsbehorighetList = ((AnropsBehorighetRepository)repository).findMatchingNonDeleted(logiskAdress,
                tjanstekonsument, tjanstekontrakt);

        return anropsbehorighetList.stream()
                .filter(ab -> ! (ab.getFromTidpunkt().after(toDate) || ab.getTomTidpunkt().before(fromDate)))
                .collect(Collectors.toList());
    }

    public Anropsbehorighet getAnropsbehorighet(long logiskAdressId, long tjanstekonsumentId, long tjanstekontraktId) {
        return ((AnropsBehorighetRepository)repository)
                .findFirstNonDeletedByIds(logiskAdressId, tjanstekonsumentId, tjanstekontraktId);
    }

    public boolean hasOverlappingDuplicate(Anropsbehorighet a) {
        if (a.getLogiskAdress() == null || a.getTjanstekontrakt() == null || a.getTjanstekonsument() == null) return false;
        List<Anropsbehorighet> candidates = ((AnropsBehorighetRepository)repository)
                .findMatchingNonDeleted(a.getLogiskAdress().getId(), a.getTjanstekonsument().getId(), a.getTjanstekontrakt().getId());
        for (Anropsbehorighet c : candidates) {
            if (c.getId() != a.getId() && timeSpansOverlap(a,c)) return true;
        }
        return false;
    }

    private boolean timeSpansOverlap(Anropsbehorighet a1, Anropsbehorighet a2) {
        if (a1.getTomTidpunkt().before(a2.getFromTidpunkt())) return false; // a1 before a2
        if (a1.getFromTidpunkt().after(a2.getTomTidpunkt())) return false; // a1 after a2
        return true; // else overlapping
    }
}
