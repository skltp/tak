package se.skltp.tak.web.validator;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;
import se.skltp.tak.core.entity.AbstractVersionInfo;
import se.skltp.tak.core.entity.AnropsAdress;
import se.skltp.tak.core.entity.Anropsbehorighet;
import se.skltp.tak.web.service.AnropsAdressService;
import se.skltp.tak.web.service.AnropsBehorighetService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class EntityValidator implements Validator {

    private final AnropsAdressService anropsAdressService;
    private final AnropsBehorighetService anropsBehorighetService;

    public EntityValidator(AnropsAdressService anropsAdressService, AnropsBehorighetService anropsBehorighetService) {
        this.anropsAdressService = anropsAdressService;
        this.anropsBehorighetService = anropsBehorighetService;
    }

    @Override
    public boolean supports(Class clazz) {
        return AbstractVersionInfo.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        if (AnropsAdress.class.equals(target.getClass())) {
            validateAnropsadress(errors, (AnropsAdress) target);
        }
        if (Anropsbehorighet.class.equals(target.getClass())) {
            validateAnropsbehorighet(errors, (Anropsbehorighet) target);
        }
    }

    private void validateAnropsadress(Errors errors, AnropsAdress a) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adress", "empty");
        rejectIfNotMatching(errors, a.getAdress(),"[0-9a-zA-Z_.:/\\-?&]*", "adress");
        rejectIfWrongLength(errors, a.getAdress(), 5, 255, "adress");

        rejectIfNull(errors, a.getTjanstekomponent(), "tjanstekomponent");
        rejectIfNull(errors, a.getRivTaProfil(), "rivTaProfil");

        if (anropsAdressService.hasDuplicate(a)) errors.reject("duplicate.anropsadress");
    }

    private void validateAnropsbehorighet(Errors errors, Anropsbehorighet a) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "integrationsavtal", "empty");
        rejectIfLeadingOrTrailingWhitespace(errors, a.getIntegrationsavtal(), "integrationsavtal");

        rejectIfNull(errors, a.getTjanstekonsument(), "tjanstekonsument");
        rejectIfNull(errors, a.getTjanstekontrakt(), "tjanstekontrakt");
        rejectIfNull(errors, a.getLogiskAdress(), "logiskAdress");
        rejectIfNull(errors, a.getFromTidpunkt(), "fromTidpunkt");
        rejectIfNull(errors, a.getTomTidpunkt(), "tomTidpunkt");

        if (errors.hasErrors()) return;

        if (a.getFromTidpunkt().after(a.getTomTidpunkt())) {
            errors.reject("date.order.anropsbehorighet");
        } else if (anropsBehorighetService.hasOverlappingDuplicate(a)) {
            errors.reject("overlapping.anropsbehorighet");
        }
    }

    private void rejectIfNull(Errors errors, Object value, String fieldName) {
        if(value == null) {
            errors.reject("missing.instance." + fieldName);
        }
    }

    private void rejectIfLeadingOrTrailingWhitespace(Errors errors, String value, String fieldName) {
        if(value == null || !value.trim().equals(value)) {
            errors.reject("whitespace.instance." + fieldName);
        }
    }

    private void rejectIfNotMatching(Errors errors, String value, String regex, String fieldName) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        if(!matcher.matches()) {
            errors.reject("invalid.content.instance." + fieldName);
        }
    }

    private void rejectIfWrongLength(Errors errors, String value, int min, int max, String fieldName) {
        if(value.length() < min || value.length() > max) {
            errors.reject("invalid.length.instance." + fieldName);
        }
    }
}
