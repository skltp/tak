package se.skltp.tak.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import se.skltp.tak.core.entity.AnropsAdress;

import java.util.List;

public interface AnropsAdressRepository extends JpaRepository<AnropsAdress, Long> {

    @Query("select aa from AnropsAdress aa " +
           "where aa.deleted=FALSE and aa.adress =:adress and aa.rivTaProfil.namn=:rivta " +
           "and aa.rivTaProfil.deleted=FALSE and aa.tjanstekomponent.hsaId=:komponent " +
           "and aa.tjanstekomponent.deleted=FALSE")
    List<AnropsAdress> getAnropsAdress(String rivta, String komponent, String adress);
}
