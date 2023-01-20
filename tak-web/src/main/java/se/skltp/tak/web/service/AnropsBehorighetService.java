package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.web.repository.AnropsBehorighetRepository;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AnropsBehorighetService extends EntityServiceBase<Anropsbehorighet> {

    @Autowired
    AnropsBehorighetService(AnropsBehorighetRepository repository) {
        super(repository);
    }

    @Override
    public String getEntityName() {
        return "Anropsbehörighet";
    }

    @Override
    public Anropsbehorighet createEntity() {
        return new Anropsbehorighet();
    }

    @Override
    public Map<String, String> getListFilterFieldOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("integrationsavtal", "Integrationsavtal");
        options.put("tjanstekonsument", "Tjänstekonsument");
        options.put("tjanstekontrakt", "Tjänstekontrakt");
        options.put("logiskAdress", "Logisk adress");
        options.put("anropsAdress", "Anropsadress");
        return options;
    }

    @Override
    public String getFieldValue(String fieldName, Anropsbehorighet entity) {
        switch (fieldName) {
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
            if (timeSpansOverlap(a,c)) return true;
        }
        return false;
    }

    private boolean timeSpansOverlap(Anropsbehorighet a1, Anropsbehorighet a2) {
        if (a1.getTomTidpunkt().before(a2.getFromTidpunkt())) return false; // a1 before a2
        if (a1.getFromTidpunkt().after(a2.getTomTidpunkt())) return false; // a1 after a2
        return true; // else overlapping
    }
}
