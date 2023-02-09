package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.PubVersion;
import se.skltp.tak.web.dto.PagedEntityList;
import se.skltp.tak.web.repository.PublicationVersionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PublicationVersionService{

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

  public PubVersion update(PubVersion instance) {
    return repository.save(instance);
  }
}
