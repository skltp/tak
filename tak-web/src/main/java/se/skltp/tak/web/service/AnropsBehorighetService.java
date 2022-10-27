package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.web.repository.AnropsBehorighetRepository;

import java.sql.Date;
import java.util.List;
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

    public List<Anropsbehorighet> getAnropsbehorighet(String logiskAdress, String tjanstekonsument,
                                                      String tjanstekontrakt, Date fromDate, Date toDate) {
        List<Anropsbehorighet> anropsbehorighetList = ((AnropsBehorighetRepository)repository).getAnropsbehorighet(logiskAdress,
                tjanstekonsument, tjanstekontrakt);

        return anropsbehorighetList.stream()
                .filter(ab -> ! (ab.getFromTidpunkt().after(toDate) || ab.getTomTidpunkt().before(fromDate)))
                .collect(Collectors.toList());
    }
}
