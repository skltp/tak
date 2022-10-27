package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.web.repository.VagvalRepository;

import java.sql.Date;
import java.util.List;
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

    public List<Vagval> getVagval(String logiskAdress, String tjanstekontrakt, Date fromDate, Date toDate) {
        List<Vagval> vagvalList = ((VagvalRepository)repository).getVagval(logiskAdress, tjanstekontrakt);

        return vagvalList.stream()
                .filter(v -> ! (v.getFromTidpunkt().after(toDate) || v.getTomTidpunkt().before(fromDate)))
                .collect(Collectors.toList());
    }
}
