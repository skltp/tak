package se.skltp.tak.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se.skltp.tak.web.entity.Anvandare;
import se.skltp.tak.web.repository.AnvandareRepository;

@Service
public class AnvandareService {

    @Autowired
    AnvandareRepository repository;

    public Anvandare getAnvandareByUsername(String username) {
        return repository.findFirstByAnvandarnamn(username);
    }
}
