package se.skltp.tak.web.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.entity.Locktb;
import se.skltp.tak.web.repository.LockRepository;


@Service
public class LockService {
    static final Logger log = LoggerFactory.getLogger(LockService.class);
    LockRepository repository;

    @Autowired
    public LockService(LockRepository repository) {
        this.repository = repository;
    }

    public Locktb retrieveLock() {
        Locktb locktb = findOrCreateLock("PubVersion");
        if(locktb.getLocked() == 1) {
            String msg = "Publish failed. A publication job is already running";
            log.error(msg);
            throw new IllegalStateException(msg);
        }
        locktb.setLocked(1);
        return repository.save(locktb);
    }

    public void releaseLock(Locktb locktb) {
        locktb.setLocked(0);
        repository.save(locktb);
    }

    private Locktb findOrCreateLock(String tableName)
    {
        Locktb locktb = repository.findFirstById(tableName);
        if(locktb == null) {
            locktb = new Locktb();
            locktb.setId("PubVersion");
            locktb.setLocked(0);
        }
        return locktb;
    }
}
