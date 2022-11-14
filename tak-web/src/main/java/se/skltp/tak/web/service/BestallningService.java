package se.skltp.tak.web.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.dto.bestallning.*;
import se.skltp.tak.web.util.BestallningsDataValidator;

import java.sql.Date;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

@Service
public class BestallningService {
    private static final Logger log = LoggerFactory.getLogger(BestallningService.class);
    private final ObjectMapper mapper = new ObjectMapper();

    private final AnropsAdressService anropsAdressService;
    private final AnropsBehorighetService anropsBehorighetService;
    private final LogiskAdressService logiskAdressService;
    private final RivTaProfilService rivTaProfilService;
    private final TjanstekomponentService tjanstekomponentService;
    private final TjanstekontraktService tjanstekontraktService;
    private final VagvalService vagvalService;
    private final BestallningsDataValidator bestallningsDataValidator;

    public BestallningService(AnropsAdressService anropsAdressService, AnropsBehorighetService anropsBehorighetService,
                              LogiskAdressService logiskAdressService, RivTaProfilService rivTaProfilService,
                              TjanstekomponentService tjanstekomponentService, TjanstekontraktService tjanstekontraktService,
                              VagvalService vagvalService, BestallningsDataValidator bestallningsDataValidator) {
        this.anropsAdressService = anropsAdressService;
        this.anropsBehorighetService = anropsBehorighetService;
        this.logiskAdressService = logiskAdressService;
        this.rivTaProfilService = rivTaProfilService;
        this.tjanstekomponentService = tjanstekomponentService;
        this.tjanstekontraktService = tjanstekontraktService;
        this.vagvalService = vagvalService;
        this.bestallningsDataValidator = bestallningsDataValidator;
    }

    public String parseAndFormatJson(String jsonInput) throws Exception {
        JsonBestallning bestallning = buildJsonBestallning(jsonInput);
        String formattedBestallning = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bestallning);
        log.info("JsonBestallning: \n " + formattedBestallning);
        checkMandatoryInfo(bestallning);
        return formattedBestallning;
    }

    public BestallningsData buildBestallningsData(String jsonInput, String userName) throws Exception {
        JsonBestallning jsonBestallning = buildJsonBestallning(jsonInput);
        BestallningsData data = new BestallningsData(jsonBestallning);
        if (!data.hasErrors()) preparePlainObjects(data);
        if (!data.hasErrors()) prepareComplexObjectsRelations(data, userName);
        if (!data.hasErrors()) prepareComplexObjects(data);
        if (!data.hasErrors()) updateHsaIdUpperCase(data);
        if (!data.hasErrors()) prepareBestallningsRapport(data);
        return data;
    }

    private JsonBestallning buildJsonBestallning(String jsonInput) throws Exception {
        if (jsonInput == null || !jsonInput.contains("{") || !jsonInput.contains("}")) {
            throw new IllegalArgumentException("Bestallning does not contain JSON object");
        }
        String trimmed = jsonInput.substring(jsonInput.indexOf("{"), jsonInput.lastIndexOf("}") + 1);
        return mapper.readValue(trimmed, JsonBestallning.class);
    }

    public void execute(BestallningsData data, String userName) {
        if (!data.hasErrors()) {
            data.getAllLogiskAdresser().forEach(it -> {
                logiskAdressService.update(it, userName);
            });

            data.getAllTjanstekomponent().forEach(it -> {
                tjanstekomponentService.update(it, userName);
            });

            data.getAllTjanstekontrakt().forEach(it -> {
                tjanstekontraktService.update(it, userName);
            });

            data.getAllAnropsAdress().forEach(it -> {
                anropsAdressService.update(it, userName);
            });

            data.getAllaVagval().forEach(it -> {
                if (it.getOldVagval() != null) {
                    vagvalService.update(it.getOldVagval(), userName);
                }
                if (it.getNewVagval() != null) {
                    vagvalService.update(it.getNewVagval(), userName);
                }
            });

            data.getAllaAnropsbehorighet().forEach(it -> {
                anropsBehorighetService.update(it, userName);
            });

            // TODO: Är detta relevant innan publicering?
            //data.getAllTjanstekontrakt().each {
            //    sendMailAboutNewTjanstekontrakt(it.namnrymd, data.fromDate);
            //}
        }
    }

    private void checkMandatoryInfo(JsonBestallning json) {
        if (json.getPlattform() == null) {
            throw new IllegalArgumentException("Bestallning is missing mandatory field: plattform");
        }
    }

    private void preparePlainObjects(BestallningsData data) {
        validateRawDataIntegrity(data);
        JsonBestallning bestallning = data.getBestallning();

        //if (grailsApplication.config.tak.platform && grailsApplication.config.tak.platform != bestallning.plattform) {
        //    String errMsg = i18nService.message(code:'bestallning.error.bad_platform')
        //    errMsg = errMsg.replace('{platformAllowed}', grailsApplication.config.tak.platform)
        //    errMsg = errMsg.replace('{platformRequested}', bestallning.plattform)
        //    data.addError(errMsg)
        //}

        BestallningsAvsnitt exkludera = bestallning.getExkludera();
        for (VagvalBestallning vagvalBestallning : exkludera.getVagval()) {
            prepareVagvalForDelete(vagvalBestallning, data);
        }
        for (AnropsbehorighetBestallning anropsbehorighetBestallning : exkludera.getAnropsbehorigheter()) {
            prepareAnropsbehorighetForDelete(anropsbehorighetBestallning, data);
        }
        for (LogiskadressBestallning logiskadressBestallning1 : exkludera.getLogiskadresser()) {
            prepareLogiskAdressForDelete(logiskadressBestallning1, data);
        }
        for (TjanstekomponentBestallning tjanstekomponentBestallning1 : exkludera.getTjanstekomponenter()) {
            prepareTjanstekomponentForDelete(tjanstekomponentBestallning1, data);
        }
        for (TjanstekontraktBestallning tjanstekontraktBestallning1 : exkludera.getTjanstekontrakt()) {
            prepareTjanstekontraktForDelete(tjanstekontraktBestallning1, data);
        }

        BestallningsAvsnitt inkludera = bestallning.getInkludera();
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

        Set<String> problem = bestallningsDataValidator.validateExists(list, anropsbehorighetBestallning);
        if (problem.isEmpty()) {
            Anropsbehorighet ab = list.get(0);
            ab.getFilter().size(); //TODO: ???
            if (data.getFromDate().before(ab.getFromTidpunkt())) {
                ab.setDeleted(null);
            } else {
                ab.setTomTidpunkt(generateDateMinusDag(data.getFromDate()));
            }
            data.put(anropsbehorighetBestallning, list.get(0));
        }
    }


    private void prepareVagvalForDelete(VagvalBestallning bestallning, BestallningsData data) {
        List<Vagval> list = vagvalService.getVagval(bestallning.getLogiskAdress(), bestallning.getTjanstekontrakt(), data.getFromDate(), data.getToDate());

        Set<String> error = bestallningsDataValidator.validateVagvalForDubblett(list);
        if (!error.isEmpty()) {
            data.addError(error);
            return;
        }

        Set<String> problem = bestallningsDataValidator.validateExists(list, bestallning);
        if (problem.isEmpty()) {
            Vagval existingVagval = list.get(0);
            existingVagval.getAnropsAdress().getVagVal().size(); //TODO: ???
            data.putOldVagval(bestallning, existingVagval);

            deactivateVagval(existingVagval, data);
        }
    }


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
        }
    }


    private void prepareTjanstekontrakt(TjanstekontraktBestallning tjanstekontraktBestallning, BestallningsData data) {
        Tjanstekontrakt tjanstekontrakt = createOrUpdate(tjanstekontraktBestallning);
        Set<String> error = bestallningsDataValidator.validate(tjanstekontrakt);
        if (error.isEmpty()) {
            data.put(tjanstekontraktBestallning, tjanstekontrakt);
        } else {
            data.addError(error);
        }
    }

    private Tjanstekontrakt createOrUpdate(TjanstekontraktBestallning bestallning) {
        Tjanstekontrakt tjanstekontrakt = tjanstekontraktService.getTjanstekontraktByNamnrymd(bestallning.getNamnrymd());
        if (tjanstekontrakt == null) {
            tjanstekontrakt = new Tjanstekontrakt();
            tjanstekontrakt.setNamnrymd(bestallning.getNamnrymd());
            tjanstekontrakt.setBeskrivning(bestallning.getBeskrivning());
            tjanstekontrakt.setMajorVersion(bestallning.getMajorVersion());
        } else {
            if (!tjanstekontrakt.getBeskrivning().equals(bestallning.getBeskrivning())) {
                tjanstekontrakt.setBeskrivning(bestallning.getBeskrivning());
            }
            if (tjanstekontrakt.getMajorVersion() != bestallning.getMajorVersion()) {
                tjanstekontrakt.setMajorVersion(bestallning.getMajorVersion());
            }
        }
        return tjanstekontrakt;
    }

    private void prepareTjanstekomponent(TjanstekomponentBestallning tjanstekomponentBestallning, BestallningsData data) {
        Tjanstekomponent tjanstekomponent = createOrUpdate(tjanstekomponentBestallning);
        Set<String> error = bestallningsDataValidator.validate(tjanstekomponent);
        if (error.isEmpty()) {
            data.put(tjanstekomponentBestallning, tjanstekomponent);
        } else {
            data.addError(error);
        }
    }

    private Tjanstekomponent createOrUpdate(TjanstekomponentBestallning bestallning) {
        Tjanstekomponent tjanstekomponent = tjanstekomponentService.getTjanstekomponentByHSAId(bestallning.getHsaId());
        if (tjanstekomponent == null) {
            tjanstekomponent = new Tjanstekomponent();
            tjanstekomponent.setHsaId(bestallning.getHsaId());
            tjanstekomponent.setBeskrivning(bestallning.getBeskrivning());
        } else {
            if (!tjanstekomponent.getBeskrivning().equals(bestallning.getBeskrivning())) {
                tjanstekomponent.setBeskrivning(bestallning.getBeskrivning());
            }
        }
        return tjanstekomponent;
    }

    private void prepareLogiskAdress(LogiskadressBestallning logiskadressBestallning, BestallningsData data) {
        LogiskAdress logiskAdress = createOrUpdate(logiskadressBestallning);
        Set<String> error = bestallningsDataValidator.validate(logiskAdress);
        if (error.isEmpty()) {
            data.put(logiskadressBestallning, logiskAdress);
        } else {
            data.addError(error);
        }
    }

    private LogiskAdress createOrUpdate(LogiskadressBestallning bestallning) {
        LogiskAdress logiskAdress = logiskAdressService.getLogiskAdressByHSAId(bestallning.getHsaId());
        if (logiskAdress == null) {
            logiskAdress = new LogiskAdress();
            logiskAdress.setHsaId(bestallning.getHsaId());
            logiskAdress.setBeskrivning(bestallning.getBeskrivning());
        } else {
            if (!logiskAdress.getBeskrivning().equals(bestallning.getBeskrivning())) {
                logiskAdress.setBeskrivning(bestallning.getBeskrivning());
            }
        }
        return logiskAdress;
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

        if (anropsbehorighetList.size() == 0) {
            BestallningsData.AnropsBehorighetRelations abData = data.getAnropsbehorighetRelations(bestallning);
            Anropsbehorighet ab = createAnropsbehorighet(abData.getLogiskadress(), abData.getTjanstekontrakt(), abData.getTjanstekomponent(), from, tom);
            data.put(bestallning, ab);
        } else if (anropsbehorighetList.size() == 1) {
            Anropsbehorighet existingAnropsbehorighet = anropsbehorighetList.get(0);
            existingAnropsbehorighet.getFilter().size(); // TODO: ???
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
        if (vagvalList.size() == 0) {
            Vagval newVagval = createVagval(newVagvalData.getLogiskAdress(), newVagvalData.getTjanstekontrakt(), newVagvalData.getAnropsAdress(), from, tom);
            data.putNewVagval(bestallning, newVagval);
            return;
        }

        Vagval existingVagval = vagvalList.get(0);
        existingVagval.getAnropsAdress().getVagVal().size(); //TODO: ???
        //samma vägval
        if (existingVagval.getAnropsAdress().getRivTaProfil() == newVagvalData.getAnropsAdress().getRivTaProfil() &&
                existingVagval.getAnropsAdress().getTjanstekomponent() == newVagvalData.getAnropsAdress().getTjanstekomponent() &&
                existingVagval.getAnropsAdress().getAdress() == bestallning.getAdress()) {
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
            if (existingVagval.getAnropsAdress().getVagVal().size() == 1 || existingVagval.getAnropsAdress().getVagVal().stream().allMatch(avv -> avv.getDeleted()))  {
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
        anropsbehorighet.setIntegrationsavtal("AUTOTAKNING"); //TODO: Parameter?
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
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, -1);
            Date d = new Date(c.getTime().getTime());
            return d;
        }
        return null;
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

}
