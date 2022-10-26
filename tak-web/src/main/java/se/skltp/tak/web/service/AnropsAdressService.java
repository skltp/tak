package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.web.repository.AnropsAdressRepository;

@Service
public class AnropsAdressService extends EntityServiceBase<AnropsAdress>{

  @Autowired
  AnropsAdressService(AnropsAdressRepository repository) {
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

  public AnropsAdress getAnropsAdress(String rivtaprofil, String tjanstekomponent, String adress) {
    // TODO: Fix issue with H2 database
    //return ((AnropsAdressRepository)repository).getAnropsAdress(rivtaprofil, tjanstekomponent, adress);
    return null;
  }
}
