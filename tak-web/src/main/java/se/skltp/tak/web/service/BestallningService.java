/*
 * Copyright © 2014-2026 Inera.
 * Copyright owner URL: https://www.inera.se/
 * SKLTP overview page: https://inera.atlassian.net/wiki/spaces/SKLTP/overview
 * This library is free software under the GNU Lesser General Public License v2.1 or later.
 * Please refer to the full license files at the project root.
 */
package se.skltp.tak.web.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
import se.skltp.tak.web.util.BatchLookupUtil;
import se.skltp.tak.web.validator.BestallningsDataValidator;

@Service
@Transactional
public class BestallningService {
    private static final Logger log = LoggerFactory.getLogger(BestallningService.class);
    private final ObjectMapper mapper;

    AnropsAdressService anropsAdressService;
    AnropsBehorighetService anropsBehorighetService;
    LogiskAdressService logiskAdressService;
    RivTaProfilService rivTaProfilService;
    TjanstekomponentService tjanstekomponentService;
    TjanstekontraktService tjanstekontraktService;
    VagvalService vagvalService;
    ConfigurationService configurationService;
    AlerterService alerterService;
    BestallningsDataValidator bestallningsDataValidator;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public BestallningService(
            ObjectMapper mapper,
            AnropsAdressService anropsAdressService,
            AnropsBehorighetService anropsBehorighetService,
            LogiskAdressService logiskAdressService,
            RivTaProfilService rivTaProfilService,
            TjanstekomponentService tjanstekomponentService,
            TjanstekontraktService tjanstekontraktService,
            VagvalService vagvalService,
            ConfigurationService configurationService,
            AlerterService alerterService,
            BestallningsDataValidator bestallningsDataValidator
    ) {
        this.mapper = mapper;
        this.anropsAdressService = anropsAdressService;
        this.anropsBehorighetService = anropsBehorighetService;
        this.logiskAdressService = logiskAdressService;
        this.rivTaProfilService = rivTaProfilService;
        this.tjanstekomponentService = tjanstekomponentService;
        this.tjanstekontraktService = tjanstekontraktService;
        this.vagvalService = vagvalService;
        this.configurationService = configurationService;
        this.alerterService = alerterService;
        this.bestallningsDataValidator = bestallningsDataValidator;
    }

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
        if (!data.hasErrors()) prepareComplexObjectsRelations(data, userName, new LookupCache());
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
        if (data.hasErrors()) {
            return;
        }

        addPlainEntities(data, userName);
        updateAddresses(data, userName);
        updatePermissions(data, userName);
        updateRoutings(data, userName);
        alertOnNewContracts(data);
    }

    private void addPlainEntities(BestallningsData data, String userName) {
        data.getAllLogiskAdresser().forEach(la -> logiskAdressService.add(la, userName));
        data.getAllTjanstekomponent().forEach(tk -> tjanstekomponentService.add(tk, userName));
        data.getAllTjanstekontrakt().forEach(tk -> tjanstekontraktService.add(tk, userName));
    }

    private void updateAddresses(BestallningsData data, String userName) {
        data.getAllAnropsAdress().forEach(aa -> anropsAdressService.update(aa, userName));
    }

    private void updatePermissions(BestallningsData data, String userName) {
        data.getAllaAnropsbehorighet().forEach(ab -> anropsBehorighetService.update(ab, userName));
    }

    private void updateRoutings(BestallningsData data, String userName) {
        for (BestallningsData.VagvalPair vvp : data.getAllaVagval()) {
            if (vvp.getOldVagval() != null) {
                vagvalService.update(vvp.getOldVagval(), userName);
            }
            if (vvp.getNewVagval() != null) {
                vagvalService.add(vvp.getNewVagval(), userName);
            }
        }
    }

    private void alertOnNewContracts(BestallningsData data) {
        data.getAllTjanstekontrakt().forEach(tk ->
            alerterService.alertOnNewContract(tk.getNamnrymd(), data.getFromDate())
        );
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
        Set<String> existingLogiskaAdresser = logiskAdressService.getExistingHsaIdKeys(
            inkludera.getLogiskadresser().stream().map(LogiskadressBestallning::getHsaId).collect(Collectors.toSet()));
        inkludera.getLogiskadresser()
            .removeIf(logiskadress -> existingLogiskaAdresser.contains(BatchLookupUtil.key(logiskadress.getHsaId())));
    }

    void validateRawDataIntegrity(BestallningsData data) {
        data.addError(bestallningsDataValidator.validateDataDubletter(data.getBestallning()));
    }

    private void prepareComplexObjectsRelations(BestallningsData data, String userName, LookupCache cache) {
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
            prepareAnropsbehorighetRelations(abBestallning, data, userName, cache);
        }

        for (VagvalBestallning vagvalBestallning : bestallning.getInkludera().getVagval()) {
            prepareVagvalRelations(vagvalBestallning, data, userName, cache);
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


    private void prepareVagvalRelations(VagvalBestallning vagvalBestallning, BestallningsData data, String userName,
                                        LookupCache cache) {
        RivTaProfil rivTaProfil = findRivtaInDB(vagvalBestallning.getRivtaprofil(), cache);
        Tjanstekomponent tjanstekomponent = findTjanstekomponentInDBorOrder(vagvalBestallning.getTjanstekomponent(), data, cache);
        LogiskAdress logiskAdress = findLogiskAdressInDBorOrder(vagvalBestallning.getLogiskAdress(), data, cache);
        Tjanstekontrakt tjanstekontrakt = findTjanstekontraktInDBorOrder(vagvalBestallning.getTjanstekontrakt(), data, cache);

        Set<String> errors = bestallningsDataValidator.validateRelatedObjects(vagvalBestallning, rivTaProfil, logiskAdress,
                tjanstekomponent, tjanstekontrakt, userName);
        if (!errors.isEmpty()) {
            data.addError(errors);
            return;
        }

        AnropsAdress adress = findOrCreateAnropsAdress(vagvalBestallning, data, rivTaProfil, tjanstekomponent);
        // Many vägval share the same AnropsAdress instance; validate (incl. DB duplicate check) once per instance
        errors = cache.anropsAdressErrors.computeIfAbsent(adress,
                a -> bestallningsDataValidator.validateAnropAddress(a, userName));
        if (!errors.isEmpty()) {
            data.addError(errors);
            return;
        }

        data.putRelations(vagvalBestallning, adress, logiskAdress, tjanstekontrakt);
    }

    private void prepareAnropsbehorighetRelations(AnropsbehorighetBestallning abBestallning, BestallningsData data,
                                                  String userName, LookupCache cache) {
        Tjanstekomponent tjanstekonsument = findTjanstekomponentInDBorOrder(abBestallning.getTjanstekonsument(), data, cache);
        LogiskAdress logiskAdress = findLogiskAdressInDBorOrder(abBestallning.getLogiskAdress(), data, cache);
        Tjanstekontrakt tjanstekontrakt = findTjanstekontraktInDBorOrder(abBestallning.getTjanstekontrakt(), data, cache);

        Set<String> errors = bestallningsDataValidator.validateRelatedObjects(abBestallning, logiskAdress, tjanstekonsument,
                tjanstekontrakt, userName);
        if (!errors.isEmpty()) {
            data.addError(errors);
            return;
        }

        data.putRelations(abBestallning, logiskAdress, tjanstekonsument, tjanstekontrakt);
    }

    private LogiskAdress findLogiskAdressInDBorOrder(String hsaId, BestallningsData data, LookupCache cache) {
        LogiskAdress existLogiskAdress = LookupCache.get(cache.logiskAdress, hsaId,
                logiskAdressService::getLogiskAdressByHSAId);
        if (existLogiskAdress == null) {
            existLogiskAdress = data.getLogiskAdress(hsaId);

        }
        return existLogiskAdress;
    }

    private Tjanstekomponent findTjanstekomponentInDBorOrder(String hsaId, BestallningsData data, LookupCache cache) {
        Tjanstekomponent existTjanstekomponent = LookupCache.get(cache.tjanstekomponent, hsaId,
                tjanstekomponentService::getTjanstekomponentByHSAId);
        if (existTjanstekomponent == null) {
            existTjanstekomponent = data.getTjanstekomponent(hsaId);
        }
        return existTjanstekomponent;
    }

    private Tjanstekontrakt findTjanstekontraktInDBorOrder(String namnrymd, BestallningsData data, LookupCache cache) {
        Tjanstekontrakt existTjanstekontrakt = LookupCache.get(cache.tjanstekontrakt, namnrymd,
                tjanstekontraktService::getTjanstekontraktByNamnrymd);
        if (existTjanstekontrakt == null) {
            existTjanstekontrakt = data.getTjanstekontrakt(namnrymd);
        }
        return existTjanstekontrakt;
    }

    private RivTaProfil findRivtaInDB(String rivta, LookupCache cache) {
        return LookupCache.get(cache.rivTaProfil, rivta, rivTaProfilService::getRivTaProfilByNamn);
    }


    private void prepareComplexObjects(BestallningsData data) {
        JsonBestallning bestallning = data.getBestallning();
        Date from = data.getFromDate();
        Date tom = data.getToDate();

        // Existing anropsbehörigheter/vägval are fetched with one batched query each instead of one query per row
        List<AnropsbehorighetBestallning> abBestallningar = bestallning.getInkludera().getAnropsbehorigheter();
        Map<String, List<Anropsbehorighet>> existingAnropsbehorigheter = anropsBehorighetService.getAnropsbehorighetGroupedByKey(
                abBestallningar.stream().map(AnropsbehorighetBestallning::getLogiskAdress).collect(Collectors.toSet()),
                abBestallningar.stream().map(AnropsbehorighetBestallning::getTjanstekonsument).collect(Collectors.toSet()),
                abBestallningar.stream().map(AnropsbehorighetBestallning::getTjanstekontrakt).collect(Collectors.toSet()));
        for (AnropsbehorighetBestallning abBestallning : abBestallningar) {
            List<Anropsbehorighet> candidates = existingAnropsbehorigheter.getOrDefault(BatchLookupUtil.key(
                    abBestallning.getLogiskAdress(), abBestallning.getTjanstekonsument(), abBestallning.getTjanstekontrakt()),
                    List.of());
            prepareAnropsbehorighet(abBestallning, data, from, tom, AnropsBehorighetService.filterOverlapping(candidates, from, tom));
        }

        List<VagvalBestallning> vvBestallningar = bestallning.getInkludera().getVagval();
        Map<String, List<Vagval>> existingVagval = vagvalService.getVagvalGroupedByKey(
                vvBestallningar.stream().map(VagvalBestallning::getLogiskAdress).collect(Collectors.toSet()),
                vvBestallningar.stream().map(VagvalBestallning::getTjanstekontrakt).collect(Collectors.toSet()));
        for (VagvalBestallning vvBestallning : vvBestallningar) {
            List<Vagval> candidates = existingVagval.getOrDefault(
                    BatchLookupUtil.key(vvBestallning.getLogiskAdress(), vvBestallning.getTjanstekontrakt()), List.of());
            prepareVagval(vvBestallning, data, from, tom, VagvalService.filterOverlapping(candidates, from, tom));
        }
    }

    private void prepareAnropsbehorighet(AnropsbehorighetBestallning bestallning, BestallningsData data, Date from, Date tom,
                                         List<Anropsbehorighet> anropsbehorighetList) {
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

    private void prepareVagval(VagvalBestallning bestallning, BestallningsData data, Date from, Date tom,
                               List<Vagval> vagvalList) {
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
                entityManager.detach(existingVagval.getAnropsAdress());
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
        LocalDate localDate = date.toLocalDate().minusDays(1);
        return Date.valueOf(localDate);
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

    /**
     * Per-build memoization of DB lookups. The DB is not modified while an order is being built,
     * so repeated lookups of the same key return the same result (including null = not in DB).
     */
    private static class LookupCache {
        private final Map<String, LogiskAdress> logiskAdress = new HashMap<>();
        private final Map<String, Tjanstekomponent> tjanstekomponent = new HashMap<>();
        private final Map<String, Tjanstekontrakt> tjanstekontrakt = new HashMap<>();
        private final Map<String, RivTaProfil> rivTaProfil = new HashMap<>();
        private final Map<AnropsAdress, Set<String>> anropsAdressErrors = new IdentityHashMap<>();

        private static <T> T get(Map<String, T> map, String key, Function<String, T> lookup) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
            T value = lookup.apply(key);
            map.put(key, value);
            return value;
        }
    }
}
