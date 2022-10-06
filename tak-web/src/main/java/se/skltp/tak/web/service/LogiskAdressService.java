package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.web.repository.LogiskAdressRepository;

@Service
public class LogiskAdressService extends EntityServiceBase<LogiskAdress>{

  @Autowired
  LogiskAdressService(LogiskAdressRepository repository) {
    super(repository);
  }

  @Override
  public String getEntityName() {
    return "Logisk Adress";
  }

  @Override
  public LogiskAdress createEntity() {
    return new LogiskAdress();
  }
}
