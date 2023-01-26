package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

  @Override
  public Map<String, String> getListFilterFieldOptions() {
    Map<String, String> options = new LinkedHashMap<>();
    options.put("adress", "Adress");
    options.put("tjanstekomponent", "Tjänstekomponent");
    options.put("rivTaProfil", "RIV-TA-profil");
    return options;
  }

  @Override
  public String getFieldValue(String fieldName, AnropsAdress entity) {
    switch (fieldName) {
      case "adress":
        return entity.getAdress();
      case "tjanstekomponent":
        return entity.getTjanstekomponent().getHsaId();
      case "rivTaProfil":
        return entity.getRivTaProfil().getNamn();
      default:
        throw new IllegalArgumentException();
    }
  }

  public AnropsAdress getAnropsAdress(String rivtaprofil, String tjanstekomponent, String adress) {
    List<AnropsAdress> match = ((AnropsAdressRepository)repository).findMatchingNonDeleted(rivtaprofil, tjanstekomponent, adress);
    return match.isEmpty() ? null : match.get(0);
  }

  public boolean hasDuplicate(AnropsAdress a) {
    if (a == null || a.getAdress() == null || a.getRivTaProfil() == null || a.getTjanstekomponent() == null) return false;
    AnropsAdress match = ((AnropsAdressRepository)repository)
            .findUnique(a.getRivTaProfil().getId(), a.getTjanstekomponent().getId(), a.getAdress(), a.getDeleted());

    return match != null && match.getId() != a.getId();
  }
}
