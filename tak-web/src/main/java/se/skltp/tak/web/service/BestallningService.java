package se.skltp.tak.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.core.entity.LogiskAdress;
import se.skltp.tak.core.entity.RivTaProfil;
import se.skltp.tak.core.entity.Tjanstekomponent;
import se.skltp.tak.core.entity.Tjanstekontrakt;
import se.skltp.tak.core.entity.Vagval;
import se.skltp.tak.web.dto.bestallning.AnropsbehorighetBestallning;
import se.skltp.tak.web.dto.bestallning.BestallningsAvsnitt;
import se.skltp.tak.web.dto.bestallning.BestallningsData;
import se.skltp.tak.web.dto.bestallning.JsonBestallning;
import se.skltp.tak.web.dto.bestallning.LogiskadressBestallning;
import se.skltp.tak.web.dto.bestallning.TjanstekomponentBestallning;
import se.skltp.tak.web.dto.bestallning.TjanstekontraktBestallning;
import se.skltp.tak.web.dto.bestallning.VagvalBestallning;
import se.skltp.tak.web.validator.BestallningsDataValidator;

@Service
@Transactional
public class BestallningService {
    private static final Logger log = LoggerFactory.getLogger(BestallningService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired AnropsAdressService anropsAdressService;
    @Autowired AnropsBehorighetService anropsBehorighetService;
    @Autowired LogiskAdressService logiskAdressService;
    @Autowired RivTaProfilService rivTaProfilService;
    @Autowired TjanstekomponentService tjanstekomponentService;
    @Autowired TjanstekontraktService tjanstekontraktService;
    @Autowired VagvalService vagvalService;
    @Autowired ConfigurationService configurationService;
    @Autowired AlerterService alerterService;
    @Autowired BestallningsDataValidator bestallningsDataValidator;

    @PersistenceContext
    private EntityManager entityManager;

    public String parseAndFormatJson(String jsonInput) throws JsonProcessingException {
        JsonBestallning bestallning = buildJsonBestallning(jsonInput);
        String formattedBestallning = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bestallning);
        log.info("JsonBestallning: \n {} ", formattedBestallning);
        checkMandatoryInfo(bestallning);
        return formattedBestallning;
    }

    public BestallningsData buildBestallningsData(String jsonInput, String userName)
        throws JsonProcessingException {
        JsonBestallning jsonBestallning = buildJsonBestallning(jsonInput);
        BestallningsData data = new BestallningsData(jsonBestallning);
        if (!data.hasErrors()) checkOrderPlatform(data);
        if (!data.hasErrors()) preparePlainObjects(data);
        if (!data.hasErrors()) prepareComplexObjectsRelations(data, userName);
        if (!data.hasErrors()) prepareComplexObjects(data);
        if (!data.hasErrors()) updateHsaIdUpperCase(data);
        if (!data.hasErrors()) prepareBestallningsRapport(data);
        return data;
    }

    private JsonBestallning buildJsonBestallning(String jsonInput)
        throws IllegalArgumentException, JsonProcessingException {
        if (jsonInput == null || !jsonInput.contains("{") || !jsonInput.contains("}")) {
            throw new IllegalArgumentException("Beställning does not contain JSON object");
        }
        String trimmed = jsonInput.substring(jsonInput.indexOf("{"), jsonInput.lastIndexOf("}") + 1);
        return mapper.readValue(trimmed, JsonBestallning.class);
    }

    public void execute(BestallningsData data, String userName) {
        if (!data.hasErrors()) {
            for (LogiskAdress la : data.getAllLogiskAdresser()) logiskAdressService.add(la, userName);
            for (Tjanstekomponent tk : data.getAllTjanstekomponent()) tjanstekomponentService.add(tk, userName);
            for (Tjanstekontrakt tk : data.getAllTjanstekontrakt()) tjanstekontraktService.add(tk, userName);
            for (AnropsAdress aa : data.getAllAnropsAdress()) anropsAdressService.update(aa, userName);
            for (Anropsbehorighet ab : data.getAllaAnropsbehorighet()) anropsBehorighetService.update(ab, userName);

            for (BestallningsData.VagvalPair vvp : data.getAllaVagval()) {
                if (vvp.getOldVagval() != null) vagvalService.update(vvp.getOldVagval(), userName);
                if (vvp.getNewVagval() != null) vagvalService.add(vvp.getNewVagval(), userName);
            }

            for (Tjanstekontrakt tk : data.getAllTjanstekontrakt()) {
                alerterService.alertOnNewContract(tk.getNamnrymd(), data.getFromDate());
            }
        }
    }

    private void checkMandatoryInfo(JsonBestallning json) {
        if (json.getPlattform() == null) {
            throw new IllegalArgumentException("Bestallning is missing mandatory field: plattform");
        }
    }

    private void checkOrderPlatform(BestallningsData data) {
        String activePlatform = configurationService.getPlatform();
        if (activePlatform != null && !activePlatform.equals(data.getOrderPlatform())) {
            String errMsg = "Beställningen avser plattform %s, men den här instansen av tak-web hanterar %s.";
            data.addError(String.format(errMsg, data.getOrderPlatform(), activePlatform));
        }
    }

    private void preparePlainObjects(BestallningsData data) {
        validateRawDataIntegrity(data);
        JsonBestallning bestallning = data.getBestallning();

        BestallningsAvsnitt exkludera = bestallning.getExkludera();
        //NTP-2291 Via jsonBeställning, plain objects(Tjanstekontrakt, Tjanstekomponent,Logiskadress)
        // can't be deleted
        deleteFromBestallningAllExcludedPlainObjects(exkludera);

        for (VagvalBestallning vagvalBestallning : exkludera.getVagval()) {
            prepareVagvalForDelete(vagvalBestallning, data);
        }
        for (AnropsbehorighetBestallning anropsbehorighetBestallning : exkludera.getAnropsbehorigheter()) {
            prepareAnropsbehorighetForDelete(anropsbehorighetBestallning, data);
        }
        /*   Möjligheten att ta bort objekt via JSON-beställning har kommenterats bort enligt NTP-2291

        for (LogiskadressBestallning logiskadressBestallning1 : exkludera.getLogiskadresser()) {
            prepareLogiskAdressForDelete(logiskadressBestallning1, data);
        }
        for (TjanstekomponentBestallning tjanstekomponentBestallning1 : exkludera.getTjanstekomponenter()) {
            prepareTjanstekomponentForDelete(tjanstekomponentBestallning1, data);
        }
        for (TjanstekontraktBestallning tjanstekontraktBestallning1 : exkludera.getTjanstekontrakt()) {
            prepareTjanstekontraktForDelete(tjanstekontraktBestallning1, data);
        } */


        BestallningsAvsnitt inkludera = bestallning.getInkludera();
        //NTP-2291 Via jsonBeställning, plain objects(Tjanstekontrakt, Tjanstekomponent,Logiskadress)
        // can't be changed
        deleteFromBestallningAllExistingIncludedPlainObjects(inkludera);

        for (LogiskadressBestallning logiskadressBestallning : inkludera.getLogiskadresser()) {
            prepareLogiskAdress(logiskadressBestallning, data);
        }
        for (TjanstekomponentBestallning tjanstekomponentBestallning : inkludera.getTjanstekomponenter()) {
            prepareTjanstekomponent(tjanstekomponentBestallning, data);
        }
        for (TjanstekontraktBestallning tjanstekontraktBestallning : inkludera.getTjanstekontrakt()) {
            prepareTjanstekontrakt(tjanstekontraktBestallning, data);
        }
    }

    private void deleteFromBestallningAllExcludedPlainObjects(BestallningsAvsnitt excludera) {
        excludera.getTjanstekontrakt().clear();
        excludera.getTjanstekomponenter().clear();
        excludera.getLogiskadresser().clear();
    }

    private void deleteFromBestallningAllExistingIncludedPlainObjects(BestallningsAvsnitt inkludera){
        inkludera.getTjanstekontrakt()
            .removeIf(tjanstekontrakt -> tjanstekontraktService.existsByNamnrymd(tjanstekontrakt.getNamnrymd()));
        inkludera.getTjanstekomponenter()
            .removeIf(tjanstekomponent -> tjanstekomponentService.existsByHsaId(tjanstekomponent.getHsaId()));
        inkludera.getLogiskadresser()
            .removeIf(logiskadress -> logiskAdressService.existsByHsaId(logiskadress.getHsaId()));
    }

    void validateRawDataIntegrity(BestallningsData data) {
        data.addError(bestallningsDataValidator.validateDataDubletter(data.getBestallning()));
    }

    private void prepareComplexObjectsRelations(BestallningsData data, String userName) {
        JsonBestallning bestallning = data.getBestallning();

        Set<String> vagvalIncludeErrors = bestallningsDataValidator.validateHasRequiredFields(
                bestallning.getInkludera().getVagval(),
                "Det saknas information i beställningen för att kunna skapa Vägval.",
                VagvalBestallning::hasRequiredFieldsForInclude);
        data.addError(vagvalIncludeErrors);

        Set<String> vagvalExcludeErrors = bestallningsDataValidator.validateHasRequiredFields(
                bestallning.getExkludera().getVagval(),
                "Det saknas information i beställningen för att kunna radera Vägval.",
                VagvalBestallning::hasRequiredFieldsForExclude);
        data.addError(vagvalExcludeErrors);

        Set<String> anropsbehorighetIncludeErrors = bestallningsDataValidator.validateHasRequiredFields(
                bestallning.getInkludera().getAnropsbehorigheter(),
                "Det saknas information i beställningen för att kunna skapa Anropsbehörighet.",
                AnropsbehorighetBestallning::hasRequiredFields);
        data.addError(anropsbehorighetIncludeErrors);

        Set<String> anropsbehorighetExcludeErrors = bestallningsDataValidator.validateHasRequiredFields(
                bestallning.getExkludera().getAnropsbehorigheter(),
                "Det saknas information i beställningen för att kunna radera Anropsbehörighet.",
                AnropsbehorighetBestallning::hasRequiredFields);
        data.addError(anropsbehorighetExcludeErrors);

        if (data.hasErrors()) {
            // Om något av värdena som kontrolleras ovan är null kan det resultera i exceptions
            // vid fortsätt kontroll, så returnera här utan att leta efter fler fel.
            return;
        }

        for (AnropsbehorighetBestallning abBestallning : bestallning.getInkludera().getAnropsbehorigheter()) {
            prepareAnropsbehorighetRelations(abBestallning, data, userName);
        }

        for (VagvalBestallning vagvalBestallning : bestallning.getInkludera().getVagval()) {
            prepareVagvalRelations(vagvalBestallning, data, userName);
        }
    }

    private void prepareAnropsbehorighetForDelete(AnropsbehorighetBestallning anropsbehorighetBestallning, BestallningsData data) {
        List<Anropsbehorighet> list = anropsBehorighetService.getAnropsbehorighet(anropsbehorighetBestallning.getLogiskAdress(),
                anropsbehorighetBestallning.getTjanstekonsument(), anropsbehorighetBestallning.getTjanstekontrakt(), data.getFromDate(), data.getToDate());

        Set<String> error = bestallningsDataValidator.validateAnropsbehorighetForDubblett(list);
        if (!error.isEmpty()) {
            data.addError(error);
            return;
        }

        if (!list.isEmpty()) {
            Anropsbehorighet ab = list.get(0);
            //Detach the anropsbehorighet from the persistence context to avoid saving it when the transaction completes
            entityManager.detach(ab);

            if (data.getFromDate().before(ab.getFromTidpunkt())) {
                ab.setDeleted(null);
            } else {
                ab.setTomTidpunkt(generateDateMinusDag(data.getFromDate()));
            }
            data.put(anropsbehorighetBestallning, ab);
        }
    }


    private void prepareVagvalForDelete(VagvalBestallning bestallning, BestallningsData data) {
        List<Vagval> list = vagvalService.getVagval(bestallning.getLogiskAdress(), bestallning.getTjanstekontrakt(), data.getFromDate(), data.getToDate());

        Set<String> error = bestallningsDataValidator.validateVagvalForDubblett(list);
        if (!error.isEmpty()) {
            data.addError(error);
            return;
        }

        if (!list.isEmpty()) {
            Vagval existingVagval = list.get(0);
            //Detach the vagval from the persistence context to avoid saving it when the transaction completes
            entityManager.detach(existingVagval);

            data.putOldVagval(bestallning, existingVagval);
            deactivateVagval(existingVagval, data);
        }
    }

    private void prepareTjanstekontrakt(TjanstekontraktBestallning bestallning, BestallningsData data) {
        Tjanstekontrakt tjanstekontrakt = new Tjanstekontrakt();
        tjanstekontrakt.setNamnrymd(bestallning.getNamnrymd());
        tjanstekontrakt.setBeskrivning(bestallning.getBeskrivning());
        tjanstekontrakt.setMajorVersion(bestallning.getMajorVersion());
        Set<String> error = bestallningsDataValidator.validate(tjanstekontrakt);
        if (error.isEmpty()) {
            data.put(bestallning, tjanstekontrakt);
        } else {
            data.addError(error);
        }
    }

    private void prepareTjanstekomponent(TjanstekomponentBestallning bestallning,
        BestallningsData data) {
        Tjanstekomponent tjanstekomponent = new Tjanstekomponent();
        tjanstekomponent.setHsaId(bestallning.getHsaId());
        tjanstekomponent.setBeskrivning(bestallning.getBeskrivning());
        Set<String> error = bestallningsDataValidator.validate(tjanstekomponent);
        if (error.isEmpty()) {
            data.put(bestallning, tjanstekomponent);
        } else {
            data.addError(error);
         }
    }

    private void prepareLogiskAdress(LogiskadressBestallning bestallning, BestallningsData data) {
        LogiskAdress logiskAdress = new LogiskAdress();
        logiskAdress.setHsaId(bestallning.getHsaId());
        logiskAdress.setBeskrivning(bestallning.getBeskrivning());
        Set<String> error = bestallningsDataValidator.validate(logiskAdress);
        if (error.isEmpty()) {
            data.put(bestallning, logiskAdress);
        } else {
            data.addError(error);
        }
    }


    private void prepareVagvalRelations(VagvalBestallning vagvalBestallning, BestallningsData data, String userName) {
        RivTaProfil rivTaProfil = findRivtaInDB(vagvalBestallning.getRivtaprofil());
        Tjanstekomponent tjanstekomponent = findTjanstekomponentInDBorOrder(vagvalBestallning.getTjanstekomponent(), data);
        LogiskAdress logiskAdress = findLogiskAdressInDBorOrder(vagvalBestallning.getLogiskAdress(), data);
        Tjanstekontrakt tjanstekontrakt = findTjanstekontraktInDBorOrder(vagvalBestallning.getTjanstekontrakt(), data);

        Set<String> errors = bestallningsDataValidator.validateRelatedObjects(vagvalBestallning, rivTaProfil, logiskAdress,
                tjanstekomponent, tjanstekontrakt, userName);
        if (!errors.isEmpty()) {
            data.addError(errors);
            return;
        }

        AnropsAdress adress = findOrCreateAnropsAdress(vagvalBestallning, data, rivTaProfil, tjanstekomponent);
        errors = bestallningsDataValidator.validateAnropAddress(adress, userName);
        if (!errors.isEmpty()) {
            data.addError(errors);
            return;
        }

        data.putRelations(vagvalBestallning, adress, logiskAdress, tjanstekontrakt);
    }

    private void prepareAnropsbehorighetRelations(AnropsbehorighetBestallning abBestallning, BestallningsData data, String userName) {
        Tjanstekomponent tjanstekonsument = findTjanstekomponentInDBorOrder(abBestallning.getTjanstekonsument(), data);
        LogiskAdress logiskAdress = findLogiskAdressInDBorOrder(abBestallning.getLogiskAdress(), data);
        Tjanstekontrakt tjanstekontrakt = findTjanstekontraktInDBorOrder(abBestallning.getTjanstekontrakt(), data);

        Set<String> errors = bestallningsDataValidator.validateRelatedObjects(abBestallning, logiskAdress, tjanstekonsument,
                tjanstekontrakt, userName);
        if (!errors.isEmpty()) {
            data.addError(errors);
            return;
        }

        data.putRelations(abBestallning, logiskAdress, tjanstekonsument, tjanstekontrakt);
    }

    private LogiskAdress findLogiskAdressInDBorOrder(String hsaId, BestallningsData data) {
        LogiskAdress existLogiskAdress = logiskAdressService.getLogiskAdressByHSAId(hsaId);
        if (existLogiskAdress == null) {
            existLogiskAdress = data.getLogiskAdress(hsaId);

        }
        return existLogiskAdress;
    }

    private Tjanstekomponent findTjanstekomponentInDBorOrder(String hsaId, BestallningsData data) {
        Tjanstekomponent existTjanstekomponent = tjanstekomponentService.getTjanstekomponentByHSAId(hsaId);
        if (existTjanstekomponent == null) {
            existTjanstekomponent = data.getTjanstekomponent(hsaId);
        }
        return existTjanstekomponent;
    }

    private Tjanstekontrakt findTjanstekontraktInDBorOrder(String namnrymd, BestallningsData data) {
        Tjanstekontrakt existTjanstekontrakt = tjanstekontraktService.getTjanstekontraktByNamnrymd(namnrymd);
        if (existTjanstekontrakt == null) {
            existTjanstekontrakt = data.getTjanstekontrakt(namnrymd);
        }
        return existTjanstekontrakt;
    }

    private RivTaProfil findRivtaInDB(String rivta) {
        return rivTaProfilService.getRivTaProfilByNamn(rivta);
    }


    private void prepareComplexObjects(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning();

        for (AnropsbehorighetBestallning abBestallning : bestallning.getInkludera().getAnropsbehorigheter()) {
            prepareAnropsbehorighet(abBestallning, data, data.getFromDate(), data.getToDate());
        }

        for (VagvalBestallning vvBestallning : bestallning.getInkludera().getVagval()) {
            prepareVagval(vvBestallning, data, data.getFromDate(), data.getToDate());
        }
    }

    private void prepareAnropsbehorighet(AnropsbehorighetBestallning bestallning, BestallningsData data, Date from, Date tom) {
        List<Anropsbehorighet> anropsbehorighetList = anropsBehorighetService.getAnropsbehorighet(bestallning.getLogiskAdress(), bestallning.getTjanstekonsument(), bestallning.getTjanstekontrakt(), from, tom);
        Set<String> error = bestallningsDataValidator.validateAnropsbehorighetForDubblett(anropsbehorighetList);
        if (!error.isEmpty()) {
            data.addError(error);
            return;
        }

        if (anropsbehorighetList.isEmpty()) {
            BestallningsData.AnropsBehorighetRelations abData = data.getAnropsbehorighetRelations(bestallning);
            Anropsbehorighet ab = createAnropsbehorighet(abData.getLogiskadress(), abData.getTjanstekontrakt(), abData.getTjanstekomponent(), from, tom);
            data.put(bestallning, ab);
        } else if (anropsbehorighetList.size() == 1) {
            Anropsbehorighet existingAnropsbehorighet = anropsbehorighetList.get(0);
            //Detach the Anropsbehorighet from the persistence context to avoid saving it when the transaction completes
            entityManager.detach(existingAnropsbehorighet);

            if (from.before(existingAnropsbehorighet.getFromTidpunkt())) {
                existingAnropsbehorighet.setFromTidpunkt(from);
                existingAnropsbehorighet.setTomTidpunkt(tom);
            } else {
                existingAnropsbehorighet.setTomTidpunkt(tom);
            }
            data.put(bestallning, existingAnropsbehorighet);
        }
    }

    private void prepareVagval(VagvalBestallning bestallning, BestallningsData data, Date from, Date tom) {
        List<Vagval> vagvalList = vagvalService.getVagval(bestallning.getLogiskAdress(), bestallning.getTjanstekontrakt(), from, tom);
        Set<String> error = bestallningsDataValidator.validateVagvalForDubblett(vagvalList);
        if (!error.isEmpty()) {
            data.addError(error);
            return;
        }

        BestallningsData.VagvalRelations newVagvalData = data.getVagvalRelations(bestallning);
        if (vagvalList.isEmpty()) {
            Vagval newVagval = createVagval(newVagvalData.getLogiskAdress(), newVagvalData.getTjanstekontrakt(), newVagvalData.getAnropsAdress(), from, tom);
            data.putNewVagval(bestallning, newVagval);
            return;
        }

        Vagval existingVagval = vagvalList.get(0);
        //Detach the vagval from the persistence context to avoid saving it when the transaction completes
        entityManager.detach(existingVagval);

        //samma vägval
        if (existingVagval.getAnropsAdress().getRivTaProfil() == newVagvalData.getAnropsAdress().getRivTaProfil() &&
                existingVagval.getAnropsAdress().getTjanstekomponent() == newVagvalData.getAnropsAdress().getTjanstekomponent() &&
                existingVagval.getAnropsAdress().getAdress().equals(bestallning.getAdress())) {
            if (from.before(existingVagval.getFromTidpunkt())) {
                existingVagval.setFromTidpunkt(from);
                existingVagval.setTomTidpunkt(tom);
            } else {
                existingVagval.setTomTidpunkt(tom);
            }
        } else { //vägval med annan anropsAdress
            Vagval newVagval = createVagval(newVagvalData.getLogiskAdress(), newVagvalData.getTjanstekontrakt(), newVagvalData.getAnropsAdress(), from, tom);
            data.putNewVagval(bestallning, newVagval);

            deactivateVagval(existingVagval, data);
        }
        data.putOldVagval(bestallning, existingVagval);
    }

    private void deactivateVagval(Vagval existingVagval, BestallningsData data){
        if (data.getFromDate().before(existingVagval.getFromTidpunkt())) {
            existingVagval.setDeleted(null);
            if (existingVagval.getAnropsAdress().getVagVal().size() == 1 ||
                existingVagval.getAnropsAdress().getVagVal().stream().allMatch(AbstractVersionInfo::getDeleted))  {
                existingVagval.getAnropsAdress().setDeleted(null);
                data.putAnropsAdress(existingVagval.getAnropsAdress());
            }
        } else {
            existingVagval.setTomTidpunkt(generateDateMinusDag(data.getFromDate()));
        }
    }

    private AnropsAdress findOrCreateAnropsAdress(VagvalBestallning bestallning, BestallningsData data, RivTaProfil profil, Tjanstekomponent tjanstekomponent) {
        AnropsAdress adress = data.getAnropsAdress(profil, tjanstekomponent, bestallning.getAdress());

        if (adress == null) {
            adress = anropsAdressService.getAnropsAdress(bestallning.getRivtaprofil(), bestallning.getTjanstekomponent(), bestallning.getAdress());
            if (adress == null) {
                adress = new AnropsAdress();
                adress.setAdress(bestallning.getAdress());
                adress.setRivTaProfil(profil);
                adress.setTjanstekomponent(tjanstekomponent);
            }
            data.putAnropsAdress(adress);
        }
        return adress;
    }

    private Anropsbehorighet createAnropsbehorighet(LogiskAdress logiskAdress, Tjanstekontrakt tjanstekontrakt, Tjanstekomponent tjanstekomponent, Date from, Date tom) {
        Anropsbehorighet anropsbehorighet = new Anropsbehorighet();
        anropsbehorighet.setLogiskAdress(logiskAdress);
        anropsbehorighet.setTjanstekontrakt(tjanstekontrakt);
        anropsbehorighet.setTjanstekonsument(tjanstekomponent);
        anropsbehorighet.setIntegrationsavtal("AUTOTAKNING");
        anropsbehorighet.setFromTidpunkt(from);
        anropsbehorighet.setTomTidpunkt(tom);
        return anropsbehorighet;
    }

    private Vagval createVagval(LogiskAdress logiskAdress, Tjanstekontrakt tjanstekontrakt, AnropsAdress anropsAdress, Date from, Date tom) {
        Vagval vagval = new Vagval();
        vagval.setLogiskAdress(logiskAdress);
        vagval.setTjanstekontrakt(tjanstekontrakt);
        vagval.setAnropsAdress(anropsAdress);
        vagval.setFromTidpunkt(from);
        vagval.setTomTidpunkt(tom);
        return vagval;
    }


    private Date generateDateMinusDag(Date date) {
        if (date == null) return null;

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, -1);
        return new Date(c.getTime().getTime());
    }

    // Json orders may contain lower case letters in HsaId
    // Values stored in database should be upper case
    private void updateHsaIdUpperCase(BestallningsData data)
    {
        data.getAllLogiskAdresser().forEach(it -> it.setHsaId(it.getHsaId().toUpperCase()));
        data.getAllTjanstekomponent().forEach(it -> it.setHsaId(it.getHsaId().toUpperCase()));
    }

    private void prepareBestallningsRapport(BestallningsData data) {
        data.buildBestallningsRapport();
    }

    /*   Möjligheten att ta bort objekt via JSON-beställning har kommenterats bort enligt NTP-2291

    private void prepareLogiskAdressForDelete(LogiskadressBestallning logiskadressBestallning, BestallningsData data) {
        LogiskAdress logiskAdress = logiskAdressService.getLogiskAdressByHSAId(logiskadressBestallning.getHsaId());

        if (logiskAdress != null) {
            logiskAdress.setDeleted(null);
            Set<String> error = bestallningsDataValidator.validateLogiskAdressRelationsForDelete(logiskAdress);
            if (error.isEmpty()) {
                data.put(logiskadressBestallning, logiskAdress);
            } else {
                data.addError(error);
            }
            //Detach the logiskAdress from the persistence context to avoid saving it when the transaction completes
            entityManager.detach(logiskAdress);
        }
    }

    private void prepareTjanstekomponentForDelete(TjanstekomponentBestallning tjanstekomponentBestallning, BestallningsData data) {
        Tjanstekomponent tjanstekomponent = tjanstekomponentService.getTjanstekomponentByHSAId(tjanstekomponentBestallning.getHsaId());

        if (tjanstekomponent != null) {
            tjanstekomponent.setDeleted(null);
            Set<String> error = bestallningsDataValidator.validateTjanstekomponentRelationsForDelete(tjanstekomponent);
            if (error.isEmpty()) {
                data.put(tjanstekomponentBestallning, tjanstekomponent);
            } else {
                data.addError(error);
            }
            //Detach the tjanstekomponent from the persistence context to avoid saving it when the transaction completes
            entityManager.detach(tjanstekomponent);
        }
    }

    private void prepareTjanstekontraktForDelete(TjanstekontraktBestallning tjanstekontraktBestallning, BestallningsData data) {
        Tjanstekontrakt tjanstekontrakt = tjanstekontraktService.getTjanstekontraktByNamnrymd(tjanstekontraktBestallning.getNamnrymd());
        if (tjanstekontrakt != null) {
            tjanstekontrakt.setDeleted(null);
            Set<String> error = bestallningsDataValidator.validateTjanstekontraktForDelete(tjanstekontrakt);
            if (error.isEmpty()) {
                data.put(tjanstekontraktBestallning, tjanstekontrakt);
            } else {
                data.addError(error);
            }
            //Detach the tjanstekontrakt from the persistence context to avoid saving it when the transaction completes
            entityManager.detach(tjanstekontrakt);
        }
    }
*/


}
