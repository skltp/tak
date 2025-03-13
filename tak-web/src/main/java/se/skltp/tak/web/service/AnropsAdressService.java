package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.web.repository.AnropsAdressRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import se.skltp.tak.web.repository.QueryGenerator;

@Service
public class AnropsAdressService extends EntityServiceBase<AnropsAdress>{

  @Autowired
  AnropsAdressService(AnropsAdressRepository repository, QueryGenerator<AnropsAdress> queryGenerator) {
    super(repository, queryGenerator);
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
    options.put("tjanstekomponent.hsaId", "Tjänstekomponent");
    options.put("rivTaProfil", "RIV-TA-profil");
    return options;
  }

  @Override
  public long getId(AnropsAdress entity) {
    return entity == null ? 0 : entity.getId();
  }

  @Override
  protected List<AbstractVersionInfo> getEntityDependencies(AnropsAdress entity) {
    List<AbstractVersionInfo> deps = new ArrayList<>();
    deps.addAll(entity.getVagVal());
    return deps;
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
