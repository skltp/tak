package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.web.repository.VagvalRepository;

import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VagvalService extends EntityServiceBase<Vagval>{

    @Autowired
    VagvalService(VagvalRepository repository) {
        super(repository);
    }

    @Override
    public String getEntityName() {
        return "Vägval";
    }

    @Override
    public Vagval createEntity() {
        return new Vagval();
    }

    @Override
    public Map<String, String> getListFilterFieldOptions() {
        Map<String, String> options = new LinkedHashMap<>();
        options.put("rivTaProfil", "RIV-TA-profil");
        options.put("tjanstekontrakt", "Tjänstekontrakt");
        options.put("logiskAdress", "Logisk adress");
        options.put("anropsAdress", "Anropsadress");
        return options;
    }

    @Override
    public String getFieldValue(String fieldName, Vagval entity) {
        switch (fieldName) {
            case "rivTaProfil":
                return entity.getAnropsAdress().getRivTaProfil().getNamn();
            case "tjanstekontrakt":
                return entity.getTjanstekontrakt().getNamnrymd();
            case "logiskAdress":
                return entity.getLogiskAdress().getHsaId();
            case "anropsAdress":
                return entity.getAnropsAdress().getAdress();
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    protected List<AbstractVersionInfo> getEntityDependencies(Vagval entity) {
        return null;
    }

    @Override
    public long getId(Vagval entity) {
        return entity == null ? 0 : entity.getId();
    }

    public List<Vagval> getVagval(String logiskAdress, String tjanstekontrakt, Date fromDate, Date toDate) {
        List<Vagval> vagvalList = ((VagvalRepository)repository).findMatchingNonDeleted(logiskAdress, tjanstekontrakt);

        return vagvalList.stream()
                .filter(v -> ! (v.getFromTidpunkt().after(toDate) || v.getTomTidpunkt().before(fromDate)))
                .collect(Collectors.toList());
    }

    public boolean hasOverlappingDuplicate(Vagval v) {
        if (v == null || v.getLogiskAdress() == null || v.getTjanstekontrakt() == null) return false;
        List<Vagval> candidates = ((VagvalRepository)repository)
                .findMatchingNonDeleted(v.getLogiskAdress().getId(), v.getTjanstekontrakt().getId());
        for (Vagval c : candidates) {
            if (c.getId() != v.getId() && timeSpansOverlap(v,c)) return true;
        }
        return false;
    }

    private boolean timeSpansOverlap(Vagval v1, Vagval v2) {
        if (v1.getTomTidpunkt().before(v2.getFromTidpunkt())) return false; // a1 before a2
        if (v1.getFromTidpunkt().after(v2.getTomTidpunkt())) return false; // a1 after a2
        return true; // else overlapping
    }
}
