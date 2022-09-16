package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.web.repository.TjanstekomponentRepository;

@Service
public class TjanstekomponentService extends EntityServiceBase<Tjanstekomponent> {

    @Autowired
    TjanstekomponentService(TjanstekomponentRepository repository) {
        super(repository);
    }

    @Override
    public String getEntityName() { return "Tjänstekomponent"; }

    @Override
    public Tjanstekomponent createEntity() {
        return new Tjanstekomponent();
    }
}
