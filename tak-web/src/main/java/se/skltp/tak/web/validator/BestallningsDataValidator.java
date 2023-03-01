package se.skltp.tak.web.validator;

import org.springframework.stereotype.Component;
import se.skltp.tak.core.entity.*;
import se.skltp.tak.web.dto.bestallning.*;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.*;
import java.util.function.Function;

@Component
public class BestallningsDataValidator {

    private Validator validator;

    public BestallningsDataValidator(Validator validator) {
        this.validator = validator;
    }

    public Set<String> validateDataDubletter(JsonBestallning bestallning) {
        Set<String> errors = new HashSet<>();

        detectDuplicates(
                bestallning.getInkludera().getAnropsbehorigheter(),
                bestallning.getExkludera().getAnropsbehorigheter(),
                errors,
                "Anropsbehörigheten %s får inte finnas på flera ställen i beställningen.",
                duplicatedItem -> Arrays.asList(duplicatedItem.getLogiskAdress(), duplicatedItem.getTjanstekonsument(), duplicatedItem.getTjanstekontrakt()));

        detectDuplicates(
                bestallning.getInkludera().getVagval(),
                bestallning.getExkludera().getVagval(),
                errors,
                "Vägvalet %s får inte finnas på flera ställen i beställningen.",
                duplicatedItem -> Arrays.asList(duplicatedItem.getLogiskAdress(), duplicatedItem.getTjanstekontrakt()));

        detectDuplicates(
                bestallning.getInkludera().getTjanstekontrakt(),
                bestallning.getExkludera().getTjanstekontrakt(),
                errors,
                "Tjänstekontraktet %s får inte finnas på flera ställen i beställningen.",
                duplicatedItem -> Arrays.asList(duplicatedItem.getNamnrymd()));

        detectDuplicates(
                bestallning.getInkludera().getTjanstekomponenter(),
                bestallning.getExkludera().getTjanstekomponenter(),
                errors,
                "Tjänstekomponenten %s får inte finnas på flera ställen i beställningen.",
                duplicatedItem -> Arrays.asList(duplicatedItem.getHsaId()));

        detectDuplicates(
                bestallning.getInkludera().getLogiskadresser(),
                bestallning.getExkludera().getLogiskadresser(),
                errors,
                "Den logiska adressen %s får inte finnas på flera ställen i beställningen.",
                duplicatedItem -> Arrays.asList(duplicatedItem.getHsaId()));

        return errors;
    }

    private static <T> void detectDuplicates(
            List<T> includes,
            List<T> excludes,
            Set<String> errors,
            String rawErrorMsg,
            Function<T,List<String>> msgPartsFunc) {

        List<T> allItems = allIncludedAndExcluded(includes, excludes);

        // Note: It does not work to check duplicates by using a hashSet and check for failed inserts
        // (Because the used classes overrides equals() but not hashCode(), so all items must be compared
        // using equals())

        for (int itemIteration = 0; itemIteration < allItems.size(); itemIteration++) {
            for (int cmpIteration = itemIteration + 1; cmpIteration < allItems.size(); cmpIteration++) {
                T item = allItems.get(itemIteration);
                T cmpItem = allItems.get(cmpIteration);
                if (item.equals(cmpItem)) {
                    errors.add(String.format(rawErrorMsg, msgPartsFunc.apply(item)));
                    break;
                }
            }
        }
    }

    private static <T> List<T> allIncludedAndExcluded(Collection<? extends T>... inputs) {
        List<T> allItems = new ArrayList<T>();
        for (Collection<? extends T> input : inputs) {
            addAllUnlessNull(allItems, input);
        }
        return allItems;
    }

    private static <T> void addAllUnlessNull(List<T> target, Collection<? extends T> input) {
        if (input != null) {
            target.addAll(input);
        }
    }

    public <T> Set<String> validateHasRequiredFields(List<T> items, String errorMsg, Function<T, Boolean> hasRequiredFields) {
        Set<String> errors = new HashSet<>();
        for (T item : items) {
            if (!hasRequiredFields.apply(item)){
                errors.add(String.format("%s (%s)", errorMsg, item.toString()));
            }
        }
        return errors;
    }


    public Set<String> validate(Tjanstekontrakt tjanstekontrakt) {
        Set<String> error = new HashSet<>();
        Set<ConstraintViolation<Tjanstekontrakt>> violations = validator.validate(tjanstekontrakt);
        for (ConstraintViolation<Tjanstekontrakt> violation : violations) {
            error.add("Skapa Tjänstekontrakt: " + violation.getMessage());
        }
        return error;
    }

    public Set<String> validate(Tjanstekomponent tjanstekomponent) {
        Set<String> error = new HashSet<>();
        Set<ConstraintViolation<Tjanstekomponent>> violations = validator.validate(tjanstekomponent);
        for (ConstraintViolation<Tjanstekomponent> violation : violations) {
            error.add("Skapa Tjänstekomponent: " + violation.getMessage());
        }
        return error;
    }

    public Set<String> validate(LogiskAdress logiskAdress) {
        Set<String> error = new HashSet<>();
        Set<ConstraintViolation<LogiskAdress>> violations = validator.validate(logiskAdress);
        for (ConstraintViolation<LogiskAdress> violation : violations) {
            error.add("Skapa Logisk Adress: " + violation.getMessage());
        }
        return error;
    }

    public Set<String> validateRelatedObjects(VagvalBestallning bestallning, RivTaProfil profil, LogiskAdress logiskAdress,
                                              Tjanstekomponent tjanstekonsument, Tjanstekontrakt tjanstekontrakt, String userName) {
        Set<String> error = new HashSet<>();

        if (profil == null) {
            error.add(String.format("Skapa Vägval: RivTaProfil med namn = %s finns inte.", bestallning.getRivtaprofil()));
        } else {
            if (profil.getId() != 0L && !profil.isPublished() && !profil.getUpdatedBy().equals(userName)) {
                error.add(String.format("Skapa Vägval: RivTaProfil med namn = %s är opublicerad och skapad av %s.", profil.getNamn(), profil.getUpdatedBy()));
            }
        }

        if (tjanstekonsument == null) {
            error.add(String.format("Skapa Vägval: Tjänstekomponent med HSAId = %s finns inte.", bestallning.getTjanstekomponent()));
        } else {
            if (tjanstekonsument.getId() != 0L && !tjanstekonsument.isPublished() && !tjanstekonsument.getUpdatedBy().equals(userName)) {
                error.add(String.format("Skapa Vägval: Tjänstekomponent med HSAId = %s är opublicerad och skapad av %s.", tjanstekonsument.getHsaId(), tjanstekonsument.getUpdatedBy()));
            }
        }

        if (logiskAdress == null) {
            error.add(String.format("Skapa Vägval: Logisk Adress med HSAId = %s finns inte.", bestallning.getLogiskAdress()));
        } else {
            if (logiskAdress.getId() != 0L && !logiskAdress.isPublished() && !logiskAdress.getUpdatedBy().equals(userName)) {
                error.add(String.format("Skapa Vägval: Logisk Adress med HSAId = %s är opublicerad och skapad av %s.", logiskAdress.getHsaId(), logiskAdress.getUpdatedBy()));
            }
        }

        if (tjanstekontrakt == null) {
            error.add(String.format("Skapa Vägval: Tjänstekontrakt med namnrymd = %s finns inte.", bestallning.getTjanstekontrakt()));
        } else {
            if (tjanstekontrakt.getId() != 0L && !tjanstekontrakt.isPublished() && !tjanstekontrakt.getUpdatedBy().equals(userName)) {
                error.add(String.format("Skapa Vägval: Tjänstekontrakt med namnrymd = %s är opublicerad och skapad av %s.", tjanstekontrakt.getNamnrymd(), tjanstekontrakt.getUpdatedBy()));
            }
        }

        return error;
    }

    public Set<String> validateRelatedObjects(AnropsbehorighetBestallning bestallning, LogiskAdress logiskAdress,
                                              Tjanstekomponent tjanstekonsument, Tjanstekontrakt tjanstekontrakt, String userName) {
        Set<String> error = new HashSet<>();

        if (tjanstekonsument == null) {
            error.add(String.format("Skapa Anropsbehörighet: Tjänstekomponent med HSAId = %s finns inte.", bestallning.getTjanstekonsument()));
        } else {
            if (tjanstekonsument.getId() != 0L && !tjanstekonsument.isPublished() && !tjanstekonsument.getUpdatedBy().equals(userName)) {
                error.add(String.format("Skapa Anropsbehörighet: Tjänstekomponent med HSAId = %s är opublicerad och skapad av %s.", tjanstekonsument.getHsaId(), tjanstekonsument.getUpdatedBy()));
            }
        }

        if (tjanstekontrakt == null) {
            error.add(String.format("Skapa Anropsbehörighet: Tjänstekontrakt med namnrymd = %s finns inte.", bestallning.getTjanstekontrakt()));
        } else {
            if (tjanstekontrakt.getId() != 0L && !tjanstekontrakt.isPublished() && !tjanstekontrakt.getUpdatedBy().equals(userName)) {
                error.add(String.format("Skapa Anropsbehörighet: Tjänstekontrakt med namnrymd = %s är opublicerad och skapad av %s.", tjanstekontrakt.getNamnrymd(), tjanstekontrakt.getUpdatedBy()));
            }
        }

        if (logiskAdress == null) {
            error.add(String.format("Skapa Anropsbehörighet: Logisk Adress med HSAId = %s finns inte.", bestallning.getLogiskAdress()));
        } else {
            if (logiskAdress.getId() != 0L && !logiskAdress.isPublished() && !logiskAdress.getUpdatedBy().equals(userName)) {
                error.add(String.format("Skapa Anropsbehörighet: LogiskAdress med HSAId = %s är opublicerad och skapad av %s.", logiskAdress.getHsaId(), logiskAdress.getUpdatedBy()));
            }
        }

        return error;
    }


    public Set<String> validateAnropAddress(AnropsAdress adress, String userName) {
        Set<String> errors = new HashSet<>();
        Set<ConstraintViolation<AnropsAdress>> violations = validator.validate(adress);
        for (ConstraintViolation<AnropsAdress> violation : violations) {
            errors.add("Skapa Vägval: " + violation.getMessage());
        }

        if (adress.getId() != 0l && !adress.isPublished() && adress.getUpdatedBy() != userName) {
            errors.add(String.format("Skapa Vägval: Anropsadress (%s, %s, %s) är opublicerad och skapad av %s.",
                    adress.getAdress(), adress.getRivTaProfil(), adress.getTjanstekomponent(), adress.getUpdatedBy()));
        }
        return errors;
    }

    public Set<String> validateVagvalForDubblett(List<Vagval> list) {
        Set<String> error = new HashSet<>();
        if (list.size() > 1) {
            error.add(String.format("Det finns mer än ett vagval i databasen med dessa parametrar %s - %s.",
                    list.get(0).getLogiskAdress(), list.get(0).getTjanstekontrakt()));
        }
        return error;
    }

    public Set<String> validateAnropsbehorighetForDubblett(List<Anropsbehorighet> list) {
        Set<String> error = new HashSet<>();
        if (list.size() > 1) {
            error.add(String.format("Det finns mer än en anropsbehörighet i databasen med dessa parametrar %s - %s - %s.",
                    list.get(0).getLogiskAdress(), list.get(0).getTjanstekontrakt(), list.get(0).getTjanstekonsument()));
        }
        return error;
    }

    public Set<String> validateLogiskAdressRelationsForDelete(LogiskAdress logiskAdress) {
        Set<String> error = new HashSet<>();
        if (logiskAdress.getVagval().stream().anyMatch(it -> !it.getDeleted() ))
            error.add(String.format("Logisk Adress %s kan inte raderas: det finns vägval som pekar på den.", logiskAdress.getHsaId()));
        if (logiskAdress.getAnropsbehorigheter().stream().anyMatch(it -> !it.getDeleted() ))
            error.add(String.format("Logisk Adress %s kan inte raderas: det finns anropsbehörighet som pekar på den.", logiskAdress.getHsaId()));
        return error;
    }

    public Set<String> validateTjanstekomponentRelationsForDelete(Tjanstekomponent tjanstekomponent) {
        Set<String> error = new HashSet<>();
        if (tjanstekomponent.getAnropsAdresser().stream().anyMatch(it -> !it.getDeleted() ))
            error.add(String.format("Tjänstekomponent %s kan inte raderas: det finns anropsadress som pekar på den.", tjanstekomponent.getHsaId()));
        if (tjanstekomponent.getAnropsbehorigheter().stream().anyMatch(it -> !it.getDeleted() ))
            error.add(String.format("Tjänstekomponent %s kan inte raderas: det finns anropsbehörighet som pekar på den.", tjanstekomponent.getHsaId()));
        return error;
    }

    public Set<String> validateTjanstekontraktForDelete(Tjanstekontrakt tjanstekontrakt) {
        Set<String> error = new HashSet<>();
        if (tjanstekontrakt.getVagval().stream().anyMatch(it -> !it.getDeleted() ))
            error.add(String.format("Tjänstekontrakt %s kan inte raderas: det finns vägval som pekar på det.", tjanstekontrakt.getNamnrymd()));
        if (tjanstekontrakt.getAnropsbehorigheter().stream().anyMatch(it -> !it.getDeleted() ))
            error.add(String.format("Tjänstekontrakt %s kan inte raderas: det finns anropsbehörighet som pekar på det.", tjanstekontrakt.getNamnrymd()));
        return error;
    }

}
