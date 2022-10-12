package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AnropsAdress;

@Service
public class AnropsAdressService extends EntityServiceBase<AnropsAdress>{

  @Autowired
  AnropsAdressService(JpaRepository<AnropsAdress, Long> repository) {
    super(repository);
  }

  @Override
  public String getEntityName() {
    return "Anropsadress";
  }

  @Override
  public AnropsAdress createEntity() {
    return new AnropsAdress();
  }
}
