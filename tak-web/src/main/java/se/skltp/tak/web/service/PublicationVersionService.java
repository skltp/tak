package se.skltp.tak.web.service;

import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.PublicationVersionRepository;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PublicationVersionService{

  int currentFormatVersion = 1;

  @Autowired
  PublicationVersionRepository repository;

  @Autowired
  public PublicationVersionService(PublicationVersionRepository repository) {
    this.repository = repository;
  }

  public PagedEntityList<PubVersion> getEntityList(Integer offset, Integer max) {
    List<PubVersion> contents = repository.findAll().stream()
        .skip(offset)
        .limit(max)
        .collect(Collectors.toList());
    long total = repository.count();
    return new PagedEntityList<>(contents, (int) total, offset, max);
  }

  public Optional<PubVersion> findById(Long id) {
    return repository.findById(id);
  }
  public PubVersion add(PubVersion instance, String username) {
    instance.setFormatVersion(currentFormatVersion);
    instance.setTime(new Date(System.currentTimeMillis()));
    instance.setUtforare(username);

    // TODO: GET CURRENT-MOST COMPLETE PV-INSTANCE FROM DB

    // TODO: MERGE NEW PV ON TOP OF OLD COMPLETE-PV

    //TODO: return repository.save(instance); //SAVE

    return instance;
  }
  public PubVersion update(PubVersion instance) {
    return repository.save(instance);
  }
}
