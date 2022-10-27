package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import java.util.List;

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
    List<AnropsAdress> match = ((AnropsAdressRepository)repository).findMatchingNonDeleted(rivtaprofil, tjanstekomponent, adress);
    return match.isEmpty() ? null : match.get(0);
  }
}
