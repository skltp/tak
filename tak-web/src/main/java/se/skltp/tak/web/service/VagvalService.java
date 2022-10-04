package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.web.repository.VagvalRepository;

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
}
